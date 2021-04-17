package nhlstreams.data.ingest;

import java.util.List;

import com.google.gson.JsonObject;

import lombok.Data;
import lombok.extern.flogger.Flogger;
import nhlstreams.data.model.Game;
import nhlstreams.data.model.events.Event;

@Flogger
@Data
public class DataParser {
	private Game game;

	// The objects are game, datetime, status, teams, players, venue.
	public void getGameMetaData(JsonObject gameObject) {
		GameMetaDataParser metadataParser = new GameMetaDataParser(gameObject);
		this.game = metadataParser.parse();
	}
	
	public void getEvents(JsonObject gameObject) {
		JsonObject liveData = gameObject.get("liveData").getAsJsonObject();
		LiveDataParser parser = new LiveDataParser(liveData, game);
		
		List<Event> eventList = parser.parse(liveData);
		log.atInfo().log("this is the event body: %s", eventList);
	}

}
