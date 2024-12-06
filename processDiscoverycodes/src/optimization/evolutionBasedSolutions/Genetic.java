package optimization.evolutionBasedSolutions;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import model.FPTA;
import optimization.BasicObject;
import optimization.Optimization;

public class Genetic extends Optimization {

    private double crossoverRate;
    private double mutationRate;
    private List<Individual> population;
    FileWriter populationFWiter, frontierWriter;
    /*-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+*/
    public Genetic(int id, int maxGenerations,int size , double crossoverRate, double mutationRate) {
        super(id,size,maxGenerations);
        
        this.crossoverRate = crossoverRate;
        this.mutationRate = mutationRate;
        this.population = new ArrayList<>();
        initializePopulation();
        try {
        	populationFWiter = new FileWriter("populationGenResult.txt");
        	frontierWriter = new FileWriter("frontGenResult.txt");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    
    }
    /*-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+*/
    public void initializePopulation() {
        for (int i = 0; i < populationSize; i++) {
            population.add(new Individual(i, actions.size(), eventLog));
        }
    }
    /*-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+*/

    public void optimize() {
        ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        List<Future<Void>> futures = new ArrayList<>();
        for (Individual genum : population) {
        	
            futures.add(executor.submit(() -> {
                double fitness[] = optimizationFunction(genum);
                genum.setFitness(fitness[0]);
                synchronized (this) { // Synchronize access to shared variables
                    populationFWiter.write("("+genum.getMetrics()[0]+","+genum.getMetrics()[1]+")\n");
                    if (fitness[0] > globalBestValue) {
                        bestModel = genum.getEdgeNode().getCurrentFPTA().cloneFPTA();
                        setGlobalBestValue(fitness[0]);
                        double best[] = new double[2];
                        best[0] = fitness[0];
                        best[1] = fitness[1];
                        setBestMetric(best);        
                    }
                }
                return null; // Return type for Future
            }));
        }

        executor.shutdown(); // Initiates an orderly shutdown
        try {
            // Wait for all tasks to complete
            for (Future<Void> future : futures) {
                future.get(); // This will block until the task is complete
            }
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }
    /*-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+*/
    public void selection() {
    	
        List<Individual> newPopulation = new ArrayList<>();
        Random rand = new Random();
        int tournamentSize = 2; // The size of the tournament

        for (int i = 0; i < populationSize; i++) {
            Individual best = null;
            for (int j = 0; j < tournamentSize; j++) {
                Individual candidate = population.get(rand.nextInt(populationSize));
                if (best == null || candidate.getFitness() > best.getFitness()) {
                    best = candidate;
                }
            }
            newPopulation.add(new Individual(best)); // Add the best individual from the tournament to the new population
        }

        population = newPopulation; // Replace the old population with the new one
    }
    /*-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+*/
    private void crossover() {
    	
    	
        Random rand = new Random();
        List<Individual> offspringList = new ArrayList<>();

        for (int i = 0; i < population.size()-1; i += 1) {
            if (rand.nextDouble() < crossoverRate) {
                Individual parent1 = population.get(i);
                Individual parent2 = population.get(i + 1);
                Individual[] offspring = parent1.crossover(parent2);
                offspringList.add(offspring[0]);
                offspringList.add(offspring[1]);
                
            }
        }
        
        if(population.size() < populationSize)
        {
        	while (population.size() < populationSize && !offspringList.isEmpty()) {
        		
        		population.add(offspringList.remove(0)); // Add the first offspring
           }
           if(population.size() < populationSize)
           {
        	   while (population.size() < populationSize) {
                   population.add(new Individual(0, actions.size(), eventLog));
               }
           }
        }
        
        // If the population is already at the desired size, randomly replace three individuals
        else {
        	Collections.sort(population, Comparator.comparingDouble(BasicObject ::getFitness));
            for (int j = 0; j < populationSize /3 && !offspringList.isEmpty(); j++) {
                int randomIndex = rand.nextInt(population.size());
                population.set(randomIndex, offspringList.remove(0)); // Replace a random individual
            }
        }        
    }
    /*-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+*/
    private void mutation() {
        Random rand = new Random();
        for (Individual individual : population) {
            if (rand.nextDouble() < mutationRate) {
                individual.mutate();
            }
        }
    }
    /*-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+*/
    public void runParetoFrontier() {
    	List<Individual> removelist = new ArrayList<Individual>();
    	for(Individual genum:population)
    	{
    		for(Individual genum1:population)
    		{
    			if(genum.equals(genum1)==false)
    			if(genum1.getMetrics()[0]>genum.getMetrics()[0])
    				if(genum1.getMetrics()[1]>genum.getMetrics()[1])
    				{
    					removelist.add(genum1);
    					continue;
    				}
    		}
    	}
    	 population.removeAll(removelist);
    }
    /*-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+*/
    public void run() {
    	System.out.println("started ... ");
        for (int generation = 0; generation < maxIter; generation++) {
        	optimize();
           // selection(); 
        	runParetoFrontier();
            crossover();     
            mutation();
            //System.out.println("round-->"+generation);
        }
        optimize();
        runParetoFrontier();
        for(Individual genum:population)
        	frontier.add(genum);
        for(BasicObject front: frontier)
			try {
				frontierWriter.write("("+front.getMetrics()[0]+","+front.getMetrics()[1]+")\n");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        try {
			frontierWriter.close();
			populationFWiter.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
    
        
    }
    /*-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+*/
    public static void main(String[] args) {
        // Example usage
    }

    /*-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+*/

}