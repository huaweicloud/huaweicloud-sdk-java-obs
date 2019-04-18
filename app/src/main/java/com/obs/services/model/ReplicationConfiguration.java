package com.obs.services.model;

import java.util.ArrayList;
import java.util.List;

/**
 * 桶的跨Region复制配置
 *
 */
public class ReplicationConfiguration extends HeaderResponse{
	
	private String agency;
	
	private List<Rule> rules;

	public static class Rule {
		private String id;
		private RuleStatusEnum status;
		private String prefix;
		private Destination destination;
		
		/**
		 * 获取规则ID
		 * @return 规则ID
		 */
		public String getId() {
			return id;
		}

		/**
		 * 设置规则ID
		 * @param id 规则ID
		 */
		public void setId(String id) {
			this.id = id;
		}
		
		/**
		 * 获取规则状态
		 * @return 规则状态
		 */
		public RuleStatusEnum getStatus() {
			return status;
		}
		
		/**
		 * 设置规则状态
		 * @param status 规则状态
		 */
		public void setStatus(RuleStatusEnum status) {
			this.status = status;
		}
		
		/**
		 * 获取规则匹配的对象名前缀
		 * @return 对象名前缀
		 */
		public String getPrefix() {
			return prefix;
		}
		
		/**
		 * 设置规则匹配的对象名前缀
		 * @param prefix 对象名前缀
		 */
		public void setPrefix(String prefix) {
			this.prefix = prefix;
		}
		
		/**
		 * 获取复制的目标信息
		 * @return 目标信息
		 */
		public Destination getDestination() {
			return destination;
		}
		
		/**
		 * 设置复制的目标信息
		 * @param destination 目标信息
		 */
		public void setDestination(Destination destination) {
			this.destination = destination;
		}

		@Override
		public String toString() {
			return "Rule [id=" + id + ", status=" + status + ", prefix=" + prefix + ", destination=" + destination
					+ "]";
		}
	}

	public static class Destination {
		private String bucket;
		private StorageClassEnum storageClass;
		
		/**
		 * 获取复制的目标桶名
		 * @return 目标桶名
		 */
		public String getBucket() {
			return bucket;
		}
		
		/**
		 * 设置复制的目标桶名
		 * @param bucket 目标桶名
		 */
		public void setBucket(String bucket) {
			this.bucket = bucket;
		}
		
		/**
		 * 获取复制后的对象的存储类型
		 * @return 对象的存储类型
		 */
		public StorageClassEnum getObjectStorageClass() {
			return storageClass;
		}

		/**
		 * 设置复制后的对象的存储类型
		 * @param storageClass 对象的存储类型
		 */
		public void setObjectStorageClass(StorageClassEnum storageClass) {
			this.storageClass = storageClass;
		}

		@Override
		public String toString() {
			return "Destination [bucket=" + bucket + ", storageClass=" + storageClass + "]";
		}
	}

	/**
	 * 获取复制规则列表
	 * @return 复制规则列表
	 */
	public List<Rule> getRules() {
		if (rules == null) {
			rules = new ArrayList<Rule>();
		}
		return rules;
	}

	/**
	 * 设置复制规则列表
	 * @param rules 复制规则列表
	 */
	public void setRules(List<Rule> rules) {
		this.rules = rules;
	}
	
	/**
	 * 设置委托名字
	 * @return 委托名字
	 */
	public String getAgency() {
		return agency;
	}

	/**
	 * 获取委托名字
	 * @param agency 委托名字
	 */
	public void setAgency(String agency) {
		this.agency = agency;
	}

	@Override
	public String toString() {
		return "ReplicationConfiguration [agency=" + agency + ", rules=" + rules + "]";
	}
}
