package nhlstreams.data.model;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import lombok.Data;
import lombok.extern.flogger.Flogger;
import nhlstreams.data.model.events.ScoreState;
import nhlstreams.data.model.exceptions.PlayerNotFoundException;
import nhlstreams.data.model.exceptions.TeamNotFoundException;
import nhlstreams.data.model.orgs.Team;

@Flogger
@Data
public class Game implements Serializable{
	private static final long serialVersionUID = 3592683826524004182L;
	
	private String pk;
	private String season;
	private String type;
	private Status gameStatus;
	private long startTime;
	private long endTime;
	private Map<Integer, Player> homePlayers = new HashMap<>();
	private Map<Integer, Player> awayPlayers = new HashMap<>();
	private Team awayTeam;
	private Team homeTeam;
	private ScoreState scoreState = new ScoreState();
	
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
}
