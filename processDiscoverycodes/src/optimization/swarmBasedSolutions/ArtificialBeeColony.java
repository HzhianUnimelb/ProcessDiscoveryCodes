package optimization.swarmBasedSolutions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import model.FPTA;
import optimization.Optimization;

public class ArtificialBeeColony extends Optimization {

    private double limit; // Limit for abandoning food sources
    private ArrayList<FoodSource> foodSources;
    private double[] fitnessValues;
    private double[]bestsource;
    double bestFitness;

    public ArtificialBeeColony(int id, int maxIter,int populationSize , double limit) {
        super(id, populationSize, maxIter);
        this.limit = limit;
        this.foodSources = new ArrayList<>();
        this.fitnessValues = new double[populationSize];
        bestsource = new double[2];
        initializeFoodSources();
        bestFitness = Double.NEGATIVE_INFINITY;
    }
    /*-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+*/
    private void initializeFoodSources() {
        for (int i = 0; i < populationSize; i++) {
            FoodSource foodSource = new FoodSource(randomSolution(),id, getEventLog(), actions);
            foodSources.add(foodSource);
            fitnessValues[i] = optimizationFunction(foodSource)[0];
        }
    }
    /*-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+*/
    
    public void employedBeesPhase() {
    	ExecutorService executor = Executors.newFixedThreadPool(populationSize);
        List<Future<?>> futures = new ArrayList<>();

        for (int i = 0; i < foodSources.size(); i++) {
            final int index = i; // Capture the current index
            futures.add(executor.submit(() -> {
                FoodSource newFoodSource = employBee(foodSources.get(index));
                double newFitness = optimizationFunction(newFoodSource)[0];
                if ( newFitness> fitnessValues[index]) {
                    foodSources.set(index, newFoodSource);
                    fitnessValues[index] = newFitness;
                    
                    foodSources.get(index).resetLimit(); // Reset the limit since the source improved
                } else {
                    foodSources.get(index).incrementLimit(); // Increment the limit
                }
            }));
        }

        // Wait for all tasks to complete
        for (Future<?> future : futures) {
            try {
                future.get(); // This will block until the task is complete
            } catch (Exception e) {
                e.printStackTrace(); // Handle exceptions
            }
        }

        executor.shutdown(); // Shutdown the executor
    
    }
    /*-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+*/
    public void onlookerBeesPhase() {
    	 ExecutorService executor = Executors.newFixedThreadPool(populationSize);
         List<Future<?>> futures = new ArrayList<>();

         for (int i = 0; i < populationSize; i++) {
             final int index = i; // Capture the current index
             futures.add(executor.submit(() -> {
                 FoodSource selectedSource = selectFoodSourceByProbability();
                 FoodSource newFoodSource = employBee(selectedSource);
                 double newFitness = optimizationFunction(newFoodSource)[0];
                 if (newFitness > fitnessValues[index]) {
                     foodSources.set(index, newFoodSource);
                     fitnessValues[index] = newFitness;
                     selectedSource.resetLimit();
                 } else {
                     selectedSource.incrementLimit();
                 }
             }));
         }

         // Wait for all tasks to complete
         for (Future<?> future : futures) {
             try {
                 future.get(); // This will block until the task is complete
             } catch (Exception e) {
                 e.printStackTrace(); // Handle exceptions
             }
         }

         executor.shutdown(); // Shutdown the executor
    }
    /*-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+*/  
    public void  scoutBeesPhase() {   	
    	 ExecutorService executor = Executors.newFixedThreadPool(populationSize);
         List<Future<?>> futures = new ArrayList<>();

         for (int i = 0; i < populationSize; i++) {
             final int index = i; // Capture the current index
             futures.add(executor.submit(() -> {
                 if (foodSources.get(index).getLimit() >= limit) {
                     foodSources.get(index).resetFoodSource(randomSolution()); // Replace with a new random solution
                     fitnessValues[index] = optimizationFunction(foodSources.get(index))[0];
                 }
             }));
         }

         // Wait for all tasks to complete
         for (Future<?> future : futures) {
             try {
                 future.get(); // This will block until the task is complete
             } catch (Exception e) {
                 e.printStackTrace(); // Handle exceptions
             }
         }

         executor.shutdown(); // Shutdown the executor
    }
    /*-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+*/
    public void optimize() {
        for (int iteration = 0; iteration < maxIter; iteration++) {
            // Employed Bees Phase
        	employedBeesPhase();
            // Onlooker Bees Phase
            onlookerBeesPhase();
            // Scout Bees Phase
            scoutBeesPhase();
            for (int i=0;i<fitnessValues.length;i++){
                if (fitnessValues[i] > bestFitness) {
                    bestFitness = fitnessValues[i];
                    bestsource = foodSources.get(i).getSolution();
                    bestModel = foodSources.get(i).getEdgeNode().getCurrentFPTA();
         	        bestMetric[0] = fitnessValues[1];
         	        bestMetric[1] = fitnessValues[2];		
                }
                double pos[]=foodSources.get(i).getSolution();

                System.out.print(" * locaiton ("+pos[0]+","+pos[1]+") --> "+fitnessValues[i]);

            }
            System.out.println("\nround number "+iteration+" "+bestFitness);
        }

    }
    /*-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+*/
    private FoodSource employBee(FoodSource source) {
        // Generate a new food source based on the current one
        double[] newSolution = source.getSolution().clone();
        int dimension = newSolution.length;
        int randomIndex = new Random().nextInt(dimension);
        double perturbation = 0.1 * (new Random().nextDouble() * 2 - 1); // Perturb the solution slightly
        newSolution[randomIndex] += perturbation;
        if(newSolution[randomIndex]>1)
        	 newSolution[randomIndex]-=2*perturbation;
        newSolution[randomIndex] = Math.abs(newSolution[randomIndex]);
        source.resetFoodSource(newSolution);
        return source;
    }
    /*-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+*/
    private FoodSource selectFoodSourceByProbability() {
        // Select a food source based on fitness
        double totalFitness = 0;
        for (double fitness : fitnessValues) {
            totalFitness += fitness;
        }

        double randomValue = new Random().nextDouble() * totalFitness;
        double cumulativeFitness = 0;

        for (int i = 0; i < fitnessValues.length; i++) {
            cumulativeFitness += fitnessValues[i];
            if (cumulativeFitness >= randomValue) {
                return foodSources.get(i);
            }
        }
        return null; // Should not reach here
    }
    /*-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+*/
    public int getBestFitness() {
        double bestFitness = Double.NEGATIVE_INFINITY;
        int index = -1;
        for (int i=0;i<fitnessValues.length;i++){
            if (fitnessValues[i] > bestFitness) {
                bestFitness = fitnessValues[i];
                index=i;
            }
        }
        return index;
    }

    /*-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+*/
    private double[] randomSolution() {
        // Generate a random solution
        double[] solution = new double[dimensions]; // Adjust dimensions accordingly
        solution[0] = new Random(System.currentTimeMillis()).nextDouble(0.1, 0.99);
        solution[1] = 0.1+new Random(System.currentTimeMillis()).nextDouble(0.1, 0.99);
        return solution;
    }
    /*-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+*/
    public static void main(String[] args) {
        // Example usage
       // ArtificialBeeColony abc = new ArtificialBeeColony(1, 20, 100, 10);
        //abc.optimize();
    }
    /*-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+*/
	@Override
	public void run() {
		// TODO Auto-generated method stub
		
		optimize();
    	
	}
}