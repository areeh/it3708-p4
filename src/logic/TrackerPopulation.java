package logic;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class TrackerPopulation extends Population{

	public TrackerPopulation(Settings set) {
		super(set);
		
		Random rng = new Random();
		
		children = new ArrayList<Individual>(set.getGenerationSize());
		for (int i=0; i<set.getGenerationSize(); i++) {
			children.add(new TrackerIndividual(set, null));			
		}
		
		if (children.size() != set.getGenerationSize()) {
			System.err.println("Created wrong number of children");
		}
				
	}

	protected Pair<Individual, Individual> crossover(Pair<Individual, Individual> parents) {
		
		if (rng.nextDouble() > set.getCrossoverRate()) {
			return parents;
		}
		
		int split = rng.nextInt(set.getGenotypeSize());
		
		Individual child1 = new TrackerIndividual(
				parents.x.getGenotype().substring(0, split) + parents.y.getGenotype().substring(split, set.getGenotypeSize()),
				set, null);
		Individual child2 = new TrackerIndividual(
				parents.y.getGenotype().substring(0, split) + parents.x.getGenotype().substring(split, set.getGenotypeSize()),
				set, null);

		
		return new Pair<Individual, Individual>(child1, child2);	
	}
	
	public void fullGenRepl() {
		int elitesNr = 4;
		Individual[] elites = new Individual[elitesNr];
		adults = children;
		if (adults != null && set.isElitism()) {
			System.out.println("Ran elitism");
			Collections.sort(adults);
			for (int i=0; i<elitesNr; i++) {
				elites[i] = adults.get(i);
			}
			for (int i=0; i<elitesNr; i++) {
				adults.remove(rng.nextInt(adults.size()));
				adults.add(elites[i]);
			}
		}
		children = null;
		
		if (adults.size() != set.getGenerationSize()) {
			System.err.println("Wrong adults size");
		}
	}
	
	public List<Double> getBestPheno2() {
		List<Integer> bestpheno = getBestPheno();
		List<Double> res = new ArrayList<Double>(bestpheno.size());
		
		for (Integer val : bestpheno) {
			res.add((double)val);
		}
		
		return res;
		
	}
	

}



