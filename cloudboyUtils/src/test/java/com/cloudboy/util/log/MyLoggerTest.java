package com.cloudboy.util.log;

import org.junit.Test;

public class MyLoggerTest {
	private static MyLogger logger = MyLogger.getLogger(MyLoggerTest.class);

	@Test
	public void testGetLogger() {
		
		logger.info("123");
	}

}
