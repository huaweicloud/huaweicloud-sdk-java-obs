package com.obs.services.sample;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.obs.services.IObsClient;
import com.obs.services.ObsClient;
import com.obs.services.ObsConfiguration;
import com.obs.services.exception.ObsException;
import com.obs.services.internal.Constants;
import com.obs.services.internal.ServiceException;
import com.obs.services.internal.io.MayRepeatableInputStream;
import com.obs.services.internal.utils.ServiceUtils;
import com.obs.services.model.AbortMultipartUploadRequest;
import com.obs.services.model.AccessControlList;
import com.obs.services.model.AppendObjectRequest;
import com.obs.services.model.AppendObjectResult;
import com.obs.services.model.AuthTypeEnum;
import com.obs.services.model.BucketCors;
import com.obs.services.model.BucketCorsRule;
import com.obs.services.model.BucketLoggingConfiguration;
import com.obs.services.model.BucketMetadataInfoRequest;
import com.obs.services.model.BucketMetadataInfoResult;
import com.obs.services.model.BucketNotificationConfiguration;
import com.obs.services.model.BucketQuota;
import com.obs.services.model.BucketStorageInfo;
import com.obs.services.model.BucketTagInfo;
import com.obs.services.model.BucketTagInfo.TagSet;
import com.obs.services.model.BucketVersioningConfiguration;
import com.obs.services.model.CanonicalGrantee;
import com.obs.services.model.CompleteMultipartUploadRequest;
import com.obs.services.model.CopyObjectRequest;
import com.obs.services.model.CopyPartRequest;
import com.obs.services.model.CopyPartResult;
import com.obs.services.model.DeleteObjectsRequest;
import com.obs.services.model.DownloadFileRequest;
import com.obs.services.model.DownloadFileResult;
import com.obs.services.model.GetObjectMetadataRequest;
import com.obs.services.model.GetObjectRequest;
import com.obs.services.model.GrantAndPermission;
import com.obs.services.model.GranteeInterface;
import com.obs.services.model.GroupGrantee;
import com.obs.services.model.HttpMethodEnum;
import com.obs.services.model.InitiateMultipartUploadRequest;
import com.obs.services.model.InitiateMultipartUploadResult;
import com.obs.services.model.KeyAndVersion;
import com.obs.services.model.LifecycleConfiguration;
import com.obs.services.model.ListBucketsRequest;
import com.obs.services.model.ListBucketsResult;
import com.obs.services.model.ListMultipartUploadsRequest;
import com.obs.services.model.ListObjectsRequest;
import com.obs.services.model.ListVersionsResult;
import com.obs.services.model.MultipartUpload;
import com.obs.services.model.MultipartUploadListing;
import com.obs.services.model.ObjectListing;
import com.obs.services.model.ObjectMetadata;
import com.obs.services.model.ObjectRepleaceMetadata;
import com.obs.services.model.ObsBucket;
import com.obs.services.model.ObsObject;
import com.obs.services.model.OptionsInfoRequest;
import com.obs.services.model.OptionsInfoResult;
import com.obs.services.model.Owner;
import com.obs.services.model.PartEtag;
import com.obs.services.model.Permission;
import com.obs.services.model.ProgressListener;
import com.obs.services.model.ProgressStatus;
import com.obs.services.model.PutObjectRequest;
import com.obs.services.model.Redirect;
import com.obs.services.model.RestoreObjectRequest;
import com.obs.services.model.RestoreObjectRequest.RestoreObjectStatus;
import com.obs.services.model.RouteRule;
import com.obs.services.model.RouteRuleCondition;
import com.obs.services.model.TemporarySignatureRequest;
import com.obs.services.model.TemporarySignatureResponse;
import com.obs.services.model.TopicConfiguration;
import com.obs.services.model.UploadFileRequest;
import com.obs.services.model.UploadPartRequest;
import com.obs.services.model.UploadPartResult;
import com.obs.services.model.VersionOrDeleteMarker;
import com.obs.services.model.WebsiteConfiguration;

public class ObsSdkDemo {

	static volatile ObsClient client;

