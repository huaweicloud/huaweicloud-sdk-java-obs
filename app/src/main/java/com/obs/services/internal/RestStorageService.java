/**
 * 
 * JetS3t : Java S3 Toolkit
 * Project hosted at http://bitbucket.org/jmurty/jets3t/
 *
 * Copyright 2006-2010 James Murty
 * 
 * Copyright 2019 Huawei Technologies Co.,Ltd.
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use
 * this file except in compliance with the License.  You may obtain a copy of the
 * License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed
 * under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
 * CONDITIONS OF ANY KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations under the License.
 */
package com.obs.services.internal;

import com.obs.log.ILogger;
import com.obs.log.InterfaceLogBean;
import com.obs.log.LoggerBuilder;
import com.obs.services.internal.Constants.CommonHeaders;
import com.obs.services.internal.consensus.CacheManager;
import com.obs.services.internal.consensus.SegmentLock;
import com.obs.services.internal.handler.XmlResponsesSaxParser;
import com.obs.services.internal.io.UnrecoverableIOException;
import com.obs.services.internal.security.ProviderCredentialThreadContext;
import com.obs.services.internal.security.ProviderCredentials;
import com.obs.services.internal.utils.*;
import com.obs.services.model.AuthTypeEnum;
import com.obs.services.model.HttpMethodEnum;
import okhttp3.*;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLException;
import javax.net.ssl.TrustManagerFactory;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.lang.reflect.Method;
import java.net.ConnectException;
import java.net.URI;
import java.net.UnknownHostException;
import java.text.ParseException;
import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicBoolean;

public abstract class RestStorageService {
	private static final ILogger log = LoggerBuilder.getLogger(RestStorageService.class);

	private static final Set<Class<? extends IOException>> nonRetriableClasses = new HashSet<Class<? extends IOException>>();
	
	private static final String REQUEST_TIMEOUT_CODE = "RequestTimeout";
	
	static {
		nonRetriableClasses.add(UnknownHostException.class);
		nonRetriableClasses.add(SSLException.class);
		nonRetriableClasses.add(ConnectException.class);
	}

	protected OkHttpClient httpClient;

	protected AtomicBoolean shuttingDown = new AtomicBoolean(false);

	protected ObsProperties obsProperties;

	protected volatile ProviderCredentials credentials;

	protected KeyManagerFactory keyManagerFactory;

	protected TrustManagerFactory trustManagerFactory;

	protected CacheManager apiVersionCache;

	protected SegmentLock segmentLock;
	
	protected Semaphore semaphore;

	protected RestStorageService() {
		
	}

	protected void initHttpClient(Dispatcher httpDispatcher) {
		
		OkHttpClient.Builder builder = RestUtils.initHttpClientBuilder(this, obsProperties, keyManagerFactory,
				trustManagerFactory, httpDispatcher);

		if (this.obsProperties.getBoolProperty(ObsConstraint.PROXY_ISABLE, true)) {
			String proxyHostAddress = this.obsProperties.getStringProperty(ObsConstraint.PROXY_HOST, null);
			int proxyPort = this.obsProperties.getIntProperty(ObsConstraint.PROXY_PORT, -1);
			String proxyUser = this.obsProperties.getStringProperty(ObsConstraint.PROXY_UNAME, null);
			String proxyPassword = this.obsProperties.getStringProperty(ObsConstraint.PROXY_PAWD, null);
			String proxyDomain = this.obsProperties.getStringProperty(ObsConstraint.PROXY_DOMAIN, null);
			String proxyWorkstation = this.obsProperties.getStringProperty(ObsConstraint.PROXY_WORKSTATION, null);
			RestUtils.initHttpProxy(builder, proxyHostAddress, proxyPort, proxyUser, proxyPassword, proxyDomain,
					proxyWorkstation);
		}

		this.httpClient = builder.build();
		//Fix okhttp bug
		int maxConnections = this.obsProperties.getIntProperty(ObsConstraint.HTTP_MAX_CONNECT, ObsConstraint.HTTP_MAX_CONNECT_VALUE);
		this.semaphore = new Semaphore(maxConnections);
	}

	protected void shutdownImpl() {
		if(shuttingDown.compareAndSet(false, true)) {
			this.credentials = null;
			this.obsProperties = null;
			if (this.httpClient != null) {
				try {
					Method dispatcherMethod = httpClient.getClass().getMethod("dispatcher");
					if (dispatcherMethod != null) {
						Method m = dispatcherMethod.invoke(httpClient).getClass().getDeclaredMethod("executorService");
						m.setAccessible(true);
						Object exeService = m.invoke(httpClient.dispatcher());
						if (exeService instanceof ExecutorService) {
							ExecutorService executorService = (ExecutorService)exeService;
							executorService.shutdown();
						}
					}
				} catch (Exception e) {
					//ignore
				} 
				if (httpClient.connectionPool() != null) {
					httpClient.connectionPool().evictAll();
				}
				httpClient = null;
			}
		}
		if (apiVersionCache != null) {
			apiVersionCache.clear();
			apiVersionCache = null;
		}
		if (segmentLock != null) {
			segmentLock.clear();
			segmentLock = null;
		}
	}

	protected boolean retryRequest(IOException exception, int executionCount, int retryMaxCount,
			Request request, Call call) {
		if (executionCount > retryMaxCount) {
			return false;
		}
		if (nonRetriableClasses.contains(exception.getClass())) {
			return false;
		} else {
			for (final Class<? extends IOException> rejectException : nonRetriableClasses) {
				if (rejectException.isInstance(exception)) {
					return false;
				}
			}
		}
		
		if(call.isCanceled()) {
			return false;
		}
		
		return true;
	}
	
