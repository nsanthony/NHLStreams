package nhlstreams.data.model;

import java.io.Serializable;

public enum Handedness implements Serializable {
	LEFT, RIGHT, UNKNOWN;
	
	public Handedness getHand(String hand) {
		switch(hand){
		case "L":
			return LEFT;
		case  "R":
			return RIGHT;
		default:
			return UNKNOWN;
		}
	}
}
