package logic;

import java.awt.GridLayout;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JPanel;

import org.jfree.ui.RefineryUtilities;

import display.OptionsPanel;
import display.Plot;
import display.TrackerGui;

public class Simulation {
	Population population;
	Settings set;
	int gens;
	boolean success;
	TrackerLogic tl;
	TrackerGui tg;
	CTRNN ctrnn;
	CTRNN ctrnn2;
		
	List<String[][]> boards;
	List<Double> bestFits;
	List<Double> stdDevs;
	List<Double> averages;
	List<Integer> bestPheno;
	int generationNr;
	private boolean stop;
	
	JFrame f;
	
	List<JPanel> currentPlots;

	public Simulation(Settings set) {
		this.set = set;
		
		System.out.println("Started normal population");
		population = new Population(set);
		
		generationNr = 0;
		
		bestFits = new ArrayList<Double>();
		stdDevs = new ArrayList<Double>();
		averages = new ArrayList<Double>();
		
		stop = true;
		
		//Problem specific
		if (set.getFitnessFunc() == "flatland") {
			set.setGenotypeSize(set.getNrOfWeights());
		}
		
		if (set.getAdultSelectionType() == "full") {
			set.setChildrenSize(set.getGenerationSize());
		}
	}
	
	public void EAStep() {
		generationNr += 1;
		
		System.out.println("Started EAStep");
		population.createPhenotypes();
		System.out.println("Created phenos");
		population.evalGeneration();
		System.out.println("Got fitness");
		population.selectAdults();
		System.out.println("Selected adults");
		population.reproduce();
		System.out.println("Reproduced");
		log();
		//System.out.println("Logged");
		
				
			
	}
	
	public void runLoop() {		
		System.out.println("started loop");
		
		int gens = set.getNrOfGenerations();
		
		if (stop) {
			System.out.println("Stopped run");
		} else {
			if (set.isToSuccess()) {
				while (!population.getSuccess() && generationNr < set.getUpperLimit()) {
					EAStep();
					gens--;
				}
				System.out.println("Got success");
			} else {
				while (gens > 0 && !population.getSuccess()) {
					EAStep();
					gens--;
				}			
			}	
		}
		
		
		//Problem specific
		if (set.getFitnessFunc() == "tracker") {
			runTrackerSimulation();

		}
		plot();
		stop = true;
	}
	
	public void plot() {
		//System.out.println("ran plot");
		f = new JFrame("Plots");
		f.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		f.setLayout(new GridLayout(1, 0));
		
		f.add(new Plot(bestFits, "Progression of the best fitness", "Best fitness"));
		f.add(new Plot(stdDevs, "Progression of standard deviation", "Standard deviation"));
		f.add(new Plot(averages, "Progression of average fitness", "Average fitness"));
		
		f.pack();
		f.setVisible(true);
	}

	private void logIndividuals(List<Individual> inds) {
		for (Individual ind : inds) {
			System.out.println("Individual fitness: ");
			System.out.println(ind.getFitness());
			System.out.println("Genotype: ");
			System.out.println(ind.getGenotype());
			System.out.println("Phenotype: ");
			System.out.println(ind.getPhenotype());
		}
	}
		
	private void log() {
		//To get correct data, first calc stats
		population.calcStats();
		
		stdDevs.add(population.getStdDev());
		averages.add(population.getAvgFit());
		bestFits.add(population.getBestFit());
		
		System.out.println("Generation statistics: ");
		System.out.println("Generation number: " + generationNr);
		System.out.println("Best fitness: " + population.getBestFit());
		System.out.println("Average fitness: " + population.getAvgFit());
		System.out.println("Standard deviation: " + population.getStdDev());
		if (set.getFitnessFunc() == "flatland") {
			System.out.println("Best phenotype: " + population.getBestPheno2());
		} else {
			System.out.println("Best phenotype: " + population.getBestPheno());			
		}
		System.out.println("Best genotype: " + population.getBestGeno());
		
		System.out.println("Run had " + population.errorCount + " errors");
		System.out.println("Chose " + population.getGoodPairs() + " good pairs.");
		System.out.println("Chose " + population.getBadPairs() + " bad pairs.");
		
		
	}
	
	
	public void runTrackerSimulation() {
		String[][] board = UtilMethods.createTrackerBoard();
		ctrnn = new CTRNN(set.getLayout(), population.getBestPheno2(), set);
		ctrnn2 = new CTRNN(set.getLayout(), population.getBestPheno2(), set, false);
		System.out.println("attempting to initialize trackerGui");
		tg = new TrackerGui(board);
		System.out.println("Started trackerGui for simulation");
		TrackerLogic tl = new TrackerLogic(tg, set.getObjectSequence(), set);
		System.out.println("Started trackerlogic for simulation");
		tl.spawnObjectGui();
		for (int i=0; i<600; i++) {
			if (set.isPull()) {
				tl.moveAgentGui(ctrnn.chooseMovePull(tl.getSensorInputs()));
			} else if (set.isNoWrap()) {
				tl.moveAgentGui(ctrnn.chooseMoveNoPull(tl.getSensorInputsNoWrap()));
			} else {
				tl.moveAgentGui(ctrnn.chooseMoveNoPull(tl.getSensorInputs()));
			}
			tl.moveObjectGui();
			//tl.colorBackground();
		}
		System.out.println("Finished graphic simulation");
		
		System.out.println("children");
		for (Individual ind : population.children) {
			ind.develop();
			System.out.println(ind.getPhenotype2());
		}
		
		System.out.println("adults");
		for (Individual ind : population.adults) {
			ind.develop();
			System.out.println(ind.getPhenotype2());
		}
	}
	
	public void setStop(boolean stop) {
		this.stop = stop;
	}
	

	public Settings getSettings() {
		return set;
	}

	public boolean isStopped() {
		return stop;
	}

	public void start() {
		reset();
		if (set.getFitnessFunc() == "surpriseqlocal" || set.getFitnessFunc() == "surpriseqglobal") {
			System.out.println("Ran seq population");
		} else if (set.getFitnessFunc() == "tracker") {
			System.out.println("Attempted to start tracker population");

			population = new TrackerPopulation(set);
			System.out.println("Created tracker population");
		}
		stop = false;
		
		/*
		if (currentPlots.size() > 0) {
			System.out.println("Current plots");
			
			f.setVisible(false);
			for (JPanel p : currentPlots) {
				f.remove(p);
			}
			
		}
		*/
		
		runLoop();
	}

	public void stop() {
		stop = true;
		reset();
	}
	
	public void reset() {
		bestFits = new ArrayList<Double>();
		stdDevs = new ArrayList<Double>();
		averages = new ArrayList<Double>();
		generationNr = 0	;
		population = new Population(set);
		
		if (set.getAdultSelectionType() == "full") {
			set.setChildrenSize(set.getGenerationSize());
		}
		
	}
	
	public static void main(String[] args) {
		Simulation a = new Simulation(new Settings());
		a.runLoop();
	}

}
