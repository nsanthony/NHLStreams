package common.model.orgs;

public enum Venue {
	BBAT(5027, "BB&T Center", "Sunrise"),
	ACC(5015, "Air Canada Center","Toronto"),
	AA(5017, "Amalie Arean", "Tampa"),
	BC(5026, "Barclays Center", "Brooklyn"),
	GRA(5043, "Gaila River Arena", "Glendale"),
	EC(5076, "Enterprise Center", "St. Louis"),
	TDG(5085, "TD Gardens", "Boston"),
	BMTSP(5058, "Bell MTS Place",  "Winnipeg"),
	RP(5100, "Rogers Place", "Edmonton"),
	PNC(5066, "PNC Arena", "Raleigh"),
	BA(5030, "Bridgestone Arena", "Nashville"),
	UNC(5092, "United Center", "Chicago"),
	LCA(5145, "Little Caesars Arena", "Detroit"),
	XEC(5098, "Xcel Energy Center", "St.Paul"),
	AAC(5019, "American Airlines Center", "Dallas"),
	NWS(5059, "Nationwide Arena", "Columbus"),
	MSG(5054, "Madison Square Garden", "New York"),
	PRC(6001, "Prudential Center", "Newark"),
	WFC(5096, "Wells Fargo Center", "Philadelphia"),
	CAPONE(5094, "Capital One Arena", "Washington"),
	KBC(5039, "KeyBank Center", "Buffalo"),
	PPG(5034, "PPG Paints Arena", "Pittsburgh"),
	BCR(5028, "Bell Centre", "Montréal"),
	CTC(5031, "Canadian Tire Centre", "Ottawa"),
	HON(5046, "Honda Center", "Aneheim"),
	TMA(5178, "T-Mobile Arena", "Las Vegas"),
	ROG(5073, "Rogers Arena", "Vancouver"),
	BALL(5064, "Ball Arena", "Denver"),
	STAPLES(5081, "STAPLES Center", "Los Angeles"),
	
	NULL(0, "null", "null");
	
	public int id;
	public String name;
	public String link;
	public String city;
	
	Venue(int id, String name, String city){
		this.id = id;
		this.name = name;
		this.link = "/api/v1/venues/" + id;
		this.city = city;
	}

}
