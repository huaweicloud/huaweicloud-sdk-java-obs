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

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.jamesmurty.utils.XMLBuilder;
import com.obs.services.internal.utils.ServiceUtils;
import com.obs.services.model.AbstractNotification;
import com.obs.services.model.AccessControlList;
import com.obs.services.model.BucketCors;
import com.obs.services.model.BucketCorsRule;
import com.obs.services.model.BucketDirectColdAccess;
import com.obs.services.model.BucketEncryption;
import com.obs.services.model.BucketLoggingConfiguration;
import com.obs.services.model.BucketNotificationConfiguration;
import com.obs.services.model.BucketQuota;
import com.obs.services.model.BucketStoragePolicyConfiguration;
import com.obs.services.model.BucketTagInfo;
import com.obs.services.model.CanonicalGrantee;
import com.obs.services.model.EventTypeEnum;
import com.obs.services.model.FunctionGraphConfiguration;
import com.obs.services.model.GrantAndPermission;
import com.obs.services.model.GranteeInterface;
import com.obs.services.model.GroupGrantee;
import com.obs.services.model.GroupGranteeEnum;
import com.obs.services.model.KeyAndVersion;
import com.obs.services.model.LifecycleConfiguration;
import com.obs.services.model.LifecycleConfiguration.NoncurrentVersionTransition;
import com.obs.services.model.LifecycleConfiguration.Rule;
import com.obs.services.model.LifecycleConfiguration.Transition;
import com.obs.services.model.Owner;
import com.obs.services.model.PartEtag;
import com.obs.services.model.Permission;
import com.obs.services.model.Redirect;
import com.obs.services.model.ReplicationConfiguration;
import com.obs.services.model.RestoreObjectRequest;
import com.obs.services.model.RouteRule;
import com.obs.services.model.RouteRuleCondition;
import com.obs.services.model.SSEAlgorithmEnum;
import com.obs.services.model.StorageClassEnum;
import com.obs.services.model.TopicConfiguration;
import com.obs.services.model.WebsiteConfiguration;
import com.obs.services.model.fs.FSStatusEnum;

public class V2Convertor implements IConvertor {
	
	private static IConvertor instance = new V2Convertor();
	
	protected V2Convertor() {
		
	}
	
	public static IConvertor getInstance() {
		return instance;
	}
	
	@Override
	public String transCompleteMultipartUpload(List<PartEtag> parts) throws ServiceException {
		try {
			XMLBuilder builder = XMLBuilder.create("CompleteMultipartUpload");
			Collections.sort(parts, new Comparator<PartEtag>() {
				@Override
				public int compare(PartEtag o1, PartEtag o2) {
					if (o1 == o2) {
						return 0;
					}
					if (o1 == null) {
						return -1;
					}
					if (o2 == null) {
						return 1;
					}
					return o1.getPartNumber().compareTo(o2.getPartNumber());
				}

			});
			for (PartEtag part : parts) {
				builder.e("Part").e("PartNumber").t(part.getPartNumber() == null ? "" : part.getPartNumber().toString()).up()
					.e("ETag").t(ServiceUtils.toValid(part.geteTag()));
			}
			return builder.asString();
		} catch (Exception e) {
			throw new ServiceException(e);
		}
	}

	@Override
	public String transBucketLoction(String location) throws ServiceException {
		try {
			XMLBuilder builder = XMLBuilder.create("CreateBucketConfiguration")
					.elem("LocationConstraint").text(ServiceUtils.toValid(location));
			return builder.asString();
		} catch (Exception e) {
			throw new ServiceException(e);
		}
	}

	@Override
	public String transVersioningConfiguration(String bucketName, String status) throws ServiceException {
		try {
			XMLBuilder builder = XMLBuilder.create("VersioningConfiguration")
					.elem("Status").text(ServiceUtils.toValid(status));
			return builder.asString();
		} catch (Exception e) {
			throw new ServiceException("Failed to build XML document for versioning", e);
		}
	}

