package com.contacts.model;

public class Server {
	private String serverIp;
	private int serverPort;

	public String getServerIp() {
		return serverIp;
	}

	public void setServerIp(String serverIp) {
		this.serverIp = serverIp;
	}

	public int getServerPort() {
		return serverPort;
	}

	public void setServerPort(int serverPort) {
		this.serverPort = serverPort;
	}

	public String toString() {
		return this.serverIp + " -> " + this.serverPort;
	}
}