	static ObsClient getObsClient() throws ObsException {
		if (client != null) {
			return client;
		}
		String endPoint = "http://10.183.175.49"; // 存储服务器地址
		endPoint = "http://10.183.184.229";
		// endPoint = "8.42.253.26";
		// endPoint = "8.42.147.3";
		endPoint = "http://10.175.38.50";
		endPoint = "http://obs.cn-north-1.myhuaweicloud.com";
		endPoint = "http://10.175.38.120";
//		endPoint = "http://8.45.130.2";
		// endPoint = "http://8.45.140.2";
		// endPoint = "http://100.114.236.5";
		// endPoint = "100.114.239.250";
		String ak = "UDSIAMSTUBTEST000333"; // 接入证书
		// ak = "A8W3OZWG8Z9URTDU7B9O";
		ak = "XVSM6UAMWTPEC8G6IYBY";
		// ak = "EZPSMMTRAFLLCSU6HDTI";
		// ak = "2MKSA12GYK0MKA2IPJQZ";
		// ak = "UDSIAMSTUBTEST000003";
		ak = "8TUS1UJPWK1QEHB8RW6Q";
		// ak = "UDSIAMSTUBTEST000010";
//		ak = "VXYNVG7AC2UTWTWF9PEZ";
//		ak = "OE0WTUCNR5FSPNJVJK4S";
		ak = "KCC522VNAPTY7HJ7UDEL";
//		ak = "UDSIAMSTUBTEST002977";
		String sk = "Udsiamstubtest000000UDSIAMSTUBTEST000333"; // 安全证书
		// sk = "ofN0wG880aiCt2es17awsJcea5VNsajEMvEffMk4";
		sk = "NxiNK1IfQVtZ4dSPNMw9QdU6EYLmXJJgvz8pB1Gq";
		// sk = "eDjOvGICKs02yBl4ckeP5mYpayjLmNF6tbT4JDxX";
		// sk = "JLSzpioyB0jgpgyY0wViieZvsXKFe0SPcrhKUOZd";
		// sk = "Udsiamstubtest000000UDSIAMSTUBTEST000003";
		sk = "WbWxR1uojJ2DyhLUlU3cb8OeTNkFDXPGOvmSY0Ep";
		// sk = "Udsiamstubtest000000UDSIAMSTUBTEST000010";
//		sk = "UBcFmneLsLPdBQcFCyrKpywSFTebL4nSeabMtNUt";
//		sk = "2eLy8HpwNt0PiHQQgkzaifdDfAGfeweXJRpr0NC1";
		sk = "iL7bD7fegefA3yCHwttPfrqDIxaOC1E98xF7FJDP";
//		sk = "Udsiamstubtest000000UDSIAMSTUBTEST002977";

		ObsConfiguration config = new ObsConfiguration();
		config.setEndPoint(endPoint);
		config.setValidateCertificate(false);
		config.setVerifyResponseContentType(true);
		config.setMaxConnections(1000);
		config.setMaxErrorRetry(0);
		config.setKeepAlive(true);
		config.setAuthType(AuthTypeEnum.V4);
		config.setAuthTypeNegotiation(false);
		config.setEndpointHttpsPort(35000);
//		config.setDisableDnsBucket(true);
//		config.setAuthType(AuthTypeEnum.V4);
//		config.setHttpsOnly(false);
//		config.setAuthTypeNegotiation(false);
		// config.setUploadStreamRetryBufferSize(1024);
		// 实例化ObsClient服务
		client = new ObsClient(ak, sk, config);
		return client;
	}

	static void createBucket(String bucketName) {

		try {
			// 创建桶实例
			ObsBucket s3Bucket = new ObsBucket();
			// String location = "region1-cd";
			Map<String, Object> metadata = new HashMap<String, Object>();
			// s3Bucket.setLocation("R1");
			s3Bucket.setBucketName(bucketName);

			// AccessControlList acl = new AccessControlList();
			// Owner owner = new Owner();
			// owner.setId("domainiddomainiddomainiddo020999");
			// acl.setOwner(owner);
			//
			// GranteeInterface grantee = new
			// CanonicalGrantee("domainiddomainiddomainiddo020999");
			// Permission permission = Permission.PERMISSION_READ;
			// acl.grantPermission(grantee, permission);
			// s3Bucket.setAcl(acl);
			// 调用create接口创建桶，并获得创建的桶对象
			ObsBucket rS3Bucket = getObsClient().createBucket(s3Bucket);
			System.out.println("Bucket name: " + rS3Bucket.getBucketName() + ", location: " + rS3Bucket.getLocation()
					+ ",metadata:" + rS3Bucket.getResponseHeaders());
		} catch (ObsException e) {
			e.printStackTrace();
		}
	}

	static void getBucketMetadata() {
		try {
			BucketMetadataInfoRequest request = new BucketMetadataInfoRequest();
			request.setBucketName("my-obs-bucket-demo3");
			request.setOrigin("http://www.a.com");
			// request.getRequestHeaders().add("header2");
			// request.getRequestHeaders().add("header1");
			BucketMetadataInfoResult result = getObsClient().getBucketMetadata(request);
			System.out.println("DefaultStorageClass:" + result.getDefaultStorageClass());
			System.out.println("AllowOrigin:" + result.getAllowOrigin());
			System.out.println("MaxAge:" + result.getMaxAge());
			System.out.println("AllowHeaders:" + result.getAllowHeaders());
			System.out.println("AllowMethods:" + result.getAllowMethods());
			System.out.println("ExposeHeaders:" + result.getExposeHeaders());

			// System.out.println(getObsClient().getBucketMetadata("bucket001"));
		} catch (ObsException e) {
			System.out.println("getBucketMetadata failed. Error message: " + e.getErrorMessage() + ". ResponseCode: "
					+ e.getResponseCode());
		}
	}

