package nhlstreams.data.ingest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.google.common.flogger.StackSize;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import lombok.extern.flogger.Flogger;
import nhlstreams.data.model.Game;
import nhlstreams.data.model.events.Event;
import nhlstreams.data.model.exceptions.EventTypeUnknownException;
import nhlstreams.data.model.exceptions.PeriodTypeNotFoundException;
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
		Map<String, JsonObject> sampleEventMap = new HashMap<>();
		for(JsonElement play: plays) {
			JsonObject playObject = play.getAsJsonObject();
			String eventID = DataUtils.getField("result", playObject)
					.getAsJsonObject().get("eventTypeId").getAsString();
			if(!sampleEventMap.containsKey(eventID)) {
				sampleEventMap.put(eventID, playObject);
				log.atInfo().log("%s : %s", eventID, playObject);
			}
			getEvent(play);
		}
		
		return eventList;
	}
	
	public void getEvent(JsonElement playElement) {
		JsonObject play = playElement.getAsJsonObject();
		Event event = new Event(game);
		try {
			event.getEvent(play);
		} catch (EventTypeUnknownException | PeriodTypeNotFoundException e) {
			// TODO Auto-generated catch block
			log.atSevere().withCause(e).withStackTrace(StackSize.NONE)
				.log("Failed to get event....");
		}
		
		
	}

}