	private ServiceException handleThrowable(Request request, Response response, InterfaceLogBean reqBean, Call call, Throwable t) {
		
		ServiceException serviceException = (t instanceof ServiceException) ? (ServiceException) t : new ServiceException("Request Error: " + t, t);
		serviceException.setRequestHost(request.header(CommonHeaders.HOST));
		serviceException.setRequestVerb(request.method());
		serviceException.setRequestPath(request.url().toString());
		
		if(response != null) {
			ServiceUtils.closeStream(response);
			serviceException.setResponseCode(response.code());
			serviceException.setResponseStatus(response.message());
			serviceException.setResponseDate(response.header(CommonHeaders.DATE));
			serviceException.setErrorIndicator(response.header(CommonHeaders.X_RESERVED_INDICATOR));
			serviceException
			.setResponseHeaders(ServiceUtils.cleanRestMetadataMapV2(convertHeadersToMap(response.headers()),
					getRestHeaderPrefix(), getRestMetadataPrefix()));
			if(!ServiceUtils.isValid(serviceException.getErrorRequestId())) {
				serviceException.setRequestAndHostIds(response.header(getIHeaders().requestIdHeader()),
						response.header(getIHeaders().requestId2Header()));
			}
		}
		
		if (log.isWarnEnabled()) {
			log.warn(serviceException);
		}
		
		if (call != null) {
			call.cancel();
		}
		return serviceException;
	}
	
	private void performRequestAsync(final Request request, final RequestContext context, final ObsCallback<Response, ServiceException> callback) throws InterruptedException{
		this.performRequestAsync(request, context, callback, false);
	}
	
	private boolean isLocationHostOnly(String location) {
		boolean isOnlyHost = false;
		
		URI uri;
		uri = URI.create(location);
		String path = uri.getPath();
		
		if (location.indexOf("?") < 0) {
			if(path == null || path.isEmpty() || path.equals("/")) {
				isOnlyHost = true;
			}
		}
		
		return isOnlyHost;
	}
	
	private void performRequestAsync(final Request request, final RequestContext context, final ObsCallback<Response, ServiceException> callback, final boolean isOEF) throws InterruptedException {
		
		Call call = httpClient.newCall(request);
		final long start = System.currentTimeMillis();
		call.enqueue(new Callback() {
			@Override
			public void onResponse(Call call, Response response) throws IOException {
				try {
					int responseCode = response.code();
					context.reqBean.setRespParams("[responseCode: " + responseCode + "][request-id: "
							+ response.header(getIHeaders().requestIdHeader(), "") + "]");
					
					String contentType = response.header(CommonHeaders.CONTENT_TYPE);
					if(log.isDebugEnabled()) {
						log.debug("Response for '" + context.method + "'. Content-Type: " + contentType + ", ResponseCode:" + responseCode + ", Headers: "
								+ response.headers());
					}
					if (log.isTraceEnabled()) {
						if (response.body() != null) {
							log.trace("Entity length: " + response.body().contentLength());
						}
					}
					
					if (responseCode >= 300 && responseCode < 400 && responseCode != 304) {
						String location = response.header(CommonHeaders.LOCATION);
						if (!ServiceUtils.isValid(location)) {
							ServiceException exception = new ServiceException("Try to redirect, but location is null!");
							context.reqBean.setResponseInfo("Request Error:" + exception.getMessage(),
									"|" + responseCode + "|" + response.message() + "|");
							throw exception;
						}
						
						if (location.indexOf("?") < 0) {
							location = addRequestParametersToUrlPath(location, context.requestParameters, isOEF);
						} 
						
						context.internalErrorCount++;
						
						if (context.internalErrorCount > context.retryMaxCount) {
							String xmlMessage = null;
							try {
								if( response.body() != null) {
									xmlMessage = response.body().string();
								}
							}catch (IOException e) {
							}
							throw new ServiceException("Exceeded 3xx redirect limit (" + context.retryMaxCount + ").", xmlMessage);
						}else {
							ServiceUtils.closeStream(response);
						}
						
						if(context.doSignature && isLocationHostOnly(location)) {
							performRequestAsync(authorizeHttpRequest(request, context.bucketName, location), context, callback); 
						}else {
							Request.Builder builder = request.newBuilder();
							RestStorageService.this.setHost(builder, request, location);
							performRequestAsync(builder.build(), context, callback);
						}
						return;
					}else if((responseCode >= 400 && responseCode < 500) || responseCode == 304) {
						String xmlMessage = null;
						try {
							if( response.body() != null) {
								xmlMessage = response.body().string();
							}
						}catch (IOException e) {
						}
						ServiceException exception = new ServiceException("Request Error.", xmlMessage);
						if (REQUEST_TIMEOUT_CODE.equals(exception.getErrorCode())) {
							context.internalErrorCount++;
							if (context.internalErrorCount < context.retryMaxCount) {
								if (log.isWarnEnabled()) {
									log.warn("Retrying connection that failed with RequestTimeout error"
											+ ", attempt number " + context.internalErrorCount + " of " + context.retryMaxCount);
								}
								performRequestAsync(authorizeHttpRequest(request, context.bucketName, null), context, callback); 
								return;
							}
							if (log.isErrorEnabled()) {
								log.error("Exceeded maximum number of retries for RequestTimeout errors: "
										+ context.retryMaxCount);
							}
						}
						throw exception;
					}else if (responseCode >= 500) {
						context.reqBean.setResponseInfo("Internal Server error(s).", String.valueOf(responseCode));
						if (log.isErrorEnabled()) {
							log.error(context.reqBean);
						}
						context.internalErrorCount++;
						sleepOnInternalError(context.internalErrorCount, context.retryMaxCount, response, context.reqBean);
						performRequestAsync(authorizeHttpRequest(request, context.bucketName, null), context, callback); 
						return;
					} 
					if (log.isInfoEnabled()) {
						context.reqBean.setRespTime(new Date());
						context.reqBean.setResultCode(Constants.RESULTCODE_SUCCESS);
						log.info(context.reqBean);
					}
					callback.onSuccess(response);
				} catch (Throwable t) {
					ServiceException s = handleThrowable(request, response, context.reqBean, call, t);
					callback.onFailure(s);
				}finally {
					if (log.isInfoEnabled()) {
						log.info("OkHttp cost " + (System.currentTimeMillis() - start) + " ms to apply http request");
					}
				}
			}
			
			@Override
			public void onFailure(Call call, IOException e) {
				try {
					if (e instanceof UnrecoverableIOException) {
						if (context.lastException != null) {
							throw context.lastException;
						} else {
							throw e;
						}
					}
					context.lastException = e;
					context.internalErrorCount++;
					if (retryRequest(e, context.internalErrorCount, context.retryMaxCount, request, call)) {
						long delayMs = 50L * (int) Math.pow(2, context.internalErrorCount);
						Thread.sleep(delayMs);
						performRequestAsync(authorizeHttpRequest(request, context.bucketName, null), context, callback); 
						return;
					}
					
					if ((e instanceof ConnectException) || (e instanceof InterruptedIOException)) {
						ServiceException se = new ServiceException("Request error. ", e);
						se.setResponseCode(408);
						se.setErrorCode("RequestTimeOut");
						se.setErrorMessage(e.getMessage());
						se.setResponseStatus("Request error. ");
						throw se;
					}
					throw e;
				}catch (Throwable t) {
					ServiceException s = handleThrowable(request, null, context.reqBean, call, t);
					callback.onFailure(s);
				}finally {
					if (log.isInfoEnabled()) {
						log.info("OkHttp cost " + (System.currentTimeMillis() - start) + " ms to apply http request");
					}
				}
			}
		});
	}
	
