package nhlstreams.data.ingest;

import java.sql.Date;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.common.flogger.StackSize;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import lombok.Data;
import lombok.extern.flogger.Flogger;
import nhlstreams.data.model.Game;
import nhlstreams.data.model.Player;
import nhlstreams.data.model.Position;
import nhlstreams.data.model.Status;
import nhlstreams.data.model.exceptions.ConferenceNotFoundException;
import nhlstreams.data.model.exceptions.DivsionNotFoundException;
import nhlstreams.data.model.exceptions.TeamNotFoundException;
import nhlstreams.data.model.exceptions.VenueNotFoundException;
import nhlstreams.data.model.orgs.Team;
import nhlstreams.data.processing.DataUtils;

@Flogger
@Data
public class GameMetaDataParser {
	
	private Game game = new Game();
	private JsonObject gameObject;
	
	public GameMetaDataParser(JsonObject gameObject){
		this.gameObject = gameObject;
	}
	
	public Game parse() {
		JsonObject gameData = gameObject.get("gameData").getAsJsonObject();
		
		getGameMetadata(gameData.get("game"));
		getHomeAwayTeams(gameData.get("teams"));
		getGameStatus(gameData.get("status"));
		getDatetime(gameData.get("datetime"));
		getPlayerData(gameData.get("players"));
		
		return game;
	}
	
	//{"id":13,"name":"Florida Panthers","link":"/api/v1/teams/13",
	//"venue":{"id":5027,"name":"BB&T Center","link":"/api/v1/venues/5027",
	//"city":"Sunrise","timeZone":{"id":"America/New_York","offset":-5,"tz":"EST"}},
	//"abbreviation":"FLA","triCode":"FLA","teamName":"Panthers","locationName":"Florida",
	//"firstYearOfPlay":"1993","division":{"id":17,"name":"Atlantic","link":"/api/v1/divisions/17"},
	//"conference":{"id":6,"name":"Eastern","link":"/api/v1/conferences/6"},
	//"franchise":{"franchiseId":33,"teamName":"Panthers","link":"/api/v1/franchises/33"},
	//"shortName":"Florida","officialSiteUrl":"http://www.floridapanthers.com","franchiseId":33,"active":true}
	public void getHomeAwayTeams(JsonElement teamElement) {
		JsonObject awayTeamObject = teamElement.getAsJsonObject()
				.get("away").getAsJsonObject();
		
		JsonObject homeTeamObject = teamElement.getAsJsonObject()
				.get("home").getAsJsonObject();
		
		
		try {
			game.setHomeTeam(new Team(homeTeamObject));
			game.setAwayTeam(new Team(awayTeamObject));
		} catch (VenueNotFoundException | DivsionNotFoundException | 
				TeamNotFoundException | ConferenceNotFoundException e) {
			// TODO Auto-generated catch block
			log.atSevere().withCause(e).withStackTrace(StackSize.FULL)
				.log("Failed to iniliazie home/away teams \n%s \n\n%s", homeTeamObject, awayTeamObject);
		}
	}
	
	public void getPlayerData(JsonElement playerElement) throws NullPointerException {
		Map<Integer, Player> homePlayers = new HashMap<>();
		Map<Integer, Player> awayPlayers = new HashMap<>();
		
		for (Entry<String, JsonElement> entry : playerElement.getAsJsonObject().entrySet()) {
			Player player = new Player();
			JsonObject playerObject = entry.getValue().getAsJsonObject();
			try {
				player.setId(DataUtils.getField("id", playerObject).getAsInt());
				player.setFullName(DataUtils.getField("fullName", playerObject).getAsString());
				player.setLink(DataUtils.getField("link", playerObject).getAsString());
				player.setPrimaryPosition(getPosition(playerObject));
				player.setFirstName(DataUtils.getField("firstName", playerObject).getAsString());
				player.setLastName(DataUtils.getField("lastName", playerObject).getAsString());
				//player.setPrimaryNumber(DataUtils.getField("primaryNumber", playerObject).getAsInt());
				player.setBirthDate(Date.valueOf(DataUtils.getField("birthDate", playerObject).getAsString()));
				player.setCurrentAge(DataUtils.getField("currentAge", playerObject).getAsInt());
				player.setBirthCity(DataUtils.getField("birthCity", playerObject).getAsString());
				player.setBirthStateProvince(DataUtils.getField("birthStateProvince", playerObject).getAsString());
				player.setBirthCountry(DataUtils.getField("birthCountry", playerObject).getAsString());
				player.setNationality(DataUtils.getField("nationality", playerObject).getAsString());
				player.setHieght(getHeight(DataUtils.getField("height", playerObject).getAsString()));
				player.setWeight(DataUtils.getField("weight", playerObject).getAsInt());
				//player.setActive(DataUtils.getField("active", playerObject).getAsBoolean()); //bug with this not always being there
				player.setAltCaptain(DataUtils.getField("alternateCaptain", playerObject).getAsBoolean());
				player.setCaptain(DataUtils.getField("captain", playerObject).getAsBoolean());
				player.setRookie(DataUtils.getField("rookie", playerObject).getAsBoolean());
				player.setRosterStatus(getRosterStatus(playerObject));
				player.setCurrentTeam(findTeam(DataUtils.getField("currentTeam", playerObject).getAsJsonObject()));
				player.setHand(DataUtils.getField("shootsCatches", playerObject).getAsString());
	
				if(player.getCurrentTeam().getId() == game.getHomeTeam().getId()) {
					homePlayers.put(player.getId(), player);
				}else if(player.getCurrentTeam().getId() == game.getAwayTeam().getId()) {
					awayPlayers.put(player.getId(), player);
				}
			}catch (NumberFormatException e) {
				log.atSevere().withCause(e).withStackTrace(StackSize.FULL)
					.log("Failed to read field...");
			}
		}
		game.setHomePlayers(homePlayers);
		game.setAwayPlayers(awayPlayers);
	}
	
