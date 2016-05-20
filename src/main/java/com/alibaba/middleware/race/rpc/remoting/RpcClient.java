package com.alibaba.middleware.race.rpc.remoting;

import java.util.Map;

import io.netty.channel.Channel;

/**
 * @author kang.xiao
 * @date 2015年7月22日上午2:56:44
 */
public class RpcClient {

	// 同步执行
	public Object send(Object data) throws Exception {
		Map<String, Object> connect = NettyConnectionPool.getConnect();
		Channel channel = (Channel) connect.get("channel");
		ObjectTransferClientHandler hanlder = (ObjectTransferClientHandler) connect
				.get("hanlder");
		channel.writeAndFlush(data);
		Object response = hanlder.getResponseData();
		NettyConnectionPool.returnConnect(connect);
		return response;
	}

}
