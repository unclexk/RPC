package com.alibaba.middleware.race.rpc.remoting;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;

import com.alibaba.middleware.race.rpc.remoting.serialization.KryoSerialization;
import com.alibaba.middleware.race.rpc.remoting.serialization.KyroFactory;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

/**
 * @author kang.xiao
 * @date 2015年7月28日下午2:56:21
 */
public class KryoDecoder extends ByteToMessageDecoder {

	private static final int HEAD_LENGTH = 4;
	private static KyroFactory factory = new KyroFactory();

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

		byte[] body = new byte[dataLength];
		in.readBytes(body);
		Object o = convertToObject(body);
		out.add(o);
	}

	private Object convertToObject(byte[] body) {
		// TODO Auto-generated method stub
		ByteArrayInputStream in = new ByteArrayInputStream(body);
		KryoSerialization kryoSerialization = new KryoSerialization(factory);
		Object obj = null;
		try {
			obj = kryoSerialization.deserialize(in);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return obj;
	}

}