	public Team findTeam(JsonObject cTeamObject) {
		int currentTeamId = cTeamObject.get("id").getAsInt();
		if(currentTeamId == game.getHomeTeam().getId()) {
			return game.getHomeTeam();
		}else if(currentTeamId == game.getAwayTeam().getId()) {
			return game.getAwayTeam();
		}
		return null;
	}

	public Position getPosition(JsonObject playerObject) {
		String code = DataUtils.getField("code", DataUtils.getField("primaryPosition", playerObject).getAsJsonObject()).getAsString();
		
		switch (code) {
		case "C":
			return Position.CENTER;
		case "R":
			return Position.RIGHTWING;
		case "L":
			return Position.LEFTWING;
		case "D":
			return Position.DEFENSE;
		case "G":
			return Position.GOALIE;
		default:
			log.atSevere().log("Failed to get position for %s", code);
			return null;
		}
	}

	public void getGameMetadata(JsonElement metaDataElement) {
		JsonObject gameMetadataObject = metaDataElement.getAsJsonObject();
		
		game.setPk(DataUtils.getField("pk", gameMetadataObject).getAsString());
		game.setSeason(DataUtils.getField("season", gameMetadataObject).getAsString());
		game.setType(DataUtils.getField("type", gameMetadataObject).getAsString());
	}

	public void getGameStatus(JsonElement statusElement) {
		String detailedCode = DataUtils.getField("detailedState", statusElement.getAsJsonObject()).getAsString();
		
		switch (detailedCode) {
		case "Final":
			game.setGameStatus(Status.FINAL);
		default:
			game.setGameStatus(Status.FINAL);
		}
	}

	// "dateTime":"2018-01-03T01:00:00Z","endDateTime":"2018-01-03T03:43:41Z"
	public void getDatetime(JsonElement datetimeElement) {
		JsonObject datetimeObject = datetimeElement.getAsJsonObject();
		
		String startTimeString = DataUtils.getField("dateTime", datetimeObject).getAsString();
		String endTimeString = DataUtils.getField("endDateTime", datetimeObject).getAsString();
		
		ZonedDateTime startTime = ZonedDateTime.parse(startTimeString);
		game.setStartTime(startTime.toEpochSecond());
		
		if(!endTimeString.equals("null")) {
			ZonedDateTime endTime = ZonedDateTime.parse(endTimeString);
			game.setEndTime(endTime.toEpochSecond());
		}
	}

	public int getHeight(String heightString) {
		Pattern pattern = Pattern.compile("\\d+");
		Matcher matcher = pattern.matcher(heightString);
		int[] hieghtElements = new int[2];
		int i = 0;
		while (matcher.find()) {
			hieghtElements[i] = Integer.valueOf(matcher.group());
			i++;
		}
		double converted = 2.54 * (12 * hieghtElements[0] + hieghtElements[1]);
		return (int) converted;
	}

	public Boolean getRosterStatus(JsonObject playerObject) {
		String status = DataUtils.getField("rosterStatus", playerObject).getAsString();
		switch (status) {
		case "Y":
			return true;
		case "N":
			return false;
		default:
			return true;
		}
	}


}
