package samples_java;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import com.obs.services.ObsClient;
import com.obs.services.ObsConfiguration;
import com.obs.services.exception.ObsException;
import com.obs.services.internal.utils.ServiceUtils;
import com.obs.services.model.ObsBucket;
import com.obs.services.model.RestoreObjectRequest;
import com.obs.services.model.RestoreObjectRequest.RestoreObjectStatus;
import com.obs.services.model.RestoreTierEnum;
import com.obs.services.model.StorageClassEnum;

/**
 * This sample demonstrates how to download an cold object 
 * from OBS using the OBS SDK for Java.
 */
public class RestoreObjectSample
{
    private static final String endPoint = "https://your-endpoint";
    
    private static final String ak = "*** Provide your Access Key ***";
    
    private static final String sk = "*** Provide your Secret Key ***";
    
    
    private static ObsClient obsClient;
    
    private static String bucketName = "my-obs-cold-bucket-demo";
    
    private static String objectKey = "my-obs-cold-object-key-demo";
    
    public static void main(String[] args) throws InterruptedException, IOException
    {
        ObsConfiguration config = new ObsConfiguration();
        config.setSocketTimeout(30000);
        config.setConnectionTimeout(10000);
        config.setEndPoint(endPoint);
        try
        {
            /*
             * Constructs a obs client instance with your account for accessing OBS
             */
            obsClient = new ObsClient(ak, sk, config);
            
            /*
             * Create a cold bucket
             */
            System.out.println("Create a new cold bucket for demo\n");
            ObsBucket bucket = new ObsBucket();
            bucket.setBucketName(bucketName);
            bucket.setBucketStorageClass(StorageClassEnum.COLD);
            obsClient.createBucket(bucket);
            
            /*
             * Create a cold object
             */
            System.out.println("Create a new cold object for demo\n");
            String content = "Hello OBS";
            obsClient.putObject(bucketName, objectKey, new ByteArrayInputStream(content.getBytes("UTF-8")), null);
            
            /*
             * Restore the cold object
             */
            System.out.println("Restore the cold object");
            RestoreObjectRequest restoreObjectRequest = new RestoreObjectRequest(bucketName, objectKey,null, 1, RestoreTierEnum.EXPEDITED);
            System.out.println("\t"+(obsClient.restoreObject(restoreObjectRequest) ==  RestoreObjectStatus.INPROGRESS));
            
            /*
             * Wait 6 minute to get the object
             */
            Thread.sleep(60 * 6 * 1000);
            
            /*
             * Get the cold object status
             */
            System.out.println("Get the cold object status");
            restoreObjectRequest = new RestoreObjectRequest(bucketName, objectKey,null, 1, RestoreTierEnum.EXPEDITED);
            System.out.println("\t"+(obsClient.restoreObject(restoreObjectRequest) ==  RestoreObjectStatus.AVALIABLE) + "\n");
            
            /*
             * Get the cold object
             */
            System.out.println("Get the cold object");
            System.out.println("\tcontent:" + ServiceUtils.toString(obsClient.getObject(bucketName, objectKey, null).getObjectContent()));
            
            
            /*
             * Delete the cold object
             */
            obsClient.deleteObject(bucketName, objectKey, null);
            
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
}
