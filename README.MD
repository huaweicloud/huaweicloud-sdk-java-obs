Version 3.19.5.3

New features:


Resolved issues:

-----------------------------------------------------------------------------------

Version 3.19.5.2

New features:


Resolved issues:
1. Updated the copyright notice.

-----------------------------------------------------------------------------------

Version 3.19.5

New features:
1. Added the project ID parameter to SSE-KMS.
2. Added the detailed error flag returned by the OBS server when an exception occurs on ObsException.getErrorIndicator.

Resolved issues:
1. [Function] Fixed the issue that upload and download progress bar cannot be displayed when content-length is not set.
2. [Function] Fixed the issue that content-type is automatically set to video/mp4 for uploaded files with file name extension of .mp4.
3. [Function] Fixed the issue of object download failure in the data retrieval scenario.

-----------------------------------------------------------------------------------

Version 3.1.3
New features:
1. Added bucket encryption APIs: ObsClient.setBucketEncryption, ObsClient.getBucketEncryption, and ObsClient.deleteBucketEncryption. Currently, only the SSE-KMS encryption is supported.
2. Added the enumerated type (SSEAlgorithmEnum) for server-side encryption. The server-side encryption model ServerAlgorithm and ServerEncryption are marked as Deprecated.

Documentation & Demo
1. Modified the encryption sample code in the section describing server-side encryption in the Developer Guide.

Resolved issues:
1. Optimized the logging when exceptions occur.
2. Fixed the issue that errors may occur when the ByteArrayInputStream data flow is transferred during object upload.
3. Optimized the level of access logs to avoid ambiguity.
4. Changed the lower limit of resumable upload API on a part size from 5 MB to 100KB.

-----------------------------------------------------------------------------------

Version 3.1.2.1

Resolved issues:
1. Modified the default value of maxIdleConnections in ObsConfiguration to 1000.

-----------------------------------------------------------------------------------

Version 3.1.2

New features:
1. FunctionGraph configuration and query are supported in the bucket event notification APIs: ObsClient.setBucketNotification and ObsClient.getBucketNotification.

Documentation & Demo
1. Added the description of FunctionGraph configuration in the section about event notification in the Developer Guide.

Resolved issues:
1. Fixed the issue that the error information reported by the bucket creation API ObsClient.createBucket is incorrect due to protocol negotiation.
2. Fix the bugs at the bottom layer of okhttp3.Dispatcher. This bug causes the issue that the maximum number of concurrent requests exceeds the upper limit.

-----------------------------------------------------------------------------------

Version 3.1.1

New features:
1. Integrated log4j 1.x as the log component.
2. Added the temporary authentication access API (ObsClient.createGetTemporarySignature) that supports policy setting.
3. The API for object upload (ObsClient.putObject) can automatically identify a wider MIME type.

Resolved issues:
1. Fixed the issue that multiple TopicConfigurations cannot be set for the bucket event notification API (ObsClient.setBucketNotification).
2. Fixed the issue that the SDK is incompatible with JDK 9 or later versions.





