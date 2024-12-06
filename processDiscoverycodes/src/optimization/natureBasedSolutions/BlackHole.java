package optimization.natureBasedSolutions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import optimization.Optimization;

public class BlackHole extends Optimization {
	private List<Star> stars;
	private int blackhole=0;
	private double totalFitness=0;
	private double moveFactor;
	public BlackHole(int id, int maxIter, int populationSize,double moveFactor) {
        super(id, populationSize, maxIter);
        this.stars = new ArrayList<Star>();
        this.globalBestValue = 0;
        this.setMoveFactor(moveFactor);
        setBestMetric(new double[2]);
        
        for (int i = 0; i < populationSize; i++) {
        	stars.add(new Star(id, getEventLog(), actions));      
        }
    }
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		optimize();
	}

	@Override
	public void optimize() {
		
		for(int i=0;i<maxIter;i++) {
			updateBlackHole();
			for(int j=0;j<stars.size();j++)
			{
				moveTowardBkHole(j);
				System.out.println("("+stars.get(j).solution[0]+" "+stars.get(j).solution[1]+")");
			}
			updateBlackHole();
	            // Create a list to hold Future objects
	        double threshold = calculateEventHorizenRadius();
	        for(int j=0;j<stars.size();j++)
			{
	        	if(j!=blackhole)
				{
					if(globalBestValue - stars.get(j).getFitness()<threshold)
					{
						stars.get(j).setRandomPosition();
					}
				}
			}
		  }
	}
	public void updateBlackHole() {
	    ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
	    List<Future<Void>> futures = new ArrayList<>();

	    for (int j = 0; j < stars.size(); j++) {
	        final int index = j; // Final variable for use in lambda
	        futures.add(executor.submit(() -> {
	        	double values[]=optimizationFunction(stars.get(index));
	            stars.get(index).setFitness(values[0]);
	            double fitness = stars.get(index).getFitness();
	            synchronized (this) { // Synchronize access to shared resources
	                if (fitness > globalBestValue) {
	                    bestSolution = stars.get(index).solution.clone();
	                    globalBestValue = fitness;
	                    setBestModel(stars.get(index).getEdgeNode().getCurrentFPTA().cloneFPTA());
	                    double best[]= {values[1],values[2]};
	                   setBestMetric(best);
	                    System.out.println("round() stars (" + index + ")" + getId() + " best result-->" + globalBestValue + " (" + bestSolution[0] + " " + bestSolution[1] + ")");
	                    blackhole = index;
	                }
	                totalFitness += fitness; // Update total fitness
	            }
	            return null; // Return type for Future
	        }));
	    }

	    // Wait for all tasks to complete
	    for (Future<Void> future : futures) {
	        try {
	            future.get(); // This will block until the task is complete
	        } catch (Exception e) {
	            e.printStackTrace(); // Handle exceptions
	        }
	    }

	    executor.shutdown(); // Shutdown the executor service
	}


	public void moveTowardBkHole(int j) {
		double distance = 0.001+Math.sqrt(Math.pow(stars.get(j).getSolution()[0] - bestSolution[0], 2) +
                Math.pow(stars.get(j).getSolution()[1] - bestSolution[1], 2));
		stars.get(j).getSolution()[0] += moveFactor * (bestSolution[0] - stars.get(j).getSolution()[0])/distance;
		stars.get(j).getSolution()[1] += moveFactor * (bestSolution[1] - stars.get(j).getSolution()[1])/distance;
		stars.get(j).solution[0] = Math.max(0.1, Math.min(0.99, stars.get(j).solution[0]));
		stars.get(j).solution[1] = Math.max(0.1, Math.min(0.99, stars.get(j).solution[1]));
	}
	
	public double calculateEventHorizenRadius()
	{
		return globalBestValue/totalFitness;
	}
	public double getMoveFactor() {
		return moveFactor;
	}
	public void setMoveFactor(double moveFactor) {
		this.moveFactor = moveFactor;
	}
}
