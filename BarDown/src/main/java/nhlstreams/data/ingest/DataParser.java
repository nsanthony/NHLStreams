package nhlstreams.data.ingest;

import java.sql.Date;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import lombok.extern.flogger.Flogger;
import nhlstreams.data.model.Game;
import nhlstreams.data.model.Player;
import nhlstreams.data.model.Position;
import nhlstreams.data.model.Status;
import nhlstreams.data.model.Team;
import nhlstreams.data.model.Venue;
import nhlstreams.data.processing.DataUtils;

@Flogger
public class DataParser {

	private Game game = new Game();
	
	public static String parseData(String input) {
		// this needs to do something. What input?

		return input;

	}

	// move this to another method and change the test.
	public static int testingThis() {
		return 2;
	}

	// The objects are game, datetime, status, teams, players, venue.
	public Game getGameMetaData(JsonObject gameObject) {
		JsonObject gameData = gameObject.get("gameData").getAsJsonObject();
		
		getPlayerData(gameData.get("players"));
		getGameMetadata(gameData.get("game"));
		getGameStatus(gameData.get("status"));
		getDatetime(gameData.get("datetime"));
		
		return game;
	}

	public void getPlayerData(JsonElement playerElement) throws NullPointerException {
		Map<String, Player> players = new HashMap<>();
		for (Entry<String, JsonElement> entry : playerElement.getAsJsonObject().entrySet()) {
			Player player = new Player();
			JsonObject playerObject = entry.getValue().getAsJsonObject();
			// {"id":8471735,"fullName":"Keith
			// Yandle","link":"/api/v1/people/8471735","firstName":"Keith","lastName":"Yandle","primaryNumber":"3","birthDate":"1986-09-09","currentAge":34,
			// "birthCity":"Boston","birthStateProvince":"MA","birthCountry":"USA","nationality":"USA","height":"6'1\"",
			// "weight":196,"active":true,"alternateCaptain":true,"captain":false,"rookie":false,"shootsCatches":"L","rosterStatus":"Y",
			// "currentTeam":{"id":13,"name":"FloridaPanthers","link":"/api/v1/teams/13","triCode":"FLA"},
			// "primaryPosition":{"code":"D","name":"Defenseman","type":"Defenseman","abbreviation":"D"}}

			player.setId(DataUtils.getField("id", playerObject).getAsInt());
			player.setFullName(DataUtils.getField("fullName", playerObject).getAsString());
			player.setLink(DataUtils.getField("link", playerObject).getAsString());
			player.setPrimaryPosition(getPosition(playerObject));
			player.setFirstName(DataUtils.getField("firstName", playerObject).getAsString());
			player.setLastName(DataUtils.getField("lastName", playerObject).getAsString());
			player.setPrimaryNumber(DataUtils.getField("primaryNumber", playerObject).getAsInt());
			player.setBirthDate(Date.valueOf(DataUtils.getField("birthDate", playerObject).getAsString()));
			player.setCurrentAge(DataUtils.getField("currentAge", playerObject).getAsInt());
			player.setBirthCity(DataUtils.getField("birthCity", playerObject).getAsString());
			player.setBirthStateProvince(DataUtils.getField("birthStateProvince", playerObject).getAsString());
			player.setBirthCountry(DataUtils.getField("birthCountry", playerObject).getAsString());
			player.setNationality(DataUtils.getField("nationality", playerObject).getAsString());
			player.setHieght(getHeight(DataUtils.getField("height", playerObject).getAsString()));
			player.setWeight(DataUtils.getField("weight", playerObject).getAsInt());
			player.setActive(DataUtils.getField("active", playerObject).getAsBoolean());
			player.setAltCaptain(DataUtils.getField("alternateCaptain", playerObject).getAsBoolean());
			player.setCaptain(DataUtils.getField("captain", playerObject).getAsBoolean());
			player.setRookie(DataUtils.getField("rookie", playerObject).getAsBoolean());
			player.setRosterStatus(getRosterStatus(playerObject));
			player.setCurrentTeam(new Team(DataUtils.getField("currentTeam", playerObject)));
			player.setHand(DataUtils.getField("shootsCatches", playerObject).getAsString());

			players.put(player.getFullName(), player);
		}
		game.setPlayers(players);
	}

	public Position getPosition(JsonObject playerObject) {
		String code = DataUtils.getField("code", DataUtils.getField("primaryPosition", playerObject).getAsJsonObject()).getAsString();
		
		switch (code) {
		case "C":
			return Position.CENTER;
		case "R":
			return Position.RIGHTWING;
		case "L":
			return Position.LEFTWING;
		case "D":
			return Position.DEFENSE;
		case "G":
			return Position.GOALIE;
		default:
			log.atSevere().log("Failed to get position for %s", code);
			return null;
		}
	}

	public void getGameMetadata(JsonElement metaDataElement) {
		JsonObject gameMetadataObject = metaDataElement.getAsJsonObject();
		
		game.setPk(DataUtils.getField("pk", gameMetadataObject).getAsString());
		game.setSeason(DataUtils.getField("season", gameMetadataObject).getAsString());
		game.setType(DataUtils.getField("type", gameMetadataObject).getAsString());
	}

	public void getGameStatus(JsonElement statusElement) {
		String detailedCode = DataUtils.getField("detailedState", statusElement.getAsJsonObject()).getAsString();
		
		switch (detailedCode) {
		case "Final":
			game.setGameStatus(Status.FINAL);
		default:
			game.setGameStatus(Status.FINAL);
		}
	}

	// "dateTime":"2018-01-03T01:00:00Z","endDateTime":"2018-01-03T03:43:41Z"
	public void getDatetime(JsonElement datetimeElement) {
		JsonObject datetimeObject = datetimeElement.getAsJsonObject();
		
		String startTimeString = DataUtils.getField("dateTime", datetimeObject).getAsString();
		String endTimeString = DataUtils.getField("endDateTime", datetimeObject).getAsString();
		
		ZonedDateTime startTime = ZonedDateTime.parse(startTimeString);
		ZonedDateTime endTime = ZonedDateTime.parse(endTimeString);

		game.setStartTime(startTime.toEpochSecond());
		game.setEndTime(endTime.toEpochSecond());
	}

	// {"id":5098,"name":"Xcel Energy Center","link":"/api/v1/venues/5098"}
	public Venue getVenueData(JsonObject venueObject) {
		Venue venue = new Venue();
		venue.setId(DataUtils.getField("id", venueObject).getAsInt());
		venue.setName(DataUtils.getField("name", venueObject).getAsString());
		venue.setLink(DataUtils.getField("link", venueObject).getAsString());

		return venue;
	}

	public int getHeight(String heightString) {
		Pattern pattern = Pattern.compile("\\d+");
		Matcher matcher = pattern.matcher(heightString);
		int[] hieghtElements = new int[2];
		int i = 0;
		while (matcher.find()) {
			hieghtElements[i] = Integer.valueOf(matcher.group());
			i++;
		}
		double converted = 2.54 * (12 * hieghtElements[0] + hieghtElements[1]);
		return (int) converted;
	}

	public Boolean getRosterStatus(JsonObject playerObject) {
		String status = DataUtils.getField("rosterStatus", playerObject).getAsString();
		switch (status) {
		case "Y":
			return true;
		case "N":
			return false;
		default:
			return true;
		}
	}

}
