package com.alibaba.middleware.race.rpc.remoting;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

import com.alibaba.middleware.race.rpc.context.RpcContext;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

/**
 * @author kang.xiao
 * @date 2015年7月22日上午2:31:45
 */
public class ObjectTransferServerHandler extends ChannelInboundHandlerAdapter {

	@SuppressWarnings("unchecked")
	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg)
			throws Exception {
		Map<String, Object> requestData = (Map<String, Object>) msg;

		if (requestData.get("context") != null) {
			RpcContext.setProps((Map<String, Object>) requestData
					.get("context"));
		}

		String methodName = requestData.get("methodName").toString();

		Class<?>[] parameterTypes = (Class<?>[]) requestData
				.get("parameterTypes");
		final Object[] arguments = (Object[]) requestData.get("arguments");
		final Object service = RpcServer.remoteServiceRegisterMap
				.get(requestData.get("classTag"));
		try {
			final Method method = service.getClass().getMethod(methodName,
					parameterTypes);
			Object result = method.invoke(service, arguments);
			// 远程调用成功，返回调用结果
			ctx.write(result);
		} catch (Throwable t) {
			// 远程调用异常，返回异常
			if (t instanceof InvocationTargetException) {
				ctx.write(((InvocationTargetException) t).getTargetException());
			} else {
				ctx.write(t);
			}
		}
	}

	@Override
	public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
		ctx.flush();
		// ctx.close();
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
			throws Exception {
		cause.printStackTrace();
		ctx.close();
	}
}
