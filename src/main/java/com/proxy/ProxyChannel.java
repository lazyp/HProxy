package com.proxy;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import org.apache.log4j.Logger;

/**
 * 连接代理通道
 * @author <a href=mailto:lazy_p@163.com>lazyp</a>
 *
 */
public class ProxyChannel extends Thread {
	private static final Logger logger = Logger.getLogger(ProxyChannel.class);
	
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
			}
			toStream.flush();
		} catch (IOException e) {
			logger.error("" , e);
		} finally {
			try {
				toSocket.shutdownInput();
			} catch (IOException e) {
				logger.error("shutdown input stream error");
			}
		}
	}

}
