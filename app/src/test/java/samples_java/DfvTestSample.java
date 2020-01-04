package samples_java;

import com.obs.services.ObsClient;
import com.obs.services.ObsConfiguration;
import com.obs.services.exception.ObsException;
import com.obs.services.model.*;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

public class DfvTestSample {
  private static final String endPoint = "obs.cn-north-4.myhuaweicloud.com";
  
  private static final String ak = "Q6XCRIRL63PAL8WNDASU";
  
  private static final String sk = "ivLAmuYb4EZSymFHpYYuYF384wAOAqmWvUodlCgC";
  
  private static ObsClient obsClient;
  
  private static String bucketName = "xie-cn4";
  
  private static String objectKey = "clusterData/10/user/mapred/node-labels/copyTest";
  
  public static void main(String[] args) throws UnsupportedEncodingException
  {
      ObsConfiguration config = new ObsConfiguration();
      config.setSocketTimeout(30000);
      config.setConnectionTimeout(10000);
      config.setEndPoint(endPoint);
      config.setHttpProxy("proxy.huawei.com", 8080, "y00467639", "@yang3231138");
      config.setHttpsOnly(false);
      config.setAuthTypeNegotiation(false);
      config.setAuthType(AuthTypeEnum.V2);
      config.setHttpProtocolType(HttpProtocolTypeEnum.HTTP2_0);
//      config.setAuthType(AuthTypeEnum.OBS);
//      config.setAuthTypeNegotiation(false);
      try
      {
          /*
           * Constructs a obs client instance with your account for accessing OBS
           */
          obsClient = new ObsClient(ak, sk, config);

//          ObjectListing result = obsClient.listObjects(bucketName);
//          for(ObsObject obsObject : result.getObjects()){
//              System.out.println("\t" + obsObject.getObjectKey());
//              System.out.println("\t" + obsObject.getOwner());
//          }

          //obsClient.putObject(bucketName, objectKey + "copyTest", new ByteArrayInputStream("Copy Test".getBytes()));

          CopyObjectResult result = obsClient.copyObject(bucketName, objectKey,
                  bucketName, objectKey + "_copy");
          System.out.println("\t" + result.getEtag());

//          System.out.println("start list buckets");
//          obsClient.listBuckets();
//          System.out.println("end list buckets");
          //SetPolicy();
          
//          RestoreObjectRequest request = new RestoreObjectRequest(bucketName, objectKey,
//  				null, 1);
  		
//  		if (content.get("GlacierJobParameters") != null) {
//  			Map<String, String> m = (Map<String, String>) content.get("GlacierJobParameters");
//  			request.setRestoreTier(RestoreTierEnum.getValueFromCode(m.get("Tier")));
//  		}

//        request.setRestoreTier(RestoreTierEnum.STANDARD);
//  		obsClient.restoreObject(request);
          
//          obsClient.setBucketPolicy(bucketName, "{\r\n" + 
//          		"    \"Statement\": [\r\n" + 
//          		"        {\r\n" + 
//          		"            \"Sid\": \"Stmt1375240018062\",\r\n" + 
//          		"            \"Action\": [\r\n" + 
//          		"                \"*\"\r\n" + 
//          		"            ],\r\n" + 
//          		"            \"Effect\": \"Allow\",\r\n" + 
//          		"            \"Resource\": \"javatestbucket001\",\r\n" + 
//          		"            \"Principal\": {\r\n" + 
//          		"                \"ID\": [\r\n" + 
//          		"                    \"domain/9390dde28ae544898255f1c01aa12985\"\r\n" + 
//          		"                ]\r\n" + 
//          		"            }\r\n" + 
//          		"        }\r\n" + 
//          		"    ]\r\n" + 
//          		"}");
//          String bucket_name = "javatestbucket001";
//          String strPolicy = "{\"Statement\":[{\"Action\":[\"*\"],\"Principal\":{\"ID\":[\"domain/9390dde28ae544898255f1c01aa12985\"]},\"Resource\":\"javatestbucket001/*\",\"Effect\":\"Allow\",\"Sid\":\"Sid1\"}]}";
//          System.out.println("\t" + bucket_name);
//          System.out.println("\t" + strPolicy);
//          
//          
//          obsClient.setBucketPolicy(bucket_name, strPolicy);
//          
//          String policy = obsClient.getBucketPolicy(bucketName);
//          System.out.println("\t" + policy);
          

//			 CopyObjectRequest request = new CopyObjectRequest();
//			 request.setDestinationBucketName("javatestbucket001");
//			 request.setDestinationObjectKey("Acopy");
//			 request.setSourceBucketName("javatestbucket001");
//			 request.setSourceObjectKey("A");
//			 request.setReplaceMetadata("REPLACE".equals("MOVE"));
//			 obsClient.copyObject(request);

			/*
			 * ListObjectsRequest request = new ListObjectsRequest();
			 * request.setBucketName(bucketName); request.setMarker("A");
			 * request.setMaxKeys(-1); ObjectListing result =
			 * obsClient.listObjects(request); for(ObsObject obsObject :
			 * result.getObjects()){ System.out.println("\t" + obsObject.getObjectKey());
			 * System.out.println("\t" + obsObject.getOwner()); }
			 */
          
//          ListBucketsRequest request = new ListBucketsRequest();
//          
//          request.setQueryLocation(true);
//          List<ObsBucket> buckets = obsClient.listBuckets(request);
//          for(ObsBucket bucket : buckets){
//          System.out.println("BucketName:" + bucket.getBucketName());
//          System.out.println("CreationDate:" + bucket.getCreationDate());
//          System.out.println("Location:" + bucket.getLocation());
//          }
//          
//  		//request.setQueryLocation("true".equalsIgnoreCase(String.valueOf(headers.get("location"))));
//  		ListBucketsResult response = obsClient.listBucketsV2(request);
//  		
//  		for(ObsBucket bucket : response.getBuckets()){
//  			System.out.println("BucketName:" + bucket.getBucketName());
//  			System.out.println("CreationDate:" + bucket.getCreationDate());
//  			System.out.println("Location:" + bucket.getLocation());
//  			}
          
          /*
           * Get bucket location operation
           */
//          getBucketLocation();
          
          /*
           * Get bucket storageInfo operation 
           */
//          getBucketStorageInfo();
         
          
//          ObjectListing objectListing = obsClient.listObjects(bucketName);
//          for (ObsObject object : objectListing.getObjects())
//          {
//              System.out.println("\t" + object.getObjectKey() + " etag[" + object.getMetadata().getEtag() + "]");
//          }
//          System.out.println();
          
//          doObjectAclOperations();
          
          /*
           * Put/Get bucket quota operations
           */
//          doBucketQuotaOperation();
//          
//          /*
//           * Put/Get bucket versioning operations
//           */
//          doBucketVersioningOperation();
//          
//          /*
//           * Put/Get bucket acl operations
//           */
//          doBucketAclOperation();
//          
//          /*
//           * Put/Get/Delete bucket cors operations
//           */
//          doBucketCorsOperation();
//          
//          /*
//           * Options bucket operation
//           */
//          optionsBucket();
//          
//          /*
//           * Get bucket metadata operation
//           */
//          getBucketMetadata();

          
      }
      catch (ObsException e)
      {
          System.out.println("Response Code: " + e.getResponseCode());
          System.out.println("Error Message: " + e.getErrorMessage());
          System.out.println("Error Code:       " + e.getErrorCode());
          System.out.println("Request ID:      " + e.getErrorRequestId());
          System.out.println("Host ID:           " + e.getErrorHostId());
      }
      finally
      {
          if (obsClient != null)
          {
              try
              {
                  /*
                   * Close obs client 
                   */
                  obsClient.close();
              }
              catch (IOException e)
              {
              }
          }
      }
  }
  
