package com.cloudboy.base;

import java.text.MessageFormat;

public class AppBizException extends Exception {
	public static final long serialVersionUID = 0x01;

	private String code;

	private Object[] args;

	private String textMessage;

	public AppBizException(String msg) {
		super(msg);
	}

	private static String formatMessage(String msg, Object... args) {
		return MessageFormat.format(msg, args);
	}

	public AppBizException(String code, String msg) {
		super(code + ": " + msg);
		this.code = code;
	}
	
	public AppBizException(String code, String msg, Object... args) {
		super(code + ": " + formatMessage(msg, args));
		this.code = code;
		this.args = args;
	}

	public AppBizException(String code, String msg, Throwable cause) {
		super(code + ": " + msg, cause);
		this.code = code;
	}

	public AppBizException(String code, String msg, Object[] args, Throwable cause) {
		super(code + ": " + formatMessage(msg, args), cause);
		this.code = code;
		this.args = args;
	}

	public AppBizException(Throwable cause) {
		super(cause);
	}

	public String getCode() {
		return code;
	}

	public void setCode(String string) {
		code = string;
	}

	public Object[] getArgs() {
		return args;
	}

	public void setArgs(Object[] objects) {
		args = objects;
	}

	public String getTextMessage() {
		return textMessage;
	}

	public void setTextMessage(String textMessage) {
		this.textMessage = textMessage;
	}
}