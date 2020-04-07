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

package com.obs.services;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import com.obs.services.exception.ObsException;
import com.obs.services.model.AbortMultipartUploadRequest;
import com.obs.services.model.AccessControlList;
import com.obs.services.model.AppendObjectRequest;
import com.obs.services.model.AppendObjectResult;
import com.obs.services.model.BaseBucketRequest;
import com.obs.services.model.BucketCors;
import com.obs.services.model.BucketDirectColdAccess;
import com.obs.services.model.BucketEncryption;
import com.obs.services.model.BucketLocationResponse;
import com.obs.services.model.BucketLoggingConfiguration;
import com.obs.services.model.BucketMetadataInfoRequest;
import com.obs.services.model.BucketMetadataInfoResult;
import com.obs.services.model.BucketNotificationConfiguration;
import com.obs.services.model.BucketPolicyResponse;
import com.obs.services.model.BucketQuota;
import com.obs.services.model.BucketStorageInfo;
import com.obs.services.model.BucketStoragePolicyConfiguration;
import com.obs.services.model.BucketTagInfo;
import com.obs.services.model.BucketVersioningConfiguration;
import com.obs.services.model.CompleteMultipartUploadRequest;
import com.obs.services.model.CompleteMultipartUploadResult;
import com.obs.services.model.CopyObjectRequest;
import com.obs.services.model.CopyObjectResult;
import com.obs.services.model.CopyPartRequest;
import com.obs.services.model.CopyPartResult;
import com.obs.services.model.CreateBucketRequest;
import com.obs.services.model.DeleteObjectRequest;
import com.obs.services.model.DeleteObjectResult;
import com.obs.services.model.DeleteObjectsRequest;
import com.obs.services.model.DeleteObjectsResult;
import com.obs.services.model.DownloadFileRequest;
import com.obs.services.model.DownloadFileResult;
import com.obs.services.model.GetObjectAclRequest;
import com.obs.services.model.GetObjectMetadataRequest;
import com.obs.services.model.GetObjectRequest;
import com.obs.services.model.HeaderResponse;
import com.obs.services.model.InitiateMultipartUploadRequest;
import com.obs.services.model.InitiateMultipartUploadResult;
import com.obs.services.model.LifecycleConfiguration;
import com.obs.services.model.ListBucketsRequest;
import com.obs.services.model.ListBucketsResult;
import com.obs.services.model.ListMultipartUploadsRequest;
import com.obs.services.model.ListObjectsRequest;
import com.obs.services.model.ListPartsRequest;
import com.obs.services.model.ListPartsResult;
import com.obs.services.model.ListVersionsRequest;
import com.obs.services.model.ListVersionsResult;
import com.obs.services.model.ModifyObjectRequest;
import com.obs.services.model.ModifyObjectResult;
import com.obs.services.model.MultipartUploadListing;
import com.obs.services.model.ObjectListing;
import com.obs.services.model.ObjectMetadata;
import com.obs.services.model.ObsBucket;
import com.obs.services.model.ObsObject;
import com.obs.services.model.PostSignatureRequest;
import com.obs.services.model.PostSignatureResponse;
import com.obs.services.model.PutObjectRequest;
import com.obs.services.model.PutObjectResult;
import com.obs.services.model.PutObjectsRequest;
import com.obs.services.model.ReadAheadQueryResult;
import com.obs.services.model.ReadAheadRequest;
import com.obs.services.model.ReadAheadResult;
import com.obs.services.model.RenameObjectRequest;
import com.obs.services.model.RenameObjectResult;
import com.obs.services.model.ReplicationConfiguration;
import com.obs.services.model.RequestPaymentConfiguration;
import com.obs.services.model.RequestPaymentEnum;
import com.obs.services.model.RestoreObjectRequest;
import com.obs.services.model.RestoreObjectRequest.RestoreObjectStatus;
import com.obs.services.model.RestoreObjectResult;
import com.obs.services.model.RestoreObjectsRequest;
import com.obs.services.model.SetBucketAclRequest;
import com.obs.services.model.SetBucketCorsRequest;
import com.obs.services.model.SetBucketDirectColdAccessRequest;
import com.obs.services.model.SetBucketEncryptionRequest;
import com.obs.services.model.SetBucketLifecycleRequest;
import com.obs.services.model.SetBucketLoggingRequest;
import com.obs.services.model.SetBucketNotificationRequest;
import com.obs.services.model.SetBucketPolicyRequest;
import com.obs.services.model.SetBucketQuotaRequest;
import com.obs.services.model.SetBucketReplicationRequest;
import com.obs.services.model.SetBucketRequestPaymentRequest;
import com.obs.services.model.SetBucketStoragePolicyRequest;
import com.obs.services.model.SetBucketTaggingRequest;
import com.obs.services.model.SetBucketVersioningRequest;
import com.obs.services.model.SetBucketWebsiteRequest;
import com.obs.services.model.SetObjectAclRequest;
import com.obs.services.model.SetObjectMetadataRequest;
import com.obs.services.model.TaskProgressStatus;
import com.obs.services.model.TemporarySignatureRequest;
import com.obs.services.model.TemporarySignatureResponse;
import com.obs.services.model.TruncateObjectRequest;
import com.obs.services.model.TruncateObjectResult;
import com.obs.services.model.UploadFileRequest;
import com.obs.services.model.UploadPartRequest;
import com.obs.services.model.UploadPartResult;
import com.obs.services.model.UploadProgressStatus;
import com.obs.services.model.WebsiteConfiguration;

/**
 * OBS基础接口
 */
public interface IObsClient {

    /**
     * 
     * 刷新临时访问密钥
     * 
     * @param accessKey
     *            临时访问密钥中的AK
     * @param secretKey
     *            临时访问密钥中的SK
     * @param securityToken
     *            安全令牌
     * 
     */
    void refresh(String accessKey, String secretKey, String securityToken);

    /**
     * 生成临时授权访问参数
     * 
     * @param request
     *            临时授权访问的请求参数
     * @return 临时授权访问的响应结果
     * @throws ObsException
     *             OBS SDK自定义异常，当调用接口失败、访问OBS失败时抛出该异常
     */
    TemporarySignatureResponse createTemporarySignature(TemporarySignatureRequest request);

    /**
     * 生成基于浏览器表单的授权访问参数
     * 
     * @param request
     *            基于V4的浏览器表单授权访问请求参数
     * @return 基于V4的浏览器表单授权访问的响应结果
     * @throws ObsException
     *             OBS SDK自定义异常，当调用接口失败、访问OBS失败时抛出该异常
     */
    PostSignatureResponse createPostSignature(PostSignatureRequest request) throws ObsException;

    /**
     * 创建桶 <br>
     * <p>
     * <b>桶命名规范：</b>
     * </p>
     * <ul>
     * <li>只能包含小写字母、数字、"-"、"."。
     * <li>只能以数字或字母开头。
     * <li>长度要求不少于3个字符，并且不能超过63个字符。
     * <li>不能是IP地址。
     * <li>不能以"-"结尾。
     * <li>不可以包括有两个相邻的"."。
     * <li>"."和"-"不能相邻，如"my-.bucket"和"my.-bucket "都是非法的。
     * </ul>
     * 
     * @param bucketName
     *            桶名
     * @return 桶信息
     * @throws ObsException
     *             OBS SDK自定义异常，当调用接口失败、访问OBS失败时抛出该异常
     */
    ObsBucket createBucket(String bucketName) throws ObsException;

    /**
     * 创建桶 <br>
     * 按照用户指定的桶名和指定的区域创建一个新桶。
     * <p>
     * <b>桶命名规范：</b>
     * </p>
     * <ul>
     * <li>只能包含小写字母、数字、"-"、"."。
     * <li>只能以数字或字母开头。
     * <li>长度要求不少于3个字符，并且不能超过63个字符。
     * <li>不能是IP地址。
     * <li>不能以"-"结尾。
     * <li>不可以包括有两个相邻的"."。
     * <li>"."和"-"不能相邻，如"my-.bucket"和"my.-bucket "都是非法的。
     * </ul>
     * 
     * 
     * @param bucketName
     *            桶名
     * @param location
     *            创建桶的区域， 如果使用的终端节点归属于默认区域，可以不携带此参数；如果使用的终端节点归属于其他区域，则必须携带此参数
     * @return 桶信息
     * @throws ObsException
     *             OBS SDK自定义异常，当调用接口失败、访问OBS失败时抛出该异常
     */
    ObsBucket createBucket(String bucketName, String location) throws ObsException;