  private static void SetPolicy() {
	  AccessControlList acl = new AccessControlList();
	  
	  Owner owner = new Owner();
  	  owner.setId("domainiddomainiddomainiddo006000");
  	  owner.setDisplayName("domainnamedom006000");
  	  acl.setOwner(owner);
  	  
  	  acl.setDelivered(false);
  	  
  	  CanonicalGrantee tee = new CanonicalGrantee("domainiddomainiddomainiddo006000");
  	  tee.setDisplayName("domainnamedom006000");
  	  acl.grantPermission(tee, Permission.PERMISSION_FULL_CONTROL);
  	  
  	  GroupGrantee tee2 = new GroupGrantee("http://acs.amazonaws.com/groups/s3/LogDelivery");
//  	GroupGrantee tee2 = new GroupGrantee("http://acs.amazonaws.com/groups/global/AllUsers");
//  	  GroupGrantee tee2 = new GroupGrantee("http://acs.amazonaws.com/groups/global/AuthenticatedUsers");
	  acl.grantPermission(tee2, Permission.PERMISSION_FULL_CONTROL);
	  
	  System.out.println("the acl = " + acl);
	  
	  obsClient.setBucketAcl(bucketName, null, acl);
  }

private static void doObjectAclOperations()
          throws ObsException
      {
  	AccessControlList acl_ = obsClient.getObjectAcl(bucketName, objectKey);
  	System.out.println(acl_);
  	    
  	
	    	AccessControlList acl = new AccessControlList();
	    	Owner owner = new Owner();
	    	owner.setId("1bddfc511d5a44d4a0299cac3a3bbd0f");
	    	acl.setOwner(owner);
	    	// 为指定用户设置完全控制权限
	    	acl.grantPermission(new CanonicalGrantee("75b07a9802444d969df2f4326420db96"), Permission.PERMISSION_FULL_CONTROL);
	    	// 为所有用户设置读权限
	    	acl.grantPermission(GroupGrantee.ALL_USERS, Permission.PERMISSION_READ);
	    	
	    	System.out.println(acl.toString());
	    	HeaderResponse res = obsClient.setObjectAcl(bucketName, objectKey, acl);
  	
  	///类生产环境
//  	AccessControlList acl = new AccessControlList();
//  	Owner owner = new Owner();
//  	owner.setId("6da89d92ea1141d4abfab627fb7bb7b7");
//  	acl.setOwner(owner);
//  	// 为指定用户设置完全控制权限
//  	acl.grantPermission(new CanonicalGrantee("6da89d92ea1141d4abfab627fb7bb7b7"), Permission.PERMISSION_FULL_CONTROL);
//  	// 为所有用户设置读权限
//  	acl.grantPermission(GroupGrantee.ALL_USERS, Permission.PERMISSION_READ);
//  	HeaderResponse res = obsClient.setObjectAcl(bucketName, objectKey, acl);
  	
//          System.out.println("Setting object ACL to public-read \n");
//          
//          obsClient.setObjectAcl(bucketName, objectKey, null, AccessControlList.REST_CANNED_PUBLIC_READ, null);
//          
//          System.out.println("Getting object ACL " + obsClient.getObjectAcl(bucketName, objectKey, null) + "\n");
//          
//          System.out.println("Setting object ACL to private \n");
//          
//          obsClient.setObjectAcl(bucketName, objectKey, "private", null, null);
//          
          System.out.println("Getting object ACL " + obsClient.getObjectAcl(bucketName, objectKey, null) + "\n");
          System.out.println("Getting the requestId " + res.getRequestId());
      }
  
