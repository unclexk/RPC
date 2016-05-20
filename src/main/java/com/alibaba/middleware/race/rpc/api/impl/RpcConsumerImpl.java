package com.alibaba.middleware.race.rpc.api.impl;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import com.alibaba.middleware.race.rpc.aop.ConsumerHook;
import com.alibaba.middleware.race.rpc.api.RpcConsumer;
import com.alibaba.middleware.race.rpc.async.ResponseCallbackListener;
import com.alibaba.middleware.race.rpc.async.ResponseFuture;
import com.alibaba.middleware.race.rpc.context.RpcContext;
import com.alibaba.middleware.race.rpc.model.RpcResponse;
import com.alibaba.middleware.race.rpc.remoting.RpcClient;

/**
 * @author kang.xiao
 * @date 2015年7月21日上午2:22:19
 */
public class RpcConsumerImpl extends RpcConsumer {

	private static HashMap<String, Boolean> asyncCallMap = new HashMap<String, Boolean>();
	private static ExecutorService threadPool = Executors.newCachedThreadPool();
	private RpcClient client;

	private String className;
	private String version;
	private int clientTimeout;
	private ConsumerHook hook;
	private ResponseCallbackListener callback;

	@Override
	public RpcConsumer interfaceClass(Class<?> interfaceClass) {
		// TODO Auto-generated method stub
		className = interfaceClass.getName();
		return super.interfaceClass(interfaceClass);
	}

	@Override
	public RpcConsumer version(String version) {
		// TODO Auto-generated method stub
		this.version = version;
		return this;
	}

	@Override
	public RpcConsumer clientTimeout(int clientTimeout) {
		// TODO Auto-generated method stub
		this.clientTimeout = clientTimeout;
		return this;
	}

	@Override
	public RpcConsumer hook(ConsumerHook hook) {
		// TODO Auto-generated method stub
		this.hook = hook;
		return this;
	}

	@Override
	public Object instance() {
		// TODO Auto-generated method stub
		return super.instance();
	}

	@Override
	public void asynCall(String methodName) {
		// TODO Auto-generated method stub
		asyncCallMap.put(methodName, true);
	}

	@Override
	public <T extends ResponseCallbackListener> void asynCall(
			String methodName, T callbackListener) {
		// TODO Auto-generated method stub
		this.callback = callbackListener;
		asynCall(methodName);
	}

	@Override
	public void cancelAsyn(String methodName) {
		// TODO Auto-generated method stub
		asyncCallMap.put(methodName, false);
	}

	@Override
	public Object invoke(Object proxy, Method method, Object[] args)
			throws Throwable {
		// TODO Auto-generated method stub
		if (client == null) {
			client = new RpcClient();
		}

		if (hook != null) {
			hook.before(null);
		}

		final HashMap<String, Object> data = new HashMap<String, Object>();
		data.put("methodName", method.getName());
		data.put("parameterTypes", method.getParameterTypes());
		data.put("arguments", args);
		data.put("classTag", className + version);

		// 加入context
		if (RpcContext.getProps().size() != 0) {
			data.put("context", RpcContext.getProps());
		}

		if (asyncCallMap.get(method.getName()) == null
				|| !asyncCallMap.get(method.getName())) {
			// 同步调用
			Future<Object> future = threadPool.submit(new Callable<Object>() {
				@Override
				public Object call() throws Exception {
					// TODO Auto-generated method stub
					Object obj = client.send(data);
					return obj;
				}
			});
			try {

				Object response = future.get(clientTimeout,
						TimeUnit.MILLISECONDS);
				if (response instanceof Throwable) {
					throw (Exception) response;
				}

				if (hook != null) {
					hook.after(null);
				}

				return response;
			} catch (Exception e) {
				throw e;
			}

		} else {
			// 异步调用
			Callable<Object> call = new Callable<Object>() {
				public Object call() throws Exception {
					// 开始执行耗时操作
					RpcResponse response = new RpcResponse();
					Object obj;
					obj = client.send(data);
					if (obj instanceof Throwable) {
						throw (Exception) obj;
					}
					response.setAppResponse(obj);
					if (callback != null) {
						callback.onResponse(obj);
					}
					return response;
				}
			};

			Future<Object> future = threadPool.submit(call);
			ResponseFuture.futureThreadLocal.set(future);

			if (hook != null) {
				hook.after(null);
			}

			return null;
		}

	}

}
