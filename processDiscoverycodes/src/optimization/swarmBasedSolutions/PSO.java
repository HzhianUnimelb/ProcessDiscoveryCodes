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

public class PSO  extends Optimization{
	
    private Particle[] particles;
    private double[] globalBestPosition;
    private boolean flag;
    /*-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+*/
    public PSO(int id ,int maxIter, int populationSize) {
    	super(id,populationSize,maxIter);
        this.maxIter = maxIter;
        particles = new Particle[populationSize];
        globalBestPosition = new double[2];
        globalBestValue = 0;
        setBestMetric(new double[2]);
        // Initialize particles
        for (int i = 0; i < populationSize; i++) {
            particles[i] = new Particle(id, getEventLog(), actions);
        }
    }
    /*-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+*/
    public void optimize() {
        ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        
        for (int i = 0; i < maxIter; i++) {
            System.out.println(getId() + " round--->" + i);
            
            // Create a list to hold Future objects
            List<Future<Void>> futures = new ArrayList<>();
            Random rand = new Random(System.currentTimeMillis());
            for (Particle particle : particles) {
                futures.add(executor.submit(() -> {
                    double r1 = rand.nextDouble();
                    double r2 = rand.nextDouble();

                    // Update velocity
                    particle.velocity[0] = particle.velocity[0] + 0.5 * r1 * (particle.bestPosition[0] - particle.solution[0]) + 0.5 * r2 * (globalBestPosition[0] - particle.solution[0]);
                    particle.velocity[1] = particle.velocity[1] + 0.5 * r1 * (particle.bestPosition[1] - particle.solution[1]) + 0.5 * r2 * (globalBestPosition[1] - particle.solution[1]);

                    // Update position
                    particle.solution[0] = particle.solution[0] + particle.velocity[0];
                    particle.solution[1] = particle.solution[1] + particle.velocity[1];

                    // Ensure particles stay within the bounds
                   // particle.solution[0] = Math.max(0.1, Math.min(0.99, particle.solution[0]));
                    //particle.solution[1] = Math.max(0.1, Math.min(0.99, particle.solution[1]));
                    if(particle.solution[0]<0 || particle.solution[0]>1)
                    	particle.solution[0] =  rand.nextDouble() * (0.90 - 0.1) + 0.1;
                    if(particle.solution[1]<0 || particle.solution[1]>1)
                    	particle.solution[1] =  rand.nextDouble() * (0.90 - 0.1) + 0.1;
                    // Evaluate the optimization function
                    double currValue[] = optimizationFunction(particle);
                    System.out.println("pos("+ particle.solution[0]+","+ particle.solution[1]+") curr("+currValue+") +bestvalue "+globalBestValue);

                    synchronized (this) { // Synchronize access to shared variables
                        if (currValue[0] > globalBestValue) {
                            globalBestPosition = particle.solution.clone();
                            globalBestValue = currValue[0];
                            setBestModel(particle.getEdgeNode().getCurrentFPTA().cloneFPTA());
                            double best[]= {currValue[1],currValue[2]};
                            setBestMetric(best);
                            if (!flag) {
                                flag = true;
                            }
                            System.out.println(getId() + " best result-->" + globalBestValue);
                        } else if (currValue[0] > particle.bestValue) {
                            particle.bestPosition = particle.solution.clone();
                            particle.bestValue = currValue[0];
                        }
                    }
                    return null; // Return type for Future
                }));
            }
            // Wait for all tasks to complete
            for (Future<Void> future : futures) {
                try {
                    future.get(); // This will block until the task is complete
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        executor.shutdown(); // Shutdown the executor service
    }
    /*-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+*/
    public void run() {
    	optimize();
    }
    /*-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+*/
	public static void main(String[] args) {
		// TODO Auto-generated method stub
	//	PSO pso = new PSO(100, 100);	
	}

    /*-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+*/
   /*-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+*/
}