    /**
     * 创建桶<br>
     * 按照用户指定的桶名和指定的区域创建一个新桶。
     * <p>
     * <b>桶命名规范：</b>
     * </p>
     * <ul>
     * <li>只能包含小写字母、数字、"-"、"."。
     * <li>只能以数字或字母开头。
     * <li>长度要求不少于3个字符，并且不能超过63个字符。
     * <li>不能是IP地址。
     * <li>不能以"-"结尾。
     * <li>不可以包括有两个相邻的"."。
     * <li>"."和"-"不能相邻，如"my-.bucket"和"my.-bucket "都是非法的。
     * </ul>
     * 
     * @param bucket
     *            桶信息，包含请求参数
     * @return 桶信息
     * @throws ObsException
     *             OBS SDK自定义异常，当调用接口失败、访问OBS失败时抛出该异常
     */
    ObsBucket createBucket(ObsBucket bucket) throws ObsException;

    /**
     * 创建桶<br>
     * <p>
     * <b>桶命名规范：</b>
     * </p>
     * <ul>
     * <li>只能包含小写字母、数字、"-"、"."。
     * <li>只能以数字或字母开头。
     * <li>长度要求不少于3个字符，并且不能超过63个字符。
     * <li>不能是IP地址。
     * <li>不能以"-"结尾。
     * <li>不可以包括有两个相邻的"."。
     * <li>"."和"-"不能相邻，如"my-.bucket"和"my.-bucket "都是非法的。
     * </ul>
     * 
     * @param request
     *            创建桶请求参数
     * @return 桶信息
     * @throws ObsException
     *             OBS SDK自定义异常，当调用接口失败、访问OBS失败时抛出该异常
     * 
     */
    ObsBucket createBucket(CreateBucketRequest request) throws ObsException;

    /**
     * 重命名文件/目录，只有并行文件系统支持该接口
     * 
     * @param bucketName
     *            桶名
     * @param objectKey
     *            文件名或目录名
     * @param newObjectKey
     *            重命名后的文件名或目录名
     * @return 重命名响应结果
     * @throws ObsException
     *             OBS SDK自定义异常，当调用接口失败、访问OBS失败时抛出该异常
     */
    RenameObjectResult renameObject(String bucketName, String objectKey, String newObjectKey) throws ObsException;

    /**
     * 重命名文件/目录，只有并行文件系统支持该接口
     * 
     * @param request
     *            重命名请求参数
     * @return 重命名响应结果
     * @throws ObsException
     *             OBS SDK自定义异常，当调用接口失败、访问OBS失败时抛出该异常
     * @since 3.20.3
     */
    RenameObjectResult renameObject(final RenameObjectRequest request) throws ObsException;

    /**
     * 截断文件，只有并行文件系统支持该接口
     * 
     * @param bucketName
     *            桶名
     * @param objectKey
     *            文件名
     * @param newLength
     *            截断后的文件大小
     * @return 截断文件响应结果
     * @throws ObsException
     *             OBS SDK自定义异常，当调用接口失败、访问OBS失败时抛出该异常
     */
    TruncateObjectResult truncateObject(String bucketName, String objectKey, long newLength) throws ObsException;

    /**
     * 截断文件，只有并行文件系统支持该接口
     * 
     * @param request
     *            截断请求参数
     * @return 截断文件响应结果
     * @throws ObsException
     *             OBS SDK自定义异常，当调用接口失败、访问OBS失败时抛出该异常
     * @since 3.20.3
     */
    TruncateObjectResult truncateObject(final TruncateObjectRequest request) throws ObsException;

    /**
     * 修改写文件内容，只有并行文件系统支持该接口
     * 
     * @param bucketName
     *            桶名
     * @param objectKey
     *            文件名
     * @param position
     *            写文件的起始位置
     * @param file
     *            本地文件路径
     * @return 代表支持文件接口的桶中的文件
     * @throws ObsException
     *             OBS SDK自定义异常，当调用接口失败、访问OBS失败时抛出该异常
     */
    ModifyObjectResult modifyObject(String bucketName, String objectKey, long position, File file) throws ObsException;

    /**
     * 修改写文件内容，只有并行文件系统支持该接口
     * 
     * @param bucketName
     *            桶名
     * @param objectKey
     *            文件名
     * @param position
     *            写文件的起始位置
     * @param input
     *            待上传的数据流
     * @return 代表支持文件接口的桶中的文件
     * @throws ObsException
     *             OBS SDK自定义异常，当调用接口失败、访问OBS失败时抛出该异常
     */
    ModifyObjectResult modifyObject(String bucketName, String objectKey, long position, InputStream input)
            throws ObsException;

    /**
     * 修改写文件内容，只有并行文件系统支持该接口
     * 
     * @param request
     *            写文件内容的请求参数
     * @return 代表支持文件接口的桶中的文件
     * @throws ObsException
     *             OBS SDK自定义异常，当调用接口失败、访问OBS失败时抛出该异常
     */
    ModifyObjectResult modifyObject(ModifyObjectRequest request) throws ObsException;

    /**
     * 获取桶列表
     * 
     * @param request
     *            获取桶列表请求参数
     * @return 桶列表
     * @throws ObsException
     *             OBS SDK自定义异常，当调用接口失败、访问OBS失败时抛出该异常
     */
    List<ObsBucket> listBuckets(ListBucketsRequest request) throws ObsException;

    /**
     * 获取桶列表
     * 
     * @param request
     *            获取桶列表请求参数
     * @return 获取桶列表响应结果
     * @throws ObsException
     *             OBS SDK自定义异常，当调用接口失败、访问OBS失败时抛出该异常
     */
    ListBucketsResult listBucketsV2(ListBucketsRequest request) throws ObsException;

    /**
     * 删除桶
     * 
     * @param bucketName
     *            桶名
     * @return 公共响应头信息
     * @throws ObsException
     *             OBS SDK自定义异常，当调用接口失败、访问OBS失败时抛出该异常
     */
    HeaderResponse deleteBucket(String bucketName) throws ObsException;

    /**
     * 删除桶
     * 
     * @param request
     *            删除桶的请求参数
     * @return 公共响应头信息
     * @throws ObsException
     *             OBS SDK自定义异常，当调用接口失败、访问OBS失败时抛出该异常
     * @since 3.20.3
     */
    HeaderResponse deleteBucket(BaseBucketRequest request) throws ObsException;

    /**
     * 列举桶内的对象
     * 
     * @param request
     *            列举桶内对象请求参数
     * @return 列举桶内对象响应结果
     * @throws ObsException
     *             OBS SDK自定义异常，当调用接口失败、访问OBS失败时抛出该异常
     */
    ObjectListing listObjects(ListObjectsRequest request) throws ObsException;

    /**
     * 列举桶内的对象
     * 
     * @param bucketName
     *            桶名
     * @return 列举桶内对象响应结果
     * @throws ObsException
     *             OBS SDK自定义异常，当调用接口失败、访问OBS失败时抛出该异常
     */
    ObjectListing listObjects(String bucketName) throws ObsException;

    /**
     * 判断桶是否存在
     * 
     * @param bucketName
     *            桶名
     * @return 桶是否存在标识
     * @throws ObsException
     *             OBS SDK自定义异常，当调用接口失败、访问OBS失败时抛出该异常
     */
    boolean headBucket(String bucketName) throws ObsException;

    /**
     * 判断桶是否存在
     * 
     * @param request
     *            请求参数
     * @return 桶是否存在标识
     * @throws ObsException
     *             OBS SDK自定义异常，当调用接口失败、访问OBS失败时抛出该异常
     * @since 3.20.3
     */
    boolean headBucket(BaseBucketRequest request) throws ObsException;

    /**
     * 列举桶内多版本对象
     * 
     * @param request
     *            列举桶内多版本对象请求参数
     * @return 列举桶内多版本对象响应结果
     * @throws ObsException
     *             OBS SDK自定义异常，当调用接口失败、访问OBS失败时抛出该异常
     */
    ListVersionsResult listVersions(ListVersionsRequest request) throws ObsException;

    /**
     * 列举桶内多版本对象
     * 
     * @param bucketName
     *            桶名
     * @return 列举桶内多版本对象响应结果
     * @throws ObsException
     *             OBS SDK自定义异常，当调用接口失败、访问OBS失败时抛出该异常
     */
    ListVersionsResult listVersions(String bucketName) throws ObsException;

