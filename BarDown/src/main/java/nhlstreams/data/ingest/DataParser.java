package nhlstreams.data.ingest;

import java.util.Map;

import com.google.gson.JsonObject;

import lombok.Data;
import nhlstreams.data.model.Game;
import nhlstreams.data.model.events.Event;

@Data
public class DataParser {
	private Game game;

	// The objects are game, datetime, status, teams, players, venue.
	public void getGameMetaData(JsonObject gameObject) {
		GameMetaDataParser metadataParser = new GameMetaDataParser(gameObject);
		this.game = metadataParser.parse();
	}
	
	public Map<Integer, Event> getEvents(JsonObject gameObject) {
		JsonObject liveData = gameObject.get("liveData").getAsJsonObject();
		LiveDataParser parser = new LiveDataParser(liveData, game);
		
		Map<Integer, Event> eventList = parser.parse(liveData);
//		log.atInfo().log("this is the event body: %s", eventList);
		return eventList;
	}

}
