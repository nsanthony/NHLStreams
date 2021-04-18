package nhlstreams;

import java.io.FileWriter;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.http.HttpResponse;
import java.sql.Time;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.google.common.flogger.StackSize;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import lombok.extern.flogger.Flogger;
import nhlstreams.data.controller.DataController;
import nhlstreams.data.ingest.DataParser;
import nhlstreams.data.model.Game;
import nhlstreams.data.model.events.Event;

@Flogger
public class BarDown {
	// http://statsapi.web.nhl.com/api/v1/schedule will give you todays schedule
	private static String baseUrl = "http://statsapi.web.nhl.com/api/v1";
	private static String dailySchedule = "/schedule";
	private static String eventExtension = "/game/2017020602/feed/live";
	private static String path = "event.json";

	public static void main(String[] args) {

		DataController dataCtl = new DataController(baseUrl);
		HttpResponse<String> httpEvent = null;
		DataParser parser = new DataParser();
		try {
			List<Game> games = dataCtl.getDailySchedule();
			ZonedDateTime currentTime = ZonedDateTime.now(ZoneId.systemDefault());
			for (Game game : games) {
				if ((game.getStartTime() -  currentTime.toEpochSecond() < 0)) {
					httpEvent = dataCtl.get("/game/" + game.getPk() + "/feed/live");
					JsonObject jsonObject = new JsonParser().parse(httpEvent.body()).getAsJsonObject();

					// Gson gson = new Gson();
					// gson.toJson(jsonObject, new FileWriter(path));
					parser.getGameMetaData(jsonObject);
					Map<Integer, Event> gameEvents = parser.getEvents(jsonObject);
					Game thisgame = parser.getGame();
					log.atInfo().log("Got this schedule: %s", thisgame.getPk());
					
					log.atInfo().log("\nGame state for %s @ %s (%s): %s to %s",
							game.getAwayTeam().getShortName(), game.getHomeTeam().getShortName(),
							game.getGameStatus().abstractGameState, 
							game.getScoreState().getAway(), game.getScoreState().getHome());
				}else {
					log.atInfo().log("Game has not started %s vs %s \n%s < %s",
							game.getHomeTeam().getShortName(), game.getAwayTeam().getShortName(),
							currentTime.toEpochSecond(), game.getStartTime());
				}
				//currentTime, Date.from(Instant.ofEpochSecond(game.getStartTime()))
			}

		} catch (URISyntaxException | IOException | InterruptedException | IllegalStateException e) {
			log.atSevere().withCause(e).withStackTrace(StackSize.FULL).log("Failed to get event");
		}
	}

}