    /**
     * 列举桶内多版本对象
     * 
     * @param bucketName
     *            桶名
     * @param maxKeys
     *            列举多版本对象的最大条目数
     * @return 列举桶内多版本对象响应结果
     * @throws ObsException
     *             OBS SDK自定义异常，当调用接口失败、访问OBS失败时抛出该异常
     */
    ListVersionsResult listVersions(String bucketName, long maxKeys) throws ObsException;

    /**
     * 列举桶内多版本对象
     * 
     * @param bucketName
     *            桶名
     * @param prefix
     *            列举多版本对象时的对象名前缀
     * @param delimiter
     *            对象名进行分组的字符
     * @param keyMarker
     *            列举多版本对象的起始位置（按对象名排序）
     * @param versionIdMarker
     *            列举多版本对象的起始位置（按对象版本号排序）
     * @param maxKeys
     *            列举多版本对象的最大条目数
     * @return 列举桶内多版本对象响应结果
     * @throws ObsException
     *             OBS SDK自定义异常，当调用接口失败、访问OBS失败时抛出该异常
     */
    ListVersionsResult listVersions(String bucketName, String prefix, String delimiter, String keyMarker,
            String versionIdMarker, long maxKeys) throws ObsException;

    /**
     * 获取桶元数据
     * 
     * @param request
     *            获取桶元数据的请求参数
     * @return 获取桶元数据的响应结果
     * @throws ObsException
     *             OBS SDK自定义异常，当调用接口失败、访问OBS失败时抛出该异常
     */
    BucketMetadataInfoResult getBucketMetadata(BucketMetadataInfoRequest request) throws ObsException;

    /**
     * 获取桶访问权限
     * 
     * @param bucketName
     *            桶名
     * @return 桶的访问权限
     * @throws ObsException
     *             OBS SDK自定义异常，当调用接口失败、访问OBS失败时抛出该异常
     */
    AccessControlList getBucketAcl(String bucketName) throws ObsException;

    /**
     * 获取桶访问权限
     * 
     * @param request
     *            获取桶Acl的请求参数
     * @return 获取桶Acl的响应结果
     * @throws ObsException
     *             OBS SDK自定义异常，当调用接口失败、访问OBS失败时抛出该异常
     * @since 3.20.3
     */
    AccessControlList getBucketAcl(BaseBucketRequest request) throws ObsException;

    /**
     * 设置桶的访问权限<br>
     * 
     * @param bucketName
     *            桶名
     * @param acl
     *            访问权限
     * @return 公共响应头信息
     * @throws ObsException
     *             OBS SDK自定义异常，当调用接口失败、访问OBS失败时抛出该异常
     */
    HeaderResponse setBucketAcl(String bucketName, AccessControlList acl) throws ObsException;

    /**
     * 设置桶的访问权限<br>
     * 
     * @param request
     *            设置桶acl的请求参数
     * @return 公共响应头信息
     * @throws ObsException
     *             OBS SDK自定义异常，当调用接口失败、访问OBS失败时抛出该异常
     * @since 3.20.3
     */
    HeaderResponse setBucketAcl(SetBucketAclRequest request) throws ObsException;

    /**
     * 获取桶区域位置
     * 
     * @param bucketName
     *            桶名
     * @return 桶的区域位置
     * @throws ObsException
     *             OBS SDK自定义异常，当调用接口失败、访问OBS失败时抛出该异常
     */
    String getBucketLocation(String bucketName) throws ObsException;

    /**
     * 获取桶区域位置
     * 
     * @param request
     *            请求参数
     * @return 获取桶区域位置的响应结果
     * @throws ObsException
     *             OBS SDK自定义异常，当调用接口失败、访问OBS失败时抛出该异常
     * @since 3.20.3
     */
    BucketLocationResponse getBucketLocation(BaseBucketRequest request) throws ObsException;

    /**
     * 获取桶区域位置
     * 
     * @param bucketName
     *            桶名
     * @return 获取桶区域位置的响应结果
     * @throws ObsException
     *             OBS SDK自定义异常，当调用接口失败、访问OBS失败时抛出该异常
     */
    BucketLocationResponse getBucketLocationV2(String bucketName) throws ObsException;

    /**
     * 获取桶的存量信息
     * 
     * @param bucketName
     *            桶名
     * @return 桶的存量信息
     * @throws ObsException
     *             OBS SDK自定义异常，当调用接口失败、访问OBS失败时抛出该异常
     */
    BucketStorageInfo getBucketStorageInfo(String bucketName) throws ObsException;

    /**
     * 获取桶的存量信息
     * 
     * @param request
     *            桶名
     * @return 桶的存量信息
     * @throws ObsException
     *             OBS SDK自定义异常，当调用接口失败、访问OBS失败时抛出该异常
     * @since 3.20.3
     */
    BucketStorageInfo getBucketStorageInfo(BaseBucketRequest request) throws ObsException;

    /**
     * 获取桶配额
     * 
     * @param bucketName
     *            桶名
     * @return 桶配额
     * @throws ObsException
     *             OBS SDK自定义异常，当调用接口失败、访问OBS失败时抛出该异常
     */
    BucketQuota getBucketQuota(String bucketName) throws ObsException;

    /**
     * 获取桶配额
     * 
     * @param request
     *            请求参数
     * @return 桶配额
     * @throws ObsException
     *             OBS SDK自定义异常，当调用接口失败、访问OBS失败时抛出该异常
     * @since 3.20.3
     */
    BucketQuota getBucketQuota(BaseBucketRequest request) throws ObsException;

    /**
     * 设置桶配额
     * 
     * @param bucketName
     *            桶名
     * @param bucketQuota
     *            桶配额
     * @throws ObsException
     *             OBS SDK自定义异常，当调用接口失败、访问OBS失败时抛出该异常
     * @return 公共响应头信息
     */
    HeaderResponse setBucketQuota(String bucketName, BucketQuota bucketQuota) throws ObsException;

    /**
     * 设置桶配额
     * 
     * @param request
     *            请求参数
     * @throws ObsException
     *             OBS SDK自定义异常，当调用接口失败、访问OBS失败时抛出该异常
     * @return 公共响应头信息
     * @since 3.20.3
     */
    HeaderResponse setBucketQuota(SetBucketQuotaRequest request) throws ObsException;

    /**
     * 获取桶存储类型
     * 
     * @param bucketName
     *            桶名
     * @return 桶的存储策略
     * @throws ObsException
     *             OBS SDK自定义异常，当调用接口失败、访问OBS失败时抛出该异常
     */
    BucketStoragePolicyConfiguration getBucketStoragePolicy(String bucketName) throws ObsException;

    /**
     * 获取桶存储类型
     * 
     * @param request
     *            请求参数
     * @return 桶的存储策略
     * @throws ObsException
     *             OBS SDK自定义异常，当调用接口失败、访问OBS失败时抛出该异常
     * @since 3.20.3
     */
    BucketStoragePolicyConfiguration getBucketStoragePolicy(BaseBucketRequest request) throws ObsException;

    /**
     * 设置桶存储类型
     * 
     * @param bucketName
     *            桶名
     * @param bucketStorage
     *            桶的存储策略
     * @return 公共响应头信息
     * @throws ObsException
     *             OBS SDK自定义异常，当调用接口失败、访问OBS失败时抛出该异常
     */
    HeaderResponse setBucketStoragePolicy(String bucketName, BucketStoragePolicyConfiguration bucketStorage)
            throws ObsException;

    /**
     * 设置桶存储类型
     * 
     * @param request
     *            请求参数
     * @return 公共响应头信息
     * @throws ObsException
     *             OBS SDK自定义异常，当调用接口失败、访问OBS失败时抛出该异常
     * @since 3.20.3
     */
    HeaderResponse setBucketStoragePolicy(SetBucketStoragePolicyRequest request) throws ObsException;

    /**
     * 设置桶的跨域资源共享（CORS）配置
     * 
     * @param bucketName
     *            桶名
     * @param bucketCors
     *            CORS配置
     * @return 公共响应头信息
     * @throws ObsException
     *             OBS SDK自定义异常，当调用接口失败、访问OBS失败时抛出该异常
     */
    HeaderResponse setBucketCors(String bucketName, BucketCors bucketCors) throws ObsException;

    /**
     * 设置桶的跨域资源共享（CORS）配置
     * 
     * @param request
     *            请求参数
     * @return 公共响应头信息
     * @throws ObsException
     *             OBS SDK自定义异常，当调用接口失败、访问OBS失败时抛出该异常
     * @since 3.20.3
     */
    HeaderResponse setBucketCors(SetBucketCorsRequest request) throws ObsException;