	static void upload(int index) {
		try {
			String key = "D:\\My Installers\\开发IDE\\pycharm-community-2017.1.exe";
			key = "d:\\temp\\test.rar";
			File f = new File(key);
			key = f.getAbsolutePath().replace("\\", "/");
			PutObjectRequest request = new PutObjectRequest();
			request.setBucketName("bucket444");
			ObjectMetadata metadata = new ObjectMetadata();
			// metadata.addUserMetadata("测试", "value");
			request.setMetadata(metadata);
			// request.setExpires(10);
			// SseCHeader sseCHeader = new SseCHeader();
			// sseCHeader.setAlgorithm(ServerAlgorithm.AES256);
			// sseCHeader.setSseCKeyBase64("1Cqvsw9k480AjPrWQ96LJ/+tFk52TdWo3FMQoQFP3Vc=");
			// request.setSseCHeader(sseCHeader);
			// request.setFile(f);
//			request.setInput(new FileInputStream(f));
			 request.setInput(new ByteArrayInputStream("Hello".getBytes()));
			request.setObjectKey("src/test2");
			request.setProgressListener(new ProgressListener() {

				@Override
				public void progressChanged(ProgressStatus status) {
					System.out.println(status.getAverageSpeed());
					System.out.println(status.getInstantaneousSpeed());
					System.out.println(status.getTransferPercentage());
				}
			});
			System.out.println(getObsClient().putObject(request));

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	static void download() {
		try {

			// S3Object result =
			// getObsClient().getObject("new-bucket-glacier", "test1", null);
			ObsClient client = getObsClient();
			String objectKey = "100MB.txt";
			GetObjectRequest request = new GetObjectRequest("test-sdk-jtw", objectKey);
			ObjectRepleaceMetadata m = new ObjectRepleaceMetadata();
			m.setContentType("image/jpeg");
			request.setReplaceMetadata(m);
			long start = System.currentTimeMillis();
			// request.setImageProcess("image/resize,m_fixed,w_100,h_100/rotate,90");
			request.setProgressListener(new ProgressListener() {

				@Override
				public void progressChanged(ProgressStatus status) {
					System.out.println(status.getAverageSpeed());
					System.out.println(status.getTransferPercentage());
				}
			});
			ObsObject result = client.getObject(request);
			System.out.println(result.getMetadata().getContentMd5());
			System.out.println(result.getMetadata().getEtag());
			
			Thread.sleep(35000);
			
			ReadableByteChannel rchannel = Channels.newChannel(result.getObjectContent());
			ByteBuffer buffer = ByteBuffer.allocate(1024);
			FileOutputStream fis = new FileOutputStream(new File("d:/temp/test1.rar"));

			FileChannel wchannel = fis.getChannel();
			if (rchannel.isOpen()) {
				while (rchannel.read(buffer) != -1) {
					buffer.flip();
					while (buffer.hasRemaining()) {
						wchannel.write(buffer);
					}
					buffer.clear();
				}
			}

			System.out.println("done");
			fis.close();
			rchannel.close();
			wchannel.close();
			client.close();

			

			System.out.println(System.currentTimeMillis() - start);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	static void enableVersion() {
		try {

			System.out.println(getObsClient().getBucketVersioning("bucket001").getStatus());
			getObsClient().setBucketVersioning("bucket001",
					new BucketVersioningConfiguration(BucketVersioningConfiguration.ENABLED));

		} catch (ObsException e) {
			e.printStackTrace();
		}
	}

	static void options() {
		try {
			OptionsInfoRequest optionInfo = new OptionsInfoRequest();
			optionInfo.setOrigin("http://www.a.com");
			List<String> requestMethod = new ArrayList<String>();
			requestMethod.add("PUT");
			optionInfo.setRequestMethod(requestMethod);

			List<String> requestHeaders = new ArrayList<String>();
			optionInfo.setRequestHeaders(requestHeaders);
			OptionsInfoResult result = getObsClient().optionsBucket("bucket001", optionInfo);
			System.out.println(result);

		} catch (ObsException e) {
			e.printStackTrace();
		}
	}

	static void deleteAll() {
		try {

			ListVersionsResult list = getObsClient().listVersions("bucket003", null, null, null, null, 100, null);
			for (VersionOrDeleteMarker marker : list.getVersions()) {
				System.out.println(marker.getStorageClass());
				// getObsClient().deleteObject(list.getBuketName(), marker.getKey(),
				// marker.getVersionId());
			}

			// getObsClient().deleteBucket("bucket001");
		} catch (ObsException e) {
			e.printStackTrace();
		}
	}

	static void restoreObject() {
		try {
			RestoreObjectRequest request = new RestoreObjectRequest();
			request.setBucketName("bucket-glacier");
			// request.setBucketName("new-bucket-standard");
			request.setDays(1);
			request.setTier("Expedited");
			request.setObjectKey("TODOLIST.txt");
			// 0000015B0D62AE4E180685a0edbe834a3b4212c7a224824a000955445346485a
			// request.setVersionId("0000015B0D62AE4E180685a0edbe834a3b4212c7a224824a000955445346485a");
			IObsClient client = getObsClient();
			RestoreObjectStatus status = client.restoreObject(request);
			System.out.println(status);

			status = client.restoreObject(request);
			System.out.println(status);
		} catch (ObsException e) {
			e.printStackTrace();
		}
	}

	static void setBucketLogging() {
		try {
			BucketLoggingConfiguration loggingConfiguration = new BucketLoggingConfiguration();
			loggingConfiguration.setLogfilePrefix("access&-log");
			loggingConfiguration.setTargetBucketName("bucket-for-test-001");
			loggingConfiguration.setAgency("test");
			System.out
					.println(getObsClient().setBucketLoggingConfiguration("bucket-for-test-001", loggingConfiguration));

		} catch (ObsException e) {
			e.printStackTrace();
		}
	}

	static void deleteObjects() {
		DeleteObjectsRequest deleteObjectsRequest = new DeleteObjectsRequest();
		try {
			deleteObjectsRequest.setBucketName("bucket005");
			deleteObjectsRequest.setQuiet(false);
//			KeyAndVersion kv1 = new KeyAndVersion("test.xml");
//			KeyAndVersion kv2 = new KeyAndVersion("test.xml");
//			KeyAndVersion kv3 = new KeyAndVersion("test.txt",
//					"0000015D4FC770F2d69c29a5c123021b558e53ebda72b4df040155445346485a");
//			KeyAndVersion kv4 = new KeyAndVersion("test");
//			KeyAndVersion[] keyAndVersions = new KeyAndVersion[] { kv1, kv2, kv3, kv4 };
			deleteObjectsRequest.setKeyAndVersions(new KeyAndVersion[] {});
			System.out.println(getObsClient().deleteObjects(deleteObjectsRequest));
		} catch (ObsException e) {
			e.printStackTrace();
		}
	}

	static void getObjectMetadata() throws ServiceException {
		try {
			GetObjectMetadataRequest r = new GetObjectMetadataRequest();
			r.setBucketName("backup-meta");
			r.setObjectKey("test");
			ObjectMetadata m = getObsClient().getObjectMetadata(r);
			System.out.println(m.getUserMetadata("测试"));
			System.out.println(m.isAppendable());
			System.out.println(m.getLastModified());
			System.out.println(m.getNextPosition());
		} catch (ObsException e) {
			e.printStackTrace();
		}
	}

	static void listObjects() {
		try {
			ListObjectsRequest listObjectsRequest = new ListObjectsRequest();

			listObjectsRequest.setBucketName("2-mc");
			listObjectsRequest.setPrefix("20w/");
			listObjectsRequest.setMaxKeys(1);
			ObjectListing list = getObsClient().listObjects(listObjectsRequest);
			for (ObsObject obj : list.getObjects()) {
				System.out.println(obj.getObjectKey());
				System.out.println(obj.getMetadata().isAppendable());
				// System.out.println(obj.getBucketName());
				// System.out.println(obj.getMetadata().getEtag());
				// System.out.println(obj.getMetadata().getLastModified());
				// System.out.println(obj.getMetadata().getContentLength());
				// System.out.println(obj.getOwner().getId());
			}
			System.out.println(list.getResponseHeaders());
			System.out.println(list.getLocation());
			// System.out.println(list.getBucketName());
			// System.out.println(list.getDelimiter());
			// System.out.println(list.getMarker());
			// System.out.println(list.getNextMarker());
			// System.out.println(list.getPrefix());
			// System.out.println(list.getCommonPrefixes());
			// System.out.println(list.getMaxKeys());
			// System.out.println(list.isTruncated());
		} catch (ObsException e) {
			e.printStackTrace();
		}
	}

	static void uploadMarti() throws FileNotFoundException, InterruptedException, ServiceException {
		final String bucketName = "bucket333";
		final String key = "试试.exe";
		try {
			// ListMultipartUploadsRequest req1 = new ListMultipartUploadsRequest();
			// req1.setBucketName(bucketName);
			// req1.setMaxUploads(100);
			// MultipartUploadListing list = getObsClient().listMultipartUploads(req1);
			// for (MultipartUpload u : list.getMultipartTaskList())
			// {
			// AbortMultipartUploadRequest r = new AbortMultipartUploadRequest();
			// r.setBucketName(u.getBucketName());
			// r.setObjectKey(u.getObjectKey());
			// r.setUploadId(u.getUploadId());
			// getObsClient().abortMultipartUpload(r);
			// }

			InitiateMultipartUploadRequest request = new InitiateMultipartUploadRequest();
			request.setBucketName(bucketName);
			request.setObjectKey(key);
			request.setAcl(AccessControlList.REST_CANNED_PUBLIC_READ);
			request.setWebSiteRedirectLocation("/" + key);
			ObjectMetadata metadata = new ObjectMetadata();
			metadata.setContentType("text/plain");
			metadata.getMetadata().put("test", "value");
			request.setMetadata(metadata);
			InitiateMultipartUploadResult result = getObsClient().initiateMultipartUpload(request);

			final String uploadId = result.getUploadId();

			// final File file = new File("D:/My Installers/bigdata/hadoop_action.zip");

			final File file = new File("C:/Users/x00403408/Desktop/TODOLIST.txt");

			System.out.println(uploadId);

			final List<PartEtag> partEtags = new ArrayList<PartEtag>();

			int index = 10000;
			UploadPartRequest req = new UploadPartRequest();
			req.setBucketName(bucketName);
			req.setObjectKey(key);
			req.setPartNumber(index);
			req.setUploadId(uploadId);
			// req.setFile(file);
			req.setInput(new FileInputStream(file));
			UploadPartResult ret = getObsClient().uploadPart(req);
			PartEtag e = new PartEtag();
			e.setPartNumber(index);
			e.seteTag(ret.getEtag());
			partEtags.add(e);

			CompleteMultipartUploadRequest r = new CompleteMultipartUploadRequest();
			r.setBucketName(bucketName);
			r.setObjectKey(key);
			r.setPartEtag(partEtags);
			r.setUploadId(uploadId);
			System.out.println(getObsClient().completeMultipartUpload(r));

		} catch (ObsException e) {
			e.printStackTrace();
		}
	}

	static void getBucketLocation() {
		try {
			System.out.println(getObsClient().getBucketLocation("bucket002"));
		} catch (ObsException e) {
			e.printStackTrace();
		}
	}

	static void getBucketStorageInfo() {
		try {
			BucketStorageInfo info = getObsClient().getBucketStorageInfo("bucket001");
			System.out.println(info);
		} catch (ObsException e) {
			e.printStackTrace();
		}
	}

	static void listMultiPartUpload() {
		try {
			ListMultipartUploadsRequest request = new ListMultipartUploadsRequest();
			request.setBucketName("bucket003");
			MultipartUploadListing ret = getObsClient().listMultipartUploads(request);
			List<MultipartUpload> l = ret.getMultipartTaskList();
			for (MultipartUpload u : l) {
				System.out.println(u.getStorageClass());
			}
		} catch (ObsException e) {
			e.printStackTrace();
			System.out.println(e);
		}
	}

	static void setBucketAcl() {
		try {
			AccessControlList acl = new AccessControlList();
			Owner owner = new Owner();
			owner.setId("domainiddomainiddomainiddo000100");
			owner.setDisplayName("domainnamedom000100");
			acl.setOwner(owner);

			CanonicalGrantee grantee = new CanonicalGrantee("domainiddomainiddomainiddo000100");
			grantee.setDisplayName("domainnamedom000100");
			Permission permission = Permission.PERMISSION_READ;
			acl.grantPermission(grantee, permission);
			acl.grantPermission(grantee, Permission.PERMISSION_WRITE);

			acl.grantPermission(GroupGrantee.LOG_DELIVERY, Permission.PERMISSION_READ_ACP);
			acl.grantPermission(GroupGrantee.LOG_DELIVERY, Permission.PERMISSION_WRITE);
			System.out.println(getObsClient().setBucketAcl("bucket004", null, acl));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	static void setObjectAcl() {
		try {
			AccessControlList acl = new AccessControlList();
			Owner owner = new Owner();
			owner.setId("domainiddomainiddomainiddo000100");
			acl.setOwner(owner);

			GranteeInterface grantee = new CanonicalGrantee("xxx");
			Permission permission = Permission.PERMISSION_READ;

			acl.grantPermission(grantee, permission);

			getObsClient().setObjectAcl("bucket001", "error.html", null, AccessControlList.REST_CANNED_PUBLIC_READ,
					null);
		} catch (ObsException e) {
			e.printStackTrace();
		}
	}

	static void getObjectAcl() {
		try {
			System.out.println(getObsClient().getObjectAcl("bucket001", "test.txt", null));
		} catch (ObsException e) {
			e.printStackTrace();
		}
	}

	static void listBuckets() throws ObsException, IOException {
		try {
			ListBucketsRequest request = new ListBucketsRequest();
			request.setQueryLocation(true);
			ListBucketsResult r = getObsClient().listBucketsV2(request);
			for (ObsBucket s : r.getBuckets()) {
				System.out.println(s.getBucketName());
				System.out.println(s.getLocation());
			}
			System.out.println(r.getStatusCode());
			System.out.println(r.getResponseHeaders());

		} catch (ObsException e) {
			e.printStackTrace();
			System.out.println(e.getResponseCode());
		}
		getObsClient().close();
	}

	static void getBucketTagging() {
		try {

			BucketTagInfo result = getObsClient().getBucketTagging("bucket001");
			System.out.println(result);
		} catch (ObsException e) {
			if (e.getResponseCode() == 403) {
				index++;
			}
		}
	}

	static void deleteBucketTagging() {
		try {
			getObsClient().deleteBucketTagging("bucket001");
		} catch (ObsException e) {
			index++;
			e.printStackTrace();
		}
	}

	static void setBucketTagging() {
		try {
			BucketTagInfo request = new BucketTagInfo();
			TagSet tagSet = new TagSet();
			request.setTagSet(tagSet);

			tagSet.addTag("测试1", "值1");
			tagSet.addTag("testKey&002", "testValue001");

			getObsClient().setBucketTagging("bucket-for-test-001", request);
		} catch (ObsException e) {
			if (e.getResponseCode() == 403) {
				index++;
			}
			e.printStackTrace();
		}
	}

	static void getBucketAcl() {
		try {
			AccessControlList acl = getObsClient().getBucketAcl("bucket002");
			System.out.println(acl.getOwner());
			for (GrantAndPermission permission : acl.getGrantAndPermissions()) {
				System.out.println(permission.getGrantee());
				System.out.println(permission.getPermission());
			}

		} catch (ObsException e) {
			e.printStackTrace();
		}
	}

	static void createSignedUrl() {
		try {
			Map<String, String> headers = new HashMap<String, String>();
			// headers.put("Content-Type", "text/plain");
			Map<String, Object> queryParams = new HashMap<String, Object>();
			// queryParams.put("versionId",
			// "0000015D786F7DBEd69c29a5c123021b558e53ebda72b4df000A55445346485a");
			TemporarySignatureRequest request = new TemporarySignatureRequest(HttpMethodEnum.GET, 3600);
			request.setBucketName("bucket333");
			request.setObjectKey("key0");
			request.setQueryParams(queryParams);
			TemporarySignatureResponse resp = getObsClient().createTemporarySignature(request);
			System.out.println(resp.getSignedUrl());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	static void deleteObject() {
		try {
			getObsClient().deleteObject("bucket001", "src/", null);

		} catch (ObsException e) {
			e.printStackTrace();
		}
	}

	static void headBucket() {
		try {
			System.out.println(getObsClient().headBucket("001test"));

		} catch (ObsException e) {
			e.printStackTrace();
		}
	}

	static void deleteBucket() {
		try {
			getObsClient().deleteBucket("bucket003");

		} catch (ObsException e) {
			e.printStackTrace();
		}
	}

	static void listVersions() {
		try {
			ListVersionsResult result = getObsClient().listVersions("testbucket001");

			for (VersionOrDeleteMarker obj : result.getVersions()) {
				System.out.println(obj.getKey());
				System.out.println(obj.getVersionId());
				System.out.println(obj.getStorageClass());
				System.out.println(obj.getSize());
				System.out.println(obj.getEtag());
				System.out.println(obj.getLastModified());
				System.out.println(obj.getOwner().getId());
				System.out.println(obj.isAppendable());
			}
			System.out.println(result.getKeyMarker());
			System.out.println(result.getBucketName());
			System.out.println(result.getVersionIdMarker());
			System.out.println(result.getPrefix());
			System.out.println(result.getMaxKeys());
			System.out.println(result.isTruncated());
			System.out.println(result.getNextKeyMarker());
			System.out.println(result.getNextVersionIdMarker());
			System.out.println(result.getLocation());
		} catch (ObsException e) {
			e.printStackTrace();
		}
	}

	static void getBucketQuota() {
		try {
			BucketQuota info = getObsClient().getBucketQuota("bucket-standard");
			System.out.println(info);
		} catch (ObsException e) {
			e.printStackTrace();
		}
	}

	static void setBucketQuota() {
		try {
			BucketQuota req = new BucketQuota();
			req.setBucketQuota(0);
			getObsClient().setBucketQuota("bucket001", req);
		} catch (ObsException e) {
			e.printStackTrace();
		}
	}

	static void deleteBucketCors() {
		try {
			getObsClient().deleteBucketCors("bucket-to-get-metadata");
		} catch (ObsException e) {
			e.printStackTrace();
		}
	}

	static void getBucketCors() {
		try {
			System.out.println(getObsClient().getBucketCors("bucket001"));
		} catch (ObsException e) {
			e.printStackTrace();
		}
	}

	static void getBucketLoggingConfiguration() {
		try {
			System.out.println(getObsClient().getBucketLoggingConfiguration("bucket001"));
		} catch (ObsException e) {
			e.printStackTrace();
		}
	}

	static void setBucketLoggingConfiguration() {
		BucketLoggingConfiguration loggingConfiguration = new BucketLoggingConfiguration();
		try {
			loggingConfiguration.setTargetBucketName("bucket11111");
			// loggingConfiguration.setLogfilePrefix("logprefix");
			// GrantAndPermission targetGrant = new GrantAndPermission(new
			// CanonicalGrantee("test1"), Permission.PERMISSION_FULL_CONTROL);
			// loggingConfiguration.addTargetGrant(targetGrant);
			getObsClient().setBucketLoggingConfiguration("bucket001", loggingConfiguration, true);
		} catch (ObsException e) {
			e.printStackTrace();
		}
	}

	static void setBucketCors() throws ObsException {
		BucketCors s3BucketCors = new BucketCors();
		BucketCorsRule rule = new BucketCorsRule();
		rule.getAllowedHeader().add("Authorization");
		rule.getAllowedOrigin().add("http://www.a.com");
		rule.getAllowedOrigin().add("http://www.b.com");
		rule.getExposeHeader().add("x-obs-test1");
		rule.getExposeHeader().add("x-obs-test2");
		rule.setMaxAgeSecond(100);
		rule.getAllowedMethod().add("HEAD");
		rule.getAllowedMethod().add("GET");
		rule.getAllowedMethod().add("PUT");
		s3BucketCors.getRules().add(rule);

		System.out.println("Setting bucket CORS\n");
		System.out.println(getObsClient().setBucketCors("bucket001", s3BucketCors));
	}

	static void getBucketVersioning() {
		try {
			System.out.println(getObsClient().getBucketVersioning("bucket001"));
		} catch (ObsException e) {
			e.printStackTrace();
		}
	}

	static void setBucketVersioning() {
		try {
			BucketVersioningConfiguration configuration = new BucketVersioningConfiguration(
					BucketVersioningConfiguration.ENABLED);
			getObsClient().setBucketVersioning("bucket-standard-versional", BucketVersioningConfiguration.ENABLED);
		} catch (ObsException e) {
			e.printStackTrace();
		}
	}

	static void getBucketLifecycleConfiguration() {
		try {
			System.out.println(getObsClient().getBucketLifecycleConfiguration("bucket001"));
		} catch (ObsException e) {
			e.printStackTrace();
		}
	}

	static void deleteBucketLifecycleConfiguration() {
		try {
			getObsClient().deleteBucketLifecycleConfiguration("bucket-standard-versional");
		} catch (ObsException e) {
			e.printStackTrace();
		}
	}

	static void setBucketLifecycleConfiguration() {
		try {
			final String ruleId0 = "delete obsoleted files";
			final String matchPrefix0 = "obsoleted/";
			final String ruleId1 = "delete temporary files";
			final String matchPrefix1 = "temporary/";

			LifecycleConfiguration lifecycleConfig = new LifecycleConfiguration();
			LifecycleConfiguration.Rule rule0 = lifecycleConfig.new Rule();
			rule0.setEnabled(true);
			rule0.setId(ruleId0);
			rule0.setPrefix("");
			LifecycleConfiguration.Expiration expiration0 = lifecycleConfig.new Expiration();
			expiration0.setDays(50);
			// rule0.setExpiration(expiration0);

			LifecycleConfiguration.Transition transition0 = lifecycleConfig.new Transition();
			transition0.setDays(40);
			transition0.setStorageClass("STANDARD_IA");
			rule0.getTransitions().add(transition0);

			lifecycleConfig.addRule(rule0);

			LifecycleConfiguration.Rule rule1 = lifecycleConfig.new Rule();
			rule1.setEnabled(true);
			rule1.setId(ruleId1);
			rule1.setPrefix(matchPrefix1);
			LifecycleConfiguration.Expiration expiration1 = lifecycleConfig.new Expiration();
			expiration1.setDate(new Date());
			rule1.setExpiration(expiration1);

			// rule0.newNoncurrentVersionExpiration().setDays(50);

			rule1.newNoncurrentVersionExpiration().setDays(50);

			LifecycleConfiguration.NoncurrentVersionTransition transition1 = lifecycleConfig.new NoncurrentVersionTransition();
			transition1.setDays(40);
			transition1.setStorageClass("STANDARD_IA");
			rule1.getNoncurrentVersionTransitions().add(transition1);

			// rule0.setNoncurrentVersionTransition(transition1);

			// lifecycleConfig.addRule(rule1);

			System.out.println("Setting bucket lifecycle\n");
			getObsClient().setBucketLifecycleConfiguration("bucket-test", lifecycleConfig);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	static void getBucketPolicy() {
		try {
			System.out.println(getObsClient().getBucketPolicy("bucket-standard-versional"));
		} catch (ObsException e) {
			e.printStackTrace();
		}
	}

	static void getBucketWebsiteConfiguration() {
		try {
			System.out.println(getObsClient().getBucketWebsiteConfiguration("bucket001"));
		} catch (ObsException e) {
			e.printStackTrace();
		}
	}

	static void deleteBucketWebsiteConfiguration() {
		try {
			getObsClient().deleteBucketWebsiteConfiguration("bucket-standard-versional");
		} catch (ObsException e) {
			e.printStackTrace();
		}
	}

	static void setBucketWebsiteConfiguration() {
		try {
			WebsiteConfiguration config = new WebsiteConfiguration();
			config.setKey("error.html");
			config.setSuffix("index.html");
			// RedirectAllRequest request = new RedirectAllRequest();
			// request.setHostName("www.baidu.com");
			// request.setProtocol("http");
			// config.setRedirectAllRequestsTo(request);

			RouteRule rule = new RouteRule();
			Redirect r = new Redirect();
			r.setHostName("www.example.com");
			r.setHttpRedirectCode("305");
			r.setReplaceKeyPrefixWith("replacekeyprefix");
			rule.setRedirect(r);
			RouteRuleCondition condition = new RouteRuleCondition();
			condition.setHttpErrorCodeReturnedEquals("404");
			condition.setKeyPrefixEquals("keyprefix");
			rule.setCondition(condition);
			config.getRouteRules().add(rule);

			getObsClient().setBucketWebsiteConfiguration("bucket001", config);
		} catch (ObsException e) {
			e.printStackTrace();
		}
	}

	static void setBucketPolicy() {
		try {
			getObsClient().setBucketPolicy("bucket-standard-versional", "test");
		} catch (ObsException e) {
			e.printStackTrace();
		}
	}

	static void listMultiUploads() throws ObsException {
		ListMultipartUploadsRequest request = new ListMultipartUploadsRequest();
		request.setBucketName("my-obs-bucket-demo");
		MultipartUploadListing result = getObsClient().listMultipartUploads(request);
		for (MultipartUpload upload : result.getMultipartTaskList()) {
			getObsClient().abortMultipartUpload(new AbortMultipartUploadRequest(upload.getBucketName(),
					upload.getObjectKey(), upload.getUploadId()));
		}
	}

	static void copyPart() throws ObsException {
		String bucketName = "bucket-glacier";
		String key = "test";
		InitiateMultipartUploadRequest request = new InitiateMultipartUploadRequest();
		request.setBucketName(bucketName);
		request.setObjectKey(key);
		InitiateMultipartUploadResult result = getObsClient().initiateMultipartUpload(request);

		CopyPartRequest copyRequest = new CopyPartRequest();
		int index = 1;
		copyRequest.setSourceBucketName("bucket-test");
		copyRequest.setSourceObjectKey("2.txt");
		copyRequest.setPartNumber(index);
		copyRequest.setDestinationBucketName(bucketName);
		copyRequest.setDestinationObjectKey(key);
		copyRequest.setUploadId(result.getUploadId());
		copyRequest.setByteRangeStart(5l);
		copyRequest.setByteRangeEnd(20l);

		CopyPartResult copyResult = getObsClient().copyPart(copyRequest);

		final List<PartEtag> partEtags = new ArrayList<PartEtag>();

		PartEtag e = new PartEtag();
		e.setPartNumber(index);
		e.seteTag(copyResult.getEtag());
		partEtags.clear();
		partEtags.add(e);
		CompleteMultipartUploadRequest r = new CompleteMultipartUploadRequest();
		r.setBucketName(bucketName);
		r.setObjectKey(key);
		r.setPartEtag(partEtags);
		r.setUploadId(result.getUploadId());
		getObsClient().completeMultipartUpload(r);
	}

	private static void copyObject() {
		CopyObjectRequest request = new CopyObjectRequest("bucketname", "test", "bucketname", "test2");
		ObjectMetadata objectMeatdata = new ObjectMetadata();
		objectMeatdata.setContentType("text/plain");
		objectMeatdata.getMetadata().put("x-obs-meta-md5chksum", "value");
		request.setNewObjectMetadata(objectMeatdata);
		request.setReplaceMetadata(true);
		try {
			System.out.println(getObsClient().copyObject(request));
		} catch (ObsException e) {
			e.printStackTrace();
		}
	}

	private static void initAndAbort() {
		InitiateMultipartUploadRequest r = new InitiateMultipartUploadRequest();
		r.setBucketName("bucket-test");
		ObjectMetadata metadata = new ObjectMetadata();
		metadata.addUserMetadata("meta", "value");
		r.setMetadata(metadata);
		r.setObjectKey("test");
		r.setExpires(10);

		try {
			InitiateMultipartUploadResult ret = getObsClient().initiateMultipartUpload(r);
			System.out.println(ret);
			AbortMultipartUploadRequest request = new AbortMultipartUploadRequest();
			request.setBucketName("bucket-test");
			request.setObjectKey("test");
			request.setUploadId(ret.getUploadId());
			getObsClient().abortMultipartUpload(request);
		} catch (ObsException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	static void setBucketNotificationConfiguration() {
		try {
			TopicConfiguration c = new TopicConfiguration();
			c.setId("001");
			c.setTopic("urn:smn:region3:35667523534:topic1");
			TopicConfiguration.Filter f = new TopicConfiguration.Filter();
			c.setFilter(f);
			f.getFilterRules().add(new TopicConfiguration.Filter.FilterRule("prefix", "smn"));
			f.getFilterRules().add(new TopicConfiguration.Filter.FilterRule("suffix", ".jpg"));
			System.out.println(getObsClient().setBucketNotification("bucket001",
					new BucketNotificationConfiguration().addTopicConfiguration(c)));
		} catch (ObsException e) {
			e.printStackTrace();
		}
	}

	static void getBucketNotificationConfiguration() {
		try {
			System.out.println(getObsClient().getBucketNotification("bucket001"));
		} catch (ObsException e) {
			e.printStackTrace();
		}
	}

	static void deleteBucketPolicy(String bucketName) {
		try {
			getObsClient().deleteBucketPolicy(bucketName);
		} catch (ObsException e) {
			if (e.getResponseCode() == 403) {
				index++;
			}
			e.printStackTrace();
		}
	}

	static volatile int index = 0;

	static void listObjectsByPrefix(IObsClient obsClient, ListObjectsRequest request, ObjectListing result)
			throws ObsException {
		for (String prefix : result.getCommonPrefixes()) {
			System.out.println("文件夹 [" + prefix + "]中的文件:");
			request.setPrefix(prefix);
			result = obsClient.listObjects(request);
			for (ObsObject s3Object : result.getObjects()) {
				System.out.println("\t" + s3Object.getObjectKey());
				System.out.println("\t" + s3Object.getOwner());
			}
			listObjectsByPrefix(obsClient, request, result);
		}
	}

	static void appendObject() {
		AppendObjectRequest request = new AppendObjectRequest();
		request.setBucketName("obstrans");
		request.setObjectKey("test.zip");
		ObjectMetadata metadata = new ObjectMetadata();
		request.setMetadata(metadata);
//		request.setFile(new File("C:\\Users\\x00403408\\Desktop\\test.txt"));
		request.setInput(new ByteArrayInputStream("Hello OBS".getBytes()));
		AppendObjectResult result = getObsClient().appendObject(request);

		System.out.println(result);
		System.out.println(result.getNextPosition());
		
		request.setInput(new ByteArrayInputStream("Hello OBS".getBytes()));
		request.setPosition(result.getNextPosition());
		result = getObsClient().appendObject(request);

		System.out.println(result);
		System.out.println(result.getNextPosition());
		
		request.setInput(new ByteArrayInputStream("Hello OBS2".getBytes()));
		request.setPosition(result.getNextPosition());
		result = getObsClient().appendObject(request);

		System.out.println(result);
		System.out.println(result.getNextPosition());
	}

	static InputStream sanitizeXmlDocument(InputStream inputStream) throws ServiceException {
		if (inputStream == null) {
			return null;
		}
		BufferedReader br = null;
		try {
			StringBuilder listingDocBuffer = new StringBuilder();
			br = new BufferedReader(new InputStreamReader(inputStream, Constants.DEFAULT_ENCODING));

			char[] buf = new char[8192];
			int read = -1;
			while ((read = br.read(buf)) != -1) {
				listingDocBuffer.append(buf, 0, read);
			}
			// Replace any carriage return (\r) characters with explicit XML
			// character entities, to prevent the SAX parser from
			// misinterpreting 0x0D characters as 0x0A.
			String listingDoc = listingDocBuffer.toString().replaceAll("\r", "&#013;");
			return new ByteArrayInputStream(listingDoc.getBytes(Constants.DEFAULT_ENCODING));
		} catch (Throwable t) {
			throw new ServiceException("Failed to sanitize XML document destined", t);
		} finally {
			ServiceUtils.closeStream(br);
			ServiceUtils.closeStream(inputStream);
		}
	}

	public static void main(String[] args) throws Exception {
//		UploadFileRequest request = new UploadFileRequest("test-sdk-jtw", "test4.zip");
//		request.setEnableCheckpoint(true);
//		request.setPartSize(5 * 1024 * 1024L);
//		request.setTaskNum(10);
//		request.setProgressInterval(1024 * 1024);
//		request.setUploadFile("D:\\My Installers\\数据库\\winx64_12102_database_2of2.zip");
////		request.setUploadFile("D:\\My Installers\\数据库\\mysql-5.7.17-linux-glibc2.5-x86_64.tar.gz");
//		request.setProgressListener(new ProgressListener() {
//
//			@Override
//			public void progressChanged(ProgressStatus status) {
//				System.out.println(status.getTransferPercentage());
//			}
//		});
//		getObsClient().uploadFile(request);

//		DownloadFileRequest request = new DownloadFileRequest("backup-meta", "test.txt");
//		request.setEnableCheckpoint(true);
//		request.setPartSize(10 * 1024 * 1024L);
//		request.setProgressInterval(1024 * 1024);
//		request.setTaskNum(3);
//		request.setDownloadFile("D:\\temp\\test1.rar");
//		request.setProgressListener(new ProgressListener() {
//
//			@Override
//			public void progressChanged(ProgressStatus status) {
//				System.out.println(status.getTransferPercentage());
//			}
//		});
//		System.out.println(getObsClient().downloadFile(request));
		
		// createBucket("bucket002");
		// getObjectMetadata();
//		 appendObject();
//		 listObjects();
		// setBucketNotificationConfiguration();
		// getBucketNotificationConfiguration();
		// setBucketWebsiteConfiguration();
		// deleteBucketWebsiteConfiguration();
		// getBucketWebsiteConfiguration();
		// setBucketPolicy();
		// getBucketPolicy();
		// deleteBucketLifecycleConfiguration();
		// setBucketLifecycleConfiguration();
		// getBucketLifecycleConfiguration();
		// setBucketVersioning();
		// getBucketVersioning();
		// setBucketLoggingConfiguration();
		// setBucketLogging();
		// getBucketLoggingConfiguration();
		// getBucketLocation();
		// deleteBucketCors();
		// setBucketCors();
		// getBucketCors();
		// setBucketQuota();
		// getBucketQuota();
		// getBucketStorageInfo();
		// setBucketAcl();
		// getBucketAcl();
		// listVersions();
//		 headBucket();
		// deleteBucket();
		// deleteObject();
		// createSignedUrl();
		// setBucketTagging();
		// deleteBucketTagging();
//		 listBuckets();

		// setObjectAcl();
		// getObjectAcl();
		// listMultiPartUpload();

		// getPolicy();
		// getBucketMetadata();
//		 deleteObjects();
		// restoreObject();
		
		
		 upload(1);
//		 download();
		// enableVersion();
		// deleteAll();
		// options();
		// setBucketLogging();
		// copyPart();
		// initAndAbort();
		// uploadMarti();
		// copyObject();
//		 getObjectMetadata();
		// listMultiUploads();
		// setBucketTagging();
		// getBucketTagging();
		// deleteBucketTagging();
	}

}
