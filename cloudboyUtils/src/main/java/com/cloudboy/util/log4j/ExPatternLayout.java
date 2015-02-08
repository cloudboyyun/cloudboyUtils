package com.cloudboy.util.log4j;

import org.apache.log4j.PatternLayout;
import org.apache.log4j.helpers.PatternParser;

public class ExPatternLayout extends PatternLayout{

	@Override
	protected PatternParser createPatternParser(String pattern) {
		return new ExtendPatternParser(pattern);
	}
	

}