    /**
     * 获取桶的跨域资源共享（CORS）配置
     * 
     * @param bucketName
     *            桶名
     * @return 桶的CORS配置
     * @throws ObsException
     *             OBS SDK自定义异常，当调用接口失败、访问OBS失败时抛出该异常
     */
    BucketCors getBucketCors(String bucketName) throws ObsException;

    /**
     * 获取桶的跨域资源共享（CORS）配置
     * 
     * @param request
     *            请求参数
     * @return 桶的CORS配置
     * @throws ObsException
     *             OBS SDK自定义异常，当调用接口失败、访问OBS失败时抛出该异常
     * @since 3.20.3
     */
    BucketCors getBucketCors(BaseBucketRequest request) throws ObsException;

    /**
     * 删除桶的跨域资源共享（CORS）配置
     * 
     * @param bucketName
     *            桶名
     * @return 公共响应头信息
     * @throws ObsException
     *             OBS SDK自定义异常，当调用接口失败、访问OBS失败时抛出该异常
     */
    HeaderResponse deleteBucketCors(String bucketName) throws ObsException;

    /**
     * 删除桶的跨域资源共享（CORS）配置
     * 
     * @param request
     *            请求参数
     * @return 公共响应头信息
     * @throws ObsException
     *             OBS SDK自定义异常，当调用接口失败、访问OBS失败时抛出该异常
     * @since 3.20.3
     */
    HeaderResponse deleteBucketCors(BaseBucketRequest request) throws ObsException;

    /**
     * 获取桶的日志管理配置
     * 
     * @param bucketName
     *            桶名
     * @return 桶的日志管理配置
     * @throws ObsException
     *             OBS SDK自定义异常，当调用接口失败、访问OBS失败时抛出该异常
     */
    BucketLoggingConfiguration getBucketLogging(String bucketName) throws ObsException;

    /**
     * 获取桶的日志管理配置
     * 
     * @param request
     *            请求参数
     * @return 桶的日志管理配置
     * @throws ObsException
     *             OBS SDK自定义异常，当调用接口失败、访问OBS失败时抛出该异常
     * @since 3.20.3
     */
    BucketLoggingConfiguration getBucketLogging(BaseBucketRequest request) throws ObsException;

    HeaderResponse setBucketLoggingConfiguration(String bucketName, BucketLoggingConfiguration loggingConfiguration,
            boolean updateTargetACLifRequired) throws ObsException;

    /**
     * 设置桶的日志管理配置<br>
     * 
     * @param bucketName
     *            桶名
     * @param loggingConfiguration
     *            日志管理配置
     * @return 公共响应头信息
     * @throws ObsException
     *             OBS SDK自定义异常，当调用接口失败、访问OBS失败时抛出该异常
     */
    HeaderResponse setBucketLogging(String bucketName, BucketLoggingConfiguration loggingConfiguration)
            throws ObsException;

    /**
     * 设置桶的日志管理配置<br>
     * 
     * @param request
     *            请求参数
     * @return 公共响应头信息
     * @throws ObsException
     *             OBS SDK自定义异常，当调用接口失败、访问OBS失败时抛出该异常
     * @since 3.20.3
     */
    HeaderResponse setBucketLogging(SetBucketLoggingRequest request) throws ObsException;

    /**
     * 设置桶的多版本状态
     * 
     * @param bucketName
     *            桶名
     * @param versioningConfiguration
     *            桶的多版本状态配置
     * @return 公共响应头信息
     * @throws ObsException
     *             OBS SDK自定义异常，当调用接口失败、访问OBS失败时抛出该异常
     */
    HeaderResponse setBucketVersioning(String bucketName, BucketVersioningConfiguration versioningConfiguration)
            throws ObsException;

    /**
     * 设置桶的多版本状态
     * 
     * @param request
     *            请求参数
     * @return 公共响应头信息
     * @throws ObsException
     *             OBS SDK自定义异常，当调用接口失败、访问OBS失败时抛出该异常
     * @since 3.20.3
     */
    HeaderResponse setBucketVersioning(SetBucketVersioningRequest request) throws ObsException;

    /**
     * 获取桶的多版本状态
     * 
     * @param bucketName
     *            桶名
     * @return 桶的多版本状态配置
     * @throws ObsException
     *             OBS SDK自定义异常，当调用接口失败、访问OBS失败时抛出该异常
     */
    BucketVersioningConfiguration getBucketVersioning(String bucketName) throws ObsException;

    /**
     * 获取桶的多版本状态
     * 
     * @param request
     *            请求参数
     * @return 桶的多版本状态配置
     * @throws ObsException
     *             OBS SDK自定义异常，当调用接口失败、访问OBS失败时抛出该异常
     * @since 3.20.3
     */
    BucketVersioningConfiguration getBucketVersioning(BaseBucketRequest request) throws ObsException;

    /**
     * 设置桶的请求者付费状态
     * 
     * @param bucketName
     *            桶名
     * @param payer
     *            请求者付费状态
     * @return 公共的响应头信息
     * @throws ObsException
     *             OBS SDK自定义异常，当调用接口失败、访问OBS失败时抛出该异常
     */
    HeaderResponse setBucketRequestPayment(String bucketName, RequestPaymentEnum payer) throws ObsException;

    /**
     * 设置桶的请求者付费状态
     * 
     * @param request
     *            桶的请求者付费状态配置
     * @return 公共响应头信息
     * @throws ObsException
     *             OBS SDK自定义异常，当调用接口失败、访问OBS失败时抛出该异常
     * @since 3.20.3
     */
    HeaderResponse setBucketRequestPayment(SetBucketRequestPaymentRequest request) throws ObsException;

    /**
     * 获取桶的请求者付费状态
     * 
     * @param bucketName
     *            桶名
     * @return 桶的请求者付费状态配置
     * @throws ObsException
     *             OBS SDK自定义异常，当调用接口失败、访问OBS失败时抛出该异常
     * @since 3.20.3
     */
    RequestPaymentConfiguration getBucketRequestPayment(String bucketName) throws ObsException;

    /**
     * 获取桶的请求者付费状态
     * 
     * @param request
     *            基础的桶信息
     * @return 桶的请求者付费状态配置
     * @throws ObsException
     *             OBS SDK自定义异常，当调用接口失败、访问OBS失败时抛出该异常
     * @since 3.20.3
     */
    RequestPaymentConfiguration getBucketRequestPayment(BaseBucketRequest request) throws ObsException;

    /**
     * 获取桶的生命周期配置
     * 
     * @param bucketName
     *            桶名
     * @return 桶的生命周期配置
     * @throws ObsException
     *             OBS SDK自定义异常，当调用接口失败、访问OBS失败时抛出该异常
     */
    LifecycleConfiguration getBucketLifecycle(String bucketName) throws ObsException;

    /**
     * 获取桶的生命周期配置
     * 
     * @param request
     *            请求参数
     * @return 桶的生命周期配置
     * @throws ObsException
     *             OBS SDK自定义异常，当调用接口失败、访问OBS失败时抛出该异常
     * @since 3.20.3
     */
    LifecycleConfiguration getBucketLifecycle(BaseBucketRequest request) throws ObsException;

    /**
     * 设置桶的生命周期配置
     * 
     * @param bucketName
     *            桶名
     * @param lifecycleConfig
     *            桶的生命周期配置
     * @return 公共响应头信息
     * @throws ObsException
     *             OBS SDK自定义异常，当调用接口失败、访问OBS失败时抛出该异常
     */
    HeaderResponse setBucketLifecycle(String bucketName, LifecycleConfiguration lifecycleConfig) throws ObsException;

    /**
     * 设置桶的生命周期配置
     * 
     * @param request
     *            请求参数
     * @return 公共响应头信息
     * @throws ObsException
     *             OBS SDK自定义异常，当调用接口失败、访问OBS失败时抛出该异常
     * @since 3.20.3
     */
    HeaderResponse setBucketLifecycle(SetBucketLifecycleRequest request) throws ObsException;

    /**
     * 删除桶的生命周期配置
     * 
     * @param bucketName
     *            桶名
     * @return 公共响应头信息
     * @throws ObsException
     *             OBS SDK自定义异常，当调用接口失败、访问OBS失败时抛出该异常
     */
    HeaderResponse deleteBucketLifecycle(String bucketName) throws ObsException;

