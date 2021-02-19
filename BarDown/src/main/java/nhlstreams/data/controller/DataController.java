package nhlstreams.data.controller;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import com.google.common.flogger.FluentLogger;

public class DataController {
	private static final FluentLogger logger = FluentLogger.forEnclosingClass();   	
	private String nhlUrl;
	private HttpClient clientConnection;
	
	//This should probably set the http endpoint connection.
	public DataController(String url) {
		this.nhlUrl = url;
		initConnection();
	}
	
	
	public HttpResponse<String> getLatestEvent(String url) throws URISyntaxException, IOException, InterruptedException {
		HttpRequest request = createRequest(nhlUrl + url);
		HttpResponse<String> event = clientConnection.send(request, HttpResponse.BodyHandlers.ofString());
		
		return event;
	}
	
	
	
	public void initConnection() {
		clientConnection = HttpClient.newBuilder().build();
	}
	
	
	public HttpRequest createRequest(String url) throws URISyntaxException {
		HttpRequest request = HttpRequest.newBuilder()
				.uri(new URI(url))
				.GET()
				.build();
		
		return request;
		
	}

}