	@Override
	public String transLifecycleConfiguration(LifecycleConfiguration config) throws ServiceException {
		try {
			XMLBuilder builder = XMLBuilder.create("LifecycleConfiguration");
			for (Rule rule : config.getRules()) {
				XMLBuilder b = builder.elem("Rule");
				if (ServiceUtils.isValid2(rule.getId())) {
					b.elem("ID").t(rule.getId());
				}
				if(rule.getPrefix() != null) {
					b.elem("Prefix").t(ServiceUtils.toValid(rule.getPrefix()));
				}
				b.elem("Status").t(rule.getEnabled() ? "Enabled" : "Disabled");

				if (rule.getTransitions() != null) {
					for (Transition transition : rule.getTransitions()) {
						if (transition.getObjectStorageClass() != null) {
							XMLBuilder tBuilder = b.elem("Transition");
							if (transition.getDate() != null) {
								tBuilder.elem("Date").t(ServiceUtils.formatIso8601MidnightDate(transition.getDate()));
							} else if (transition.getDays() != null) {
								tBuilder.elem("Days").t(transition.getDays().toString());
							}
							tBuilder.elem("StorageClass").t(this.transStorageClass(transition.getObjectStorageClass()));
						}
					}
				}

				if (rule.getExpiration() != null) {
					XMLBuilder eBuilder = b.elem("Expiration");
					if (rule.getExpiration().getDate() != null) {
						eBuilder.elem("Date").t(ServiceUtils.formatIso8601MidnightDate(rule.getExpiration().getDate()));
					} else if (rule.getExpiration().getDays() != null) {
						eBuilder.elem("Days").t(rule.getExpiration().getDays().toString());
					}
				}

				if (rule.getNoncurrentVersionTransitions() != null) {
					for (NoncurrentVersionTransition noncurrentVersionTransition : rule
							.getNoncurrentVersionTransitions()) {
						if (noncurrentVersionTransition.getObjectStorageClass() != null
								&& noncurrentVersionTransition.getDays() != null) {
							XMLBuilder eBuilder = b.elem("NoncurrentVersionTransition");
							eBuilder.elem("NoncurrentDays").t(noncurrentVersionTransition.getDays().toString());
							eBuilder.elem("StorageClass")
									.t(this.transStorageClass(noncurrentVersionTransition.getObjectStorageClass()));
						}
					}
				}

				if (rule.getNoncurrentVersionExpiration() != null
						&& rule.getNoncurrentVersionExpiration().getDays() != null) {
					XMLBuilder eBuilder = b.elem("NoncurrentVersionExpiration");
					eBuilder.elem("NoncurrentDays")
							.t(rule.getNoncurrentVersionExpiration().getDays().toString());
				}
			}
			return builder.asString();
		} catch (Exception e) {
			throw new ServiceException("Failed to build XML document for lifecycle", e);
		}
	}

