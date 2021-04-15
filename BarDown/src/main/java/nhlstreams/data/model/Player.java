package nhlstreams.data.model;

import java.util.Date;

import lombok.Data;

@Data
public class Player {
	public int id;
	public String fullName;
	public String link;
	public String firstName;
	public int primaryNumber;
	public Date birthDate;
	public int currentAge;
	public String birthCity;
	public String birthStateProvince;
	public String birthCountry;
	public String nationality;
	public int hieght; //cm need to convert form feet/inches
	public int weight; //kgs (need to convert from pounds
	public Boolean active;
	public Boolean altCaptain;
	public Boolean captain;
	public Boolean rookie;
	public Handedness shoots;
	public Handedness catches;
	public String rosterStatus;
	public Team currentTeam;
	public Position primaryPosition;

}