	private static class RequestContext{
		InterfaceLogBean reqBean;
		String method;
		int retryMaxCount;
		int internalErrorCount = 0;
		String bucketName;
		Exception lastException;
		boolean doSignature;
		Map<String, String> requestParameters;
	}
	
	private static class ResponseContext{
		Response response;
		ServiceException ex;
	}
	
	protected void performRequestAsync(Request request, Map<String, String> requestParameters, String bucketName, boolean doSignature, ObsCallback<Response, ServiceException> callback) throws ServiceException, InterruptedException {
		InterfaceLogBean reqBean = new InterfaceLogBean("performRequest", "", "");
		
		if (log.isDebugEnabled()) {
			log.debug("Performing " + request.method() + " request for '" + request.url());
			log.debug("Headers: " + request.headers());
		}
		RequestContext context = new RequestContext();
		context.reqBean = reqBean;
		context.method = request.method();
		context.retryMaxCount = obsProperties.getIntProperty(ObsConstraint.HTTP_RETRY_MAX,
				ObsConstraint.HTTP_RETRY_MAX_VALUE);
		context.bucketName = bucketName;
		context.requestParameters = requestParameters;
		context.doSignature = doSignature;
		
		if(doSignature) {
			request = authorizeHttpRequest(request, bucketName, null);
		}else {
			Request.Builder builder = request.newBuilder();
			builder.headers(request.headers().newBuilder().removeAll(CommonHeaders.AUTHORIZATION).build());
			this.setHost(builder, request, null);
			request = builder.build();
		}
		this.performRequestAsync(request, context, callback);
	}
	
	protected Response performRequestAsync(Request request, Map<String, String> requestParameters, String bucketName, boolean doSignature) throws ServiceException {
		final CountDownLatch latch = new CountDownLatch(1);
		final ResponseContext context = new ResponseContext();
		try {
			this.performRequestAsync(request, requestParameters, bucketName, doSignature, new ObsCallback<Response, ServiceException>() {
				
				@Override
				public void onSuccess(Response result) {
					context.response = result;
					latch.countDown();
				}
				
				@Override
				public void onFailure(ServiceException e) {
					context.ex = e;
					latch.countDown();
				}
			});
			latch.await();
		}catch (InterruptedException e) {
			throw new ServiceException(e);
		}
		
		if(context.ex != null) {
			throw context.ex;
		}
		
		return context.response;
	}
	
	protected Response performRequestAsync(Request request, Map<String, String> requestParameters, String bucketName) throws ServiceException {
		return this.performRequestAsync(request, requestParameters, bucketName, true);
	}
	
	protected Response performRequesttWithoutSignatureAsync(Request request, Map<String, String> requestParameters, String bucketName) throws ServiceException {
		return this.performRequestAsync(request, requestParameters, bucketName, false);
	}
	

	protected Response performRequest(Request request, Map<String, String> requestParameters, String bucketName) throws ServiceException {
		return performRequest(request, requestParameters, bucketName, true);
	}

	protected Response performRequestWithoutSignature(Request request, Map<String, String> requestParameters, String bucketName) throws ServiceException {
		return performRequest(request, requestParameters, bucketName, false);
	}
	
	protected Response performRequest(Request request, Map<String, String> requestParameters, String bucketName, boolean doSignature) throws ServiceException{
		return this.performRequest(request, requestParameters, bucketName, doSignature, false);
	}

