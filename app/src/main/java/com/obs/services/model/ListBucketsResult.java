package com.obs.services.model;

import java.util.ArrayList;
import java.util.List;

/**
 * 列举桶的响应结果 
 *
 */
public class ListBucketsResult extends HeaderResponse{
	private List<ObsBucket> buckets;
	
	private Owner owner;
	
	public ListBucketsResult(List<ObsBucket> buckets, Owner owner) {
		this.buckets = buckets;
		this.owner = owner;
	}

	public List<ObsBucket> getBuckets() {
		if(buckets == null) {
			buckets = new ArrayList<ObsBucket>();
		}
		return buckets;
	}

	public Owner getOwner() {
		return owner;
	}


	@Override
	public String toString() {
		return "ListBucketsResult [buckets=" + buckets + ", owner=" + owner + "]";
	}
	
	
}
