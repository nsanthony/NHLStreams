package nhlstreams.data.model;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import lombok.Data;
import nhlstreams.data.processing.DataUtils;

@Data
public class Team {
	private String name;
	private int id;
	private String link;
	private String triCode;
	private TeamCode code;
	
	//"currentTeam":{"id":13,"name":"FloridaPanthers","link":"/api/v1/teams/13","triCode":"FLA"}
	public Team(JsonElement teamElement) {
		JsonObject teamObject = teamElement.getAsJsonObject();
		this.name = DataUtils.getField("name", teamObject).getAsString();
		this.id  = DataUtils.getField("id", teamObject).getAsInt();
		this.link = DataUtils.getField("link", teamObject).getAsString();
		this.triCode = DataUtils.getField("triCode", teamObject).getAsString();
	}
}