	protected Response performRequest(Request request, Map<String, String> requestParameters, String bucketName, boolean doSignature, boolean isOEF) throws ServiceException {
		Response response = null;
		InterfaceLogBean reqBean = new InterfaceLogBean("performRequest", "", "");
		Call call = null;
		try {
			if (log.isDebugEnabled()) {
				log.debug("Performing " + request.method() + " request for '" + request.url());
				log.debug("Headers: " + request.headers());
			}

			boolean completedWithoutRecoverableError = false;
			int internalErrorCount = 0;
			boolean wasRecentlyRedirected = false;
			Exception lastException = null;
			int responseCode = -1;
			int retryMaxCount = obsProperties.getIntProperty(ObsConstraint.HTTP_RETRY_MAX,
					ObsConstraint.HTTP_RETRY_MAX_VALUE);
			do {
				if (!wasRecentlyRedirected) {
					if(doSignature) {
						request = authorizeHttpRequest(request, bucketName, null);
					}else {
						Request.Builder builder = request.newBuilder();
						builder.headers(request.headers().newBuilder().removeAll(CommonHeaders.AUTHORIZATION).build());
						this.setHost(builder, request, null);
						builder.header(CommonHeaders.USER_AGENT, Constants.USER_AGENT_VALUE);
						request = builder.build();
					}
				} else {
					wasRecentlyRedirected = false;
				}
				long start = System.currentTimeMillis();
				
				call = httpClient.newCall(request);
				try {
					semaphore.acquire();
					response = call.execute();
				} catch (IOException e) {
					if (e instanceof UnrecoverableIOException) {
						if (lastException != null) {
							throw lastException;
						} else {
							throw e;
						}
					}
					lastException = e;
					if (retryRequest(e, ++internalErrorCount, retryMaxCount, request, call)) {
						long delayMs = 50L * (int) Math.pow(2, internalErrorCount);
						Thread.sleep(delayMs);
						continue;
					}

					if ((e instanceof ConnectException) || (e instanceof InterruptedIOException)) {
						ServiceException se = new ServiceException("Request error. ", e);
						se.setResponseCode(408);
						se.setErrorCode("RequestTimeOut");
						se.setErrorMessage(e.getMessage());
						se.setResponseStatus("Request error. ");
						throw se;
					}
					throw e;
				}finally {
					semaphore.release();
					if (log.isInfoEnabled()) {
						log.info("OkHttp cost " + (System.currentTimeMillis() - start) + " ms to apply http request");
					}
				}

				responseCode = response.code();
				reqBean.setRespParams("[responseCode: " + responseCode + "][request-id: "
						+ response.header(this.getIHeaders().requestIdHeader(), "") + "]");
				
				String contentType = response.header(CommonHeaders.CONTENT_TYPE);
				if(log.isDebugEnabled()) {
					log.debug("Response for '" + request.method() + "'. Content-Type: " + contentType + ", ResponseCode:" + responseCode + ", Headers: "
							+ response.headers());
				}
				if (log.isTraceEnabled()) {
					if (response.body() != null) {
						log.trace("Entity length: " + response.body().contentLength());
					}
				}
				
				if(isOEF && Mimetypes.MIMETYPE_JSON.equalsIgnoreCase(contentType)) {
					if((responseCode >= 400 && responseCode < 500)) {
						String xmlMessage = null;
						try {
							if( response.body() != null) {
								xmlMessage = response.body().string();
							}
						}catch (IOException e) {
						}
						
						OefExceptionMessage oefException = (OefExceptionMessage) JSONChange.jsonToObj(new OefExceptionMessage(), ServiceUtils.toValid(xmlMessage));
						ServiceException exception = new ServiceException("Request Error." + ServiceUtils.toValid(xmlMessage));
						exception.setErrorMessage(oefException.getMessage());
						exception.setErrorCode(oefException.getCode());
						exception.setErrorRequestId(oefException.getRequest_id());
						
						throw exception;
					}else if (responseCode >= 500) {
						reqBean.setResponseInfo("Internal Server error(s).", String.valueOf(responseCode));
						if (log.isErrorEnabled()) {
							log.error(reqBean);
						}
						String xmlMessage = null;
						try {
							if( response.body() != null) {
								xmlMessage = response.body().string();
							}
						}catch (IOException e) {
						}
						ServiceException exception = new ServiceException(
								"Encountered too many 5xx errors (" + internalErrorCount + "), aborting request.", xmlMessage);
						throw exception;
					} 
					else {
						completedWithoutRecoverableError = true;
					}
				}else {
					if (responseCode >= 300 && responseCode < 400 && responseCode != 304) {
						String location = response.header(CommonHeaders.LOCATION);
						if (!ServiceUtils.isValid(location)) {
							ServiceException exception = new ServiceException("Try to redirect, but location is null!");
							reqBean.setResponseInfo("Request Error:" + exception.getMessage(),
									"|" + responseCode + "|" + response.message() + "|");
							throw exception;
						}
						
						if (location.indexOf("?") < 0) {
							location = addRequestParametersToUrlPath(location, requestParameters, isOEF);
						} 

						if (doSignature && isLocationHostOnly(location)) {
							request = authorizeHttpRequest(request, bucketName, location);
						}else {
							Request.Builder builder = request.newBuilder();
							this.setHost(builder, request, location);
							request = builder.build();
						}

						internalErrorCount++;
						wasRecentlyRedirected = true;
						if (internalErrorCount > retryMaxCount) {
							String xmlMessage = null;
							try {
								if(response.body() != null) {
									xmlMessage = response.body().string();
								}
							}catch (IOException e) {
							}
							throw new ServiceException("Exceeded 3xx redirect limit (" + retryMaxCount + ").", xmlMessage);
						}
						ServiceUtils.closeStream(response);
					}else if((responseCode >= 400 && responseCode < 500) || responseCode == 304) {
						String xmlMessage = null;
						try {
							if( response.body() != null) {
								xmlMessage = response.body().string();
							}
						}catch (IOException e) {
						}
						ServiceException exception = new ServiceException("Request Error.", xmlMessage);
						if (REQUEST_TIMEOUT_CODE.equals(exception.getErrorCode())) {
							internalErrorCount++;
							if (internalErrorCount < retryMaxCount) {
								if (log.isWarnEnabled()) {
									log.warn("Retrying connection that failed with RequestTimeout error"
											+ ", attempt number " + internalErrorCount + " of " + retryMaxCount);
								}
								continue;
							}
							if (log.isErrorEnabled()) {
								log.error("Exceeded maximum number of retries for RequestTimeout errors: "
										+ retryMaxCount);
							}
						}
						throw exception;
					}else if (responseCode >= 500) {
						reqBean.setResponseInfo("Internal Server error(s).", String.valueOf(responseCode));
						if (log.isErrorEnabled()) {
							log.error(reqBean);
						}
						internalErrorCount++;
						sleepOnInternalError(internalErrorCount, retryMaxCount, response, reqBean);
					} 
					else {
						completedWithoutRecoverableError = true;
					}
				}
				
			} while (!completedWithoutRecoverableError);
		} catch (Throwable t) {
			ServiceException serviceException = this.handleThrowable(request, response, reqBean, call, t);
			throw serviceException;
		}
		
		if (log.isInfoEnabled()) {
			reqBean.setRespTime(new Date());
			reqBean.setResultCode(Constants.RESULTCODE_SUCCESS);
			log.info(reqBean);
		}
		return response;
	}

