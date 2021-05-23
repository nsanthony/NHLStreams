package data.controller.model.events;

public enum EventType {
	GAME_SCHEDULED("GAME_SCHEDULED", false),
	PERIOD_READY("PERIOD_READY", false),
	PERIOD_START("PERIOD_START", false),
	PERIOD_END("PERIOD_END", false),
	PERIOD_OFFICIAL("PERIOD_OFFICIAL", false),
	GAME_END("GAME_END", false),
	GAME_OFFICIAL("GAME_OFFICIAL", false),
	
	CHALLENGE("CHALLENGE", false),
	
	FACEOFF("FACEOFF", true),
	HIT("HIT", true),
	SHOT("SHOT", true),
	GIVEAWAY("GIVEAWAY", true),
	STOP("STOP", true),
	MISSED_SHOT("MISSED_SHOT", true),
	TAKEAWAY("TAKEAWAY", true),
	BLOCKED_SHOT("BLOCKED_SHOT", true),
	PENALTY("PENALTY", true),
	GOAL("GOAL", true);

	
	public String id;
	public Boolean involvesPlayers;
	
	EventType(String id, Boolean involvesPlayers){
		this.id = id;
		this.involvesPlayers = involvesPlayers;
	}
}
