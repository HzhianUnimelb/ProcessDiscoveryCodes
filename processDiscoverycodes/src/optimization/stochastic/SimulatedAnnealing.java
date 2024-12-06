package optimization.stochastic;
import java.util.Random;

import nodes.OptimizerEdgeNode;
import optimization.BasicObject;
import optimization.Optimization;

public class SimulatedAnnealing extends Optimization{
    private double coolingRate;
    private double absoluteTemperature;
    private Random random;
    BasicObject basicObject;
    public SimulatedAnnealing(int id ,double initialTemperature, double coolingRate) {
    	super(id, 1, 0);
        this.coolingRate = coolingRate;
        this.absoluteTemperature = initialTemperature;
        this.random = new Random(System.currentTimeMillis());
        basicObject = new BasicObject();
        basicObject.solution=new double[2];
        basicObject.setEdgeNode( new OptimizerEdgeNode(id,actions.size(),eventLog));
        basicObject.solution[0] = random.nextDouble(0.1,0.99); // Random position in [0, 1]     
        basicObject.solution[1] = random.nextDouble(0.1,1.0); // Random position in [0, 1]
       
    }

    // Objective function to minimize
    public double objectiveFunction(double x) {
        return x * x; // Example: f(x) = x^2
    }

    // Generate a neighboring solution
    public double[] getNeighbor(double currentSolution[]) {
        // Slightly modify the current solution
    	double newpos[]= new double[2];
    	for (int i = 0; i < currentSolution.length; i++) {
            // Create a small random change
            double change = (random.nextDouble() - 0.5) * 0.2; // Adjust the magnitude of change if needed
            newpos[i] = currentSolution[i] + change;

            // Clamp the new position to stay within bounds [0, 1]
            if(newpos[i]>1 || newpos[i]<0) 
            	newpos[i] = random.nextDouble();
        }
    	System.out.println(newpos[0]+" "+newpos[1]);
        return newpos; // Change by a small random value
    }

    // Acceptance probability function
    public double acceptanceProbability(double currentEnergy, double newEnergy) {
        if (newEnergy > currentEnergy) {
            return 1.0; // Always accept better solutions
        }
        return Math.exp(( currentEnergy-newEnergy)*10 / absoluteTemperature); // Probability of accepting worse solutions
    }

    // Perform the simulated annealing algorithm

    public static void main(String[] args) {
    //    SimulatedAnnealing sa = new SimulatedAnnealing(1000, 0.95);
    //    double initialSolution = 10; // Starting point
    //    double optimizedSolution = sa.optimize(initialSolution);
    //    System.out.println("Optimized Solution: " + optimizedSolution);
    //    System.out.println("Objective Function Value: " + sa.objectiveFunction(optimizedSolution));
    }

	@Override
	public void run() {
		// TODO Auto-generated method stub
		optimize();
	}

	@Override
	public void optimize() {
		 double []currentSolution = basicObject.solution;
	        double currentEnergy = optimizationFunction(basicObject)[0];
	        double fitness[] = new double[2];
	        while (absoluteTemperature > 1) {
	        	basicObject.solution  = getNeighbor(currentSolution);         
	            fitness = optimizationFunction(basicObject);
	            // Decide whether to accept the new solution
	            double randomnum= random.nextDouble();
	            if (acceptanceProbability(currentEnergy, fitness[0]) > randomnum) {
	            	
	            	System.out.println("rand("+randomnum+") accept--> "+acceptanceProbability(currentEnergy, fitness[0])+" temperature("+absoluteTemperature+") "+"old("+currentEnergy+") new("+fitness[0]+")");
	                currentEnergy = fitness[0];
	            	
	            }
	            else
	            {
	            	basicObject.solution= currentSolution;
	            	System.out.println(fitness[0]+" "+currentEnergy);
	            }
	            // Cool down the temperature
	            absoluteTemperature *= coolingRate;
	        }
	        bestModel = basicObject.getEdgeNode().getCurrentFPTA();	
	        bestMetric[0]= fitness[1];
	        bestMetric[1]= fitness[2];       
	}
}
