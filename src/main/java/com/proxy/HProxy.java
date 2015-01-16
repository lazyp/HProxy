package com.proxy;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import org.apache.log4j.Logger;

public final class HProxy {
	private static final Logger logger = Logger.getLogger(HProxy.class);
	private ServerSocket localProxyServerSocket;
	private int localProxyPort = 30000;

	public void start() throws IOException {
		logger.info("@_@ start the hproxy @_@");
		localProxyServerSocket = new ServerSocket(localProxyPort);
		while (true) {
			Socket clientSocket = localProxyServerSocket.accept();
			logger.info("accept proxy request:" + clientSocket.getRemoteSocketAddress());
			new ClientProxy(clientSocket).start();
		}
	}

	public static void main(String[] args) {
		HProxy hProxy = new HProxy();
		try {
			hProxy.start();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
