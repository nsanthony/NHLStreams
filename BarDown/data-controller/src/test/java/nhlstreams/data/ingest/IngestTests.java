package nhlstreams.data.ingest;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import com.google.gson.JsonObject;

public class IngestTests {
	
	@Mock
	JsonObject gameObject;
	
	@Test
	public void testHeightConverter() {
		String hString = "6' 1\"";
		int hInCm = 185;
		
		GameMetaDataParser parser = new GameMetaDataParser(gameObject);
		double convertedHeight = parser.getHeight(hString);
		
		assertEquals(hInCm, convertedHeight, 1);
		
	}
}
