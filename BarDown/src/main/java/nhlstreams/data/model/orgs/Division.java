package nhlstreams.data.model.orgs;

public enum Division {
	ATLANTIC(17, "Atlantic", "/api/v1/divisions/17"),
	CENTRAL(16, "Central", "/api/v1/divisions/16"),
	METRO(18, "Metropolitan", "/api/v1/divisions/18"),
	PACIFIC(15, "Pacific", "/api/v1/divisions/15");
	
	public int id;
	public String name;
	public String link;
	
	Division(int id, String name, String link){
		this.id = id;
		this.name = name;
		this.link = link;
	}

}
