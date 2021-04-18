package nhlstreams;

import java.io.IOException;
import java.net.URISyntaxException;

import com.google.common.flogger.StackSize;

import lombok.extern.flogger.Flogger;
import nhlstreams.data.controller.DataController;

@Flogger
public class BarDown {
	// http://statsapi.web.nhl.com/api/v1/schedule will give you todays schedule
	private static String baseUrl = "http://statsapi.web.nhl.com/api/v1";

	public static void main(String[] args) {

		DataController dataCtl = new DataController(baseUrl);
//		try {
//			dataCtl.getDailySchedule();
//		} catch (URISyntaxException | IOException | InterruptedException e) {
//			// TODO Auto-generated catch block
//			log.atSevere().withCause(e).withStackTrace(StackSize.FULL)
//				.log("Failed to get dail schedule...");
//		}
//		dataCtl.dailyRunner();
		dataCtl.gameRunner("2020020715");
		
		
	}

}