	protected String getRestMetadataPrefix() {
		return this.getIHeaders().headerMetaPrefix();
	}

	protected String getRestHeaderPrefix() {
		return this.getIHeaders().headerPrefix();
	}

	private boolean isProviderCredentialsInValid(ProviderCredentials providerCredentials) {
		return providerCredentials == null || providerCredentials.getObsCredentialsProvider().getSecurityKey() == null ||
				!ServiceUtils.isValid(providerCredentials.getSecurityKey().getAccessKey()) || !ServiceUtils.isValid(providerCredentials.getSecurityKey().getSecretKey()) ;
	}
	
	private URI setHost(Request.Builder builder, Request request, String url) {
		URI uri;
		if (url == null) {
			uri = request.url().uri();
		} else {
			uri = URI.create(url);
			builder.url(url);
		}
		
		String portStr;
		if (getHttpsOnly()) {
			int securePort = this.getHttpsPort();
			portStr = securePort == 443 ? "" : ":" + securePort;
		} else {
			int insecurePort = this.getHttpPort();
			portStr = insecurePort == 80 ? "" : ":" + insecurePort;
		}
		
		builder.header(CommonHeaders.HOST, uri.getHost() + portStr);
		return uri;
	}

	protected Request authorizeHttpRequest(Request request, String bucketName, String url)
			throws ServiceException {

		Headers headers = request.headers().newBuilder().removeAll(CommonHeaders.AUTHORIZATION).build();
		Request.Builder builder = request.newBuilder();
		builder.headers(headers);
		
		URI uri = this.setHost(builder, request, url);
		String hostname = uri.getHost();
		
		ProviderCredentials providerCredentials = ProviderCredentialThreadContext.getInstance()
				.getProviderCredentials();
		if (isProviderCredentialsInValid(providerCredentials)) {
			providerCredentials = this.getProviderCredentials();
		}else {
			providerCredentials.setAuthType(this.getProviderCredentials().getAuthType());
		}
		if (isProviderCredentialsInValid(providerCredentials)) {
			if (log.isInfoEnabled()) {
				log.info("Service has no Credential and is un-authenticated, skipping authorization");
			}
			return request;
		}
		
		String dateHeader = this.getIHeaders().dateHeader();
		String date = request.header(dateHeader);
		Date now = null;
		boolean isV4 = providerCredentials.getAuthType() == AuthTypeEnum.V4;
		if (date != null) {
			try {
				now = isV4 ? ServiceUtils.getLongDateFormat().parse(date) : ServiceUtils.parseRfc822Date(date);
			} catch (ParseException e) {
				throw new ServiceException(dateHeader + " is not well-format", e);
			}
		}else {
			now = new Date();
		}
		builder.header(CommonHeaders.DATE, ServiceUtils.formatRfc822Date(now));

		String securityToken = providerCredentials.getSecurityKey().getSecurityToken();
		if (ServiceUtils.isValid(securityToken)) {
			builder.header(this.getIHeaders().securityTokenHeader(), securityToken);
		}

		String fullUrl = uri.getRawPath();
		String endpoint = this.getEndpoint();
		
		if ((!this.isPathStyle() || isCname()) && hostname != null && !isV4) {
		    if (isCname()) {
		        fullUrl = "/" + hostname + fullUrl; 
            } else if (ServiceUtils.isValid(bucketName) && !endpoint.equals(hostname) && hostname.indexOf(bucketName) >=0) {
                fullUrl = "/" + bucketName + fullUrl;
            }		    
		} 

		String queryString = uri.getRawQuery();
		if (queryString != null && queryString.length() > 0) {
			fullUrl += "?" + queryString;
		}

		if (log.isDebugEnabled()) {
			log.debug("For creating canonical string, using uri: " + fullUrl);
		}

		IAuthentication iauthentication;
		if (isV4) {
			builder.header(this.getIHeaders().contentSha256Header(), V4Authentication.content_sha256);
			iauthentication = V4Authentication.makeServiceCanonicalString(request.method(),
					convertHeadersToMap(builder.build().headers()), fullUrl, providerCredentials, now);
			if (log.isDebugEnabled()) {
				log.debug("CanonicalRequest:" + iauthentication.getCanonicalRequest());
			}
		} else {
			iauthentication = Constants.AUTHTICATION_MAP.get(providerCredentials.getAuthType()).makeAuthorizationString(
					request.method(), convertHeadersToMap(builder.build().headers()), fullUrl,
					Constants.ALLOWED_RESOURCE_PARAMTER_NAMES, providerCredentials);
		}
		if (log.isDebugEnabled()) {
			log.debug("StringToSign ('|' is a newline): " + iauthentication.getStringToSign().replace('\n', '|'));
		}

		String authorizationString = iauthentication.getAuthorization();
		builder.header(CommonHeaders.AUTHORIZATION, authorizationString);
		builder.header(CommonHeaders.USER_AGENT, Constants.USER_AGENT_VALUE);
		return builder.build();
	}

