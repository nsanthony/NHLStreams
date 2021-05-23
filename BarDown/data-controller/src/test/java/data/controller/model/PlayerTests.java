package data.controller.model;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class PlayerTests {
	
	@Test
	public void testHand() {
		Player testGoalie = new Player();
		Position goalie = Position.GOALIE;
		String goalieHand = "R";
		
		Player testWinger = new Player();
		Position rightWing = Position.RIGHTWING;
		String wingerHand = "L";
		
		
		testGoalie.setPrimaryPosition(goalie);
		testWinger.setPrimaryPosition(rightWing);
		
		testGoalie.setHand(goalieHand);
		testWinger.setHand(wingerHand);
		
		assertEquals(testGoalie.getShoots(), Handedness.NONE);
		assertEquals(testWinger.getCatches(), Handedness.NONE);
		
		assertEquals(testGoalie.getHandCode(), goalieHand);
		assertEquals(testWinger.getHandCode(), wingerHand);
		
		assertEquals(testGoalie.getCatches(), Handedness.RIGHT);
		assertEquals(testWinger.getShoots(), Handedness.LEFT);
		
	}

}
