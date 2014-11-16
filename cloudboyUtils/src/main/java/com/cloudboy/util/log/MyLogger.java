package com.cloudboy.util.log;

import org.apache.log4j.Logger;

public class MyLogger {
	private Logger logger;
	
	private MyLogger(Class<?> clazz) {
		logger = Logger.getLogger(clazz);
	}
	
	static public MyLogger getLogger(Class<?> clazz) {
		return new MyLogger(clazz);
	}
	
	public void info(Object message, Object... args) {
		String value = buildMessage(message, args);
		logger.info(value);
	}
	
	public void info(Object message, Throwable t, Object... args) {
		String value = buildMessage(message, args);
		logger.info(value, t);
	}
	
	public void error(Object message, Object... args) {
		String value = buildMessage(message, args);
		logger.error(value);
	}
	
	public void error(Object message, Throwable t, Object... args) {
		String value = buildMessage(message, args);
		logger.error(value, t);
	}
	
	public void debug(Object message, Object... args) {
		String value = buildMessage(message, args);
		logger.debug(value);
	}
	
	public void debug(Object message, Throwable t, Object... args) {
		String value = buildMessage(message, args);
		logger.debug(value, t);
	}
	
	private String buildMessage(Object message, Object... args) {
		StringBuilder builder = new StringBuilder();
		builder.append(message);
		for(Object arg : args) {
			builder.append(arg);
		}
		return builder.toString();
	}
}
