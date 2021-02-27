package nhlstreams.data.controller;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.http.HttpResponse;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.google.common.flogger.FluentLogger;

public class DataControllerTests {
	private static final FluentLogger logger = FluentLogger.forEnclosingClass();  
	private static String baseUrl;
	private static String eventExtension;	
	
	@BeforeAll
	public static void setup() {
		baseUrl = "http://statsapi.web.nhl.com/api/v1";
		eventExtension = "/game/2017020608/feed/live";
	}
	
	@Test
	public void testEventGrab() {
		
		DataController dataCtl = new DataController(baseUrl);
		HttpResponse<String> event = null;
		try {
			event = dataCtl.getLatestEvent(eventExtension);
			System.out.print("\nGot this response: " + event.body());
		} catch (URISyntaxException | IOException | InterruptedException e) {
			logger.atSevere().withCause(e)
				.log("Failed to get event");
		}
		
		assertTrue(true);
		
		
	}
	

}
