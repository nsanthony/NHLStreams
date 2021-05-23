package data.controller.ingest;

import java.util.Map;

import com.google.gson.JsonObject;

import common.model.Game;
import common.model.events.Event;
import lombok.Data;

@Data
public class DataParser {
	private Game game;
	private Boolean firstPull = true;

	// The objects are game, datetime, status, teams, players, venue.
	public DataParser getGameMetaData(JsonObject gameObject) {
		GameMetaDataParser metadataParser = new GameMetaDataParser(gameObject);
		this.game = metadataParser.parse();
		return this;
	}
	
	public void getEvents(JsonObject gameObject) {
		JsonObject liveData = gameObject.get("liveData").getAsJsonObject();
		LiveDataParser parser = new LiveDataParser(liveData, game);
		
		Map<Integer, Event> eventList = parser.parse(liveData);
		this.game = parser.getGame();
		if(firstPull == true) {
			game.setGameEvents(eventList);
			firstPull = false;
		}else {
			game.updateEvents(eventList);
		}
	}

}
