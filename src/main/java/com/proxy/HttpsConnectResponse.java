package com.proxy;

public final class HttpsConnectResponse {
	private String headers = "HTTP/1.1 200 Connection Established\r\n\r\n";

	public byte[] getResponse() {
		return headers.getBytes();
	}
}
