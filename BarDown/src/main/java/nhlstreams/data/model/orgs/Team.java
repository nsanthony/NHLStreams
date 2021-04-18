package nhlstreams.data.model.orgs;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import lombok.Data;
import nhlstreams.data.model.exceptions.ConferenceNotFoundException;
import nhlstreams.data.model.exceptions.DivsionNotFoundException;
import nhlstreams.data.model.exceptions.TeamNotFoundException;
import nhlstreams.data.model.exceptions.VenueNotFoundException;
import nhlstreams.data.processing.DataUtils;

@Data
public class Team {
	private String name;
	private int id;
	private String link;
	private TeamTriCode triCode = null;
	private Venue arena = null;
	private String abbreviation;
	private String teamName;
	private String locationName;
	private String firstYearOfPlay;
	private Division division = null;
	private Conference conference = null;
	private Franchise franchise = null;
	private String shortName;
	private String officalSiteUrl;
	private int franchiseId;
	private Boolean active;

	// TODO: Should refactor this method for adaptor code. Will do in the future.
	public Team(JsonElement teamElement) throws VenueNotFoundException, DivsionNotFoundException, TeamNotFoundException,
			ConferenceNotFoundException {
		JsonObject teamObject = teamElement.getAsJsonObject();

		this.name = DataUtils.getField("name", teamObject).getAsString();
		this.id = DataUtils.getField("id", teamObject).getAsInt();
		this.link = DataUtils.getField("link", teamObject).getAsString();
		this.abbreviation = DataUtils.getField("abbreviation", teamObject).getAsString();
		this.teamName = DataUtils.getField("teamName", teamObject).getAsString();
		this.locationName = DataUtils.getField("locationName", teamObject).getAsString();
		this.firstYearOfPlay = DataUtils.getField("firstYearOfPlay", teamObject).getAsString();
		this.shortName = DataUtils.getField("shortName", teamObject).getAsString();
		this.officalSiteUrl = DataUtils.getField("officialSiteUrl", teamObject).getAsString();

//		findFranchse(DataUtils.getField("franchise", teamObject).getAsJsonObject());
		findVenue(DataUtils.getField("venue", teamObject).getAsJsonObject());
		findTeamTriCode(DataUtils.getField("triCode", teamObject).getAsString());
		findDivision(DataUtils.getField("division", teamObject).getAsJsonObject());
		findConference(DataUtils.getField("conference", teamObject).getAsJsonObject());
	}

	private void findVenue(JsonObject venueObject) throws VenueNotFoundException {
		JsonElement idElement = DataUtils.getField("id", venueObject);
		if(!idElement.getAsString().equals("null")) {
			int id = idElement.getAsInt();
			for (Venue venue : Venue.values()) {
				if (id == venue.id) {
					this.arena = venue;
				}
			}
			if (arena == null) {
				throw new VenueNotFoundException(venueObject);
			}
		}else {
			this.arena = Venue.NULL;
		}
	}

	private void findTeamTriCode(String triCode) throws TeamNotFoundException {
		for (TeamTriCode teamTriCode : TeamTriCode.values()) {
			if (triCode.equals(teamTriCode.triCode)) {
				this.triCode = teamTriCode;
			}
		}
		if (this.triCode == null) {
			throw new TeamNotFoundException(triCode);
		}
	}

	private void findDivision(JsonObject divisionObject) throws DivsionNotFoundException {
		int id = DataUtils.getField("id", divisionObject).getAsInt();
		for (Division division : Division.values()) {
			if (id == division.id) {
				this.division = division;
			}
		}
		if (division == null) {
			throw new DivsionNotFoundException(DataUtils.getField("name", divisionObject).getAsString());
		}
	}

	private void findConference(JsonObject conferenceObject) throws ConferenceNotFoundException {
		int id = DataUtils.getField("id", conferenceObject).getAsInt();
		for (Conference conference : Conference.values()) {
			if (id == conference.id) {
				this.conference = conference;
			}
		}
		if (conference == null) {
			throw new ConferenceNotFoundException(DataUtils.getField("name", conferenceObject).getAsString());
		}
	}

}
