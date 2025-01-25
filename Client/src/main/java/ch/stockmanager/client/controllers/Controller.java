package ch.stockmanager.client.controllers;

public abstract class Controller {
	private final String
		SERVER_IP;

	public Controller(String serverIp) {
		SERVER_IP = serverIp;
	}

	public abstract void update();

	public String getServerIp() {
		return SERVER_IP;
	}

	public String getUrl(String path) {
		return String.format("http://%s/%s/%s", getServerIp(), getPathPrefix(), path);
	}

	public String getUrl() {
		return getUrl("");
	}

	public abstract String getPathPrefix();
}
