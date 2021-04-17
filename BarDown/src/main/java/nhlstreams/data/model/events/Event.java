package nhlstreams.data.model.events;

public enum Event {
	GAME_SCHEDULED("Game Scheduled");
	
	public String type;
	
	Event(String type){
		this.type = type;
	}
}
