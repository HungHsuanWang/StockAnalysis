package com.ydd.model;

import com.cz.framework.StringUtil;

public class HttpResInfo {
	
	private final int sucessCode = 200;
	
	private int code = 500;
	
	private String repMsg;

	public int getCode() {
		return code;
	}

	/**
	 * 返回的不是200状态码
	 * @param code
	 */
	public void setCode(int code) {
		this.code = code;
	}

	public String getRepMsg() {
		if(this.code != 200 && StringUtil.isNotBlank(repMsg) && repMsg.length() > 100) {
			repMsg = repMsg.substring(0, 100);
		}
		return repMsg;
	}

	public void setRepMsg(String repMsg) {
		this.repMsg = repMsg;
	}
	
	public boolean isNotOk () {
		return sucessCode != this.code;
	}

	@Override
	public String toString() {
		return "HttpResInfo{" +
				"sucessCode=" + sucessCode +
				", code=" + code +
				", repMsg='" + repMsg + '\'' +
				'}';
	}
}
