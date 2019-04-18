package com.obs.services.internal;

import com.jamesmurty.utils.XMLBuilder;
import com.obs.services.internal.utils.ServiceUtils;
import com.obs.services.model.AccessControlList;
import com.obs.services.model.BucketLoggingConfiguration;
import com.obs.services.model.BucketNotificationConfiguration;
import com.obs.services.model.BucketStoragePolicyConfiguration;
import com.obs.services.model.CanonicalGrantee;
import com.obs.services.model.EventTypeEnum;
import com.obs.services.model.GrantAndPermission;
import com.obs.services.model.GranteeInterface;
import com.obs.services.model.GroupGrantee;
import com.obs.services.model.GroupGranteeEnum;
import com.obs.services.model.Owner;
import com.obs.services.model.Permission;
import com.obs.services.model.ReplicationConfiguration;
import com.obs.services.model.RestoreObjectRequest;
import com.obs.services.model.StorageClassEnum;
import com.obs.services.model.TopicConfiguration;

public class ObsConvertor extends V2Convertor {
	
	private static ObsConvertor instance = new ObsConvertor();
	
	ObsConvertor() {
		
	}
	
	public static IConvertor getInstance() {
		return instance;
	}
	
	@Override
	public String transBucketLoction(String location) throws ServiceException {
		try {
			XMLBuilder builder = XMLBuilder.create("CreateBucketConfiguration")
					.elem("Location").text(ServiceUtils.toValid(location));
			return builder.asString();
		} catch (Exception e) {
			throw new ServiceException(e);
		}
	}

	@Override
	public String transRestoreObjectRequest(RestoreObjectRequest req) throws ServiceException {
		
		try {
			XMLBuilder builder = XMLBuilder.create("RestoreRequest").elem("Days").t(String.valueOf(req.getDays())).up();
			switch (req.getRestoreTier()) {
			case EXPEDITED:
			case STANDARD:	
				builder.e("RestoreJob").e("Tier").t(req.getRestoreTier().getCode());
				break;
			default:
				break;
			}
			return builder.asString();
		} catch (Exception e) {
			throw new ServiceException("Failed to build XML document for restoreobject", e);
		}
		
	}


	@Override
	public String transStoragePolicy(BucketStoragePolicyConfiguration status) throws ServiceException {
		try {
			XMLBuilder builder = XMLBuilder.create("StorageClass")
					.text(this.transStorageClass(status.getBucketStorageClass()));
			return builder.asString();
		} catch (Exception e) {
			throw new ServiceException("Failed to build XML document for StorageClass", e);
		}
	}

