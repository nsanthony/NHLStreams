package nhlstreams;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.http.HttpResponse;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.google.common.flogger.StackSize;
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
		try {
			List<Game> games = dataCtl.getDailySchedule();
			ZonedDateTime currentTime = ZonedDateTime.now(ZoneId.systemDefault());
			for (Game game : games) {
				if ((game.getStartTime() - currentTime.toEpochSecond() < 0)) {
					DataParser parser = new DataParser();
					httpEvent = dataCtl.get("/game/" + game.getPk() + "/feed/live");
					JsonObject jsonObject = new JsonParser().parse(httpEvent.body()).getAsJsonObject();

					// Gson gson = new Gson();
					// gson.toJson(jsonObject, new FileWriter(path));
					parser.getGameMetaData(jsonObject);
					Map<Integer, Event> gameEvents = parser.getEvents(jsonObject);
					Game thisGame = parser.getGame();
					log.atInfo().log("\n\nGame state for %s @ %s (%s): %s to %s\n",
							thisGame.getAwayTeam().getShortName(), thisGame.getHomeTeam().getShortName(),
							thisGame.getGameStatus().abstractGameState, thisGame.getScoreState().getAway(),
							thisGame.getScoreState().getHome());
				} else {
					log.atInfo().log("\n\nGame has not started %s vs %s. Start time: %s\n",
							game.getHomeTeam().getShortName(),
							game.getAwayTeam().getShortName(), 
							Date.from(Instant.ofEpochSecond(game.getStartTime())));
				}
			}

		} catch (URISyntaxException | IOException | InterruptedException | IllegalStateException e) {
			log.atSevere().withCause(e).withStackTrace(StackSize.FULL).log("Failed to get event");
		}
	}

}
