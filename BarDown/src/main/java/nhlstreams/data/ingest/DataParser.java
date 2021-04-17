package nhlstreams.data.ingest;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import lombok.extern.flogger.Flogger;
import nhlstreams.data.model.Handedness;
import nhlstreams.data.model.Player;
import nhlstreams.data.model.Position;
import nhlstreams.data.model.Team;
import nhlstreams.data.processing.DataUtils;

@Flogger
public class DataParser {
	
	private JsonObject playerObject;
	
	public static String parseData(String input) {
		// this needs to do something. What input?

		return input;

	}

	// move this to another method and change the test.
	public static int testingThis() {
		return 2;
	}

	public void getGameMetaData(JsonObject gameObject) {
		JsonElement gameData = gameObject.get("gameData");
		List<Player> playerList = new ArrayList<>();

		// The objects are game, datetime, status, teams, players, venue.
		JsonObject players = gameData.getAsJsonObject().get("players").getAsJsonObject();
		for (Entry<String, JsonElement> entry : players.entrySet()) {
			Player player = getPlayerData(entry.getValue().getAsJsonObject());
			log.atInfo().log("Player entry & populated class:\n%s \n%s", entry.getValue(), player.toString());
			playerList.add(player);
		}

	}

	public Player getPlayerData(JsonObject playerObject) throws NullPointerException{
		Player player = new Player();
		this.playerObject = playerObject;
		// {"id":8471735,"fullName":"Keith
		// Yandle","link":"/api/v1/people/8471735","firstName":"Keith","lastName":"Yandle","primaryNumber":"3","birthDate":"1986-09-09","currentAge":34,
		//"birthCity":"Boston","birthStateProvince":"MA","birthCountry":"USA","nationality":"USA","height":"6'1\"",
		// "weight":196,"active":true,"alternateCaptain":true,"captain":false,"rookie":false,"shootsCatches":"L","rosterStatus":"Y",
		// "currentTeam":{"id":13,"name":"FloridaPanthers","link":"/api/v1/teams/13","triCode":"FLA"},
		//"primaryPosition":{"code":"D","name":"Defenseman","type":"Defenseman","abbreviation":"D"}}
		
		player.setId(getField("id").getAsInt());
		player.setFullName(getField("fullName").getAsString());
		player.setLink(getField("link").getAsString());
		player.setPrimaryPosition(getPosition());
		player.setFirstName(getField("firstName").getAsString());
		player.setLastName(getField("lastName").getAsString());
		player.setPrimaryNumber(getField("primaryNumber").getAsInt());
		player.setBirthDate(Date.valueOf(getField("birthDate").getAsString()));
		player.setCurrentAge(getField("currentAge").getAsInt());
		player.setBirthCity(getField("birthCity").getAsString());
		player.setBirthStateProvince(getField("birthStateProvince").getAsString());
		player.setBirthCountry(getField("birthCountry").getAsString());
		player.setNationality(getField("nationality").getAsString());
		player.setHieght(getHeight(getField("height").getAsString()));
		player.setWeight(getField("weight").getAsInt());
		player.setActive(getField("active").getAsBoolean());
		player.setAltCaptain(getField("alternateCaptain").getAsBoolean());
		player.setCaptain(getField("captain").getAsBoolean());
		player.setRookie(getField("rookie").getAsBoolean());
		player.setRosterStatus(getRosterStatus());
		player.setCurrentTeam(new Team(getField("currentTeam")));
		player.setHand(getField("shootsCatches").getAsString());
		return player;
	}
	
	public Position getPosition() {
		String code = DataUtils.getField("code", getField("primaryPosition").getAsJsonObject()).getAsString();
		switch(code) {
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
	
	public JsonElement getField(String field) {

		if(playerObject.get(field) != null) {
			return playerObject.get(field);
		}else {
			log.atWarning().log("Failed to get field %s", field);
			return DataUtils.error(field);
		}
	}
	
	
	public int getHeight(String heightString) {
		Pattern pattern = Pattern.compile("\\d+");
		Matcher matcher = pattern.matcher(heightString);
		int[] hieghtElements = new int[2];
		int i = 0;
		while(matcher.find()) {
			hieghtElements[i] = Integer.valueOf(matcher.group());
			i++;
		}
		double converted = 2.54 * (12*hieghtElements[0] + hieghtElements[1]);
		return  (int) converted;
	}
	
	public Boolean getRosterStatus() {
		String status = getField("rosterStatus").getAsString();
		switch(status) {
			case "Y":
				return true;
			case "N":
				return false;
			default:
				return true;
		}
	}
	
}
