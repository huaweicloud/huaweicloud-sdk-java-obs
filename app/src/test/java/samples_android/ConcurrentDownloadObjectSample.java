package samples_android;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.RandomAccessFile;
import java.io.Writer;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import com.obs.services.ObsClient;
import com.obs.services.ObsConfiguration;
import com.obs.services.exception.ObsException;
import com.obs.services.model.GetObjectRequest;
import com.obs.services.model.ObjectMetadata;
import com.obs.services.model.ObsObject;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.TextView;

/**
 * This sample demonstrates how to download an object concurrently
 * from OBS using the OBS SDK for Android.
 */
public class ConcurrentDownloadObjectSample extends Activity
{
    
    private static final String endPoint = "https://your-endpoint";
    
    private static final String ak = "*** Provide your Access Key ***";
    
    private static final String sk = "*** Provide your Secret Key ***";
    
    private static String bucketName = "my-obs-bucket-demo";
    
    private static String objectKey = "my-obs-object-key-demo";
    
    private static String localFilePath = "/storage/sdcard/" + objectKey;
    
    private static ExecutorService executorService = Executors.newFixedThreadPool(5);
    
    private static AtomicInteger completedBlocks = new AtomicInteger(0);
    
    private static ObsClient obsClient;
    
    private static StringBuffer sb = new StringBuffer();
    
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        ObsConfiguration config = new ObsConfiguration();
        config.setSocketTimeout(30000);
        config.setConnectionTimeout(10000);
        config.setEndPoint(endPoint);
        
        /*
        * Constructs a obs client instance with your account for accessing OBS
        */
        obsClient = new ObsClient(ak, sk, config);
        final TextView tv = (TextView)findViewById(R.id.tv);
        tv.setText("Click to start test");
        
        tv.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                tv.setClickable(false);
                AsyncTask<Void, Void, String> task = new ConcurrentDownloadTask();
                task.execute();
            }
        });
    }
    
    private static class BlockFetcher implements Runnable
    {
        
        private long startPos;
        
        private long endPos;
        
        private int blockNumber;
        
        public BlockFetcher(long startPos, long endPos, int blockNumber)
        {
            this.startPos = startPos;
            this.endPos = endPos;
            this.blockNumber = blockNumber;
        }
        
        @Override
        public void run()
        {
            RandomAccessFile raf = null;
            try
            {
                raf = new RandomAccessFile(localFilePath, "rw");
                raf.seek(startPos);
                
                GetObjectRequest getObjectRequest = new GetObjectRequest(bucketName, objectKey);
                getObjectRequest.setRangeStart(startPos);
                getObjectRequest.setRangeEnd(endPos);
                ObsObject object = obsClient.getObject(getObjectRequest);
                
                InputStream objectContent = object.getObjectContent();
                try
                {
                    byte[] buf = new byte[4096];
                    int bytesRead = 0;
                    while ((bytesRead = objectContent.read(buf)) != -1)
                    {
                        raf.write(buf, 0, bytesRead);
                    }
                    completedBlocks.incrementAndGet();
                    sb.append("Block#" + blockNumber + " done\n\n");
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
                finally
                {
                    objectContent.close();
                }
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
            finally
            {
                if (raf != null)
                {
                    try
                    {
                        raf.close();
                    }
                    catch (IOException e)
                    {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
    
    class ConcurrentDownloadTask extends AsyncTask<Void, Void, String>
    {
        
        @Override
        protected String doInBackground(Void... params)
        {
            try
            {
                
                /*
                 * Create bucket
                 */
                sb.append("Create a new bucket to upload file\n\n");
                obsClient.createBucket(bucketName);
                
                /*
                 * Upload an object to your bucket
                 */
                sb.append("Uploading a new object to OBS from a file\n\n");
                obsClient.putObject(bucketName, objectKey, createSampleFile());
                
                /*
                 * Get size of the object and pre-create a random access file to hold object data
                 */
                ObjectMetadata metadata = obsClient.getObjectMetadata(bucketName, objectKey);
                long objectSize = metadata.getContentLength();
                
                sb.append("Object size from metadata:" + objectSize + "\n\n");
                
                File localFile = new File(localFilePath);
                if (!localFile.getParentFile().exists())
                {
                    localFile.getParentFile().mkdirs();
                }
                RandomAccessFile raf = new RandomAccessFile(localFile, "rw");
                raf.setLength(objectSize);
                raf.close();
                
                /*
                 * Calculate how many blocks to be divided
                 */
                final long blockSize = 5 * 1024 * 1024L; // 5MB
                int blockCount = (int)(objectSize / blockSize);
                if (objectSize % blockSize != 0)
                {
                    blockCount++;
                }
                sb.append("Total blocks count " + blockCount + "\n\n");
                
                /*
                 * Download the object concurrently
                 */
                sb.append("Start to download " + objectKey + "\n\n");
                for (int i = 0; i < blockCount;)
                {
                    long startPos = i++ * blockSize;
                    long endPos = (i == blockCount) ? objectSize - 1 : i * blockSize - 1;
                    executorService.execute(new BlockFetcher(startPos, endPos, i));
                }
                
                /*
                 * Waiting for all blocks finished
                 */
                executorService.shutdown();
                while (!executorService.isTerminated())
                {
                    try
                    {
                        executorService.awaitTermination(5, TimeUnit.SECONDS);
                    }
                    catch (InterruptedException e)
                    {
                        e.printStackTrace();
                    }
                }
                
                /*
                 * Verify whether all blocks are finished
                 */
                if (completedBlocks.intValue() != blockCount)
                {
                    throw new IllegalStateException("Download fails due to some blocks are not finished yet");
                }
                else
                {
                    sb.append("Succeed to download object " + objectKey);
                }
                
                sb.append("Deleting object  " + objectKey + "\n\n");
                obsClient.deleteObject(bucketName, objectKey, null);
                
                return sb.toString();
            }
            catch (ObsException e)
            {
                sb.append("\n\n");
                sb.append("Response Code:" + e.getResponseCode())
                    .append("\n\n")
                    .append("Error Message:" + e.getErrorMessage())
                    .append("\n\n")
                    .append("Error Code:" + e.getErrorCode())
                    .append("\n\n")
                    .append("Request ID:" + e.getErrorRequestId())
                    .append("\n\n")
                    .append("Host ID:" + e.getErrorHostId());
                return sb.toString();
            }
            catch (Exception e)
            {
                sb.append("\n\n");
                sb.append(e.getMessage());
                return sb.toString();
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
        
        @Override
        protected void onPostExecute(String result)
        {
            TextView tv = (TextView)findViewById(R.id.tv);
            tv.setText(result);
            tv.setOnClickListener(null);
            tv.setMovementMethod(ScrollingMovementMethod.getInstance());
        }
        
        private File createSampleFile()
            throws IOException
        {
            File file = File.createTempFile("obs-android-sdk-", ".txt");
            file.deleteOnExit();
            
            Writer writer = new OutputStreamWriter(new FileOutputStream(file));
            for (int i = 0; i < 1000000; i++)
            {
                writer.write(UUID.randomUUID() + "\n\n");
                writer.write(UUID.randomUUID() + "\n\n");
            }
            writer.flush();
            writer.close();
            
            return file;
        }
        
    }
    
}
