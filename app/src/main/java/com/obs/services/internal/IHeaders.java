package com.obs.services.internal;

public interface IHeaders{
	String defaultStorageClassHeader();
	String epidHeader();
	String aclHeader();
	String requestIdHeader();
	String requestId2Header();
	String bucketRegionHeader();
	String locationHeader();
	String storageClassHeader();
	String websiteRedirectLocationHeader();
	String successRedirectLocationHeader();
	String sseKmsHeader();
	String sseKmsKeyHeader();
	String sseCHeader();
	String sseCKeyHeader();
	String sseCKeyMd5Header();
	String expiresHeader();
	String versionIdHeader();
	String copySourceHeader();
	String copySourceRangeHeader();
	String copySourceVersionIdHeader();
	String copySourceSseCHeader();
	String copySourceSseCKeyHeader();
	String copySourceSseCKeyMd5Header();
	String metadataDirectiveHeader();
	String dateHeader();
	String deleteMarkerHeader();
	String headerPrefix();
	String headerMetaPrefix();
	String securityTokenHeader();
	String contentSha256Header();
	
	String listTimeoutHeader();
	
	String objectTypeHeader();
	String nextPositionHeader();
	
	String expirationHeader();
	String restoreHeader();
	
	String serverVersionHeader();
	
	String grantReadHeader();
	String grantWriteHeader();
	String grantReadAcpHeader();
	String grantWriteAcpHeader();
	String grantFullControlHeader();
	String grantReadDeliveredHeader();
	String grantFullControlDeliveredHeader();
	
	String copySourceIfModifiedSinceHeader();
	String copySourceIfUnmodifiedSinceHeader();
	String copySourceIfNoneMatchHeader();
	String copySourceIfMatchHeader();
	
	String fsFileInterfaceHeader();
	String fsModeHeader();
	
	String azRedundancyHeader();
}
