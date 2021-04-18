package nhlstreams.data.controller;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import com.google.common.flogger.StackSize;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import lombok.extern.flogger.Flogger;
import nhlstreams.data.ingest.DataParser;
import nhlstreams.data.ingest.GameMetaDataParser;
import nhlstreams.data.model.Game;
import nhlstreams.data.model.Status;

@Flogger
public class DataController {
	private String nhlUrl;
	private HttpClient clientConnection;
	private static String dailySchedule = "/schedule";
	private static String baseUrl = "http://statsapi.web.nhl.com/api/v1";

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
			log.atInfo().log("Game %s @ %s (%s) starts at %s \n\n",
					game.getAwayTeam().getShortName(), game.getHomeTeam().getShortName(),
					pk, Date.from(Instant.ofEpochSecond(game.getStartTime())));
		}
		return gameList;
		
	}
	
	
	public void dailyRunner() {
		DataController dataCtl = new DataController(baseUrl);
		HttpResponse<String> httpEvent = null;
		try {
			List<Game> games = dataCtl.getDailySchedule();
			ZonedDateTime currentTime = ZonedDateTime.now(ZoneId.systemDefault());
			List<Game> finishedGames = new ArrayList<>();
			
			while(finishedGames.size() != games.size()) {
				for (Game game : games) {
					if ((game.getStartTime() - currentTime.toEpochSecond() < 0) && game.getGameStatus() == Status.LIVE) {
						DataParser parser = new DataParser();
						httpEvent = dataCtl.get("/game/" + game.getPk() + "/feed/live");
						JsonObject jsonObject = new JsonParser().parse(httpEvent.body()).getAsJsonObject();
	
						// Gson gson = new Gson();
						// gson.toJson(jsonObject, new FileWriter(path));
						parser.getGameMetaData(jsonObject);
						parser.getEvents(jsonObject);
						game = parser.getGame();
						
						log.atInfo().log("\n\nGame state for %s @ %s (%s): %s to %s\n",
								game.getAwayTeam().getShortName(), game.getHomeTeam().getShortName(),
								game.getGameStatus().abstractGameState, game.getScoreState().getAway(),
								game.getScoreState().getHome());
						
				
					}else if(game.getGameStatus() == Status.FINAL && !finishedGames.contains(game)) {
						finishedGames.add(game);
						
					} else {
						log.atInfo().log("\n\nGame has not started %s vs %s. Start time: %s\n",
								game.getHomeTeam().getShortName(),
								game.getAwayTeam().getShortName(), 
								Date.from(Instant.ofEpochSecond(game.getStartTime())));
					}
				}
				TimeUnit.SECONDS.sleep(10);
			}

		} catch (URISyntaxException | IOException | InterruptedException | IllegalStateException e) {
			log.atSevere().withCause(e).withStackTrace(StackSize.FULL).log("Failed to get event");
		}
	}
	
	
	
	public void gameRunner(String gamePk) {
		
		DataController dataCtl = new DataController(baseUrl);
		HttpResponse<String> httpEvent = null;
		Boolean gameComplete = false;
		try {
			DataParser parser = new DataParser();
			httpEvent = dataCtl.get("/game/" + gamePk + "/feed/live");
			JsonObject jsonObject = new JsonParser().parse(httpEvent.body()).getAsJsonObject();
			parser.getGameMetaData(jsonObject);
			Game game = parser.getGame();
			
			while(game.getGameStatus() != Status.FINAL) {
				if (game.getGameStatus() == Status.LIVE) {
					
					parser.getEvents(jsonObject);
					game = parser.getGame();
					
					log.atInfo().log("\n\nGame state for %s @ %s (%s): %s to %s w/ %s to go in %s\n",
							game.getAwayTeam().getShortName(), game.getHomeTeam().getShortName(),
							game.getGameStatus().abstractGameState, 
							game.getScoreState().getAway(), game.getScoreState().getHome(),
							game.getGameClock(), game.getPeriod());
					
			
				}else if(game.getGameStatus() == Status.FINAL) {
					gameComplete = true;
				} else {
					log.atInfo().log("\n\nGame has not started %s vs %s. Start time: %s\n",
							game.getHomeTeam().getShortName(),
							game.getAwayTeam().getShortName(), 
							Date.from(Instant.ofEpochSecond(game.getStartTime())));
				}
				TimeUnit.SECONDS.sleep(5);
			}

		} catch (URISyntaxException | IOException | InterruptedException | IllegalStateException e) {
			log.atSevere().withCause(e).withStackTrace(StackSize.FULL).log("Failed to get event");
		}
	}
		
}
