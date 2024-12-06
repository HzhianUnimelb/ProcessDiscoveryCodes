package optimization.natureBasedSolutions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.IntStream;

import optimization.Optimization;

public class GravitationalSearch extends Optimization {
    
    private List<Mass> masses;
    private double globalBestValue;
    private double gravitationalConstant;
    
    public GravitationalSearch(int id, int maxIter, int populationSize,double gravitationalConstant) {
        super(id, populationSize, maxIter);
        this.masses = new ArrayList<Mass>();
        this.globalBestValue = 0;
        this.gravitationalConstant = gravitationalConstant; // Initial gravitational constant
        setBestMetric(new double[2]);
        
        for (int i = 0; i < populationSize; i++) {
            masses.add(new Mass(id, getEventLog(), actions));
            
        }
    }

    public void optimize() {
       
        for(int i=0;i<masses.size();i++)
        	masses.get(i).setFitness(optimizationFunction(masses.get(i))[0]);
        for (int i = 0; i < maxIter; i++) {
            System.out.println(getId() + " round--->" + i);
            
            // Calculate masses based on fitness
          
            Random random = new Random(System.currentTimeMillis());
            // Create a list to hold Future objects
            ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

            for (int j = 0; j < masses.size(); j++) {
                final int index = j; // Final variable for use in lambda
                executor.submit(() -> {
                    // Calculate gravitational force
                    double[] force = calculateForce(index);
                    
                    // Update acceleration
                    masses.get(index).acceleration[0] = force[0] / masses.get(index).getFitness();
                    masses.get(index).acceleration[1] = force[1] / masses.get(index).getFitness();
                    
                    // Update velocity
                    masses.get(index).velocity[0] = random.nextDouble() * masses.get(index).velocity[0] + masses.get(index).acceleration[0];
                    masses.get(index).velocity[1] = random.nextDouble() * masses.get(index).velocity[1] + masses.get(index).acceleration[1];

                    // Update position
                    masses.get(index).solution[0] = masses.get(index).solution[0] + masses.get(index).velocity[0];
                    masses.get(index).solution[1] = masses.get(index).solution[1] + masses.get(index).velocity[1];

                    // Ensure masses stay within bounds
                    masses.get(index).solution[0] = Math.max(0.1, Math.min(0.99, masses.get(index).solution[0]));
                    masses.get(index).solution[1] = Math.max(0.1, Math.min(0.99, masses.get(index).solution[1]));

                    // Evaluate the optimization function
                    double fitness[]=optimizationFunction(masses.get(index));
                    masses.get(index).setFitness(fitness[0]);

                    // Update global best value
                    if (masses.get(index).getFitness() > globalBestValue) {
                        bestSolution = masses.get(index).solution.clone();
                        globalBestValue = masses.get(index).getFitness();
                        setBestMetric(force);
                        setBestModel(masses.get(index).getEdgeNode().getCurrentFPTA().cloneFPTA());
                        double best []={fitness[1],fitness[2]};
                        System.out.println("round() mass (" + index + ")" + getId() + " best result-->" + globalBestValue + " (" + bestSolution[0] + " " + bestSolution[1] + ")");
                        setBestMetric(best);
                    }
                });
            }

            // Shutdown the executor service
            executor.shutdown();
            while (!executor.isTerminated()) {
                // Wait for all tasks to finish
            }

            // Update gravitational constant
            gravitationalConstant = updateGravitationalConstant(i);
        }
      //  double best[]= masses.get(0).getEdgeNode().getPerformanceEstimator().calculatePerformanceMetrics(getBestModel(), getEventLog(), dimensions)
      //  setBestMetric();

    }

    public double[] calculateForce(int index) {
        double[] force = new double[2];
        Random rand = new Random();
        for (int i = 0; i < masses.size(); i++) {
        	if(i !=index)
        	{
        		double distance = calculateDistance(masses.get(i),masses.get(index))+0.001;
        		double sigleForce=0;
        		for(int j=0;j<force.length;j++)
        		{
        			sigleForce =masses.get(i).getFitness()*gravitationalConstant;
        			sigleForce*=(masses.get(i).solution[j]-masses.get(index).solution[j]);
        			sigleForce/=distance;
        			if(Math.abs(sigleForce)>9)
        				force[j]+=rand.nextDouble()*sigleForce/1000;
        			else
        				force[j]+=rand.nextDouble()*sigleForce/100;		
        		}
        		
        	}
        }
        return force;
    }
    
    public double calculateDistance(Mass mass1, Mass mass2) {
        return Math.sqrt(Math.pow(mass1.solution[0] - mass2.solution[0], 2) + Math.pow(mass1.solution[1]- mass2.solution[1], 2));
    }
    
    public double updateGravitationalConstant(int currentIteration) {
        return gravitationalConstant * (1.0 - (double)currentIteration / maxIter);
    }
    
    public void run() {
        optimize();
    }

    public static void main(String[] args) {
        // Example initialization
       
    }
}