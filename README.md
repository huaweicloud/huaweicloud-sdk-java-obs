Version 3.1.3
新特性：
1. 新增桶加密接口（ObsClient.setBucketEncryption/ObsClient.getBucketEncryption/ObsClient.deleteBucketEncryption），目前仅支持SSE-KMS的服务端加密方式；
2. 新增服务端加密方式枚举类型（SSEAlgorithmEnum），将服务端加密相关模型 ServerAlgorithm，ServerEncryption 标记为 Deprecated；

资料&demo:
1. 开发指南服务端加密章节，修改加密示例代码；

修复问题：
1. 优化异常情况下的日志记录；
2. 修复上传对象时，传入ByteArrayInputStream数据流可能导致报错的问题；
3. 优化access日志的级别，避免产生歧义；
4. 修改断点续传上传接口对段大小限制，从最小5MB改为最小100KB；

-----------------------------------------------------------------------------------

Version 3.1.2.1

修复问题：
1. 修改ObsConfiguration中maxIdleConnections参数的默认值为1000；

-----------------------------------------------------------------------------------

Version 3.1.2

新特性：
1. 桶事件通知接口（ObsClient.setBucketNotification/ObsClient.getBucketNotification）新增对函数工作流服务配置和查询的支持；

资料&demo:
1. 开发指南事件通知章节，新增对函数工作流服务配置的介绍；

修复问题：
1. 修复创建桶接口（ObsClient.createBucket）由于协议协商导致报错信息不准确的问题；
2. 修复okhttp3.Dispatcher底层的BUG，该BUG会导致最大并发数超限；

-----------------------------------------------------------------------------------

Version 3.1.1

新特性：
1. 支持集成 log4j 1.x作为日志组件;
2. 新增支持以Policy设置权限的临时鉴权访问接口（ObsClient.createGetTemporarySignature）；
3. 上传对象（ObsClient.putObject）支持自动识别更广泛的MIME类型；

修复问题：
1. 修复设置桶事件通知接口（ObsClient.setBucketNotification）无法设置多个TopicConfiguration的问题；
2. 修复SDK对JDK 9 及以上版本不兼容的问题；




