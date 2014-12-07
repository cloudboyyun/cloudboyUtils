package com.cloudboy.base;

import java.text.MessageFormat;


public class AppRTException extends RuntimeException {
	public static final long serialVersionUID = 0;
	
	/**
	 * Error Code
	 */
	private String code;

	/**
	 * Exception parameters
	 */
	private Object[] args;

	/**
	 * Exception massage
	 */
	private String textMessage;

	private static String formatMessage(String msg, Object[] args) {
		return MessageFormat.format(msg, args);
	}
	
	public AppRTException(String msg) {
		super(msg);
	}
	
	public AppRTException(String code, String msg) {
		super(code + ": " + msg);
		this.code = code;
	}

	public AppRTException(String code, String msg, Throwable cause) {
		super(code + ": " + msg, cause);
		this.code = code;
	}

	public AppRTException(String code, Object[] args, String msg) {
		super(code + ": " + formatMessage(msg, args));
		this.code = code;
		this.args = args;
	}

	public AppRTException(String code, Object[] args, String msg, Throwable cause) {
		super(code + ": " + formatMessage(msg, args), cause);
		this.code = code;
		this.args = args;
	}

	public AppRTException(Throwable cause) {
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