package com.cloudboy.util.lang;

public class StringUtils {
	
	/**
	 * If the string is empty or null, return true.
	 * @param src
	 * @return
	 */
	static public boolean isEmpty(String src) {
		String dst = trimToNull(src);
		if(dst == null) {
			return true;
		} else {
			return false;
		}
	}
	
	/**
	 * Remove the blank characters from the begin or end of the string. If the result is empty, a Null is returned.
	 * @param src
	 * @return
	 */
	static public String trimToNull(String src) {
		if(src == null) {
			return null;
		}
		String dst = src.trim();
		if("".equals(dst)) {
			return null;
		}
		return dst;
	}
	
	/**
	 * Trim the string. If the string is null, return a empty String instead of throw an exception.
	 * @param src
	 * @return
	 */
	static public String trimToEmpty(String src) {
		if(src == null) {
			return "";
		}
		return src.trim();
	}
	
	static public String[] split(String str, String separatorChars) {
		return org.apache.commons.lang3.StringUtils.split(str, separatorChars);
	}
}
