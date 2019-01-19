package com.obs.services.sample;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.obs.services.IObsClient;
import com.obs.services.ObsClient;
import com.obs.services.ObsConfiguration;
import com.obs.services.exception.ObsException;
import com.obs.services.model.AccessControlList;
import com.obs.services.model.AuthTypeEnum;
import com.obs.services.model.CanonicalGrantee;
import com.obs.services.model.GrantAndPermission;
import com.obs.services.model.GroupGrantee;
import com.obs.services.model.GroupGranteeEnum;
import com.obs.services.model.ListObjectsRequest;
import com.obs.services.model.ObjectListing;
import com.obs.services.model.ObsObject;
import com.obs.services.model.Owner;
import com.obs.services.model.Permission;
import com.obs.services.model.TemporarySignatureResponse;

import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class UploadFolder {

	static volatile IObsClient client;

	static HashMap<String, File> map = new HashMap<String, File>();

	static IObsClient getObsClient() throws ObsException {
		if (client != null) {
			return client;
		}
		String endPoint = "http://obs.cn-north-1.myhwclouds.com"; // 存储服务器地址
		String ak = "VXYNVG7AC2UTWTWF9PEZ"; // 接入证书
		String sk = "UBcFmneLsLPdBQcFCyrKpywSFTebL4nSeabMtNUt"; // 安全证书

//		endPoint = "http://10.175.38.120";
//		ak = "XVSM6UAMWTPEC8G6IYBY";
//		sk = "NxiNK1IfQVtZ4dSPNMw9QdU6EYLmXJJgvz8pB1Gq";
		// endPoint = "http://100.114.236.5";
		// ak = "UDSIAMSTUBTEST000333";
		// sk = "Udsiamstubtest000000UDSIAMSTUBTEST000333";

		ObsConfiguration config = new ObsConfiguration();
		config.setEndPoint(endPoint);
		config.setAuthTypeNegotiation(false);
		config.setAuthType(AuthTypeEnum.V2);
		// 实例化ObsClient服务
		client = new ObsClient(ak, sk, config);
		return client;
	}

	public static void uploadFolder(File folder, String prefix, String bucketName) {
		for (File file : folder.listFiles()) {
			if (file.isDirectory()) {
				String subPrefix = prefix + "/" + file.getName();
				try {
					client.putObject(bucketName, subPrefix + "/", (File) null);
				} catch (Exception e) {
					map.put(subPrefix + "/", null);
				}
				uploadFolder(file, subPrefix, bucketName);
			} else {
				try {
					client.putObject(bucketName, prefix + "/" + file.getName(), file);
				} catch (Exception e) {
					map.put(prefix + "/" + file.getName(), file);
				}
			}
		}
	}

	public static void uploadFolderFiles() {
		getObsClient();
		String folder = "C:\\Users\\x00403408\\Desktop\\dotnet";
		String destPrefix = "apidoc/cn/";
		String bucketName = "obssdk";

		File f = new File(folder);
		destPrefix += f.getName();

		uploadFolder(f, destPrefix, bucketName);

		for (Map.Entry<String, File> entry : map.entrySet()) {
			try {
				client.putObject(bucketName, entry.getKey(), entry.getValue());
			} catch (Exception e) {
				System.out.println("Failed to upload:" + entry.getKey() + ", from:" + entry.getValue());
			}
		}

	}
	
	public static void setBucketAcl() {
		getObsClient();
		String bucketName = "obssdk";
		AccessControlList acl = new AccessControlList();
		Owner owner = new Owner();
		owner.setId("bf569d72ab9b41db9fe0723c32c8b90e");
		acl.setOwner(owner);
		acl.grantPermission(new CanonicalGrantee("bf569d72ab9b41db9fe0723c32c8b90e"), Permission.PERMISSION_FULL_CONTROL);
		GrantAndPermission gap = acl.grantPermission(new GroupGrantee(GroupGranteeEnum.ALL_USERS), Permission.PERMISSION_READ);
		gap.setDelivered(true);
		client.setBucketAcl(bucketName, acl);

	}

	public static void setObjectsAcl() {
		getObsClient();
		String bucketName = "obssdk";
		ObjectListing list;
		ListObjectsRequest request = new ListObjectsRequest(bucketName);
		request.setPrefix("apidoc/cn/android");
		do {
			list = client.listObjects(request);

			for (ObsObject obj : list.getObjects()) {
				System.out.println(obj.getObjectKey());
				client.setObjectAcl(bucketName, obj.getObjectKey(), AccessControlList.REST_CANNED_PUBLIC_READ);
			}

			request.setMarker(list.getNextMarker());
		} while (list.isTruncated());

	}

	private static OkHttpClient httpClient = new OkHttpClient.Builder().followRedirects(false)
			.retryOnConnectionFailure(false).cache(null).build();

	private static void getResponse(Request request) throws IOException {
		Call c = httpClient.newCall(request);
		Response res = c.execute();
		System.out.println("\tStatus:" + res.code());
		if (res.body() != null) {
			String content = res.body().string();
			if (content == null || content.trim().equals("")) {
				System.out.println("\n");
			} else {
				System.out.println("\tContent:" + content + "\n\n");
			}
		} else {
			System.out.println("\n");
		}
		res.close();
	}

	private static Request.Builder getBuilder(TemporarySignatureResponse res) {
		Request.Builder builder = new Request.Builder();
		for (Map.Entry<String, String> entry : res.getActualSignedRequestHeaders().entrySet()) {
			builder.header(entry.getKey(), entry.getValue());
		}
		return builder.url(res.getSignedUrl());
	}

	public static void main(String[] args) throws Exception {
//		setBucketAcl();
		 setObjectsAcl();
//		 uploadFolderFiles();
	}

}
