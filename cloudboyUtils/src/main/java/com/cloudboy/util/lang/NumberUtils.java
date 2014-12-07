package com.cloudboy.util.lang;

public class NumberUtils {
	static public int defaultValue(Integer value, int defaultValue) {
		if(value == null) {
			return defaultValue;
		} else {
			return value;
		}
	}
}
