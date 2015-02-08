package com.cloudboy.util.log4j;

import org.apache.log4j.helpers.FormattingInfo;
import org.apache.log4j.helpers.PatternConverter;
import org.apache.log4j.helpers.PatternParser;
import org.apache.log4j.spi.LoggingEvent;

public class ExtendPatternParser extends PatternParser {

	public ExtendPatternParser(String pattern) {
		super(pattern);
	}

	/**
	 * ��дfinalizeConverter�����ض���ռλ����д��?T��ʾ�߳�IDռλ��
	 */
	@Override
	protected void finalizeConverter(char c) {
		if (c == 'T') {
			this.addConverter(new ExPatternConverter(this.formattingInfo));
		} else {
			super.finalizeConverter(c);
		}
	}

	private static class ExPatternConverter extends PatternConverter {

		public ExPatternConverter(FormattingInfo fi) {
			super(fi);
		}

		/**
		 * ����Ҫ��ʾ�߳�ID��ʱ�򣬷��ص�ǰ�����̵߳�ID
		 */
		@Override
		protected String convert(LoggingEvent event) {
			return String.valueOf(Thread.currentThread().getId());
		}

	}
}
