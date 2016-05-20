package com.alibaba.middleware.race.rpc.remoting;

import java.io.ByteArrayOutputStream;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

import com.alibaba.middleware.race.rpc.remoting.serialization.KryoSerialization;
import com.alibaba.middleware.race.rpc.remoting.serialization.KyroFactory;
import com.alibaba.middleware.race.rpc.remoting.serialization.ProtobufSerialization;

/**
 * @author kang.xiao
 * @date 2015年8月6日上午12:29:25
 */
public class ProtobufEncoder extends MessageToByteEncoder<Object> {

	private static final Map<Class<?>, Integer> classTypeMap = new ConcurrentHashMap<Class<?>, Integer>();
	private static final KyroFactory factory = new KyroFactory();
	private static AtomicInteger type = new AtomicInteger(0);

	@Override
	protected void encode(ChannelHandlerContext ctx, Object msg, ByteBuf out)
			throws Exception {
		// TODO Auto-generated method stub
		byte[] body = convertToBytes(msg);
		int dataLength = body.length;
		out.writeInt(dataLength);

		if (classTypeMap.get(msg.getClass()) == null) {
			ByteArrayOutputStream bo = new ByteArrayOutputStream();
			KryoSerialization kryoSerialization = new KryoSerialization(factory);
			kryoSerialization.serialize(bo, msg.getClass());
			byte[] classType = bo.toByteArray();
			int classTypeLength = classType.length;
			out.writeInt(-1);
			out.writeInt(type.get());
			out.writeInt(classTypeLength);
			out.writeBytes(classType);
			classTypeMap.put(msg.getClass(), type.get());
			type.incrementAndGet();
		} else {
			out.writeInt(classTypeMap.get(msg.getClass()));
		}

		out.writeBytes(body);
	}

	private byte[] convertToBytes(Object msg) {
		// TODO Auto-generated method stub
		return ProtobufSerialization.serialize(msg);
	}
}
