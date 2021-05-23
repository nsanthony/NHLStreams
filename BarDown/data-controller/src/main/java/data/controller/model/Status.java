package data.controller.model;

public enum Status {
	FINAL("Final", 7, "Final", 7, false),
	LIVE("Live", 3, "In Progress", 3, false),
	PREVIEW("Preview", 1, "Scheduled", 1, false),
	NULL("null", -1, "null", -1, false);
	
	public String abstractGameState;
	public int codedGameState;
	public String detailedGameState;
	public int statusCode;
	public Boolean startTimeTBD;
	
	
	Status(String abstractGameState, int codedGameState, String detailedGameState, int statusCode, Boolean startTimeTBD){
		this.abstractGameState = abstractGameState;
		this.codedGameState = codedGameState;
		this.detailedGameState = detailedGameState;
		this.statusCode = statusCode;
		this.startTimeTBD = startTimeTBD;
	}
}