	protected Response performRestHead(String bucketName, String objectKey, Map<String, String> requestParameters,
			Map<String, String> requestHeaders) throws ServiceException {

		Request.Builder builder = setupConnection(HttpMethodEnum.HEAD, bucketName, objectKey, requestParameters, null);

		addRequestHeadersToConnection(builder, requestHeaders);

		return performRequest(builder.build(), requestParameters, bucketName);
	}
	
	protected Response performRestGet(String bucketName, String objectKey, Map<String, String> requestParameters,
			Map<String, String> requestHeaders) throws ServiceException {
		return this.performRestGet(bucketName, objectKey, requestParameters, requestHeaders, false);
	}
	
	protected Response performRestGetForListBuckets(String bucketName, String objectKey, Map<String, String> requestParameters,
			Map<String, String> requestHeaders) throws ServiceException {

		//no bucket name required for listBuckets
		Request.Builder builder = setupConnection(HttpMethodEnum.GET, bucketName, objectKey, requestParameters, null, false, true);

		addRequestHeadersToConnection(builder, requestHeaders);
		return performRequest(builder.build(), requestParameters, bucketName, true, false);
	}

	protected Response performRestGet(String bucketName, String objectKey, Map<String, String> requestParameters,
			Map<String, String> requestHeaders, boolean isOEF) throws ServiceException {

		Request.Builder builder = setupConnection(HttpMethodEnum.GET, bucketName, objectKey, requestParameters, null, isOEF);

		addRequestHeadersToConnection(builder, requestHeaders);
		return performRequest(builder.build(), requestParameters, bucketName, true, isOEF);
	}
	
	protected Response performRestPut(String bucketName, String objectKey, Map<String, String> metadata,
			Map<String, String> requestParameters, RequestBody body, boolean autoRelease) throws ServiceException {
		return this.performRestPut(bucketName, objectKey, metadata, requestParameters, body, autoRelease, false);
	}
	
	protected Response performRestPut(String bucketName, String objectKey, Map<String, String> metadata,
			Map<String, String> requestParameters, RequestBody body, boolean autoRelease, boolean isOEF) throws ServiceException {
		Request.Builder builder = setupConnection(HttpMethodEnum.PUT, bucketName, objectKey, requestParameters, body, isOEF);

		renameMetadataKeys(builder, metadata);

		Response result = performRequest(builder.build(), requestParameters, bucketName, true, isOEF);

		if (autoRelease) {
			result.close();
		}

		return result;
	}
	
	protected Response performRestPost(String bucketName, String objectKey, Map<String, String> metadata,
			Map<String, String> requestParameters, RequestBody body, boolean autoRelease) throws ServiceException {
		return this.performRestPost(bucketName, objectKey, metadata, requestParameters, body, autoRelease, false);
	}

	protected Response performRestPost(String bucketName, String objectKey, Map<String, String> metadata,
			Map<String, String> requestParameters, RequestBody body, boolean autoRelease, boolean isOEF) throws ServiceException {
		Request.Builder builder = setupConnection(HttpMethodEnum.POST, bucketName, objectKey, requestParameters, body, isOEF);

		renameMetadataKeys(builder, metadata);

		Response result = performRequest(builder.build(), requestParameters, bucketName, true, isOEF);

		if (autoRelease) {
			result.close();
		}

		return result;
	}
	
	protected Response performRestDelete(String bucketName, String objectKey, Map<String, String> requestParameters, Map<String, String> metadata)
			throws ServiceException {
		return this.performRestDelete(bucketName, objectKey, requestParameters, metadata, false);
	}
	
	protected Response performRestDelete(String bucketName, String objectKey, Map<String, String> requestParameters, 
			Map<String, String> metadata, boolean isOEF)
			throws ServiceException {

		Request.Builder builder = setupConnection(HttpMethodEnum.DELETE, bucketName, objectKey, requestParameters,
				null, isOEF);
		
		renameMetadataKeys(builder, metadata);

		Response result = performRequest(builder.build(), requestParameters, bucketName, true, isOEF);
		
		result.close();

		return result;
	}
	
	protected Response performRestDelete(String bucketName, String objectKey, Map<String, String> requestParameters) throws ServiceException {
		return this.performRestDelete(bucketName, objectKey, requestParameters, true);
	}

