package com.cloudboy.util.log;

import org.junit.Test;

public class MyLoggerTest {

	@Test
	public void testGetLogger() {
		MyLogger logger = MyLogger.getLogger();
		logger.info("123");
	}

}
