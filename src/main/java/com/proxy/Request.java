package com.proxy;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.StringTokenizer;

public class Request {
	private byte[] headers;
	private String host;
	private int port;
	private boolean https;
	private InputStream bodyStream;

	public static Request parse(InputStream inputStream) throws IOException {
		//System.out.println("do parse request");
		// 解析headers
		ByteArrayOutputStream byteArrayOutStream = new ByteArrayOutputStream();
		int pre = -1;
		int b;
		while ((b = inputStream.read()) > 0) {
			byteArrayOutStream.write(b);
			if (b == '\r' && pre == '\n') {
				b = inputStream.read();
				if (b <= 0) {
					throw new IOException("解析请求头异常.");
				}
				byteArrayOutStream.write(b);
				if (b == '\n') {
					break;
				}
			}
			pre = b;
		}
		Request request = new Request();
		request.setHeaders(byteArrayOutStream.toByteArray());

		// 解析host，port，是否https
		StringTokenizer strTokenizer = new StringTokenizer(new String(request.getHeaders()), "\r\n");
		while (strTokenizer.hasMoreTokens()) {
			String line = strTokenizer.nextToken().toLowerCase();
			if (line.startsWith("connect")) {
				request.setHttps(true);// https connect
			} else if (line.startsWith("host")) {
				int port = 80;
				String host = line.split(" ")[1];
				int index = host.indexOf(":");
				if (index == -1) {
					if (request.isHttps()) {
						port = 443;
					}
				} else {
					port = Integer.parseInt(host.substring(index + 1));
					host = host.substring(0, index);
				}
				request.setHost(host);
				request.setPort(port);
			}
		}
		return request;
	}

	public byte[] getHeaders() {
		return headers;
	}

	public void setHeaders(byte[] headers) {
		this.headers = headers;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public boolean isHttps() {
		return https;
	}

	public void setHttps(boolean https) {
		this.https = https;
	}

	public InputStream getBodyStream() {
		return bodyStream;
	}

	public void setBodyStream(InputStream bodyStream) {
		this.bodyStream = bodyStream;
	}
}