	protected Response performRestDelete(String bucketName, String objectKey, Map<String, String> requestParameters, boolean autoRelease)
			throws ServiceException {

		Request.Builder builder = setupConnection(HttpMethodEnum.DELETE, bucketName, objectKey, requestParameters,
				null);

		Response result = performRequest(builder.build(), requestParameters, bucketName);
		
		if(autoRelease) {
			result.close();
		}

		return result;
	}

	protected Response performRestOptions(String bucketName, String objectKey, Map<String, String> metadata,
			Map<String, String> requestParameters, boolean autoRelease) throws ServiceException {

		Request.Builder builder = setupConnection(HttpMethodEnum.OPTIONS, bucketName, objectKey, requestParameters,
				null);

		addRequestHeadersToConnection(builder, metadata);

		Response result = performRequest(builder.build(), requestParameters, bucketName);

		if (autoRelease) {
			result.close();
		}
		return result;
	}

	protected Response performRestForApiVersion(String bucketName, String objectKey, Map<String, String> requestParameters,
	                                            Map<String, String> requestHeaders) throws ServiceException {

		Request.Builder builder = setupConnection(HttpMethodEnum.HEAD, bucketName, objectKey, requestParameters, null, false, true);

		addRequestHeadersToConnection(builder, requestHeaders);

		return performRequestWithoutSignature(builder.build(), requestParameters, bucketName);
	}

	protected void shutdown() {
		this.shutdownImpl();
	}

	protected void sleepOnInternalError(int internalErrorCount, int retryMaxCount, Response response,
			InterfaceLogBean reqBean) throws ServiceException {
		String xmlMessage = null;
		if (internalErrorCount <= retryMaxCount) {
			ServiceUtils.closeStream(response);
			long delayMs = 50L * (int) Math.pow(2, internalErrorCount);
			if (log.isWarnEnabled()) {
				log.warn("Encountered " + internalErrorCount + " Internal Server error(s), will retry in " + delayMs
						+ "ms");
			}
			try {
				Thread.sleep(delayMs);
			} catch (InterruptedException e) {
			}
		} else {
			try {
				xmlMessage = response.body().string();
			} catch (IOException e) {
			}
			ServiceException exception = new ServiceException(
					"Encountered too many 5xx errors (" + internalErrorCount + "), aborting request.", xmlMessage);
			throw exception;
		}
	}

	protected Map<String, String> convertHeadersToMap(Headers headers) {
		Map<String, String> map = new IdentityHashMap<String, String>();
		for (Map.Entry<String, List<String>> entry : headers.toMultimap().entrySet()) {
			List<String> values = entry.getValue();
			for (String value : values) {
				map.put(new String(entry.getKey()), value);
			}
		}
		return map;
	}

	protected ProviderCredentials getProviderCredentials() {
		return credentials;
	}

	protected void setProviderCredentials(ProviderCredentials credentials) {
		this.credentials = credentials;
	}

	protected void renameMetadataKeys(Request.Builder builder, Map<String, String> metadata) {
		Map<String, String> convertedMetadata = new HashMap<String, String>();
		if (metadata != null) {
			for (Map.Entry<String, String> entry : metadata.entrySet()) {
				String key = entry.getKey();
				String value = entry.getValue();
				if (!ServiceUtils.isValid(key)) {
					continue;
				}
				key = key.trim();
				if (!key.startsWith(this.getRestHeaderPrefix()) && !key.startsWith(Constants.OBS_HEADER_PREFIX)
						&& !Constants.ALLOWED_REQUEST_HTTP_HEADER_METADATA_NAMES
								.contains(key.toLowerCase(Locale.getDefault()))) {
					key = this.getRestMetadataPrefix() + key;
				}
				try {
					if(key.startsWith(this.getRestMetadataPrefix())) {
						key = RestUtils.uriEncode(key, true);
					}
					convertedMetadata.put(key, RestUtils.uriEncode(value == null ? "" : value, true));
				} catch (ServiceException e) {
					if (log.isDebugEnabled()) {
						log.debug("Ignore metadata key:" + key);
					}
				}
			}
		}
		for (Map.Entry<String, String> entry : convertedMetadata.entrySet()) {
			builder.addHeader(entry.getKey(), entry.getValue());
			if (log.isDebugEnabled()) {
				log.debug("Added request header to connection: " + entry.getKey() + "=" + entry.getValue());
			}
		}
	}

	protected Request.Builder setupConnection(HttpMethodEnum method, String bucketName, String objectKey,
			Map<String, String> requestParameters, RequestBody body) throws ServiceException{
		return this.setupConnection(method, bucketName, objectKey, requestParameters, body, false);
	}
	
	protected Request.Builder setupConnection(HttpMethodEnum method, String bucketName, String objectKey,
			Map<String, String> requestParameters, RequestBody body, boolean isOEF) throws ServiceException {
		return this.setupConnection(method, bucketName, objectKey, requestParameters, body, isOEF, false);
	}
	
