
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