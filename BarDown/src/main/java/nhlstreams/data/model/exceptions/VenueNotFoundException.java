package nhlstreams.data.model.exceptions;

import com.google.gson.JsonObject;

import lombok.extern.flogger.Flogger;

@Flogger
public class VenueNotFoundException extends Exception {
	private static final long serialVersionUID = -8340377213212811698L;
	
	public VenueNotFoundException(JsonObject venueObject) {
		log.atSevere().log("Failed to find venue for %s", venueObject);
	}



}
