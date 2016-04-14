package tests;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import display.TrackerGui;
import logic.Settings;
import logic.TrackerIndividual;
import logic.TrackerLogic;
import logic.UtilMethods;

public class TrackerLogicTest {

	Settings set;
	TrackerLogic tl;
	TrackerLogic tl2;
	TrackerGui tg;
	
	@Before
	public void setUp() {
		String[][] board = UtilMethods.createTrackerBoard();
		tg = new TrackerGui(board);
		tl = new TrackerLogic(tg, board);
		tl2 = new TrackerLogic(null, board);
	}
	
	@After
	public void tearDown() {
		set = null;
		tl = null;
		tg = null;
	}
	
	@Test
	public void getSensorInputsTest() {
		tl.spawnObjectGui();
		
		tl.moveObjectGui();
		tl.moveAgentGui("right");
		tl.colorBackground();
		
		for (int[] val : tl.getPositions("agent")) {
			System.out.println("Agent pos");
			System.out.println(val[0]);
			System.out.println(val[1]);
			
		}
		for (int[] val : tl.getPositions("object")) {
			System.out.println("Object pos");
			System.out.println(val[0]);
			System.out.println(val[1]);
			
		}
		
		Assert.assertArrayEquals(new double[] {1.0, 1.0, 0.0, 0.0, 0.0}, tl.getSensorInputs(), 0.00001);
		
		tl.moveObjectGui();
		tl.moveAgentGui("right");
		tl.colorBackground();
		
		Assert.assertArrayEquals(new double[] {1, 0, 0, 0, 0}, tl.getSensorInputs(), 0.00001);
		
		tl.moveObjectGui();
		tl.moveAgentGui("right");
		tl.colorBackground();
		
		Assert.assertArrayEquals(new double[] {0, 0, 0, 0, 0}, tl.getSensorInputs(), 0.00001);
		
		tl.moveObjectGui();
		tl.moveAgentGui("left");
		tl.colorBackground();
		
		Assert.assertArrayEquals(new double[] {1, 0, 0, 0, 0}, tl.getSensorInputs(), 0.00001);
	}
	
	@Test
	public void getSensorInputsNoGuiTest() {
		tl2.spawnObjectGui();
		
		tl2.moveObjectGui();
		tl2.moveAgentGui("left");
		tl2.colorBackground();
		
		Assert.assertArrayEquals(new double[] {1.0, 1.0, 1.0, 1.0, 0.0}, tl2.getSensorInputs(), 0.00001);
		
		tl2.moveObjectGui();
		tl2.moveAgentGui("left");
		tl2.colorBackground();
		
		Assert.assertArrayEquals(new double[] {1, 1, 1, 1, 1}, tl2.getSensorInputs(), 0.00001);
		
		tl2.moveObjectGui();
		tl2.moveAgentGui("left");
		tl2.colorBackground();
		
		Assert.assertArrayEquals(new double[] {0, 1, 1, 1, 1}, tl2.getSensorInputs(), 0.00001);
		
		tl2.moveObjectGui();
		tl2.moveAgentGui("left");
		tl2.colorBackground();
		
		Assert.assertArrayEquals(new double[] {0, 0, 1, 1, 1}, tl2.getSensorInputs(), 0.00001);
	}
}
