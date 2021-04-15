package nhlstreams;

import lombok.extern.flogger.Flogger;

@Flogger
public class BarDown {
	public static void main(String[] args) {
		
		// this will be the main we launch the streaming application from.
		int testNum = 0;
		log.atInfo().log("got this %s", String.valueOf(testNum));
	}

}