    /**
     * 删除桶的生命周期配置
     * 
     * @param request
     *            请求参数
     * @return 公共响应头信息
     * @throws ObsException
     *             OBS SDK自定义异常，当调用接口失败、访问OBS失败时抛出该异常
     * @since 3.20.3
     */
    HeaderResponse deleteBucketLifecycle(BaseBucketRequest request) throws ObsException;

    /**
     * 获取桶策略
     * 
     * @param bucketName
     *            桶名
     * @return 桶策略，JSON格式字符串
     * @throws ObsException
     *             OBS SDK自定义异常，当调用接口失败、访问OBS失败时抛出该异常
     */
    String getBucketPolicy(String bucketName) throws ObsException;

    /**
     * 获取桶策略
     * 
     * @param request
     *            参数
     * @return 桶策略，JSON格式字符串
     * @throws ObsException
     *             OBS SDK自定义异常，当调用接口失败、访问OBS失败时抛出该异常
     * @since 3.20.3
     */
    String getBucketPolicy(BaseBucketRequest request) throws ObsException;

    /**
     * 获取桶策略<br>
     * 
     * @param bucketName
     *            桶名
     * @return 获取桶策略的响应结果
     * @throws ObsException
     *             OBS SDK自定义异常，当调用接口失败、访问OBS失败时抛出该异常
     */
    BucketPolicyResponse getBucketPolicyV2(String bucketName) throws ObsException;

    /**
     * 获取桶策略<br>
     * 
     * @param request
     *            请求参数
     * @return 获取桶策略的响应结果
     * @throws ObsException
     *             OBS SDK自定义异常，当调用接口失败、访问OBS失败时抛出该异常
     * @since 3.20.3
     */
    BucketPolicyResponse getBucketPolicyV2(BaseBucketRequest request) throws ObsException;

    /**
     * 设置桶策略
     * 
     * @param bucketName
     *            桶名
     * @param policy
     *            桶策略，JSON格式字符串
     * @return 公共响应头信息
     * @throws ObsException
     *             OBS SDK自定义异常，当调用接口失败、访问OBS失败时抛出该异常
     */
    HeaderResponse setBucketPolicy(String bucketName, String policy) throws ObsException;

    /**
     * 设置桶策略
     * 
     * @param request
     *            请求参数
     * @return 公共响应头信息
     * @throws ObsException
     *             OBS SDK自定义异常，当调用接口失败、访问OBS失败时抛出该异常
     * @since 3.20.3
     */
    HeaderResponse setBucketPolicy(SetBucketPolicyRequest request) throws ObsException;

    /**
     * 删除桶策略
     * 
     * @param bucketName
     *            桶名
     * @return 公共响应头信息
     * @throws ObsException
     *             OBS SDK自定义异常，当调用接口失败、访问OBS失败时抛出该异常
     */
    HeaderResponse deleteBucketPolicy(String bucketName) throws ObsException;

    /**
     * 删除桶策略
     * 
     * @param request
     *            请求参数
     * @return 公共响应头信息
     * @throws ObsException
     *             OBS SDK自定义异常，当调用接口失败、访问OBS失败时抛出该异常
     * @since 3.20.3
     */
    HeaderResponse deleteBucketPolicy(BaseBucketRequest request) throws ObsException;

    /**
     * 获取桶的website（托管）配置
     * 
     * @param bucketName
     *            桶名
     * @return 桶的website（托管）配置
     * @throws ObsException
     *             OBS SDK自定义异常，当调用接口失败、访问OBS失败时抛出该异常
     */
    WebsiteConfiguration getBucketWebsite(String bucketName) throws ObsException;

    /**
     * 获取桶的website（托管）配置
     * 
     * @param request
     *            请求参数
     * @return 桶的website（托管）配置
     * @throws ObsException
     *             OBS SDK自定义异常，当调用接口失败、访问OBS失败时抛出该异常
     * @since 3.20.3
     */
    WebsiteConfiguration getBucketWebsite(BaseBucketRequest request) throws ObsException;

    /**
     * 设置桶的website（托管）配置
     * 
     * @param bucketName
     *            桶名
     * @param websiteConfig
     *            桶的website（托管）配置
     * @return 公共响应头信息
     * @throws ObsException
     *             OBS SDK自定义异常，当调用接口失败、访问OBS失败时抛出该异常
     */
    HeaderResponse setBucketWebsite(String bucketName, WebsiteConfiguration websiteConfig) throws ObsException;

    /**
     * 设置桶的website（托管）配置
     * 
     * @param request
     *            请求参数
     * @return 公共响应头信息
     * @throws ObsException
     *             OBS SDK自定义异常，当调用接口失败、访问OBS失败时抛出该异常
     * @since 3.20.3
     */
    HeaderResponse setBucketWebsite(SetBucketWebsiteRequest request) throws ObsException;

    /**
     * 删除桶的website（托管）配置
     * 
     * @param bucketName
     *            桶名
     * @return 公共响应头信息
     * @throws ObsException
     *             OBS SDK自定义异常，当调用接口失败、访问OBS失败时抛出该异常
     */
    HeaderResponse deleteBucketWebsite(String bucketName) throws ObsException;

    /**
     * 删除桶的website（托管）配置
     * 
     * @param request
     *            请求参数
     * @return 公共响应头信息
     * @throws ObsException
     *             OBS SDK自定义异常，当调用接口失败、访问OBS失败时抛出该异常
     * @since 3.20.3
     */
    HeaderResponse deleteBucketWebsite(BaseBucketRequest request) throws ObsException;

    /**
     * 获取桶标签
     * 
     * @param bucketName
     *            桶名
     * @return 桶标签
     * @throws ObsException
     *             OBS SDK自定义异常，当调用接口失败、访问OBS失败时抛出该异常
     */
    BucketTagInfo getBucketTagging(String bucketName) throws ObsException;

    /**
     * 获取桶标签
     * 
     * @param request
     *            请求参数
     * @return 桶标签
     * @throws ObsException
     *             OBS SDK自定义异常，当调用接口失败、访问OBS失败时抛出该异常
     * @since 3.20.3
     */
    BucketTagInfo getBucketTagging(BaseBucketRequest request) throws ObsException;

    /**
     * 设置桶标签
     * 
     * @param bucketName
     *            桶名
     * @param bucketTagInfo
     *            桶标签
     * @return 公共响应头信息
     * @throws ObsException
     *             OBS SDK自定义异常，当调用接口失败、访问OBS失败时抛出该异常
     */
    HeaderResponse setBucketTagging(String bucketName, BucketTagInfo bucketTagInfo) throws ObsException;

    /**
     * 设置桶标签
     * 
     * @param request
     *            请求参数
     * @return 公共响应头信息
     * @throws ObsException
     *             OBS SDK自定义异常，当调用接口失败、访问OBS失败时抛出该异常
     * @since 3.20.3
     */
    HeaderResponse setBucketTagging(SetBucketTaggingRequest request) throws ObsException;

    /**
     * 删除桶标签
     * 
     * @param bucketName
     *            桶名
     * @return 公共响应头信息
     * @throws ObsException
     *             OBS SDK自定义异常，当调用接口失败、访问OBS失败时抛出该异常
     */
    HeaderResponse deleteBucketTagging(String bucketName) throws ObsException;

    /**
     * 删除桶标签
     * 
     * @param request
     *            请求参数
     * @return 公共响应头信息
     * @throws ObsException
     *             OBS SDK自定义异常，当调用接口失败、访问OBS失败时抛出该异常
     * @since 3.20.3
     */
    HeaderResponse deleteBucketTagging(BaseBucketRequest request) throws ObsException;

    /**
     * 获取桶加密配置
     * 
     * @param bucketName
     *            桶名
     * @return 桶加密配置
     * @throws ObsException
     *             OBS SDK自定义异常，当调用接口失败、访问OBS失败时抛出该异常
     */
    BucketEncryption getBucketEncryption(String bucketName) throws ObsException;

    /**
     * 获取桶加密配置
     * 
     * @param request
     *            请求参数
     * @return 桶加密配置
     * @throws ObsException
     *             OBS SDK自定义异常，当调用接口失败、访问OBS失败时抛出该异常
     * @since 3.20.3
     */
    BucketEncryption getBucketEncryption(BaseBucketRequest request) throws ObsException;

