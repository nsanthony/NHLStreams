package nhlstreams.data.ingest;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import lombok.extern.flogger.Flogger;
import nhlstreams.data.model.Game;
import nhlstreams.data.model.events.Event;
import nhlstreams.data.processing.DataUtils;

@Flogger
public class LiveDataParser {
	private JsonObject liveData;
	private JsonObject startingData;
	private Game game;
	private List<Event> eventList = new ArrayList<>();
	
	LiveDataParser(JsonObject startingData, Game game){
		this.startingData = startingData;
		this.game = game;
	}
	
	public List<Event> parse(JsonObject liveDataObject) {
		this.liveData = liveDataObject;
		JsonArray plays = liveDataObject
				.get("plays").getAsJsonObject()
				.get("allPlays").getAsJsonArray();
		
		for(JsonElement play: plays) {
			JsonObject playObject = play.getAsJsonObject();
			getEvent(play);
//			log.atInfo().log("%s", playObject);
		}
		
		return eventList;
	}
	
	public void getEvent(JsonElement playElement) {
		JsonObject play = playElement.getAsJsonObject();
		JsonObject eventElementType = play
				.get("result").getAsJsonObject();
//		log.atInfo().log("Got event %s", play);
		if(eventElementType.equals("Game Scheduled")) {
			eventList.add(Event.GAME_SCHEDULED);
		}
	}

}
