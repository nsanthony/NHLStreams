package nhlstreams.data.ingest;

import java.util.Map.Entry;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import lombok.extern.flogger.Flogger;
import nhlstreams.data.model.Player;

@Flogger
public class DataParser {

	
	
	public static String parseData(String input) {
		//this needs to do something. What input?
		
		return input;
		
	}
	
	
	//move this to another method and change the test. 
	public static int testingThis() {		
		return 2;
	}
	
	public static void getGameMetaData(JsonObject gameObject) {
		JsonElement gameData = gameObject.get("gameData");
		
		//The objects are game, datetime, status, teams, players, venue.
		JsonObject players = gameData.getAsJsonObject().get("players").getAsJsonObject();
		for(Entry<String, JsonElement> entry: players.entrySet()) {
			Player player = getPlayerData(entry.getValue().getAsJsonObject());
			log.atInfo().log("Player %s: %s", player.getId(), player.getFullName());
			log.atInfo().log("%s : %s", entry.getKey(), entry.getValue().toString());
		}
		
	}
	
	public static Player getPlayerData(JsonObject playerObject) {
		Player player = new Player();
		
		player.setId(playerObject.get("id").getAsInt());
		player.setFullName(playerObject.get("fullName").getAsString());
		
		return player;
	}
	
}
