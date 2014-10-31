package com.proxy;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.logging.Logger;

import org.apache.commons.lang3.StringUtils;

public class ClientProxy extends Thread {
	private static final Logger logger = Logger.getLogger(ClientProxy.class.getName());
	private Socket clientSocket;

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
			
			//logger.info(new String(request.getHeaders()));
			
			if (StringUtils.isBlank(request.getHost())) {
				this.closeSocket(clientSocket);
				return;
			}

			Socket remoteSocket = connect(request.getHost(), request.getPort());
			if(remoteSocket == null){
				this.closeSocket(clientSocket);
				return;
			}
			
			//header
			if(request.isHttps()){
				clientSocket.getOutputStream().write(new HttpsConnectResponse().getResponse());
			}else{
				remoteSocket.getOutputStream().write(request.getHeaders());
			}
			
			new ProxyChannel(clientSocket, remoteSocket).start();
			new ProxyChannel(remoteSocket, clientSocket).start();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private Socket connect(String host, int port) {
		try {
			return new Socket(host, port);
		} catch (UnknownHostException e) {
			logger.warning("unknown host");
		} catch (IOException e) {
			logger.warning(e.getMessage());
		}
		return null;
	}

	private void closeSocket(Socket socket) {
		if (socket != null && socket.isConnected()) {
			try {
				socket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
