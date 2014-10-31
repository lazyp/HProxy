package com.proxy;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

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
				// close
				if (!fromSocket.isOutputShutdown()) {
					fromSocket.shutdownOutput();
				}

				if (!fromSocket.isInputShutdown()) {
					fromSocket.shutdownInput();
				}

				if (!toSocket.isInputShutdown()) {
					toSocket.shutdownInput();
				}
				if (!toSocket.isOutputShutdown()) {
					toSocket.shutdownOutput();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

}