	@Override
	public String transWebsiteConfiguration(WebsiteConfiguration config) throws ServiceException {
		try {
			XMLBuilder builder = XMLBuilder.create("WebsiteConfiguration");
			if (config.getRedirectAllRequestsTo() != null) {
				if (null != config.getRedirectAllRequestsTo().getHostName()) {
					builder = builder.elem("RedirectAllRequestsTo").elem("HostName")
							.text(ServiceUtils.toValid(config.getRedirectAllRequestsTo().getHostName()));
				}
				if (null != config.getRedirectAllRequestsTo().getRedirectProtocol()) {
					builder = builder.up().elem("Protocol")
							.text(config.getRedirectAllRequestsTo().getRedirectProtocol().getCode());
				}
				builder.up().up();
				return builder.asString();
			}
			if (ServiceUtils.isValid2(config.getSuffix())) {
				builder.elem("IndexDocument").elem("Suffix").text(config.getSuffix()).up().up();
			}
			if (ServiceUtils.isValid2(config.getKey())) {
				builder.elem("ErrorDocument").elem("Key").text(config.getKey()).up().up();
			}
			if (null != config.getRouteRules() && config.getRouteRules().size() > 0) {
				builder = builder.elem("RoutingRules");
				for (RouteRule routingRule : config.getRouteRules()) {
					builder = builder.elem("RoutingRule");
					RouteRuleCondition condition = routingRule.getCondition();
					Redirect redirect = routingRule.getRedirect();
					if (null != condition) {
						builder = builder.elem("Condition");
						String keyPrefixEquals = condition.getKeyPrefixEquals();
						String hecre = condition.getHttpErrorCodeReturnedEquals();
						if (ServiceUtils.isValid2(keyPrefixEquals)) {
							builder = builder.elem("KeyPrefixEquals").text(keyPrefixEquals);
							builder = builder.up();
						}
						if (ServiceUtils.isValid2(hecre)) {
							builder = builder.elem("HttpErrorCodeReturnedEquals").text(hecre);
							builder = builder.up();
						}
						builder = builder.up();
					}
					if (null != redirect) {
						builder = builder.elem("Redirect");
						String hostName = redirect.getHostName();
						String repalceKeyWith = redirect.getReplaceKeyWith();
						String replaceKeyPrefixWith = redirect.getReplaceKeyPrefixWith();
						String redirectCode = redirect.getHttpRedirectCode();
						if (ServiceUtils.isValid2(hostName)) {
							builder = builder.elem("HostName").text(hostName);
							builder = builder.up();
						}
						if (ServiceUtils.isValid2(redirectCode)) {
							builder = builder.elem("HttpRedirectCode").text(redirectCode);
							builder = builder.up();
						}
						if (ServiceUtils.isValid2(repalceKeyWith)) {
							builder = builder.elem("ReplaceKeyWith").text(repalceKeyWith);
							builder = builder.up();
						}
						if (ServiceUtils.isValid2(replaceKeyPrefixWith)) {
							builder = builder.elem("ReplaceKeyPrefixWith").text(replaceKeyPrefixWith);
							builder = builder.up();
						}
						if (redirect.getRedirectProtocol() != null) {
							builder = builder.elem("Protocol").text(redirect.getRedirectProtocol().getCode());
							builder = builder.up();
						}
						builder = builder.up();
					}
					builder = builder.up();
				}
				builder = builder.up();
			}
			return builder.asString();
		} catch (Exception e) {
			throw new ServiceException("Failed to build XML document for website", e);
		}
	}

	@Override
	public String transRestoreObjectRequest(RestoreObjectRequest req) throws ServiceException {
		try {
			XMLBuilder builder = XMLBuilder.create("RestoreRequest").elem("Days").t(String.valueOf(req.getDays())).up();
			if (req.getRestoreTier() != null) {
				builder.e("GlacierJobParameters").e("Tier").t(req.getRestoreTier().getCode());
			}
			return builder.asString();
		} catch (Exception e) {
			throw new ServiceException("Failed to build XML document for restoreobject", e);
		}
	}

	@Override
	public String transBucketQuota(BucketQuota quota) throws ServiceException {
		try {
			XMLBuilder builder = XMLBuilder.create("Quota").elem("StorageQuota")
					.text(String.valueOf(quota.getBucketQuota())).up();
			return builder.asString();
		} catch (Exception e) {
			throw new ServiceException("Failed to build XML document for storageQuota", e);
		}
	}
	
	@Override
	public String transBucketEcryption(BucketEncryption encryption) throws ServiceException {
	    String algorithm = encryption.getSseAlgorithm().getCode();
	    String kmsKeyId = "";
	    if (algorithm.equals(SSEAlgorithmEnum.KMS.getCode())) {
	        algorithm = "aws:" + algorithm;
	        kmsKeyId = encryption.getKmsKeyId();
	    }
	    try {
	        XMLBuilder builder = XMLBuilder.create("ServerSideEncryptionConfiguration").e("Rule").e("ApplyServerSideEncryptionByDefault");
	        builder.e("SSEAlgorithm").t(algorithm);
            if (ServiceUtils.isValid(kmsKeyId)) {
                builder.e("KMSMasterKeyID").t(kmsKeyId);
            }
	        return builder.asString();
	    } catch (Exception e) {
            throw new ServiceException("Failed to build XML document for bucketEncryption", e);
        }
	    
	}

