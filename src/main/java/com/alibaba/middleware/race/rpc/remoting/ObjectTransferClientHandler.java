package com.alibaba.middleware.race.rpc.remoting;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

/**
 * @author kang.xiao
 * @date 2015年7月22日上午2:46:38
 */
public class ObjectTransferClientHandler extends ChannelInboundHandlerAdapter {

	private BlockingQueue<Object> queue = new ArrayBlockingQueue<Object>(1);

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg)
			throws Exception {
		// you can use the Object from Server here
		queue.add(msg);
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
			throws Exception {
		cause.printStackTrace();
		ctx.close();
	}

	public Object getResponseData() throws InterruptedException {
		return queue.poll(Integer.MAX_VALUE, TimeUnit.MILLISECONDS);
	}

}