  private static void optionsBucket() throws ObsException
  {
      System.out.println("Options bucket\n");
      OptionsInfoRequest optionInfo = new OptionsInfoRequest();
      optionInfo.setOrigin("http://www.a.com");
      optionInfo.getRequestHeaders().add("Authorization");
      optionInfo.getRequestMethod().add("PUT");
      System.out.println(obsClient.optionsBucket(bucketName, optionInfo));
  }

  private static void getBucketMetadata()
      throws ObsException
  {
      System.out.println("Getting bucket metadata\n");
      BucketMetadataInfoRequest request = new BucketMetadataInfoRequest(bucketName);
      request.setOrigin("http://www.a.com");
      request.getRequestHeaders().add("Authorization");
      BucketMetadataInfoResult result = obsClient.getBucketMetadata(request);
      System.out.println("StorageClass:" + result.getDefaultStorageClass());
      System.out.println("\tAllowedOrigins " + result.getAllowOrigin());
      System.out.println("\tAllowedMethods " + result.getAllowMethods());
      System.out.println("\tAllowedHeaders " + result.getAllowHeaders());
      System.out.println("\tExposeHeaders " + result.getExposeHeaders());
      System.out.println("\tMaxAgeSeconds " + result.getMaxAge() + "\n");
      
      System.out.println("Deleting bucket CORS\n");
      obsClient.deleteBucketCors(bucketName);
  }
  
  private static void doBucketVersioningOperation()
      throws ObsException
  {
      System.out.println("Getting bucket versioning config " + obsClient.getBucketVersioning(bucketName) + "\n");
      //Enable bucket versioning
      obsClient.setBucketVersioning(bucketName, BucketVersioningConfiguration.ENABLED);
      System.out.println("Current bucket versioning config " + obsClient.getBucketVersioning(bucketName) + "\n");
      //Suspend bucket versioning
      BucketVersioningConfiguration suspended = new BucketVersioningConfiguration("Suspended");
      obsClient.setBucketVersioning(bucketName, suspended);
      System.out.println("Current bucket versioning config " + obsClient.getBucketVersioning(bucketName) + "\n");
  }
  
  private static void doBucketQuotaOperation()
      throws ObsException
  {
      BucketQuota quota = new BucketQuota();
      //Set bucket quota to 1GB
      quota.setBucketQuota(1024 * 1024 * 1024);
      obsClient.setBucketQuota(bucketName, quota);
      System.out.println("Getting bucket quota " + obsClient.getBucketQuota(bucketName) + "\n");
  }
  
  private static void getBucketStorageInfo()
      throws ObsException
  {
      BucketStorageInfo storageInfo = obsClient.getBucketStorageInfo(bucketName);
      System.out.println("Getting bucket storageInfo " + storageInfo + "\n");
  }
  
  private static void doBucketAclOperation()
      throws ObsException
  {
      System.out.println("Setting bucket ACL to public-read \n");
      
      obsClient.setBucketAcl(bucketName, null, AccessControlList.REST_CANNED_PUBLIC_READ);
      
      System.out.println("Getting bucket ACL " + obsClient.getBucketAcl(bucketName) + "\n");
      
      System.out.println("Setting bucket ACL to private \n");
      
      obsClient.setBucketAcl(bucketName, "private", null);
      
      System.out.println("Getting bucket ACL " + obsClient.getBucketAcl(bucketName) + "\n");
  }
  
  private static void doBucketCorsOperation()
      throws ObsException
  {
      BucketCors bucketCors = new BucketCors();
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
      bucketCors.getRules().add(rule);
      
      System.out.println("Setting bucket CORS\n");
      obsClient.setBucketCors(bucketName, bucketCors);
      
      System.out.println("Getting bucket CORS:" + obsClient.getBucketCors(bucketName) + "\n");
      
  }
  
  private static void getBucketLocation()
      throws ObsException
  {
      String location = obsClient.getBucketLocation(bucketName);
      System.out.println("Getting bucket location " + location + "\n");
  }
}
