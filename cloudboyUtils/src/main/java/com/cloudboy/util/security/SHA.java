package com.cloudboy.util.security;

import org.apache.commons.codec.digest.DigestUtils;

public class SHA {
	public String sha1(String data) {
		return DigestUtils.sha1Hex(data);
	}
}
