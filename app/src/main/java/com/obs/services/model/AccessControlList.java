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

package com.obs.services.model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 桶或对象的访问权限（Access Control List， ACL）， 包含了一组为指定被授权者（
 * {@link com.obs.services.model.GranteeInterface}） 分配特定权限（
 * {@link com.obs.services.model.Permission}）的集合。
 */
public class AccessControlList extends HeaderResponse {
    /**
     * 预定义访问策略 私有读写（private）
     */
    public static final AccessControlList REST_CANNED_PRIVATE = new AccessControlList();

    /**
     * 预定义访问策略 公共读私有写（public-read）
     */
    public static final AccessControlList REST_CANNED_PUBLIC_READ = new AccessControlList();

    /**
     * 预定义访问策略 公共读写（ public-read-write）
     */
    public static final AccessControlList REST_CANNED_PUBLIC_READ_WRITE = new AccessControlList();

    /**
     * 预定义访问策略 桶公共读，桶内对象公共读（public-read-delivered）
     */
    public static final AccessControlList REST_CANNED_PUBLIC_READ_DELIVERED = new AccessControlList();

    /**
     * 预定义访问策略 桶公共读写，桶内对象公共读写（public-read-write-delivered）
     */
    public static final AccessControlList REST_CANNED_PUBLIC_READ_WRITE_DELIVERED = new AccessControlList();

    /**
     * 预定义访问策略 授权用户读私有写（authenticated-read）
     */
    @Deprecated
    public static final AccessControlList REST_CANNED_AUTHENTICATED_READ = new AccessControlList();

    /**
     * 预定义访问策略 桶所有者读对象所有者读写（ bucket-owner-read）
     */
    @Deprecated
    public static final AccessControlList REST_CANNED_BUCKET_OWNER_READ = new AccessControlList();

    /**
     * 预定义访问策略 桶所有者读写对象所有者读写（ bucket-owner-full-control）
     */
    @Deprecated
    public static final AccessControlList REST_CANNED_BUCKET_OWNER_FULL_CONTROL = new AccessControlList();

    /**
     * 预定义访问策略 日志投递组写（ log-delivery-write）
     */
    @Deprecated
    public static final AccessControlList REST_CANNED_LOG_DELIVERY_WRITE = new AccessControlList();

    private Set<GrantAndPermission> grants;

    private Owner owner;

    private boolean delivered;

    /**
     * 获取对象的ACL传递标识
     * 
     * @return ACL传递标识
     */
    public boolean isDelivered() {
        return delivered;
    }

    /**
     * 设置对象的ACL传递标识，只对对象权限有效
     * 
     * @param delivered
     *            ACL传递标识
     */
    public void setDelivered(boolean delivered) {
        this.delivered = delivered;
    }

    /**
     * 获取所有者
     * 
     * @return 所有者
     */
    public Owner getOwner() {
        return owner;
    }

    /**
     * 设置所有者
     * 
     * @param owner
     *            所有者
     */
    public void setOwner(Owner owner) {
        this.owner = owner;
    }

    /**
     * 获取ACL中的所有权限
     * 
     * @return 所有的权限组
     */
    public Set<GrantAndPermission> getGrants() {
        if (grants == null) {
            grants = new HashSet<GrantAndPermission>();
        }
        return grants;
    }

    /**
     * 获取ACL中指定{@link com.obs.services.model.GranteeInterface}的权限
     * 
     * @param grantee
     *            被授权者
     * @return {@link com.obs.services.model.GranteeInterface}的权限列表
     */
    public List<Permission> getPermissionsForGrantee(GranteeInterface grantee) {
        List<Permission> permissions = new ArrayList<Permission>();
        for (GrantAndPermission gap : getGrants()) {
            if (gap.getGrantee().equals(grantee)) {
                permissions.add(gap.getPermission());
            }
        }
        return permissions;
    }

    /**
     * 为ACL中指定{@link com.obs.services.model.GranteeInterface}授权特定权限
     * {@link com.obs.services.model.Permission}。
     * 
     * @param grantee
     *            被授权者
     * @param permission
     *            {@link com.obs.services.model.Permission}中定义的权限
     * @return 权限信息
     */
    public GrantAndPermission grantPermission(GranteeInterface grantee, Permission permission) {
        return grantPermission(grantee, permission, false);
    }

    /**
     * 为ACL中指定{@link com.obs.services.model.GranteeInterface}授权特定权限
     * {@link com.obs.services.model.Permission}。
     *
     * @param grantee
     *            被授权者
     * @param permission
     *            {@link com.obs.services.model.Permission}中定义的权限
     * @param delivered
     *            桶的ACL是否向桶内对象传递
     * @return 权限信息
     */
    public GrantAndPermission grantPermission(GranteeInterface grantee, Permission permission, boolean delivered) {
        GrantAndPermission obj = new GrantAndPermission(grantee, permission);
        obj.setDelivered(delivered);
        getGrants().add(obj);
        return obj;
    }

    /**
     * 为ACL添加权限组
     * 
     * @param grantAndPermissions
     *            权限组
     */
    public void grantAllPermissions(GrantAndPermission[] grantAndPermissions) {
        for (int i = 0; i < grantAndPermissions.length; i++) {
            GrantAndPermission gap = grantAndPermissions[i];
            grantPermission(gap.getGrantee(), gap.getPermission(), gap.isDelivered());
        }
    }

    /**
     * 获取ACL中的所有权限
     * 
     * @return 所有的权限组
     */
    public GrantAndPermission[] getGrantAndPermissions() {
        return getGrants().toArray(new GrantAndPermission[getGrants().size()]);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("[");
        for (GrantAndPermission item : getGrantAndPermissions()) {
            sb.append(item.toString()).append(",");
        }
        sb.append("]");
        return "AccessControlList [owner=" + owner + ", grants=" + sb.toString() + "]";
    }

}