	@Override
	public String transStoragePolicy(BucketStoragePolicyConfiguration status) throws ServiceException {
		try {
			XMLBuilder builder = XMLBuilder.create("StoragePolicy")
					.elem("DefaultStorageClass")
					.text(this.transStorageClass(status.getBucketStorageClass()));
			return builder.asString();
		} catch (Exception e) {
			throw new ServiceException("Failed to build XML document for StoragePolicy", e);
		}
	}

	@Override
	public String transBucketLoggingConfiguration(BucketLoggingConfiguration c) throws ServiceException {
		try {
			XMLBuilder builder = XMLBuilder.create("BucketLoggingStatus");
			if (c.isLoggingEnabled()) {
				XMLBuilder enabledBuilder = builder.elem("LoggingEnabled");
				if(c.getTargetBucketName() != null) {
					enabledBuilder.elem("TargetBucket")
					.text(ServiceUtils.toValid(c.getTargetBucketName()));
				}
				
				if(c.getLogfilePrefix() != null) {
					enabledBuilder.elem("TargetPrefix").text(ServiceUtils.toValid(c.getLogfilePrefix()));
				}
				GrantAndPermission[] grants = c.getTargetGrants();
				if (grants.length > 0) {
					XMLBuilder grantsBuilder = enabledBuilder.elem("TargetGrants");
					for (GrantAndPermission gap : grants) {
						GranteeInterface grantee = gap.getGrantee();
						Permission permission = gap.getPermission();
						if(permission != null) {
							XMLBuilder subBuilder = null;
							if (grantee instanceof CanonicalGrantee) {
								subBuilder = XMLBuilder.create("Grantee")
										.attr("xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance")
										.attr("xsi:type", "CanonicalUser").element("ID").text(ServiceUtils.toValid(grantee.getIdentifier()));
								String displayName = ((CanonicalGrantee) grantee).getDisplayName();
								if (ServiceUtils.isValid2(displayName)) {
									subBuilder.up().element("DisplayName")
									.text(displayName);
								}
							} else if (grantee instanceof GroupGrantee) {
								subBuilder = XMLBuilder.create("Grantee")
										.attr("xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance")
										.attr("xsi:type", "Group").element("URI").text(this.transGroupGrantee(((GroupGrantee)grantee).getGroupGranteeType()));
							}
							
							if(subBuilder != null) {
								grantsBuilder.elem("Grant").importXMLBuilder(subBuilder).elem("Permission")
								.text(ServiceUtils.toValid(permission.getPermissionString()));
							}
						}
					}
				}
			}
			return builder.asString();
		} catch (Exception e) {
			throw new ServiceException("Failed to build XML document for BucketLoggingConfiguration", e);
		}
	}

