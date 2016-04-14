package tests;

import java.util.ArrayList;
import java.util.List;

import org.jfree.chart.axis.Tick;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import display.TrackerGui;
import logic.CTRNN;
import logic.TrackerIndividual;
import logic.TrackerLogic;
import logic.UtilMethods;
import logic.Settings;

public class CTRNNTest {
	
	private int[] layout1;
	private List<Double> phenotype1;
	private int[] layout2;
	private List<Double> phenotype2;
	CTRNN ctrnn;
	CTRNN ctrnn2;
	TrackerLogic tl;
	
	@Before
	public void setUp() {
		layout1 = new int[] {1, 1, 1};
		phenotype1 = new ArrayList<Double>(8);
		for (int i=0; i<8; i++) {
			phenotype1.add((double)i+1);
		}
		layout2 = new int[] {5, 2, 2};
		tl = new TrackerLogic(new TrackerGui(UtilMethods.createTrackerBoard()));
		TrackerIndividual ti =  new TrackerIndividual(new Settings(), tl);
		ti.develop();
		phenotype2 = ti.getPhenotype2();
		ctrnn = new CTRNN(layout1, phenotype1);
		ctrnn2 = new CTRNN(layout2, phenotype2);
		
	}
	
	@After
	public void tearDown() {
		layout1 = null;
		phenotype1 = null;
		ctrnn = null;
	}
	
	//TODO: more complicated constructorTest?
	
	@Test
	public void constructorTest() {
		CTRNN testCtrnn = new CTRNN(layout1, phenotype1, false);
		Assert.assertEquals(1.0, testCtrnn.inputWeights[0], 0.001);
		Assert.assertEquals(2.0, testCtrnn.hiddenWeights[0][0], 0.001);
		Assert.assertArrayEquals(new double[] {3.0, 4.0}, testCtrnn.biasWeights, 0.001);
		Assert.assertArrayEquals(new double[] {5.0, 6.0}, testCtrnn.gains, 0.001);
		Assert.assertArrayEquals(new double[] {7.0, 8.0}, testCtrnn.timeConstants, 0.001);
		
	}
	@Test
	public void constructorTest2() {
		Assert.assertEquals(10, ctrnn2.inputWeights.length);
		Assert.assertEquals(4, ctrnn2.hiddenWeights[0].length);
		Assert.assertEquals(4, ctrnn2.gains.length);
		Assert.assertEquals(4, ctrnn2.biasWeights.length);
		Assert.assertEquals(4, ctrnn2.timeConstants.length);
	}
	
	@Test
	public void lengthOfPhenotypeTest() {
		Assert.assertEquals(8, ctrnn.lengthOfPhenotype());
	}
	
	@Test
	public void lengthOfPhenotypeTest2() {
		Assert.assertEquals(26, ctrnn2.lengthOfPhenotype());
	}
	
	
	@Test
	public void scaleRangesTest() {
		Assert.assertTrue(isCorrectRange(ctrnn.inputWeights, -5.0, 5.0));
		for (int i=0; i<ctrnn.hiddenWeights.length; i++) {
			Assert.assertTrue(isCorrectRange(ctrnn.hiddenWeights[i], -5.0, 5.0));			
		}
		Assert.assertTrue(isCorrectRange(ctrnn.biasWeights, -10.0, 0));
		Assert.assertTrue(isCorrectRange(ctrnn.gains, 1.0, 5.0));
		Assert.assertTrue(isCorrectRange(ctrnn.timeConstants, 1.0, 2.0));
	}
	
	@Test
	public void scaleRangesTestComplicated() {
		Assert.assertTrue(isCorrectRange(ctrnn2.inputWeights, -5.0, 5.0));
		for (int i=0; i<ctrnn2.hiddenWeights.length; i++) {
			Assert.assertTrue(isCorrectRange(ctrnn2.hiddenWeights[i], -5.0, 5.0));			
		}
		Assert.assertTrue(isCorrectRange(ctrnn2.biasWeights, -10.0, 0));
		Assert.assertTrue(isCorrectRange(ctrnn2.gains, 1.0, 5.0));
		Assert.assertTrue(isCorrectRange(ctrnn2.timeConstants, 1.0, 2.0));
	}
	
	
	/*
	@Test
	public void runNNTest() {
		tl.spawnObjectGui();
		ctrnn2.runNN(tl.getSensorInputs());
	}
	*/
	
	private boolean isCorrectRange(double[] vals, double rangeMin, double rangeMax) {
		for (double val : vals) {
			if (val > rangeMax || val < rangeMin) {
				return false;
			}
		}
		return true;
	}

}
