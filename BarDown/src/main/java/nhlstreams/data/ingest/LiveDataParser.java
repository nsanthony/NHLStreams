package nhlstreams.data.ingest;

import java.util.HashMap;
import java.util.Map;

import com.google.common.flogger.StackSize;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import lombok.Data;
import lombok.extern.flogger.Flogger;
import nhlstreams.data.model.Game;
import nhlstreams.data.model.events.Event;
import nhlstreams.data.model.exceptions.EventTypeUnknownException;
import nhlstreams.data.model.exceptions.PeriodTypeNotFoundException;

@Flogger
@Data
public class LiveDataParser {
	private Game game;
	private Map<Integer, Event> eventList = new HashMap<>();
	
	LiveDataParser(JsonObject startingData, Game game){
		this.game = game;
	}
	
	public Map<Integer, Event> parse(JsonObject liveDataObject) {
		JsonArray plays = liveDataObject
				.get("plays").getAsJsonObject()
				.get("allPlays").getAsJsonArray();
		for(JsonElement play: plays) {
			Event currentEvent = getEvent(play);
			eventList.put(currentEvent.getEventId(), currentEvent);
		}
		
		return eventList;
	}
	
	public Event getEvent(JsonElement playElement) {
		JsonObject play = playElement.getAsJsonObject();
		Event event = new Event(game);
		try {
			event.getEvent(play);
			game = event.getGame();
		} catch (EventTypeUnknownException | PeriodTypeNotFoundException e) {
			// TODO Auto-generated catch block
			log.atSevere().withCause(e).withStackTrace(StackSize.NONE)
				.log("Failed to get event....");
		}
		return event;
	}

}
