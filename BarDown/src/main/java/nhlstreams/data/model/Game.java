package nhlstreams.data.model;

import java.io.Serializable;

import lombok.Data;

@Data
public class Game implements Serializable{
	private static final long serialVersionUID = 3592683826524004182L;
	
	private String pk;
	private String season;
	private String type;
	private Status gameStatus;
}
