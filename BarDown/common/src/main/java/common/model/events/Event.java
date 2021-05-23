package common.model.events;

import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.Map;

import com.google.common.flogger.StackSize;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import common.model.Game;
import common.model.Player;
import common.model.exceptions.EventTypeUnknownException;
import common.model.exceptions.PeriodTypeNotFoundException;
import common.model.exceptions.PlayerNotFoundException;
import common.model.exceptions.TeamNotFoundException;
import common.model.orgs.Team;
import common.utils.DataUtils;
import lombok.Data;
import lombok.extern.flogger.Flogger;

@Flogger
@Data
public class Event {
	private Game game;
	private EventType type = null;
	private Map<Integer, Player> eventPlayers;
	private int eventIndex;
	private int eventId;
	private int period;
	private PeriodType periodType = null;
	private String ordinalNum;
	private int periodTime;
	private int periodTimeRemaining;
	private long dateTime;
	private Coordinates coords;
	private Team team;
	private JsonObject eventString;

	public Event(Game game) {
		this.game = game;
	}

	public void getEvent(JsonObject eventObject) throws EventTypeUnknownException, PeriodTypeNotFoundException {
		this.type = getEventType(eventObject);
		this.eventString = eventObject;

		if (type.involvesPlayers && !type.id.equals(EventType.STOP.id)) {
			getPlayers(eventObject);
			getEventDetails(eventObject);
			getEventCoords(eventObject);
			getEventTeam(eventObject);
		} else if (type.involvesPlayers && type.id.equals(EventType.STOP.id)) {
			getEventDetails(eventObject);
		}
	}

	private EventType getEventType(JsonObject eventObject) throws EventTypeUnknownException {
		JsonElement resultElement = DataUtils.getField("result", eventObject);
		String type = resultElement.getAsJsonObject().get("eventTypeId").getAsString();

		for (EventType eventType : EventType.values()) {
			if (type.equals(eventType.id)) {
				return eventType;
			}
		}
		log.atSevere().withStackTrace(StackSize.NONE).log("Failed to get event of type %s \nEvent: %s \nResult: %s",
				type, eventObject, resultElement);
		throw new EventTypeUnknownException();
	}

	private void getPlayers(JsonObject eventObject) {
		eventPlayers = new HashMap<>();
		try {
			JsonArray playerArray = eventObject.get("players").getAsJsonArray();
			for (JsonElement playerElement : playerArray) {
				int id = playerElement.getAsJsonObject().get("player").getAsJsonObject().get("id").getAsInt();
				Player player;
				try {
					player = game.getPlayerById(id);
					eventPlayers.put(id, player);
				} catch (PlayerNotFoundException e) {
					log.atSevere().withCause(e).log("Failed to find player %s...", playerElement.getAsJsonObject()
							.get("player").getAsJsonObject().get("fullName").getAsString());
				}

			}
		} catch (NullPointerException e) {
			log.atSevere().withCause(e).log("Failed to get players for %s", eventObject);
		}

	}

	// about":{"eventIdx":4,"eventId":51,"period":1,"periodType":"REGULAR","ordinalNum":"1st","periodTime":"00:24",
	// "periodTimeRemaining":"19:36","dateTime":"2018-01-03T00:19:28Z","goals":{"away":0,"home":0}},
	// "coordinates":{"x":75.0,"y":38.0},"team":{"id":14,"name":"Tampa Bay
	// Lightning","link":"/api/v1/teams/14","triCode":"TBL"}
	private void getEventDetails(JsonObject eventObject) throws PeriodTypeNotFoundException {
		JsonObject about = eventObject.get("about").getAsJsonObject();
		try {

			this.eventIndex = about.get("eventIdx").getAsInt();
			this.eventId = about.get("eventId").getAsInt();
			this.period = about.get("period").getAsInt();
			this.dateTime = ZonedDateTime.parse(about.get("dateTime").getAsString()).toEpochSecond();

			getPeriodType(about.get("periodType").getAsString());
			getPeriodTimes(about);
			getScoreState(about.get("goals").getAsJsonObject());
		} catch (NullPointerException e) {
			log.atSevere().withCause(e).log("Failed to get value for event %s : %s", this.type.id, about);
		}
	}

	private void getPeriodType(String type) throws PeriodTypeNotFoundException {
		for (PeriodType periodType : PeriodType.values()) {
			if (type.equals(periodType.type)) {
				this.periodType = periodType;
			}
		}
		if (this.periodType == null) {
			log.atSevere().log("Failed to get period of type %s...", type);
			throw new PeriodTypeNotFoundException();
		}
	}

	private void getPeriodTimes(JsonObject about) {
		this.periodTime = getSeconds(about.get("periodTime").getAsString());
		this.periodTimeRemaining = getSeconds(about.get("periodTimeRemaining").getAsString());
	}

	private int getSeconds(String clockTime) {
		String[] minutesSeconds = clockTime.split(":");
		int secondsFromMinutes = 60 * Integer.valueOf(minutesSeconds[0]);
		int seconds = Integer.valueOf(minutesSeconds[1]);
		if((secondsFromMinutes + seconds) > game.getPeriodTime()) {
			game.setGameClock(clockTime);
			game.setPeriod(String.valueOf(period));
		}
		return secondsFromMinutes + seconds;
	}

	private void getScoreState(JsonObject scoreObject) {
		ScoreState scoreState = game.getScoreState();
		scoreState.setHome(scoreObject.get("home").getAsInt());
		scoreState.setAway(scoreObject.get("away").getAsInt());

		game.setScoreState(scoreState);
	}

	private void getEventCoords(JsonObject eventObject) {
		JsonElement coordsObject = DataUtils.getField("coordinates", eventObject);
		if (!coordsObject.equals("null")) {
			coords = new Coordinates();
			JsonElement xcoords = DataUtils.getField("x", coordsObject.getAsJsonObject());
			JsonElement ycoords = DataUtils.getField("y", eventObject.getAsJsonObject());
			if (!xcoords.getAsString().equals("null") && !ycoords.getAsString().equals("null")) {
				coords.setX(xcoords.getAsDouble());
				coords.setY(ycoords.getAsDouble());
			}
		}
	}

	private void getEventTeam(JsonObject eventObject) {
		int eventTeamId = eventObject.get("team").getAsJsonObject().get("id").getAsInt();
		try {
			this.team = game.getTeamById(eventTeamId);
		} catch (TeamNotFoundException e) {
			log.atSevere().withCause(e).log("Failed to find %s in game...", eventObject.get("team").getAsString());
		}
	}
	
	//TODO: fill out this method to make more readable.
//	public String toString() {
//		return this.toString();
//	}

}
