package com.cloudboy.util.httpClient;

public interface HttpClientService {
	
	public String postXML(String url, String xml);
	
	/**
	 * Send the request message in XML format by post method
	 * @param url
	 * @param xml
	 * @param reqEncoding
	 * @param connectionTimeout
	 * @param readTimeout
	 * @return
	 */
	public String postXML(String url, String xml, String reqEncoding, Integer connectionTimeout, Integer readTimeout);
}