	@Override
	public String transBucketCors(BucketCors cors) throws ServiceException {
    	try {
			XMLBuilder builder = XMLBuilder.create("CORSConfiguration");
			for (BucketCorsRule rule : cors.getRules()) {
				builder = builder.e("CORSRule");
				if (rule.getId() != null) {
					builder.e("ID").t(rule.getId());
				}
				if(rule.getAllowedMethod() != null) {
					for(String method : rule.getAllowedMethod()) {
						builder.e("AllowedMethod").t(ServiceUtils.toValid(method));
					}
				}
				
				if(rule.getAllowedOrigin() != null) {
					for(String origin : rule.getAllowedOrigin()) {
						builder.e("AllowedOrigin").t(ServiceUtils.toValid(origin));
					}
				}
				
				if(rule.getAllowedHeader() != null) {
					for(String header : rule.getAllowedHeader()) {
						builder.e("AllowedHeader").t(ServiceUtils.toValid(header));
					}
				}
				builder.e("MaxAgeSeconds").t(String.valueOf(rule.getMaxAgeSecond()));
				if(rule.getExposeHeader() != null) {
					for(String exposeHeader : rule.getExposeHeader()) {
						builder.e("ExposeHeader").t(ServiceUtils.toValid(exposeHeader));
					}
				}
				builder = builder.up();
			}
			return builder.asString();
		}catch (Exception e) {
			throw new ServiceException("Failed to build XML document for cors", e);
		}
	}

	
	@Override
	public String transAccessControlList(AccessControlList acl, boolean isBucket) throws ServiceException {
		Owner owner = acl.getOwner();
		GrantAndPermission[] grants = acl.getGrantAndPermissions();
		try {
			XMLBuilder builder = XMLBuilder.create("AccessControlPolicy");
			if (owner != null) {
				builder = builder.elem("Owner").elem("ID").text(ServiceUtils.toValid(owner.getId()));
				if (null != owner.getDisplayName()) {
					builder.up().elem("DisplayName").text(owner.getDisplayName());
				}
				builder = builder.up().up();
			}
			if(grants.length > 0) {
				XMLBuilder accessControlList = builder.elem("AccessControlList");				
				for (GrantAndPermission gap : grants) {
					GranteeInterface grantee = gap.getGrantee();
					Permission permission = gap.getPermission();
					XMLBuilder subBuilder = null;
					if (grantee instanceof CanonicalGrantee) {
						subBuilder = XMLBuilder.create("Grantee")
								.attr("xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance")
								.attr("xsi:type", "CanonicalUser").element("ID").text(ServiceUtils.toValid(grantee.getIdentifier()));
						String displayName = ((CanonicalGrantee) grantee).getDisplayName();
						if (ServiceUtils.isValid2(displayName)) {
							subBuilder.up().element("DisplayName").text(displayName);
						}
					} else if (grantee instanceof GroupGrantee) {
						subBuilder = XMLBuilder.create("Grantee")
								.attr("xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance").attr("xsi:type", "Group")
								.element("URI").text(this.transGroupGrantee(((GroupGrantee)grantee).getGroupGranteeType()));
					}else if(grantee != null) {
						subBuilder = XMLBuilder.create("Grantee")
								.attr("xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance")
								.attr("xsi:type", "CanonicalUser").element("ID").text(ServiceUtils.toValid(grantee.getIdentifier()));
					}
					
					if(subBuilder != null) {
						XMLBuilder grantBuilder = accessControlList.elem("Grant").importXMLBuilder(subBuilder);
						if(permission != null) {
							grantBuilder.elem("Permission")
							.text(ServiceUtils.toValid(permission.getPermissionString()));
						}
					}
				}
			}
			return builder.asString();
		} catch (Exception e) {
			throw new ServiceException("Failed to build XML document for ACL", e);
		} 
	}
	
	public String transKeyAndVersion(KeyAndVersion[] objectNameAndVersions, boolean isQuiet)
			throws ServiceException {
		try {
			XMLBuilder builder = XMLBuilder.create("Delete").elem("Quiet")
					.text(String.valueOf(isQuiet)).up();
			for (KeyAndVersion nav : objectNameAndVersions) {
				XMLBuilder objectBuilder = builder.elem("Object").elem("Key").text(ServiceUtils.toValid(nav.getKey())).up();
				if (ServiceUtils.isValid(nav.getVersion())) {
					objectBuilder.elem("VersionId").text(nav.getVersion());
				}
			}
			return builder.asString();
		} catch (Exception e) {
			throw new ServiceException("Failed to build XML document", e);
		}
	}

	@Override
	public String transBucketTagInfo(BucketTagInfo bucketTagInfo) throws ServiceException {
		try {
			XMLBuilder builder = XMLBuilder.create("Tagging").e("TagSet");
			for (BucketTagInfo.TagSet.Tag tag : bucketTagInfo.getTagSet().getTags()) {
				if (tag != null) {
					builder.e("Tag")
					.e("Key").t(ServiceUtils.toValid(tag.getKey()))
					.up()
					.e("Value")
					.t(ServiceUtils.toValid(tag.getValue()));
				}
			}
			return builder.up().asString();
		} catch (Exception e) {
			throw new ServiceException("Failed to build XML document for Tagging", e);
		}
	}

