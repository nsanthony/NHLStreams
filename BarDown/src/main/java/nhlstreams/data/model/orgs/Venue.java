package nhlstreams.data.model.orgs;

public enum Venue {
	BBAT(5027, "BB&T Center", "/api/v1/venues/5027", "Sunrise"),
	ACC(5015, "Air Canada Center", "/api/v1/venues/5015", "Toronto"),
	AA(5017, "Amalie Arean", "/api/v1/venues/5015", "Tampa"),
	BC(5026, "Barclays Center", "/api/v1/venues/5016", "Brooklyn"),
	TDG(5085, "TD Gardens", "/api/v1/venues/5085", "Boston");
	
	public int id;
	public String name;
	public String link;
	public String city;
	
	Venue(int id, String name, String link, String city){
		this.id = id;
		this.name = name;
		this.link = link;
		this.city = city;
	}

}
