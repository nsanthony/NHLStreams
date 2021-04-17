package nhlstreams.data.model.orgs;

import lombok.extern.flogger.Flogger;

@Flogger
public class TeamNotFoundException extends Exception {
	private static final long serialVersionUID = 8072388492653431285L;
	TeamNotFoundException(String error){
		log.atSevere().log("Failed to find team %s", error);
	}

}