	protected Request.Builder setupConnection(HttpMethodEnum method, String bucketName, String objectKey,
			Map<String, String> requestParameters, RequestBody body, boolean isOEF, boolean isListBuckets) throws ServiceException {

		boolean pathStyle = this.isPathStyle();
		String endPoint = this.getEndpoint();
		boolean isCname = this.isCname();
		String hostname = (isCname || isListBuckets) ? endPoint : ServiceUtils.generateHostnameForBucket(RestUtils.encodeUrlString(bucketName),
				pathStyle, endPoint);
		String resourceString = "/";
		if (hostname.equals(endPoint) && !isCname && bucketName.length() > 0 )
		{
			resourceString += RestUtils.encodeUrlString(bucketName);
		}
		if (objectKey != null) {
			resourceString += ((pathStyle && !isCname) ? "/" : "") + RestUtils.encodeUrlString(objectKey);
		}

		String url = null;
		if (getHttpsOnly()) {
			int securePort = this.getHttpsPort();
			String securePortStr = securePort == 443 ? "" : ":" + securePort;
			url = "https://" + hostname + securePortStr + resourceString;
		} else {
			int insecurePort = this.getHttpPort();
			String insecurePortStr = insecurePort == 80 ? "" : ":" + insecurePort;
			url = "http://" + hostname + insecurePortStr + resourceString;
		}
		if (log.isDebugEnabled()) {
			log.debug("OBS URL: " + url);
		}
		url = addRequestParametersToUrlPath(url, requestParameters, isOEF);

		Request.Builder builder = new Request.Builder();
		builder.url(url);
		if (body == null) {
			body = RequestBody.create(null, "");
		}
		switch (method) {
		case PUT:
			builder.put(body);
			break;
		case POST:
			builder.post(body);
			break;
		case HEAD:
			builder.head();
			break;
		case GET:
			builder.get();
			break;
		case DELETE:
			builder.delete(body);
			break;
		case OPTIONS:
			builder.method("OPTIONS", null);
			break;
		default:
			throw new IllegalArgumentException("Unrecognised HTTP method name: " + method);
		}
		
		if(!this.isKeepAlive()) {
			builder.addHeader("Connection", "Close");
		}
		
		return builder;
	}

	protected String addRequestParametersToUrlPath(String urlPath, Map<String, String> requestParameters)
	{
		return this.addRequestParametersToUrlPath(urlPath, requestParameters, false);
	}
	
	protected String addRequestParametersToUrlPath(String urlPath, Map<String, String> requestParameters, boolean isOEF)
			throws ServiceException {
		if (requestParameters != null) {
			for (Map.Entry<String, String> entry : requestParameters.entrySet()) {
				String key = entry.getKey();
				String value = entry.getValue();
				if(isOEF) {
					if(isPathStyle()) {
						urlPath += "/" + key;
					}else {
						urlPath += key;
					}	
				}else {
					urlPath += (urlPath.indexOf("?") < 0 ? "?" : "&") + RestUtils.encodeUrlString(key);
				}
				if (ServiceUtils.isValid(value)) {
					urlPath += "=" + RestUtils.encodeUrlString(value);
					if (log.isDebugEnabled()) {
						log.debug("Added request parameter: " + key + "=" + value);
					}
				} else {
					if (log.isDebugEnabled()) {
						log.debug("Added request parameter without value: " + key);
					}
				}
			}
		}
		return urlPath;
	}
	
	protected XmlResponsesSaxParser getXmlResponseSaxParser() throws ServiceException {
		return new XmlResponsesSaxParser();
	}

	protected void addRequestHeadersToConnection(Request.Builder builder, Map<String, String> requestHeaders) {
		if (requestHeaders != null) {
			for (Map.Entry<String, String> entry : requestHeaders.entrySet()) {
				String key = entry.getKey();
				String value = entry.getValue();
				if (!ServiceUtils.isValid(key) || value == null) {
					continue;
				}
				key = key.trim();

				if (!Constants.ALLOWED_REQUEST_HTTP_HEADER_METADATA_NAMES.contains(key.toLowerCase(Locale.getDefault()))
						&& !key.startsWith(this.getRestHeaderPrefix())) {
					continue;
				}
				builder.addHeader(key, value);
				if (log.isDebugEnabled()) {
					log.debug("Added request header to connection: " + key + "=" + value);
				}
			}
		}
	}
	
	protected IHeaders getIHeaders() {
		return Constants.HEADERS_MAP.get(this.getProviderCredentials().getAuthType());
	}

	protected IConvertor getIConvertor() {
		return Constants.CONVERTOR_MAP.get(this.getProviderCredentials().getAuthType());
	}


	protected boolean isKeepAlive()
    {
        return this.obsProperties.getBoolProperty(ObsConstraint.KEEP_ALIVE, true);
    }
	
	protected String getEndpoint() {
		return this.obsProperties.getStringProperty(ObsConstraint.END_POINT, "");
	}

	protected boolean isPathStyle() {
		return this.obsProperties.getBoolProperty(ObsConstraint.DISABLE_DNS_BUCKET, false);
	}

	protected int getHttpPort() {
		return this.obsProperties.getIntProperty(ObsConstraint.HTTP_PORT, ObsConstraint.HTTP_PORT_VALUE);
	}

	protected int getHttpsPort() {
		return this.obsProperties.getIntProperty(ObsConstraint.HTTPS_PORT, ObsConstraint.HTTPS_PORT_VALUE);
	}

	protected boolean getHttpsOnly() {
		return this.obsProperties.getBoolProperty(ObsConstraint.HTTPS_ONLY, true);
	}

	protected boolean isAuthTypeNegotiation() {
		return this.obsProperties.getBoolProperty(ObsConstraint.AUTH_TYPE_NEGOTIATION, true);
	}

	protected CacheManager getApiVersionCache() {
		return apiVersionCache;
	}
	
	protected boolean isCname() {
		return this.obsProperties.getBoolProperty(ObsConstraint.IS_CNAME, false);
	}

	protected String getFileSystemDelimiter() {
		return this.obsProperties.getStringProperty(ObsConstraint.FS_DELIMITER, "/");
	}

}
