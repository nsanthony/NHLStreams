package common.model;

public enum Position {
	DEFENSE("D", "Defenseman", "Defenseman", "D"),
	CENTER("C", "Center", "Forward", "C"),
	LEFTWING("L", "Left Wing", "Forward", "LW"),
	RIGHTWING("R", "Right Wing", "Forward", "RW"),
	GOALIE("G", "Goalie", "Goalie", "G");
	
	
	public String code;
	public String name;
	public String type;
	public String abbreviation;

	//"primaryPosition":{"code":"D","name":"Defenseman","type":"Defenseman","abbreviation":"D"}}
	Position(String code, String name, String type, String abbreviation) {
		this.code = code;
		this.name = name;
		this.type = type;
		this.abbreviation = abbreviation;
	}
}
