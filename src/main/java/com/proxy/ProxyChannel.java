package com.proxy;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

/**
 * 连接代理通道
 * @author <a href=mailto:lazy_p@163.com>lazyp</a>
 *
 */
public class ProxyChannel extends Thread {
	private Socket fromSocket;
	private Socket toSocket;

	public ProxyChannel(Socket fromSocket, Socket toSocket) {
		super();
		this.fromSocket = fromSocket;
		this.toSocket = toSocket;
	}

	@Override
	public void run() {
		try {
			InputStream fromStream = fromSocket.getInputStream();
			OutputStream toStream = toSocket.getOutputStream();
			byte[] buf = new byte[512];
			int len = -1;
			while ((len = fromStream.read(buf)) > 0) {
				toStream.write(buf, 0, len);
				toStream.flush();
			}
			toStream.flush();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (!toSocket.isOutputShutdown()) {
					toSocket.shutdownOutput();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

}
