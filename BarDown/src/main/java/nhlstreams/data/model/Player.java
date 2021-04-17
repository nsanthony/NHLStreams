package nhlstreams.data.model;

import java.io.Serializable;
import java.util.Date;

import lombok.Data;

@Data
public class Player implements Serializable{
	private static final long serialVersionUID = -3225130932018706475L;
	
	public int id;
	public String fullName;
	public String link;
	public String firstName;
	public String lastName;
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
	private String handCode;
	public Boolean rosterStatus;
	public Team currentTeam;
	public Position primaryPosition;
	
	public void setHand(String handCode) {
		if(primaryPosition != null) {
			if(primaryPosition == Position.GOALIE) {
				this.catches = Handedness.UNKNOWN.getHand(handCode);
			}
			else{ 
				this.shoots = Handedness.UNKNOWN.getHand(handCode);
			}
		}else {
			throw new NullPointerException("Player position not set. Do that first....");
		}
	}

}
