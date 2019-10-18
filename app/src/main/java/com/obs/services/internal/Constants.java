/**
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

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import com.obs.services.internal.utils.AbstractAuthentication;
import com.obs.services.internal.utils.ObsAuthentication;
import com.obs.services.internal.utils.V2Authentication;
import com.obs.services.model.AuthTypeEnum;

public class Constants
{
	public static class CommonHeaders {
		
		public static final String CONTENT_LENGTH = "Content-Length";
		
		public static final String CONTENT_TYPE = "Content-Type";
		
		public static final String HOST = "Host";
		
		public static final String ETAG = "ETag";
		
		public static final String CONTENT_MD5 = "Content-MD5";
		
		public static final String ORIGIN = "Origin";
		
		public static final String USER_AGENT = "User-Agent";
		
		public static final String PROXY_AUTHORIZATION = "Proxy-Authorization";
		
		public static final String LOCATION = "Location";
		
		public static final String ACCESS_CONTROL_REQUEST_HEADERS = "Access-Control-Request-Headers";
		public static final String ACCESS_CONTROL_REQUEST_METHOD = "Access-Control-Request-Method";
		
		public static final String ACCESS_CONTROL_ALLOW_HEADERS = "Access-Control-Allow-Headers";
		public static final String ACCESS_CONTROL_MAX_AGE = "Access-Control-Max-Age";
		public static final String ACCESS_CONTROL_ALLOW_ORIGIN = "Access-Control-Allow-Origin";
		public static final String ACCESS_CONTROL_ALLOW_METHODS = "Access-Control-Allow-Methods";
		public static final String ACCESS_CONTROL_EXPOSE_HEADERS = "Access-Control-Expose-Headers";
		
		
		public static final String CACHE_CONTROL = "Cache-Control";
		
		public static final String CONTENT_DISPOSITION = "Content-Disposition";
		
		public static final String CONTENT_ENCODING = "Content-Encoding";
		
		public static final String CONTENT_LANGUAGE = "Content-Language";
		
		public static final String EXPIRES = "Expires";
		
		public static final String DATE = "Date";
		
		public static final String LAST_MODIFIED = "Last-Modified";
		
		public static final String CONNECTION = "Connection";
		
		public static final String AUTHORIZATION = "Authorization";
		
		public static final String RANGE = "Range";
		
		public static final String IF_MODIFIED_SINCE = "If-Modified-Since";
		
		public static final String IF_UNMODIFIED_SINCE = "If-Unmodified-Since";
		
		public static final String IF_MATCH = "If-Match";
		
		public static final String IF_NONE_MATCH = "If-None-Match";
		
		public static final String X_RESERVED_INDICATOR = "x-reserved-indicator";
	}
	
	public static class ObsRequestParams{
		public static final String UPLOAD_ID = "uploadId";
		public static final String VERSION_ID = "versionId";
		public static final String PREFIX = "prefix";
		public static final String MARKER = "marker";
		public static final String MAX_KEYS = "max-keys";
		public static final String MAX_UPLOADS = "max-uploads";
		public static final String DELIMITER = "delimiter";
		public static final String KEY_MARKER = "key-marker";
		public static final String UPLOAD_ID_MARKER = "upload-id-marker";
		public static final String VERSION_ID_MARKER = "version-id-marker";
		public static final String RESPONSE_CONTENT_TYPE = "response-content-type";
		public static final String RESPONSE_CONTENT_LANGUAGE = "response-content-language";
		public static final String RESPONSE_EXPIRES = "response-expires";
		public static final String RESPONSE_CACHE_CONTROL = "response-cache-control";
		public static final String RESPONSE_CONTENT_DISPOSITION = "response-content-disposition";
		public static final String RESPONSE_CONTENT_ENCODING = "response-content-encoding";
		public static final String X_IMAGE_PROCESS = "x-image-process";
		public static final String POSITION = "position";
		
		public static final String MAX_PARTS = "max-parts";
		public static final String PART_NUMBER_MARKER = "part-number-marker";
		public static final String PART_NUMBER = "partNumber";
		
		public static final String NAME = "name";
		public static final String LENGTH = "length";
		
		public static final String READAHEAD = "readAhead";
		public static final String X_CACHE_CONTROL = "x-cache-control";
		public static final String TASKID = "taskID";
	}
	
	
	public static final Map<AuthTypeEnum, IHeaders> HEADERS_MAP;
	public static final Map<AuthTypeEnum, IConvertor> CONVERTOR_MAP;
	public static final Map<AuthTypeEnum, AbstractAuthentication> AUTHTICATION_MAP;
	
	static {
		HEADERS_MAP = new HashMap<AuthTypeEnum, IHeaders>();
		HEADERS_MAP.put(AuthTypeEnum.V2, V2Headers.getInstance());
		HEADERS_MAP.put(AuthTypeEnum.V4, V2Headers.getInstance());
		HEADERS_MAP.put(AuthTypeEnum.OBS, ObsHeaders.getInstance());
		
		CONVERTOR_MAP = new HashMap<AuthTypeEnum, IConvertor>();
		CONVERTOR_MAP.put(AuthTypeEnum.V2, V2Convertor.getInstance());
		CONVERTOR_MAP.put(AuthTypeEnum.V4, V2Convertor.getInstance());
		CONVERTOR_MAP.put(AuthTypeEnum.OBS, ObsConvertor.getInstance());
		
		AUTHTICATION_MAP = new HashMap<AuthTypeEnum, AbstractAuthentication>();
		AUTHTICATION_MAP.put(AuthTypeEnum.V2, V2Authentication.getInstance());
		AUTHTICATION_MAP.put(AuthTypeEnum.OBS, ObsAuthentication.getInstance());
	}
	
    public static final String ACL_PRIVATE = "private";
    public static final String ACL_PUBLIC_READ = "public-read";
    public static final String ACL_PUBLIC_READ_WRITE = "public-read-write";
    public static final String ACL_PUBLIC_READ_DELIVERED = "public-read-delivered";
    public static final String ACL_PUBLIC_READ_WRITE_DELIVERED = "public-read-write-delivered";
    public static final String ACL_AUTHENTICATED_READ = "authenticated-read";
    public static final String ACL_BUCKET_OWNER_READ = "bucket-owner-read";
    public static final String ACL_BUCKET_OWNER_FULL_CONTROL = "bucket-owner-full-control";
    public static final String ACL_LOG_DELIVERY_WRITE = "log-delivery-write";
    
    public static final String ALL_USERS_URI = "http://acs.amazonaws.com/groups/global/AllUsers";

    public static final String AUTHENTICATED_USERS_URI = "http://acs.amazonaws.com/groups/global/AuthenticatedUsers";

    public static final String LOG_DELIVERY_URI = "http://acs.amazonaws.com/groups/s3/LogDelivery";
    
    public static final String PERMISSION_FULL_CONTROL = "FULL_CONTROL";
    
    public static final String PERMISSION_READ = "READ";
    
    public static final String PERMISSION_WRITE = "WRITE";
    
    public static final String PERMISSION_READ_ACP = "READ_ACP";

    public static final String PERMISSION_WRITE_ACP = "WRITE_ACP";
    
    public static final String PERMISSION_READ_OBJECT = "READ_OBJECT";
    
    public static final String PERMISSION_FULL_CONTROL_OBJECT = "FULL_CONTROL_OBJECT";

    public static final String DERECTIVE_COPY = "COPY";
    
    public static final String DERECTIVE_REPLACE = "REPLACE";
    
    public static final String DERECTIVE_REPLACE_NEW = "REPLACE_NEW";
    
	public static final String RESULTCODE_SUCCESS = "0";
	
    public static final String SERVICE = "s3";
    
    public static final String REQUEST_TAG = "aws4_request";
    
    public static final String V4_ALGORITHM = "AWS4-HMAC-SHA256";
    
    public static final String SHORT_DATE_FORMATTER = "yyyyMMdd";
    
    public static final String LONG_DATE_FORMATTER = "yyyyMMdd'T'HHmmss'Z'";
    
    public static final String HEADER_DATE_FORMATTER = "EEE, dd MMM yyyy HH:mm:ss z";
    
    public static final String EXPIRATION_DATE_FORMATTER = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
    
    public static final TimeZone GMT_TIMEZONE = TimeZone.getTimeZone("GMT");
    
    public static final String OBS_SDK_VERSION = "3.19.7.3";
    
    public static final String USER_AGENT_VALUE = "obs-sdk-java/" + Constants.OBS_SDK_VERSION;
    
    public static final String DEFAULT_ENCODING = "UTF-8";
    
    public static final String ISO_8859_1_ENCOING = "ISO-8859-1";
    
    public static final String HMAC_SHA1_ALGORITHM = "HmacSHA1";
    
    public static final String HMAC_SHA256_ALGORITHM = "HmacSHA256";
    
    public static final String OBS_HEADER_PREFIX = "x-obs-";
    
    public static final String OBS_HEADER_META_PREFIX = "x-obs-meta-";
    
    public static final String V2_HEADER_PREFIX = "x-amz-";
    
    public static final String V2_HEADER_META_PREFIX = "x-amz-meta-";
    
    public static final String V2_HEADER_PREFIX_CAMEL = "X-Amz-";
    
    public static final String REQUEST_ID_HEADER = "request-id";
    
    public static final String OEF_MARKER = "oef-marker";
    
    public static final String TRUE = "true";
    
    public static final String FALSE = "false";
    
    public static final String ENABLED = "Enabled";
    
    public static final String DISABLED = "Disabled";
    
    public static final String YES = "yes";
    
    
    public static final List<String> ALLOWED_RESPONSE_HTTP_HEADER_METADATA_NAMES = Collections.unmodifiableList(Arrays.asList(
        "content-type",
        "content-md5",
        "content-length",
        "content-language",
        "expires",
        "origin",
        "cache-control",
        "content-disposition",
        "content-encoding",
        "x-default-storage-class",
        "location",
        "date",
        "etag",
        "host",
        "last-modified",
        "content-range",
        "x-reserved",
        "x-reserved-indicator",
        "access-control-allow-origin",
        "access-control-allow-headers",
        "access-control-max-age",
        "access-control-allow-methods",
        "access-control-expose-headers",
        "connection"
        ));
    
    public static final List<String> ALLOWED_REQUEST_HTTP_HEADER_METADATA_NAMES = Collections.unmodifiableList(Arrays.asList(
        "content-type",
        "content-md5",
        "content-length",
        "content-language",
        "expires",
        "origin",
        "cache-control",
        "content-disposition",
        "content-encoding",
        "access-control-request-method",
        "access-control-request-headers",
        "success-action-redirect",
        "x-default-storage-class",
        "location",
        "date",
        "etag",
        "range",
        "host",
        "if-modified-since",
        "if-unmodified-since",
        "if-match",
        "if-none-match",
        "last-modified",
        "content-range",
        "x-cache-control"));
    
    
    public static final List<String> ALLOWED_RESOURCE_PARAMTER_NAMES = Collections.unmodifiableList(Arrays.asList(
    	"acl",
    	"backtosource",
        "policy",
        "torrent",
        "logging",
        "location",
        "storageinfo",
        "quota",
        "storagepolicy",
        "storageclass",
        "requestpayment",
        "versions",
        "versioning",
        "versionid",
        "uploads",
        "uploadid",
        "partnumber",
        "website",
        "notification",
        "lifecycle",
        "deletebucket",
        "delete",
        "cors",
        "restore",
        "tagging",
        "replication",
        "metadata",
        "encryption",
        "directcoldaccess",
        /**
         * File System API
         */
        "append",
        "position",
        "truncate",
        "modify",
        "rename",
        "length",
        "name",
        "fileinterface",
        
        "readahead",
        
        "response-content-type",
        "response-content-language",
        "response-expires",
        "response-cache-control",
        "response-content-disposition",
        "response-content-encoding",
        "x-image-save-bucket",
        "x-image-save-object",
        "x-image-process",
        "x-obs-sse-kms-key-project-id",
        "x-oss-process"));
    
}
