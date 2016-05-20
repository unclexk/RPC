package com.alibaba.middleware.race.rpc.remoting;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;

import java.util.HashMap;

/**
 * RPC服务器
 * 
 * @author kang.xiao
 * @date 2015年7月21日上午3:35:05
 */
public class RpcServer {

	private static final EventLoopGroup bossGroup = new NioEventLoopGroup();
	private static final EventLoopGroup workerGroup = new NioEventLoopGroup();

	public static HashMap<String, Object> remoteServiceRegisterMap;
	private int port;

	/**
	 * 初始化服务器
	 * 
	 * @param port
	 *            端口号
	 */
	public RpcServer(int port) {
		this.port = port;
		// 初始化服务注册表
		remoteServiceRegisterMap = new HashMap<String, Object>();
	}

	public void startServer() {
		try {
			ServerBootstrap b = new ServerBootstrap();
			b.group(bossGroup, workerGroup)
					.channel(NioServerSocketChannel.class)
					.option(ChannelOption.SO_BACKLOG, 128)
					.childOption(ChannelOption.TCP_NODELAY, true)
					.childOption(ChannelOption.SO_KEEPALIVE, true)
					.childHandler(new ChannelInitializer<SocketChannel>() {
						@Override
						public void initChannel(SocketChannel ch)
								throws Exception {
							 KryoEncoder encoder = new KryoEncoder();
							 KryoDecoder decoder = new KryoDecoder();
							// ProtobufEncoder encoder = new ProtobufEncoder();
							// ProtobufDecoder decoder = new ProtobufDecoder();
							ch.pipeline().addLast(encoder, decoder,
									new ObjectTransferServerHandler());
						}
					});
			System.out.println("Server start at port:" + port);
			ChannelFuture f = b.bind(port).sync();
			f.channel().closeFuture().sync();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			bossGroup.shutdownGracefully();
			workerGroup.shutdownGracefully();
		}
	}
}
