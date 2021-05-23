package data.controller.model;

import java.io.Serializable;
import java.util.Date;

import data.controller.model.orgs.Team;
import lombok.Data;

@Data
public class Player implements Serializable{
	private static final long serialVersionUID = -3225130932018706475L;
	
	private int id;
	private String fullName;
	private String link;
	private String firstName;
	private String lastName;
	private int primaryNumber;
	private Date birthDate;
	private int currentAge;
	private String birthCity;
	private String birthStateProvince;
	private String birthCountry;
	private String nationality;
	private int hieght; //cm need to convert form feet/inches
	private int weight; //kgs (need to convert from pounds
	private Boolean active;
	private Boolean altCaptain;
	private Boolean captain;
	private Boolean rookie;
	private Handedness shoots = Handedness.NONE;
	private Handedness catches = Handedness.NONE;
	private String handCode;
	private Boolean rosterStatus;
	private Team currentTeam;
	private Position primaryPosition;
	
	public void setHand(String handCode) {
		this.handCode = handCode;
		if(primaryPosition != null) {
			if(primaryPosition == Position.GOALIE) {
				this.catches = getHandEnum();
			}
			else{ 
				this.shoots = getHandEnum();
			}
		}else {
			throw new NullPointerException("Player position not set. Do that first....");
		}
	}
	
	private Handedness getHandEnum() {
		switch(handCode){
		case "R":
			return Handedness.RIGHT;
		case "L":
			return Handedness.LEFT;
		default:
			return Handedness.NONE;
		}
	}

}
