package com.alibaba.middleware.race.rpc.remoting;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

/**
 * @author kang.xiao
 * @date 2015年7月26日上午5:17:05
 */
public class NettyConnectionPool {

	private static int maxConnectCount = 1024;
	private static int connectCount = 0;
	private static EventLoopGroup eventLoopGroup = new NioEventLoopGroup();
	private static BlockingQueue<Map<String, Object>> channelQueue = new ArrayBlockingQueue<Map<String, Object>>(
			maxConnectCount);
	public static String host = System.getProperty("SIP");
	public static int port = 8888;

	public static Map<String, Object> getConnect() throws Exception {
		Map<String, Object> channel = channelQueue.poll();
		if (channel == null) {
			if (connectCount < maxConnectCount) {
				Map<String, Object> connect = connect();
				connectCount++;
				return connect;
			} else {
				Map<String, Object> connect = channelQueue.poll(
						Integer.MAX_VALUE, TimeUnit.MILLISECONDS);
				if (((Channel) connect.get("channel")).isOpen()) {
					return connect;
				} else {
					channelQueue.remove(connect);
					return getConnect();
				}
			}
		} else {
			return channel;
		}
	}

	public static void returnConnect(Map<String, Object> channelReturn) {
		if (channelQueue.size() < maxConnectCount) {
			channelQueue.add(channelReturn);
		}
	}

	private static Map<String, Object> connect() throws Exception {
		Bootstrap bootstrap = new Bootstrap();
		final ObjectTransferClientHandler hanlder = new ObjectTransferClientHandler();
		bootstrap.group(eventLoopGroup).channel(NioSocketChannel.class)
				.option(ChannelOption.SO_KEEPALIVE, true)
				.option(ChannelOption.TCP_NODELAY, true)
				.handler(new ChannelInitializer<SocketChannel>() {
					@Override
					protected void initChannel(SocketChannel ch)
							throws Exception {
						KryoEncoder encoder = new KryoEncoder();
						KryoDecoder decoder = new KryoDecoder();
						// ProtobufEncoder encoder = new ProtobufEncoder();
						// ProtobufDecoder decoder = new ProtobufDecoder();
						ch.pipeline().addLast(encoder, decoder, hanlder);
					}
				});

		Channel channel = bootstrap.connect(host, port).sync().channel();
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("channel", channel);
		map.put("hanlder", hanlder);
		return map;
	}

}
