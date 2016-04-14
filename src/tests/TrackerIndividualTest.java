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

public class TrackerIndividualTest {

	Settings set;
	TrackerLogic tl;
	TrackerIndividual ti;
	
	@Before
	public void setUp() {
		set = new Settings();
		tl = new TrackerLogic(new TrackerGui(UtilMethods.createTrackerBoard()), set.getObjectSequence());
		ti = new TrackerIndividual(set, tl);
		ti.develop();
		
		
	}
	
	@After
	public void tearDown() {
		set = null;
		tl = null;
		ti = null;
	}
	
	@Test
	public void initGenotypeTest() {
		Assert.assertEquals(set.getGenotypeSize(), ti.genotype.length());
	}
	
	@Test
	public void developToPhenoTest() {
		Assert.assertEquals(set.getGenotypeSize()/set.getBitSize(), ti.getPhenotype().size());
	}

}