    /**
     * 设置桶加密配置
     * 
     * @param bucketName
     *            桶名
     * @param bucketEncryption
     *            桶加密配置
     * @return 公共响应头信息
     * @throws ObsException
     *             OBS SDK自定义异常，当调用接口失败、访问OBS失败时抛出该异常
     */
    HeaderResponse setBucketEncryption(String bucketName, BucketEncryption bucketEncryption) throws ObsException;

    /**
     * 设置桶加密配置
     * 
     * @param request
     *            请求参数
     * @return 公共响应头信息
     * @throws ObsException
     *             OBS SDK自定义异常，当调用接口失败、访问OBS失败时抛出该异常
     * @since 3.20.3
     */
    HeaderResponse setBucketEncryption(SetBucketEncryptionRequest request) throws ObsException;

    /**
     * 删除桶加密配置
     * 
     * @param bucketName
     *            桶名
     * @return 公共响应头信息
     * @throws ObsException
     *             OBS SDK自定义异常，当调用接口失败、访问OBS失败时抛出该异常
     */
    HeaderResponse deleteBucketEncryption(String bucketName) throws ObsException;

    /**
     * 删除桶加密配置
     * 
     * @param request
     *            请求参数
     * @return 公共响应头信息
     * @throws ObsException
     *             OBS SDK自定义异常，当调用接口失败、访问OBS失败时抛出该异常
     * @since 3.20.3
     */
    HeaderResponse deleteBucketEncryption(BaseBucketRequest request) throws ObsException;

    /**
     * 设置桶的跨Region复制配置
     * 
     * @param bucketName
     *            桶名
     * @param replicationConfiguration
     *            跨Region复制配置
     * @return 公共响应头信息
     * @throws ObsException
     *             OBS SDK自定义异常，当调用接口失败、访问OBS失败时抛出该异常
     * 
     */
    HeaderResponse setBucketReplication(String bucketName, ReplicationConfiguration replicationConfiguration)
            throws ObsException;

    /**
     * 设置桶的跨Region复制配置
     * 
     * @param request
     *            请求参数
     * @return 公共响应头信息
     * @throws ObsException
     *             OBS SDK自定义异常，当调用接口失败、访问OBS失败时抛出该异常
     * @since 3.20.3
     */
    HeaderResponse setBucketReplication(SetBucketReplicationRequest request) throws ObsException;

    /**
     * 获取桶的跨Region复制配置
     * 
     * @param bucketName
     *            桶名
     * @return 跨Region复制配置
     * @throws ObsException
     *             OBS SDK自定义异常，当调用接口失败、访问OBS失败时抛出该异常
     */
    ReplicationConfiguration getBucketReplication(String bucketName) throws ObsException;

    /**
     * 获取桶的跨Region复制配置
     * 
     * @param request
     *            请求参数
     * @return 跨Region复制配置
     * @throws ObsException
     *             OBS SDK自定义异常，当调用接口失败、访问OBS失败时抛出该异常
     * @since 3.20.3
     */
    ReplicationConfiguration getBucketReplication(BaseBucketRequest request) throws ObsException;

    /**
     * 删除桶的跨Region复制配置
     * 
     * @param bucketName
     *            桶名
     * @return 公共响应头信息
     * @throws ObsException
     *             OBS SDK自定义异常，当调用接口失败、访问OBS失败时抛出该异常
     */
    HeaderResponse deleteBucketReplication(String bucketName) throws ObsException;

    /**
     * 删除桶的跨Region复制配置
     * 
     * @param request
     *            请求参数
     * @return 公共响应头信息
     * @throws ObsException
     *             OBS SDK自定义异常，当调用接口失败、访问OBS失败时抛出该异常
     * @since 3.20.3
     */
    HeaderResponse deleteBucketReplication(BaseBucketRequest request) throws ObsException;

    /**
     * 获取桶的消息通知配置
     * 
     * @param bucketName
     *            桶名
     * @return 桶的消息通知配置
     * @throws ObsException
     *             OBS SDK自定义异常，当调用接口失败、访问OBS失败时抛出该异常
     */
    BucketNotificationConfiguration getBucketNotification(String bucketName) throws ObsException;

    /**
     * 获取桶的消息通知配置
     * 
     * @param request
     *            请求参数
     * @return 桶的消息通知配置
     * @throws ObsException
     *             OBS SDK自定义异常，当调用接口失败、访问OBS失败时抛出该异常
     * @since 3.20.3
     */
    BucketNotificationConfiguration getBucketNotification(BaseBucketRequest request) throws ObsException;

    /**
     * 设置桶的消息通知配置
     * 
     * @param bucketName
     *            桶名
     * @param bucketNotificationConfiguration
     *            桶的消息通知配置
     * @return 公共响应头信息
     * @throws ObsException
     *             OBS SDK自定义异常，当调用接口失败、访问OBS失败时抛出该异常
     */
    HeaderResponse setBucketNotification(String bucketName,
            BucketNotificationConfiguration bucketNotificationConfiguration) throws ObsException;

    /**
     * 设置桶的消息通知配置
     * 
     * @param request
     *            请求参数
     * @return 公共响应头信息
     * @throws ObsException
     *             OBS SDK自定义异常，当调用接口失败、访问OBS失败时抛出该异常
     * @since 3.20.3
     */
    HeaderResponse setBucketNotification(SetBucketNotificationRequest request) throws ObsException;

    /**
     * 上传对象
     * 
     * @param bucketName
     *            桶名
     * @param objectKey
     *            对象名
     * @param input
     *            待上传的数据流
     * @param metadata
     *            对象的属性
     * @return 上传对象的响应结果
     * @throws ObsException
     *             OBS SDK自定义异常，当调用接口失败、访问OBS失败时抛出该异常
     */
    PutObjectResult putObject(String bucketName, String objectKey, InputStream input, ObjectMetadata metadata)
            throws ObsException;

    /**
     * 上传对象
     * 
     * @param bucketName
     *            桶名
     * @param objectKey
     *            对象名
     * @param input
     *            待上传的数据流
     * @return 上传对象的响应结果
     * @throws ObsException
     *             OBS SDK自定义异常，当调用接口失败、访问OBS失败时抛出该异常
     */
    PutObjectResult putObject(String bucketName, String objectKey, InputStream input) throws ObsException;

    /**
     * 上传对象
     * 
     * @param request
     *            上传对象请求参数
     * @return 上传对象的响应结果
     * @throws ObsException
     *             OBS SDK自定义异常，当调用接口失败、访问OBS失败时抛出该异常
     */
    PutObjectResult putObject(PutObjectRequest request) throws ObsException;

    /**
     * 上传对象
     * 
     * @param bucketName
     *            桶名
     * @param objectKey
     *            对象名
     * @param file
     *            待上传的文件
     * @return 上传对象的响应结果
     * @throws ObsException
     *             OBS SDK自定义异常，当调用接口失败、访问OBS失败时抛出该异常
     */
    PutObjectResult putObject(String bucketName, String objectKey, File file) throws ObsException;

    /**
     * 上传对象
     * 
     * @param bucketName
     *            桶名
     * @param objectKey
     *            对象名
     * @param file
     *            待上传的文件
     * @param metadata
     *            对象的属性
     * @return 上传对象的响应结果
     * @throws ObsException
     *             OBS SDK自定义异常，当调用接口失败、访问OBS失败时抛出该异常
     */
    PutObjectResult putObject(String bucketName, String objectKey, File file, ObjectMetadata metadata)
            throws ObsException;

    /**
     * 追加上传对象
     * 
     * @param request
     *            追加上传请求参数
     * @return 追加上传响应结果
     * @throws ObsException
     *             OBS SDK自定义异常，当调用接口失败、访问OBS失败时抛出该异常
     */
    AppendObjectResult appendObject(AppendObjectRequest request) throws ObsException;

    /**
     * 上传文件，支持断点续传模式
     * 
     * @param uploadFileRequest
     *            上传文件请求参数
     * @return 合并段响应结果
     * @throws ObsException
     *             OBS SDK自定义异常，当调用接口失败、访问OBS失败时抛出该异常
     */
    CompleteMultipartUploadResult uploadFile(UploadFileRequest uploadFileRequest) throws ObsException;

    /**
     * 批量上传文件
     * 
     * @param request
     *            批量上传文件的请求参数
     * @return 批量任务执行状态
     * @throws ObsException
     *             OBS SDK自定义异常，当调用接口失败、访问OBS失败时抛出该异常
     */
    UploadProgressStatus putObjects(PutObjectsRequest request) throws ObsException;

