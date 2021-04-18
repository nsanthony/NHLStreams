package nhlstreams.data.model.orgs;

public enum Division {
	ATLANTIC(17, "Atlantic"),
	CENTRAL(16, "Central"),
	METRO(18, "Metropolitan"),
	PACIFIC(15, "Pacific"),
	
	//covid ones:
	EAST(25, "MassMutual East"),
	DISCENTRAL(26, "Discover Central"),
	WEST(27, "Honda West"),
	NORTH(28, "Scotia North");
	
	public int id;
	public String name;
	public String link;
	
	Division(int id, String name){
		this.id = id;
		this.name = name;
		this.link = "/api/v1/divisions/" + id;
	}

}
