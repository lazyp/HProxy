package com.proxy;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

public class ClientProxy extends Thread {
	private static final Logger logger = Logger.getLogger(ClientProxy.class);
	private Socket clientSocket;
	private Socket remoteSocket;

	public ClientProxy(Socket clientSocket) {
		this.clientSocket = clientSocket;
	}

	@Override
	public void run() {
		try {
			Request request = Request.parse(clientSocket.getInputStream());
			if (request.isHttps()) {
				logger.info("https request");
			}

			// logger.info(new String(request.getHeaders()));

			if (StringUtils.isBlank(request.getHost())) {
				logger.error("\n\n无法解析出HOST字段 ???\n");
				logger.error(new String(request.getHeaders()));
				return;
			}

			logger.info("target host:" + request.getHost());

			remoteSocket = connect(request.getHost(), request.getPort());
			if (remoteSocket == null) {
				return;
			}

			// header
			if (request.isHttps()) {
				// 如果是https需要回应一个established响应给游览器
				clientSocket.getOutputStream().write(new HttpsConnectResponse().getResponse());
			} else {
				remoteSocket.getOutputStream().write(request.getHeaders());
			}

			ProxyChannel requestProxyChannel = new ProxyChannel(clientSocket, remoteSocket);
			requestProxyChannel.start();
			ProxyChannel responseProxyChannel = new ProxyChannel(remoteSocket, clientSocket);
			responseProxyChannel.start();

			responseProxyChannel.join();
			requestProxyChannel.join();
		} catch (IOException e) {
			logger.error("" , e);
		} catch (InterruptedException e) {
			logger.error(e.getMessage());
		} finally {
			this.closeSocket(clientSocket);
			this.closeSocket(remoteSocket);
		}
	}

	private Socket connect(String host, int port) {
		try {
			return new Socket(host, port);
		} catch (UnknownHostException e) {
			logger.error("unknown host");
		} catch (IOException e) {
			logger.error(e.getMessage());
		}
		return null;
	}

	private void closeSocket(Socket socket) {
		if (socket != null && socket.isConnected()) {
			try {
				socket.close();
			} catch (IOException e) {
				logger.error("close the socket exception.");
			}
		}
	}
}