    /**
     * 判断对象是否存在
     * 
     * @param buckeName
     *            桶名
     * @param objectKey
     *            对象名
     * @return 对象是否存在
     * @throws ObsException
     *             OBS SDK自定义异常，当调用接口失败、访问OBS失败时抛出该异常
     */
    boolean doesObjectExist(String buckeName, String objectKey) throws ObsException;

    /**
     * 判断对象是否存在
     * 
     * @param request
     *            对象属性的请求参数
     * @return 对象是否存在
     * @throws ObsException
     *             OBS SDK自定义异常，当调用接口失败、访问OBS失败时抛出该异常
     */
    boolean doesObjectExist(GetObjectMetadataRequest request) throws ObsException;

    /**
     * 下载文件，支持断点续传模式
     * 
     * @param downloadFileRequest
     *            下载文件的请求参数
     * @return 下载文件的响应结果
     * @throws ObsException
     *             OBS SDK自定义异常，当调用接口失败、访问OBS失败时抛出该异常
     */
    DownloadFileResult downloadFile(DownloadFileRequest downloadFileRequest) throws ObsException;

    /**
     * 下载对象
     * 
     * @param request
     *            下载对象的请求参数
     * @return 对象信息，包含对象数据流
     * @throws ObsException
     *             OBS SDK自定义异常，当调用接口失败、访问OBS失败时抛出该异常
     */
    ObsObject getObject(GetObjectRequest request) throws ObsException;

    /**
     * 下载对象
     * 
     * @param bucketName
     *            桶名
     * @param objectKey
     *            对象名
     * @param versionId
     *            对象版本号
     * @return 对象信息，包含对象数据流
     * @throws ObsException
     *             OBS SDK自定义异常，当调用接口失败、访问OBS失败时抛出该异常
     */
    ObsObject getObject(String bucketName, String objectKey, String versionId) throws ObsException;

    /**
     * 下载对象
     * 
     * @param bucketName
     *            桶名
     * @param objectKey
     *            对象名
     * @return 对象信息，包含对象数据流
     * @throws ObsException
     *             OBS SDK自定义异常，当调用接口失败、访问OBS失败时抛出该异常
     */
    ObsObject getObject(String bucketName, String objectKey) throws ObsException;

    /**
     * 获取对象属性
     * 
     * @param request
     *            获取对象属性的请求参数
     * @return 对象的属性
     * @throws ObsException
     *             OBS SDK自定义异常，当调用接口失败、访问OBS失败时抛出该异常
     */
    ObjectMetadata getObjectMetadata(GetObjectMetadataRequest request) throws ObsException;

    /**
     * 获取对象属性
     * 
     * @param bucketName
     *            桶名
     * @param objectKey
     *            对象名
     * @param versionId
     *            对象版本号
     * @return 对象的属性
     * @throws ObsException
     *             OBS SDK自定义异常，当调用接口失败、访问OBS失败时抛出该异常
     */
    ObjectMetadata getObjectMetadata(String bucketName, String objectKey, String versionId) throws ObsException;

    /**
     * 获取对象属性
     * 
     * @param bucketName
     *            桶名
     * @param objectKey
     *            对象名
     * @return 对象的属性
     * @throws ObsException
     *             OBS SDK自定义异常，当调用接口失败、访问OBS失败时抛出该异常
     */
    ObjectMetadata getObjectMetadata(String bucketName, String objectKey) throws ObsException;

    /**
     * 设置对象属性
     * 
     * @param request
     *            设置对象属性的请求参数
     * @return 对象的属性
     * @throws ObsException
     *             OBS SDK自定义异常，当调用接口失败、访问OBS失败时抛出该异常
     */
    ObjectMetadata setObjectMetadata(SetObjectMetadataRequest request) throws ObsException;

    /**
     * 取回归档存储对象
     * 
     * @param request
     *            取回归档存储对象的请求参数
     * @return 取回归档存储对象的状态
     * @throws ObsException
     *             OBS SDK自定义异常，当调用接口失败、访问OBS失败时抛出该异常
     * 
     */
    @Deprecated
    RestoreObjectStatus restoreObject(RestoreObjectRequest request) throws ObsException;

    /**
     * 取回归档存储对象
     * 
     * @param request
     *            取回归档存储对象的请求参数
     * @return 取回归档存储对象的结果
     * @throws ObsException
     *             OBS SDK自定义异常，当调用接口失败、访问OBS失败时抛出该异常
     * 
     */
    RestoreObjectResult restoreObjectV2(RestoreObjectRequest request) throws ObsException;

    /**
     * 批量取回归档存储对象
     * 
     * @param request
     *            批量取回归档存储对象的请求参数
     * @return 批量任务执行状态
     *
     * @throws ObsException
     *             OBS SDK自定义异常，当调用接口失败、访问OBS失败时抛出该异常
     * 
     */

    TaskProgressStatus restoreObjects(RestoreObjectsRequest request) throws ObsException;

    /**
     * 删除对象
     * 
     * @param bucketName
     *            桶名
     * @param objectKey
     *            对象名
     * @param versionId
     *            对象版本号
     * @return 公共响应头信息
     * @throws ObsException
     *             OBS SDK自定义异常，当调用接口失败、访问OBS失败时抛出该异常
     */

    DeleteObjectResult deleteObject(String bucketName, String objectKey, String versionId) throws ObsException;

    /**
     * 删除对象
     * 
     * @param bucketName
     *            桶名
     * @param objectKey
     *            对象名
     * @return 公共响应头信息
     * @throws ObsException
     *             OBS SDK自定义异常，当调用接口失败、访问OBS失败时抛出该异常
     */
    DeleteObjectResult deleteObject(String bucketName, String objectKey) throws ObsException;

    /**
     * 删除对象
     * 
     * @param request
     *            删除对象的请求参数
     * @return 公共响应头信息
     * @throws ObsException
     *             OBS SDK自定义异常，当调用接口失败、访问OBS失败时抛出该异常
     * @since 3.20.3
     */
    DeleteObjectResult deleteObject(DeleteObjectRequest request) throws ObsException;

    /**
     * 批量删除对象
     * 
     * @param deleteObjectsRequest
     *            批量删除对象的请求参数
     * @return 批量删除对象的响应结果
     * @throws ObsException
     *             OBS SDK自定义异常，当调用接口失败、访问OBS失败时抛出该异常
     */
    DeleteObjectsResult deleteObjects(DeleteObjectsRequest deleteObjectsRequest) throws ObsException;

    /**
     * 获取对象访问权限
     * 
     * @param bucketName
     *            桶名
     * @param objectKey
     *            对象名
     * @param versionId
     *            对象版本号
     * @return 对象的访问权限
     * @throws ObsException
     *             OBS SDK自定义异常，当调用接口失败、访问OBS失败时抛出该异常
     */
    AccessControlList getObjectAcl(String bucketName, String objectKey, String versionId) throws ObsException;

    /**
     * 获取对象访问权限
     * 
     * @param bucketName
     *            桶名
     * @param objectKey
     *            对象名
     * @return 对象的访问权限
     * @throws ObsException
     *             OBS SDK自定义异常，当调用接口失败、访问OBS失败时抛出该异常
     */
    AccessControlList getObjectAcl(String bucketName, String objectKey) throws ObsException;

    /**
     * 获取对象访问权限
     * 
     * @param request
     *            获取对象访问权限的请求参数
     * @return 对象的访问权限
     * @throws ObsException
     *             OBS SDK自定义异常，当调用接口失败、访问OBS失败时抛出该异常
     * @since 3.20.3
     */
    AccessControlList getObjectAcl(GetObjectAclRequest request) throws ObsException;

    /**
     * 设置对象访问权限
     * 
     * @param bucketName
     *            桶名
     * @param objectKey
     *            对象名
     * @param acl
     *            访问权限
     * @param versionId
     *            对象版本号
     * @return 公共响应头信息
     * @throws ObsException
     *             OBS SDK自定义异常，当调用接口失败、访问OBS失败时抛出该异常
     */
    HeaderResponse setObjectAcl(String bucketName, String objectKey, AccessControlList acl, String versionId)
            throws ObsException;

    /**
     * 设置对象访问权限
     * 
     * @param bucketName
     *            桶名
     * @param objectKey
     *            对象名
     * @param acl
     *            访问权限
     * @return 公共响应头信息
     * @throws ObsException
     *             OBS SDK自定义异常，当调用接口失败、访问OBS失败时抛出该异常
     */
    HeaderResponse setObjectAcl(String bucketName, String objectKey, AccessControlList acl) throws ObsException;

