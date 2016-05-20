# 一个简单的RPC框架
##概述
这是一个简单的RPC框架，基于Netty 4.0.23.Final实现，序列化基于Kryo，只支持Java
##实现功能
1. 支持异步调用，提供future、callback的能力
2. 能够传输基本类型、自定义业务类型、异常类型
3. 提供RPC上下文，客户端可以透传数据给服务端
4. 提供Hook，让开发人员进行RPC层面的AOP


