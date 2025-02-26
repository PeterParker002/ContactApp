package com.contacts.notifier;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class Notifier {
	public static void notifyServer(String ip, int port, String endpoint, String cookie, String payload) {
		HttpClient client = HttpClient.newHttpClient();
		HttpRequest hrequest = HttpRequest.newBuilder().uri(URI.create("http://" + ip + ":" + port + "/" + endpoint))
				.header("Content-Type", "application/json").header("Cookie", "session=" + cookie)
				.POST(HttpRequest.BodyPublishers.ofString(payload)).build();
		client.sendAsync(hrequest, HttpResponse.BodyHandlers.ofString());
	}
}
