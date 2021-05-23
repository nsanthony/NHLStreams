package data.controller.model;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import data.controller.model.events.Event;
import data.controller.model.events.ScoreState;
import data.controller.model.exceptions.PlayerNotFoundException;
import data.controller.model.exceptions.TeamNotFoundException;
import data.controller.model.orgs.Team;
import lombok.Data;
import lombok.extern.flogger.Flogger;

@Flogger
@Data
public class Game implements Serializable{
	private static final long serialVersionUID = 3592683826524004182L;
	
	private String pk;
	private String season;
	private String type;
	private Status gameStatus = null;
	private long startTime;
	private long endTime;
	private Map<Integer, Player> homePlayers = new HashMap<>();
	private Map<Integer, Player> awayPlayers = new HashMap<>();
	private Team awayTeam;
	private Team homeTeam;
	private ScoreState scoreState = new ScoreState();
	private int periodTime;
	private String gameClock;
	private String period;
	private Map<Integer, Event> gameEvents = new HashMap<>();
	
	public Player getPlayerById(int id) throws PlayerNotFoundException {
		if(homePlayers.containsKey(id)) {
			return homePlayers.get(id);
		}else if(awayPlayers.containsKey(id)) {
			return awayPlayers.get(id);
		}
		throw new PlayerNotFoundException();
	}
	
	public Team getTeamById(int id) throws TeamNotFoundException {
		if(homeTeam.getId() == id) {
			return homeTeam;
		}else if(awayTeam.getId() == id) {
			return awayTeam;
		}else {
			throw new TeamNotFoundException();
		}
	}
	
	public void updateEvents(Map<Integer, Event> currentGameEvents) {
		Map<Integer, Event> diffEvents = new HashMap<>();
		boolean updated = false;
		
		for(Entry<Integer, Event> eventEntry: currentGameEvents.entrySet()) {
			if(!gameEvents.containsValue(eventEntry.getValue())) {
				diffEvents.put(eventEntry.getKey(), eventEntry.getValue());
				log.atInfo().log("New event at %s (%s): %s", eventEntry.getValue().getPeriodTime() , eventEntry.getKey(),
						eventEntry.getValue().getType());
				updated = true;
			}
		}
		
		if(updated == true) {
			log.atInfo().log("\n\nGame state for %s @ %s (%s): %s to %s w/ %s to go in %s\n",
					awayTeam.getShortName(), homeTeam.getShortName(),
					gameStatus.abstractGameState, 
					scoreState.getAway(), scoreState.getHome(),
					gameClock, period);
			this.gameEvents = currentGameEvents;
		}
	}
}