    /**
     * 设置对象访问权限
     * 
     * @param request
     *            请求参数
     * @return 公共响应头信息
     * @throws ObsException
     *             OBS SDK自定义异常，当调用接口失败、访问OBS失败时抛出该异常
     * @since 3.20.3
     */
    HeaderResponse setObjectAcl(SetObjectAclRequest request) throws ObsException;

    /**
     * 复制对象
     * 
     * @param request
     *            复制对象请求参数
     * @return 复制对象的响应结果
     * @throws ObsException
     *             OBS SDK自定义异常，当调用接口失败、访问OBS失败时抛出该异常
     */
    CopyObjectResult copyObject(CopyObjectRequest request) throws ObsException;

    /**
     * 复制对象
     * 
     * @param sourceBucketName
     *            源桶名
     * @param sourceObjectKey
     *            源对象名
     * @param destBucketName
     *            目标桶名
     * @param destObjectKey
     *            目标对象名
     * @return 复制对象的响应结果
     * @throws ObsException
     *             OBS SDK自定义异常，当调用接口失败、访问OBS失败时抛出该异常
     */
    CopyObjectResult copyObject(String sourceBucketName, String sourceObjectKey, String destBucketName,
            String destObjectKey) throws ObsException;

    /**
     * 初始化分段上传任务
     * 
     * @param request
     *            初始化分段上传任务的请求参数
     * @return 初始化分段上传任务的响应结果
     * @throws ObsException
     *             OBS SDK自定义异常，当调用接口失败、访问OBS失败时抛出该异常
     */
    InitiateMultipartUploadResult initiateMultipartUpload(InitiateMultipartUploadRequest request) throws ObsException;

    /**
     * 取消分段上传任务
     * 
     * @param request
     *            取消分段上传任务的请求参数
     * @return 公共响应头信息
     * @throws ObsException
     *             OBS SDK自定义异常，当调用接口失败、访问OBS失败时抛出该异常
     */
    HeaderResponse abortMultipartUpload(AbortMultipartUploadRequest request) throws ObsException;

    /**
     * 上传段
     * 
     * @param bucketName
     *            桶名
     * @param objectKey
     *            对象名
     * @param uploadId
     *            分段上传任务的ID号
     * @param partNumber
     *            分段号
     * @param input
     *            待上传的数据流
     * @return 上传段的响应结果
     * @throws ObsException
     *             OBS SDK自定义异常，当调用接口失败、访问OBS失败时抛出该异常
     */
    UploadPartResult uploadPart(String bucketName, String objectKey, String uploadId, int partNumber, InputStream input)
            throws ObsException;

    /**
     * 上传段
     * 
     * @param bucketName
     *            桶名
     * @param objectKey
     *            对象名
     * @param uploadId
     *            分段上传任务的ID号
     * @param partNumber
     *            分段号
     * @param file
     *            待上传的文件
     * @return 上传段的响应结果
     * @throws ObsException
     *             OBS SDK自定义异常，当调用接口失败、访问OBS失败时抛出该异常
     */
    UploadPartResult uploadPart(String bucketName, String objectKey, String uploadId, int partNumber, File file)
            throws ObsException;

    /**
     * 上传段
     * 
     * @param request
     *            上传段的请求参数
     * @return 上传段的响应结果
     * @throws ObsException
     *             OBS SDK自定义异常，当调用接口失败、访问OBS失败时抛出该异常
     */
    UploadPartResult uploadPart(UploadPartRequest request) throws ObsException;

    /**
     * 复制段
     * 
     * @param request
     *            复制段的请求参数
     * @return 复制段的响应结果
     * @throws ObsException
     *             OBS SDK自定义异常，当调用接口失败、访问OBS失败时抛出该异常
     */
    CopyPartResult copyPart(CopyPartRequest request) throws ObsException;

    /**
     * 合并段
     * 
     * @param request
     *            合并段的请求参数
     * @return 合并段的响应结果
     * @throws ObsException
     *             OBS SDK自定义异常，当调用接口失败、访问OBS失败时抛出该异常
     */
    CompleteMultipartUploadResult completeMultipartUpload(CompleteMultipartUploadRequest request) throws ObsException;

    /**
     * 列举已上传段
     * 
     * @param request
     *            列举已上传段的请求参数
     * @return 列举已上传段的响应结果
     * @throws ObsException
     *             OBS SDK自定义异常，当调用接口失败、访问OBS失败时抛出该异常
     */
    ListPartsResult listParts(ListPartsRequest request) throws ObsException;

    /**
     * 列举未完成的分段上传任务
     * 
     * @param request
     *            列举分段上传任务的请求参数
     * @return 分段上传任务列表
     * @throws ObsException
     *             OBS SDK自定义异常，当调用接口失败、访问OBS失败时抛出该异常
     */
    MultipartUploadListing listMultipartUploads(ListMultipartUploadsRequest request) throws ObsException;

    /**
     * 预读对象
     * 
     * @param request
     *            预读对象的请求参数
     * @return 预读对象的响应结果
     * @throws ObsException
     *             OBS SDK自定义异常，当调用接口失败、访问OBS失败时抛出该异常
     */
    ReadAheadResult readAheadObjects(ReadAheadRequest request) throws ObsException;

    /**
     * 删除预读的缓存
     * 
     * @param bucketName
     *            桶名
     * @param prefix
     *            预读对象的对象名前缀
     * @return 预读对象的响应结果
     * @throws ObsException
     *             OBS SDK自定义异常，当调用接口失败、访问OBS失败时抛出该异常
     */
    ReadAheadResult deleteReadAheadObjects(String bucketName, String prefix) throws ObsException;

    /**
     * 查询预读任务的进度
     * 
     * @param bucketName
     *            桶名
     * @param taskId
     *            预读任务ID
     * @return 查询预读任务进度的响应结果
     * @throws ObsException
     *             OBS SDK自定义异常，当调用接口失败、访问OBS失败时抛出该异常
     */
    ReadAheadQueryResult queryReadAheadObjectsTask(String bucketName, String taskId) throws ObsException;

    /**
     * 设置桶归档对象直读策略
     * 
     * @param bucketName
     *            桶名
     * @param access
     *            直读策略
     * @return 通用响应信息
     * @throws ObsException
     *             OBS SDK自定义异常，当调用接口失败、访问OBS失败时抛出该异常
     */
    HeaderResponse setBucketDirectColdAccess(String bucketName, BucketDirectColdAccess access) throws ObsException;

    /**
     * 设置桶归档对象直读策略
     * 
     * @param request
     *            请求参数
     * @return 通用响应信息
     * @throws ObsException
     *             OBS SDK自定义异常，当调用接口失败、访问OBS失败时抛出该异常
     * @since 3.20.3
     */
    HeaderResponse setBucketDirectColdAccess(SetBucketDirectColdAccessRequest request) throws ObsException;

    /**
     * 获取桶归档对象直读策略
     * 
     * @param bucketName
     *            桶名
     * @return 桶的归档对象直读策略
     * @throws ObsException
     *             OBS SDK自定义异常，当调用接口失败、访问OBS失败时抛出该异常
     */
    BucketDirectColdAccess getBucketDirectColdAccess(String bucketName) throws ObsException;

    /**
     * 获取桶归档对象直读策略
     * 
     * @param request
     *            请求参数
     * @return 桶的归档对象直读策略
     * @throws ObsException
     *             OBS SDK自定义异常，当调用接口失败、访问OBS失败时抛出该异常
     * @since 3.20.3
     */
    BucketDirectColdAccess getBucketDirectColdAccess(BaseBucketRequest request) throws ObsException;

    /**
     * 删除桶归档对象直读策略
     * 
     * @param bucketName
     *            桶名
     * @return 通用响应信息
     * @throws ObsException
     *             OBS SDK自定义异常，当调用接口失败、访问OBS失败时抛出该异常
     */
    HeaderResponse deleteBucketDirectColdAccess(String bucketName) throws ObsException;

    /**
     * 删除桶归档对象直读策略
     * 
     * @param request
     *            请求参数
     * @return 通用响应信息
     * @throws ObsException
     *             OBS SDK自定义异常，当调用接口失败、访问OBS失败时抛出该异常
     * @since 3.20.3
     */
    HeaderResponse deleteBucketDirectColdAccess(BaseBucketRequest request) throws ObsException;

    /**
     * 关闭OBS客户端，释放连接资源
     * 
     * @throws IOException
     *             客户端关闭异常
     */
    void close() throws IOException;

}