package com.alibaba.middleware.race.rpc.remoting;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.alibaba.middleware.race.rpc.remoting.serialization.KryoSerialization;
import com.alibaba.middleware.race.rpc.remoting.serialization.KyroFactory;
import com.alibaba.middleware.race.rpc.remoting.serialization.ProtobufSerialization;

/**
 * @author kang.xiao
 * @date 2015年8月6日上午12:29:09
 */
public class ProtobufDecoder extends ByteToMessageDecoder {
	private static final int HEAD_LENGTH = 4;
	private static final Map<Integer, Class<?>> classTypeMap = new ConcurrentHashMap<Integer, Class<?>>();
	private static final KyroFactory factory = new KyroFactory();

	@Override
	protected void decode(ChannelHandlerContext ctx, ByteBuf in,
			List<Object> out) throws Exception {
		// TODO Auto-generated method stub
		if (in.readableBytes() < HEAD_LENGTH) {
			return;
		}
		in.markReaderIndex();
		int dataLength = in.readInt();
		if (dataLength < 0) {
			ctx.close();
		}
		if (in.readableBytes() < dataLength) {
			in.resetReaderIndex();
			return;
		}

		Class<?> cls = null;
		int type = in.readInt();
		if (type == -1) {
			type = in.readInt();
			int typeLength = in.readInt();
			byte[] b = new byte[typeLength];
			in.readBytes(b);
			ByteArrayInputStream bi = new ByteArrayInputStream(b);
			KryoSerialization kryoSerialization = new KryoSerialization(factory);
			try {
				cls = (Class<?>) kryoSerialization.deserialize(bi);
				classTypeMap.put(type, cls);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
			cls = classTypeMap.get(type);
		}

		byte[] body = new byte[dataLength];
		in.readBytes(body);
		Object o = convertToObject(body, cls);
		out.add(o);
	}

	private Object convertToObject(byte[] body, Class<?> cls) {
		// TODO Auto-generated method stub
		Object obj = ProtobufSerialization.deserialize(body, cls);
		System.out.println(obj.getClass());
		return obj;
	}
}
