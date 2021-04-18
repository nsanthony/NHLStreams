package nhlstreams.data.model.exceptions;

import lombok.extern.flogger.Flogger;

@Flogger
public class TeamNotFoundException extends Exception {
	private static final long serialVersionUID = 8072388492653431285L;
	public TeamNotFoundException(String error){
		log.atSevere().log("Failed to find team %s", error);
	}
	
	public TeamNotFoundException() {
	}

}
