package com.cloudboy.util.lang;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class StringUtilsTest {

	@Test
	public void trimToNull() {
		String s1 = null;
		assertTrue(StringUtils.trimToNull(s1) == null);
		String s2 = " ";
		assertTrue(StringUtils.trimToNull(s2) == null);
		String s3 = " 1";
		assertTrue(StringUtils.trimToNull(s3).equals("1"));
	}
	
	@Test
	public void trimToEmpty() {
		String s1 = null;
		assertTrue(StringUtils.trimToEmpty(s1).equals(""));
		String s2 = " ";
		assertTrue(StringUtils.trimToEmpty(s2).equals(""));
		String s3 = " 1";
		assertTrue(StringUtils.trimToEmpty(s3).equals("1"));
	}
	
	@Test
	public void split() {
		String str = "JSESSIONID=4BE5FC84475099C58CCC64798AE4D1A7.springRemotingStudy-1; Path=/studyWeb/; HttpOnly";
		String[] items = StringUtils.split(str, ";");
		for(String item : items) {
			if(item.contains("JSESSIONID")) {
				System.out.println(item);
				String[] subItems = StringUtils.split(item, "=");
				for(String subItem : subItems) {
					System.out.println(subItem);
				}
			}
		}
	}
}
