package com.cloudboy.util.json;

import java.io.StringWriter;


import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion;

public class JsonUtil {
	private static ObjectMapper mapper = null;
	
	static {
		mapper = new ObjectMapper();
		mapper.setSerializationInclusion(Inclusion.NON_NULL);
	}
	
	public static String convert2String(Object obj) {
		if(obj == null) {
			return null;
		}
		
		String result = null;
		StringWriter stringWriter = new StringWriter();
		try {
			mapper.writeValue(stringWriter, obj);
			result = stringWriter.toString();
		} catch(Exception e) {
			result = obj.toString();
		}
		return result;
	}
}
