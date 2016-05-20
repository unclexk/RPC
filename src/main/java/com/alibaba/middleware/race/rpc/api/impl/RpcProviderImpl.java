package com.alibaba.middleware.race.rpc.api.impl;

import com.alibaba.middleware.race.rpc.api.RpcProvider;
import com.alibaba.middleware.race.rpc.remoting.RpcServer;

/**
 * @author kang.xiao
 * @date 2015年7月21日上午2:25:10
 */
public class RpcProviderImpl extends RpcProvider {

	private Class<?> serviceInterface;
	private String version;
	private Object serviceInstance;
	private int timeout;
	private String serializeType;

	@Override
	public RpcProvider serviceInterface(Class<?> serviceInterface) {
		// TODO Auto-generated method stub
		this.serviceInterface = serviceInterface;
		return this;
	}

	@Override
	public RpcProvider version(String version) {
		// TODO Auto-generated method stub
		this.version = version;
		return this;
	}

	@Override
	public RpcProvider impl(Object serviceInstance) {
		// TODO Auto-generated method stub
		this.serviceInstance = serviceInstance;
		return this;
	}

	@Override
	public RpcProvider timeout(int timeout) {
		// TODO Auto-generated method stub
		this.timeout = timeout;
		return this;
	}

	@Override
	public RpcProvider serializeType(String serializeType) {
		// TODO Auto-generated method stub
		this.serializeType = serializeType;
		return this;
	}

	@Override
	public void publish() {
		// TODO Auto-generated method stub
		RpcServer server = new RpcServer(8888);
		RpcServer.remoteServiceRegisterMap.put(serviceInterface.getName()
				+ version, serviceInstance);
		server.startServer();
	}

}
