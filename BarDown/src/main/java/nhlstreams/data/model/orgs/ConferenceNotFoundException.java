package nhlstreams.data.model.orgs;

import lombok.extern.flogger.Flogger;

@Flogger
public class ConferenceNotFoundException extends Exception {
	private static final long serialVersionUID = 591619277041652316L;
	
	ConferenceNotFoundException(String conferenceName){
		log.atSevere().log("Failed to find conference %s",conferenceName);
	}

}
