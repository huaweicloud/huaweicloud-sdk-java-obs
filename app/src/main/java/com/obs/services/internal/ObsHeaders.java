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


public class ObsHeaders implements IHeaders{
	
	private ObsHeaders() {
		
	}
	
	private static ObsHeaders instance = new ObsHeaders();
	
	public static IHeaders getInstance() {
		return instance;
	}
	
	@Override
	public String defaultStorageClassHeader() {
		return this.headerPrefix() + "storage-class";
	}

	@Override
    public String epidHeader() {
        return this.headerPrefix() + "epid";
    }

    @Override
	public String aclHeader() {
		return this.headerPrefix() + "acl";
	}

	@Override
	public String requestIdHeader() {
		return this.headerPrefix() + "request-id";
	}
	
	@Override
	public String requestId2Header() {
		return this.headerPrefix() + "id-2";
	}

	@Override
	public String storageClassHeader() {
		return this.headerPrefix() + "storage-class";
	}

	@Override
	public String websiteRedirectLocationHeader() {
		return this.headerPrefix() + "website-redirect-location";
	}

	@Override
	public String sseKmsHeader() {
		return this.headerPrefix() + "server-side-encryption";
	}

	@Override
	public String sseKmsKeyHeader() {
		return this.headerPrefix() + "server-side-encryption-kms-key-id";
	}
	
	@Override
	public String sseKmsProjectIdHeader() {
		return this.headerPrefix() + "sse-kms-key-project-id";
	}

	@Override
	public String sseCHeader() {
		return this.headerPrefix() + "server-side-encryption-customer-algorithm";
	}

	@Override
	public String sseCKeyHeader() {
		return this.headerPrefix() + "server-side-encryption-customer-key";
	}

	@Override
	public String sseCKeyMd5Header() {
		return this.headerPrefix() + "server-side-encryption-customer-key-MD5";
	}

	@Override
	public String expiresHeader() {
		return this.headerPrefix() + "expires";
	}

	@Override
	public String versionIdHeader() {
		return this.headerPrefix() + "version-id";
	}

	@Override
	public String copySourceSseCHeader() {
		return this.headerPrefix() + "copy-source-server-side-encryption-customer-algorithm";
	}

	@Override
	public String metadataDirectiveHeader() {
		return this.headerPrefix() + "metadata-directive";
	}

	@Override
	public String headerPrefix() {
		return Constants.OBS_HEADER_PREFIX;
	}

	@Override
	public String headerMetaPrefix() {
		return Constants.OBS_HEADER_META_PREFIX;
	}

	@Override
	public String dateHeader() {
		return this.headerPrefix() + "date";
	}

	@Override
	public String grantReadHeader() {
		return this.headerPrefix() + "grant-read";
	}

	@Override
	public String grantWriteHeader() {
		return this.headerPrefix() + "grant-write";
	}

	@Override
	public String grantReadAcpHeader() {
		return this.headerPrefix() + "grant-read-acp";
	}

	@Override
	public String grantWriteAcpHeader() {
		return this.headerPrefix() + "grant-write-acp";
	}

	@Override
	public String grantFullControlHeader() {
		return this.headerPrefix() + "grant-full-control";
	}

	@Override
	public String grantReadDeliveredHeader() {
		return this.headerPrefix() + "grant-read-delivered";
	}

	@Override
	public String grantFullControlDeliveredHeader() {
		return this.headerPrefix() + "grant-full-control-delivered";
	}

	@Override
	public String serverVersionHeader() {
		return this.headerPrefix() + "version";
	}

	@Override
	public String bucketRegionHeader() {
		return this.headerPrefix() + "bucket-location";
	}

	@Override
	public String locationHeader() {
		return null;
	}

	@Override
	public String successRedirectLocationHeader() {
		return "success-action-redirect";
	}

	@Override
	public String deleteMarkerHeader() {
		return this.headerPrefix() + "delete-marker";
	}

	@Override
	public String copySourceSseCKeyHeader() {
		return this.headerPrefix() + "copy-source-server-side-encryption-customer-key";
	}

	@Override
	public String copySourceSseCKeyMd5Header() {
		return this.headerPrefix() + "copy-source-server-side-encryption-customer-key-MD5";
	}

	@Override
	public String copySourceIfModifiedSinceHeader() {
		return this.headerPrefix() + "copy-source-if-modified-since";
	}

	@Override
	public String copySourceIfUnmodifiedSinceHeader() {
		return this.headerPrefix() + "copy-source-if-unmodified-since";
	}

	@Override
	public String copySourceIfNoneMatchHeader() {
		return this.headerPrefix() + "copy-source-if-none-match";
	}

	@Override
	public String copySourceIfMatchHeader() {
		return this.headerPrefix() + "copy-source-if-match";
	}

	@Override
	public String copySourceHeader() {
		return this.headerPrefix() + "copy-source";
	}

	@Override
	public String copySourceVersionIdHeader() {
		return this.headerPrefix() + "copy-source-version-id";
	}

	@Override
	public String expirationHeader() {
		return this.headerPrefix() + "expiration";
	}

	@Override
	public String restoreHeader() {
		return this.headerPrefix() + "restore";
	}

	@Override
	public String copySourceRangeHeader() {
		return this.headerPrefix() + "copy-source-range";
	}

	@Override
	public String securityTokenHeader() {
		return this.headerPrefix() + "security-token";
	}

	@Override
	public String contentSha256Header() {
		return null;
	}

	@Override
	public String objectTypeHeader() {
		return this.headerPrefix() + "object-type";
	}

	@Override
	public String nextPositionHeader() {
		return this.headerPrefix() + "next-append-position";
	}

	@Override
	public String listTimeoutHeader() {
		return this.headerPrefix() + "list-timeout";
	}

	@Override
	public String fsFileInterfaceHeader() {
		return this.headerPrefix() + "fs-file-interface";
	}

	@Override
	public String fsModeHeader() {
		return this.headerMetaPrefix() + "mode";
	}

	@Override
	public String azRedundancyHeader() {
		return this.headerPrefix() + "az-redundancy";
	}
}
