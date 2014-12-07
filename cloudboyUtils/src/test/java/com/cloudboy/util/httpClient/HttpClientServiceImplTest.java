package com.cloudboy.util.httpClient;

import java.net.URISyntaxException;

import org.apache.http.client.utils.URIBuilder;
import org.junit.Test;

public class HttpClientServiceImplTest {

	@Test
	public void test() throws URISyntaxException {
		String url = "https://192.168.1.106:8093/studyWeb/XMLServlet2";
		URIBuilder uriBuilder = new URIBuilder(url);
		System.out.println(uriBuilder.getHost());
		System.out.println(uriBuilder.getScheme());
		System.out.println(uriBuilder.getPath());
		System.out.println(uriBuilder.getPort());
	}

}