	@Override
	public String transBucketLoggingConfiguration(BucketLoggingConfiguration c) throws ServiceException {
		try {
			XMLBuilder builder = XMLBuilder.create("BucketLoggingStatus");
			if(c.getAgency() != null) {
				builder.e("Agency").t(ServiceUtils.toValid(c.getAgency()));
			}
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
								subBuilder = XMLBuilder.create("Grantee").element("ID").text(ServiceUtils.toValid(grantee.getIdentifier()));
							} else if (grantee instanceof GroupGrantee) {
								subBuilder = XMLBuilder.create("Grantee").element("Canned").text(this.transGroupGrantee(((GroupGrantee)grantee).getGroupGranteeType()));
							}
							grantsBuilder.elem("Grant").importXMLBuilder(subBuilder).elem("Permission")
							.text(ServiceUtils.toValid(permission.getPermissionString())).up();
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
	public String transAccessControlList(AccessControlList acl, boolean isBucket) throws ServiceException {
		Owner owner = acl.getOwner();
		GrantAndPermission[] grants = acl.getGrantAndPermissions();
		try {
			XMLBuilder builder = XMLBuilder.create("AccessControlPolicy");
			if (owner != null) {
				builder.elem("Owner").elem("ID").text(ServiceUtils.toValid(owner.getId()));
			}
			if(!isBucket) {
				builder.elem("Delivered").text(String.valueOf(acl.isDelivered()));
			}
			
			if(grants.length > 0) {
				XMLBuilder accessControlList = builder.elem("AccessControlList");				
				for (GrantAndPermission gap : grants) {
					GranteeInterface grantee = gap.getGrantee();
					Permission permission = gap.getPermission();
					
					XMLBuilder subBuilder = null;
					if (grantee instanceof CanonicalGrantee) {
						subBuilder = XMLBuilder.create("Grantee").element("ID").text(ServiceUtils.toValid(grantee.getIdentifier()));
					} else if (grantee instanceof GroupGrantee) {
						subBuilder = XMLBuilder.create("Grantee").element("Canned").text(this.transGroupGrantee(((GroupGrantee)grantee).getGroupGranteeType()));
					}else if(grantee != null) {
						subBuilder = XMLBuilder.create("Grantee").element("ID").text(ServiceUtils.toValid(grantee.getIdentifier()));
					}
					XMLBuilder grantBuilder = accessControlList.elem("Grant").importXMLBuilder(subBuilder);
					if(permission != null) {
						grantBuilder.elem("Permission").text(ServiceUtils.toValid(permission.getPermissionString()));
					}
					if(isBucket) {
						grantBuilder.e("Delivered").t(String.valueOf(gap.isDelivered()));
					}
				}
			}
			
			return builder.asString();
		} catch (Exception e) {
			throw new ServiceException("Failed to build XML document for ACL", e);
		} 
	}


	@Override
	public String transBucketNotificationConfiguration(BucketNotificationConfiguration bucketNotificationConfiguration)
			throws ServiceException {
		
		try {
			XMLBuilder builder = XMLBuilder.create("NotificationConfiguration");
			if (bucketNotificationConfiguration == null
					|| bucketNotificationConfiguration.getTopicConfigurations().isEmpty()) {
				return builder.asString();
			}
	
			for (TopicConfiguration config : bucketNotificationConfiguration.getTopicConfigurations()) {
				builder = builder.e("TopicConfiguration");
				if (config.getId() != null) {
					builder.e("Id").t(config.getId());
				}
				if (config.getFilter() != null && !config.getFilter().getFilterRules().isEmpty()) {
					builder = builder.e("Filter").e("Object");
					for (TopicConfiguration.Filter.FilterRule rule : config.getFilter().getFilterRules()) {
						if (rule != null) {
							builder.e("FilterRule").e("Name").t(ServiceUtils.toValid(rule.getName())).up()
							.e("Value").t(ServiceUtils.toValid(rule.getValue()));
						}
					}
					builder = builder.up().up();
				}
	
				if (config.getTopic() != null) {
					builder.e("Topic").t(config.getTopic());
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
	
			return builder.asString();
		} catch (Exception e) {
			throw new ServiceException("Failed to build XML document for Notification", e);
		}
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
					builder = builder.e("Destination").e("Bucket").t(bucketName).up();
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
	public String transEventType(EventTypeEnum eventType) {
		return transEventTypeStatic(eventType);
	}
	
	public static String transEventTypeStatic(EventTypeEnum eventType) {
		String eventTypeStr = "";
		if(eventType != null) {
			switch (eventType) {
			case OBJECT_CREATED_ALL:
				eventTypeStr = "ObjectCreated:*";
				break;
			case OBJECT_CREATED_PUT:
				eventTypeStr = "ObjectCreated:Put";
				break;
			case OBJECT_CREATED_POST:
				eventTypeStr = "ObjectCreated:Post";
				break;
			case OBJECT_CREATED_COPY:
				eventTypeStr = "ObjectCreated:Copy";
				break;
			case OBJECT_CREATED_COMPLETE_MULTIPART_UPLOAD:
				eventTypeStr = "ObjectCreated:CompleteMultipartUpload";
				break;
			case OBJECT_REMOVED_ALL:
				eventTypeStr = "ObjectRemoved:*";
				break;
			case OBJECT_REMOVED_DELETE:
				eventTypeStr = "ObjectRemoved:Delete";
				break;
			case OBJECT_REMOVED_DELETE_MARKER_CREATED:
				eventTypeStr = "ObjectRemoved:DeleteMarkerCreated";
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
				storageClassStr = "WARM";
				break;
			case COLD:
				storageClassStr = "COLD";
				break;
			default:
				break;
			}
		}
		return storageClassStr;
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
			return AccessControlList.REST_CANNED_PUBLIC_READ_DELIVERED;
		}else if(Constants.ACL_PUBLIC_READ_WRITE_DELIVERED.equals(cannedAcl)) {
			return AccessControlList.REST_CANNED_PUBLIC_READ_WRITE_DELIVERED;
		}
		return null;
	}

	@Override
	public String transGroupGrantee(GroupGranteeEnum groupGrantee) {
		String groupGranteeStr = "";
		if(groupGrantee != null) {
			switch (groupGrantee) {
			case ALL_USERS:
				groupGranteeStr = "Everyone";
				break;
			default:
				break;
			}
		}
		return groupGranteeStr;
	}
}
