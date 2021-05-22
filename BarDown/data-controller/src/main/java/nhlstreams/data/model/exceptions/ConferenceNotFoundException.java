package nhlstreams.data.model.exceptions;

import lombok.extern.flogger.Flogger;

@Flogger
public class ConferenceNotFoundException extends Exception {
	private static final long serialVersionUID = 591619277041652316L;
	
	public ConferenceNotFoundException(String conferenceName){
		log.atSevere().log("Failed to find conference %s",conferenceName);
	}

}