	@Override
	public String transBucketNotificationConfiguration(
			BucketNotificationConfiguration bucketNotificationConfiguration) throws ServiceException {
		try {
			XMLBuilder builder = XMLBuilder.create("NotificationConfiguration");
			if (bucketNotificationConfiguration == null) {
				return builder.asString();
			}
	
			for (TopicConfiguration config : bucketNotificationConfiguration.getTopicConfigurations()) {
			    packNotificationConfig(builder, config, "TopicConfiguration", "Topic", "S3Key");
			}
			
			for (FunctionGraphConfiguration config : bucketNotificationConfiguration.getFunctionGraphConfigurations()) {
                packNotificationConfig(builder, config, "FunctionGraphConfiguration", "FunctionGraph", "S3Key");
            }
	
			return builder.asString();
		} catch (Exception e) {
			throw new ServiceException("Failed to build XML document for Notification", e);
		}
	}
	
	protected void packNotificationConfig(XMLBuilder builder, AbstractNotification config, String configType, String urnType, String adapter) {
	    builder = builder.e(configType);
        if (config.getId() != null) {
            builder.e("Id").t(config.getId());
        }
        if (config.getFilter() != null && !config.getFilter().getFilterRules().isEmpty()) {
            builder = builder.e("Filter").e(adapter);
            for (AbstractNotification.Filter.FilterRule rule : config.getFilter().getFilterRules()) {
                if (rule != null) {
                    builder.e("FilterRule").e("Name").t(ServiceUtils.toValid(rule.getName())).up()
                            .e("Value").t(ServiceUtils.toValid(rule.getValue()));
                }
            }
            builder = builder.up().up();
        }
        String urn = null;
        if (config instanceof TopicConfiguration) {
            urn = ((TopicConfiguration)config).getTopic();
        } 
        if (config instanceof FunctionGraphConfiguration) {
            urn = ((FunctionGraphConfiguration)config).getFunctionGraph();
        }
        if (urn != null) {
            builder.e(urnType).t(urn);
        }

        if (config.getEventTypes() != null) {
            for (EventTypeEnum event : config.getEventTypes()) {
                if(event != null) {
                    builder.e("Event").t(this.transEventType(event));
                }
            }
        }
        builder = builder.up();
	}

	@Override
	public String transReplicationConfiguration(ReplicationConfiguration replicationConfiguration) throws ServiceException {
		try {
			XMLBuilder builder = XMLBuilder.create("ReplicationConfiguration").e("Agency").t(ServiceUtils.toValid(replicationConfiguration.getAgency()))
					.up();
			for (ReplicationConfiguration.Rule rule : replicationConfiguration.getRules()) {
				builder = builder.e("Rule");
				if (rule.getId() != null) {
					builder.e("ID").t(rule.getId());
				}
				builder.e("Prefix").t(ServiceUtils.toValid(rule.getPrefix()));
				if (rule.getStatus() != null) {
					builder.e("Status").t(rule.getStatus().getCode());
				}
				if (rule.getDestination() != null) {
					String bucketName = ServiceUtils.toValid(rule.getDestination().getBucket());
					builder = builder.e("Destination").e("Bucket").t(bucketName.startsWith("arn:aws:s3:::") ? bucketName : "arn:aws:s3:::" + bucketName).up();
					if (rule.getDestination().getObjectStorageClass() != null) {
						builder.e("StorageClass").t(this.transStorageClass(rule.getDestination().getObjectStorageClass()));
					}
					builder = builder.up();
				}
				builder = builder.up();
			}
			return builder.asString();
		} catch (Exception e) {
			throw new ServiceException("Failed to build XML document for Replication", e);
		}
	}
	
	@Override
	public String transBucketFileInterface(FSStatusEnum status) throws ServiceException {
		try {
			return XMLBuilder.create("FileInterfaceConfiguration").e("Status").t(status.getCode())
					.up().asString();
		} catch (Exception e) {
			throw new ServiceException("Failed to build XML document for FileInterface", e);
		}
	}
	
