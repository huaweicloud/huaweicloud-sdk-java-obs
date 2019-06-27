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
**/
package com.oef.services.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class PutExtensionPolicyRequest {
	@JsonProperty(value = "fetch")
	private FetchBean fetch;
	
	@JsonProperty(value = "transcode")
	private TranscodeBean transcode;
	
	@JsonProperty(value = "compress")
	private CompressBean compress;
	
	public PutExtensionPolicyRequest() {

	}

	public FetchBean getFetch() {
		return fetch;
	}

	public void setFetch(FetchBean fetch) {
		this.fetch = fetch;
	}

	public TranscodeBean getTranscode() {
		return transcode;
	}

	public void setTranscode(TranscodeBean transcode) {
		this.transcode = transcode;
	}

	public CompressBean getCompress() {
		return compress;
	}

	public void setCompress(CompressBean compress) {
		this.compress = compress;
	}
	
	@Override
    public String toString()
    {
		String fetchStatus = fetch == null? null: fetch.getStatus(); 
		String fetchAgency = fetch == null? null: fetch.getAgency(); 
		String transcodeStatus = transcode == null? null: transcode.getStatus(); 
		String transcodeAgency = transcode == null? null: transcode.getAgency(); 
		String compressStatus = compress == null? null: compress.getStatus(); 
		String compressAgency = compress == null? null: compress.getAgency();

        return "ExtensionPolicyRequest [fetch status=" + fetchStatus + ", fetch agency=" +  fetchAgency
            + ", transcode status=" + transcodeStatus + ", transcode agency=" + transcodeAgency 
            + ", compress status=" + compressStatus + ", compress agency=" + compressAgency + "]";
    }
}
