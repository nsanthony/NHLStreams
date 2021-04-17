package nhlstreams.data.model;

import java.io.Serializable;
import java.util.Map;

import lombok.Data;
import nhlstreams.data.model.orgs.Team;

@Data
public class Game implements Serializable{
	private static final long serialVersionUID = 3592683826524004182L;
	
	private String pk;
	private String season;
	private String type;
	private Status gameStatus;
	private long startTime;
	private long endTime;
	private Map<Integer, Player> homePlayers;
	private Map<Integer, Player> awayPlayers;
	private Team awayTeam;
	private Team homeTeam;
}
