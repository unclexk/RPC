package com.alibaba.middleware.race.rpc.remoting.serialization;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.alibaba.middleware.race.rpc.demo.service.RaceDO;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

/**
 * @author kang.xiao
 * @date 2015年7月28日下午2:03:52
 */
public class KryoSerialization {
	private final KyroFactory kyroFactory;

	public KryoSerialization(final KyroFactory kyroFactory) {
		this.kyroFactory = kyroFactory;
	}

	public void serialize(final OutputStream out, final Object message)
			throws IOException {
		Kryo kryo = kyroFactory.getKryo();
		Output output = new Output(out);
		kryo.writeClassAndObject(output, message);
		output.close();
		kyroFactory.returnKryo(kryo);
	}

	public Object deserialize(final InputStream in) throws IOException {
		Kryo kryo = kyroFactory.getKryo();
		Input input = new Input(in);
		Object result = kryo.readClassAndObject(input);
		input.close();
		kyroFactory.returnKryo(kryo);
		return result;
	}

	public static void main(String[] args) throws IOException,
			ClassNotFoundException {
		long now = System.currentTimeMillis();
		KyroFactory factory = new KyroFactory();
		for (int i = 0; i < 1000000; i++) {
			KryoSerialization kryoSerialization = new KryoSerialization(factory);
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			kryoSerialization.serialize(out, new RaceDO());
			ByteArrayInputStream in = new ByteArrayInputStream(
					out.toByteArray());
			RaceDO obj = (RaceDO) kryoSerialization.deserialize(in);

			// Object obj = new RaceDO();
			//
			// ByteArrayOutputStream out = new ByteArrayOutputStream();
			// KryoSerialization kryoSerialization = new
			// KryoSerialization(factory);
			// kryoSerialization.serialize(out, obj.getClass());
			// ByteArrayInputStream in = new ByteArrayInputStream(
			// out.toByteArray());
			// Class<?> cls = (Class<?>) kryoSerialization.deserialize(in);
			//
			// byte[] test = ProtobufSerialization.serialize(obj);
			// RaceDO rd = (RaceDO) ProtobufSerialization.deserialize(test,
			// cls);

			// ByteArrayOutputStream out = new ByteArrayOutputStream();
			// ObjectOutputStream oo = new ObjectOutputStream(out);
			// oo.writeObject(new RaceDO());
			// ByteArrayInputStream in = new ByteArrayInputStream(
			// out.toByteArray());
			// ObjectInputStream oi = new ObjectInputStream(in);
			// RaceDO obj = (RaceDO) oi.readObject();
		}
		long timelong = System.currentTimeMillis() - now;
		System.out.println(timelong);
	}
}
