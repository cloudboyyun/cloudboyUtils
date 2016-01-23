package com.cloudboy.util.httpClient;

import java.util.Map;

/**
 * 
 * @author cloudboy(yun.xia)
 *
 */
public interface HttpClientService {
	
	/**
	 * post XML-format request
	 * @param url
	 * @param xml
	 * @return
	 */
	public String postXML(String url, String xml);
	
	/**
	 * post XML-format request
	 * @param url
	 * @param xml
	 * @param reqEncoding
	 * @param connectionTimeout
	 * @param readTimeout
	 * @return
	 */
	public String postXML(String url, String xml, String reqEncoding, Integer connectionTimeout, Integer readTimeout);
	
	public String post(String url, Map<String, String> params);
}
