package common.model.events;

public enum PeriodType {
	REGULAR("REGULAR"), 
	OVERTIME("OVERTIME"), 
	SHOOTOUT("SHOOTOUT");

	
	public String type;
	
	PeriodType(String type){
		this.type = type;
	}
}
