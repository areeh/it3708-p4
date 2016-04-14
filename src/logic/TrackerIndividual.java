package logic;

import java.math.BigInteger;
import java.util.Random;

import display.TrackerGui;

public class TrackerIndividual extends Individual{
	
	private TrackerLogic tl;

	public TrackerIndividual(Settings set, TrackerLogic tl) {
		super(set);
		this.tl = tl;
		Random rng = new Random();
		
		BigInteger out = new BigInteger(set.getGenotypeSize(), rng);
		genotype = pad(out.toString(2), set.getGenotypeSize());
		
	}
	
	public TrackerIndividual(String genotype, Settings set, TrackerLogic tl) {
		super(genotype, set);
		this.tl = tl;
	}
	
	public void calcFit(String type) {
		//Run simulation
		//System.out.println("Creating CTRNN");
		CTRNN ctrnn = new CTRNN(set.getLayout(), getPhenotype2(), set);
		//System.out.println("Creating trackerlogic");
		TrackerLogic tl = new TrackerLogic(null, set.getObjectSequence(), set);
		//System.out.println("Spawning object");
		tl.spawnObjectGui();
		//System.out.println("Starting simulation");
		for (int i=0; i<600; i++) {
			if (set.isPull()) {
				tl.moveAgentGui(ctrnn.chooseMovePull(tl.getSensorInputs()));
			} else if (set.isNoWrap()) {
				tl.moveAgentGui(ctrnn.chooseMoveNoPull(tl.getSensorInputsNoWrap()));
			} else {
				tl.moveAgentGui(ctrnn.chooseMoveNoPull(tl.getSensorInputs()));
				
			}
			tl.moveObjectGui();
			tl.colorBackground();
		}
		
		//Get score
		
		fitness = tl.getScore();
	}

}
