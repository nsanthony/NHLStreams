package common.model.exceptions;

import lombok.extern.flogger.Flogger;

@Flogger
public class DivsionNotFoundException extends Exception {
	private static final long serialVersionUID = 6401943600114946822L;

	public DivsionNotFoundException(String divisionName) {
		log.atSevere().log("Failed to find division %s", divisionName);
	}

}
