package nhlstreams;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.google.common.flogger.FluentLogger;

public class BarDownTests {
	private static final FluentLogger logger = FluentLogger.forEnclosingClass();   	
	
	@Test
	public void testIntReturn() {
		int returnedValue = BarDown.testingThis();
		assertEquals(2, returnedValue);
	}
}
