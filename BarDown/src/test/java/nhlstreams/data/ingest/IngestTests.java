package nhlstreams.data.ingest;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.google.common.flogger.FluentLogger;

import nhlstreams.BarDown;

public class IngestTests {
	private static final FluentLogger logger = FluentLogger.forEnclosingClass();   	
	
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
