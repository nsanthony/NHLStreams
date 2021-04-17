package nhlstreams.data.ingest;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class IngestTests {
	
	@Test
	public void testIntReturn() {
		int returnedValue = DataParser.testingThis();
		assertEquals(2, returnedValue);
	}
	
	@Test
	public void returnString() {
		String testString = "test";
		String returnedString = DataParser.parseData(testString);
		
		assertEquals(testString, returnedString);
	}
	
	@Test
	public void testHeightConverter() {
		String hString = "6' 1\"";
		int hInCm = 185;
		DataParser parser = new DataParser();
		double convertedHeight = parser.getHeight(hString);
		
		assertEquals(hInCm, convertedHeight, 1);
		
	}
}
