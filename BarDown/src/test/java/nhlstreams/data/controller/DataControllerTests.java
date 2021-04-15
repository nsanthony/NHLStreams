package nhlstreams.data.controller;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.FileWriter;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.http.HttpResponse;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.google.common.flogger.StackSize;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import lombok.extern.flogger.Flogger;

@Flogger
public class DataControllerTests {
	private static String baseUrl;
	private static String eventExtension;	
	private static String path;
	
	@BeforeAll
	public static void setup() {
		baseUrl = "http://statsapi.web.nhl.com/api/v1";
//		eventExtension = "/game/2017020608/feed/live";
		eventExtension = "teams/13";
		path = "event.json";
		
	}
	
	@Test
	public void testEventGrab() {
		
		DataController dataCtl = new DataController(baseUrl);
		HttpResponse<String> event = null;
		try {
			event = dataCtl.getLatestEvent(eventExtension);
			JsonObject jsonObject = new JsonParser().parse(event.body()).getAsJsonObject();
			
			Gson gson = new Gson();
			gson.toJson(jsonObject, new FileWriter(path));
			log.atSevere().log("\nGot this response: " + event.body());
		} catch (URISyntaxException | IOException | InterruptedException e) {
			log.atSevere().withCause(e).withStackTrace(StackSize.FULL)
				.log("Failed to get event %s", event.body());
		}
		
		assertTrue(false);
		
		
	}
	

}
