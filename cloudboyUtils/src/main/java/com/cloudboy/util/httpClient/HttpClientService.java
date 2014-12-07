package com.cloudboy.util.httpClient;

public interface HttpClientService {
	/**
	 * Send the request message in XML format by post method
	 * @param url
	 * @param xml
	 * @param reqEncoding
	 * @param respEncoding
	 * @param connectionTimeout
	 * @param readTimeout
	 * @return
	 */
	public String postXML(String url, String xml, String reqEncoding, Integer connectionTimeout, Integer readTimeout);
}
