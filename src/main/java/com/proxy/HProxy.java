package com.proxy;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Logger;

public final class HProxy {
	private static final Logger logger = Logger.getLogger(HProxy.class.getName());
	private ServerSocket localProxyServerSocket;
	private int localProxyPort = 30000;

	public void start() throws IOException {
		logger.info("启动代理");
		localProxyServerSocket = new ServerSocket(localProxyPort);
		while (true) {
			Socket clientSocket = localProxyServerSocket.accept();
			logger.info("accept proxy request:" + clientSocket.getRemoteSocketAddress());
			new ClientProxy(clientSocket).start();
		}
	}

	public static void main(String[] args) {
		// System.setProperty("java.util.logging.config.file",
		// "logging.properties");
		HProxy hProxy = new HProxy();
		try {
			hProxy.start();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