	@Override
	public String transEventType(EventTypeEnum eventType) {
		String eventTypeStr = "";
		if(eventType != null) {
			switch (eventType) {
			case OBJECT_CREATED_ALL:
				eventTypeStr = "s3:ObjectCreated:*";
				break;
			case OBJECT_CREATED_PUT:
				eventTypeStr = "s3:ObjectCreated:Put";
				break;
			case OBJECT_CREATED_POST:
				eventTypeStr = "s3:ObjectCreated:Post";
				break;
			case OBJECT_CREATED_COPY:
				eventTypeStr = "s3:ObjectCreated:Copy";
				break;
			case OBJECT_CREATED_COMPLETE_MULTIPART_UPLOAD:
				eventTypeStr = "s3:ObjectCreated:CompleteMultipartUpload";
				break;
			case OBJECT_REMOVED_ALL:
				eventTypeStr = "s3:ObjectRemoved:*";
				break;
			case OBJECT_REMOVED_DELETE:
				eventTypeStr = "s3:ObjectRemoved:Delete";
				break;
			case OBJECT_REMOVED_DELETE_MARKER_CREATED:
				eventTypeStr = "s3:ObjectRemoved:DeleteMarkerCreated";
				break;
			default:
				break;
			}
		}
		return eventTypeStr;
	}

	@Override
	public String transStorageClass(StorageClassEnum storageClass) {
		String storageClassStr = "";
		if(storageClass != null) {
			switch (storageClass) {
			case STANDARD:
				storageClassStr = "STANDARD";
				break;
			case WARM:
				storageClassStr = "STANDARD_IA";
				break;
			case COLD:
				storageClassStr = "GLACIER";
				break;
			default:
				break;
			}
		}
		return storageClassStr;
	}
	
	@Override
	public String transBucketDirectColdAccess(BucketDirectColdAccess access) throws ServiceException {
		try {
			XMLBuilder builder = XMLBuilder.create("DirectColdAccessConfiguration");
			
			builder = builder.e("Status").t(access.getStatus().getCode());
			builder = builder.up();
			
			return builder.up().asString();
		} catch (Exception e) {
			throw new ServiceException("Failed to build XML document for Tagging", e);
		}
	}

	@Override
	public AccessControlList transCannedAcl(String cannedAcl) {
		if(Constants.ACL_PRIVATE.equals(cannedAcl)) {
			return AccessControlList.REST_CANNED_PRIVATE;
		}else if(Constants.ACL_PUBLIC_READ.equals(cannedAcl)) {
			return AccessControlList.REST_CANNED_PUBLIC_READ;
		}else if(Constants.ACL_PUBLIC_READ_WRITE.equals(cannedAcl)) {
			return AccessControlList.REST_CANNED_PUBLIC_READ_WRITE;
		}else if(Constants.ACL_PUBLIC_READ_DELIVERED.equals(cannedAcl)) {
			return AccessControlList.REST_CANNED_PUBLIC_READ;
		}else if(Constants.ACL_PUBLIC_READ_WRITE_DELIVERED.equals(cannedAcl)) {
			return AccessControlList.REST_CANNED_PUBLIC_READ_WRITE;
		}else if(Constants.ACL_AUTHENTICATED_READ.equals(cannedAcl)) {
			return AccessControlList.REST_CANNED_AUTHENTICATED_READ;
		}else if(Constants.ACL_BUCKET_OWNER_READ.equals(cannedAcl)) {
			return AccessControlList.REST_CANNED_BUCKET_OWNER_READ;
		}else if(Constants.ACL_BUCKET_OWNER_FULL_CONTROL.equals(cannedAcl)) {
			return AccessControlList.REST_CANNED_BUCKET_OWNER_FULL_CONTROL;
		}else if(Constants.ACL_LOG_DELIVERY_WRITE.equals(cannedAcl)) {
			return AccessControlList.REST_CANNED_LOG_DELIVERY_WRITE;
		}
		return null;
	}

	@Override
	public String transGroupGrantee(GroupGranteeEnum groupGrantee) {
		String groupGranteeStr = "";
		if(groupGrantee != null) {
			switch (groupGrantee) {
			case ALL_USERS:
				groupGranteeStr = Constants.ALL_USERS_URI;
				break;
			case AUTHENTICATED_USERS:
				groupGranteeStr = Constants.AUTHENTICATED_USERS_URI;
				break;
			case LOG_DELIVERY:
				groupGranteeStr = Constants.LOG_DELIVERY_URI;
				break;
			default:
				break;
			}
		}
		return groupGranteeStr;
	}

	
}
