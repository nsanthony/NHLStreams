package common.model.orgs;

public enum Conference {
	EASTERN(6, "Eastern", "/api/v1/conferences/6"),
	WESTERN(5, "Western", "/api/v1/conferences/5");
	
	public int id;
	public String name;
	public String link;
	
	Conference(int id, String name, String link){
		this.id = id;
		this.name = name;
		this.link = link;
	}

}
