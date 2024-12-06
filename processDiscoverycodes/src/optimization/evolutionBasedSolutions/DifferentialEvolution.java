package optimization.evolutionBasedSolutions;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import optimization.Optimization;

public class DifferentialEvolution extends Optimization{
    private double mutationFactor; // Mutation factor (F)
    private double crossoverRate; // Crossover rate (CR)
    private CandidateSolution[] population; // Population of candidate solutions
    
    public DifferentialEvolution(int id,int maxIter, int populationSize, double mutationFactor, double crossoverRate) {
        super(id, populationSize, maxIter);
        this.mutationFactor = mutationFactor;
        this.crossoverRate = crossoverRate;
        this.population = new CandidateSolution[populationSize]; // Initialize population
        initializePopulation();
    }

    public void initializePopulation() {
        for (int i = 0; i < populationSize; i++) {
            population[i] = new CandidateSolution(id,actions.size(),eventLog);
            population[i].setFitness(optimizationFunction(population[i])[0]); // Calculate fitness for each individual
        }
    }

    public void optimize() {
        ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        List<Future<Void>> futures = new ArrayList<>();

        for (int iteration = 0; iteration < maxIter; iteration++) {
            futures.clear(); // Clear the list for the new iteration

            for (int i = 0; i < populationSize; i++) {
                final int index = i; // Final variable for lambda expression
                futures.add(executor.submit(() -> {
                    // Mutation and Crossover
                    CandidateSolution trial = mutateAndCrossover(index);

                    // Selection
                    double trialFitness[] = optimizationFunction(trial);
                    if (trialFitness[0] > population[index].getFitness()) {
                     //   System.out.println("round (" + ") index(" + index + ")--> new(" + trialFitness + ") local(" + population[index].getFitness() + ")" + " best(" + globalBestValue + ")");
                        population[index] = trial; // Replace with the trial solution
                        population[index].setFitness(trialFitness[0]); // Update fitness
                    }
                    else
                    {
                       // System.out.println("round (" + ") index(" + index + ")--> new(" + trialFitness + ") local(" + population[index].getFitness() + ") trial("+trial.solution[0]+","+trial.solution[1]+") currecnt("+population[index].getSolution()[0]+","+population[index].getSolution()[1]+")");

                    }
                    
                    return null; // Return null since we are using Future<Void>
                }));
            }

            // Wait for all tasks to complete
            for (Future<Void> future : futures) {
                try {
                    future.get(); // Wait for the task to complete
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            // Optional: Print best fitness for tracking progress
          //  globalBestValue = getBestFitness();
            System.out.println("Iteration " + iteration + ": Best fitness " + globalBestValue);
        }

        executor.shutdown(); // Shutdown the executor service
    }
    public CandidateSolution mutateAndCrossover(int index) {
        Random random = new Random();
        // Select three distinct random indices
        int a, b, c;
        do {
            a = random.nextInt(populationSize);
        } while (a == index);
        do {
            b = random.nextInt(populationSize);
        } while (b == index || b == a);
        do {
            c = random.nextInt(populationSize);
        } while (c == index || c == a || c == b);

        // Mutation strategy: v = x_a + F * (x_b - x_c)
        double[] mutant = new double[population[0].getSolution().length];
        for (int j = 0; j < mutant.length; j++) {
        	double addedValue = mutationFactor * (population[b].getSolution()[j] - population[c].getSolution()[j]);
            mutant[j] = population[a].getSolution()[j] + addedValue;
            if(mutant[j]>1)
            	population[a].getSolution()[j]-= 2*addedValue;
        }

        // Crossover
        double[] trialSolution = new double[population[0].getSolution().length];
        for (int j = 0; j < trialSolution.length; j++) {
            if (random.nextDouble() < crossoverRate) {
                trialSolution[j] = Math.abs(mutant[j]); // Crossover with mutant
              
            } else {
                trialSolution[j] = population[index].getSolution()[j]; // Retain original
            }
        }

        return new CandidateSolution(population[index].getEdgeNode(),trialSolution);
    }

    public double getBestFitness1() {
        double bestFitness = Double.NEGATIVE_INFINITY;
        for (CandidateSolution candidate : population) {
            if (candidate.getFitness() > bestFitness) {
                bestFitness = candidate.getFitness();
                bestModel = candidate.getEdgeNode().getCurrentFPTA();
                
     	     //   bestMetric = candidate.getEdgeNode().getPerformanceEstimator().calculatePerformanceMetrics(bestModel, eventLog, populationSize);

            }
        }
        return bestFitness;
    }
    public double[] randomSolution() {
        // Generate a random solution
        double[] solution = new double[dimensions]; // Adjust dimensions accordingly
        for (int i = 0; i < dimensions; i++) {
            solution[i] = new Random(System.currentTimeMillis()).nextDouble(); // Random values between 0 and 1
        }
        return solution;
    }

    public static void main(String[] args) {
        // Example usage
     //   DifferentialEvolution de = new DifferentialEvolution(20, 100, 0.5, 0.9);
      //  de.optimize();
    }

	@Override
	public void run() {
		optimize();
	}
}