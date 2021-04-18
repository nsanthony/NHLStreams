package nhlstreams.data.controller;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import lombok.extern.flogger.Flogger;
import nhlstreams.data.ingest.DataParser;
import nhlstreams.data.ingest.GameMetaDataParser;
import nhlstreams.data.model.Game;

@Flogger
public class DataController {
	private String nhlUrl;
	private HttpClient clientConnection;
	private static String dailySchedule = "/schedule";

	// This should probably set the http endpoint connection.
	public DataController(String url) {
		this.nhlUrl = url;
		initConnection();
	}

	public HttpResponse<String> get(String url)
			throws URISyntaxException, IOException, InterruptedException {
		HttpRequest request = createRequest(nhlUrl + url);
		log.atFine().log("Sending request %s....", request);
		HttpResponse<String> event = clientConnection.send(request, HttpResponse.BodyHandlers.ofString());
		log.atFine().log("Got response: %s", event);

		return event;
	}

	public void initConnection() {
		clientConnection = HttpClient.newBuilder().build();
	}

	private HttpRequest createRequest(String url) throws URISyntaxException{
		HttpRequest request = HttpRequest.newBuilder().uri(new URI(url)).GET().build();

		return request;

	}
	
	
	public List<Game> getDailySchedule() throws URISyntaxException, IOException, InterruptedException {
		List<Game> gameList = new ArrayList<>();
		
		
		HttpResponse<String> scheduleResponse = get(dailySchedule);
		JsonObject jsonObject = new JsonParser().parse(scheduleResponse.body()).getAsJsonObject();
		
		
		JsonArray gamesObject = jsonObject
				.get("dates").getAsJsonArray()
				.get(0).getAsJsonObject()
				.get("games").getAsJsonArray();
		
		for(JsonElement gameElement: gamesObject) {
			String pk = gameElement.getAsJsonObject()
					.get("gamePk").getAsString();
			HttpResponse<String> gameDataResponse = get("/game/" + pk + "/feed/live");
			JsonObject gameObject = new JsonParser().parse(gameDataResponse.body()).getAsJsonObject();
			GameMetaDataParser parser = new GameMetaDataParser(gameObject);
			parser.getGameMetadata(gameObject
					.get("gameData").getAsJsonObject()
					.get("game"));
			
			parser.getDatetime(gameObject
					.get("gameData").getAsJsonObject()
					.get("datetime"));
		
			Game game = parser.parse();
			gameList.add(game);
		}
		return gameList;
		
	}
		
}
