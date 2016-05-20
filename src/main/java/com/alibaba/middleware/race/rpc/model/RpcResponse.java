package com.alibaba.middleware.race.rpc.model;

import java.io.Serializable;

/**
 * Created by huangsheng.hs on 2015/3/27.
 */
public class RpcResponse implements Serializable {
	private static final long serialVersionUID = -6887010484137768812L;

	private String errorMsg;

	private Object appResponse;

	public void setAppResponse(Object appResponse) {
		this.appResponse = appResponse;
	}

	public Object getAppResponse() {
		return appResponse;
	}

	public String getErrorMsg() {
		return errorMsg;
	}

	public boolean isError() {
		return errorMsg == null ? false : true;
	}
}
