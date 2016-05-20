package com.alibaba.middleware.race.rpc.remoting;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import com.alibaba.middleware.race.rpc.remoting.serialization.KryoSerialization;
import com.alibaba.middleware.race.rpc.remoting.serialization.KyroFactory;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * @author kang.xiao
 * @date 2015年7月28日下午2:50:07
 */
public class KryoEncoder extends MessageToByteEncoder<Object> {

	private static KyroFactory factory = new KyroFactory();

	@Override
	protected void encode(ChannelHandlerContext ctx, Object msg, ByteBuf out)
			throws Exception {
		// TODO Auto-generated method stub
		byte[] body = convertToBytes(msg);
		int dataLength = body.length;
		out.writeInt(dataLength);
		out.writeBytes(body);
	}

	private byte[] convertToBytes(Object msg) {
		// TODO Auto-generated method stub
		ByteArrayOutputStream bo = new ByteArrayOutputStream();
		KryoSerialization kryoSerialization = new KryoSerialization(factory);
		try {
			kryoSerialization.serialize(bo, msg);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return bo.toByteArray();
	}

}
