package optimization;

import java.util.Random;

public class OptimizerFinder {

    NeuralNetwork neunet;
    int numParticles = 20; // Number of particles in the swarm
    int maxIterations = 1000; // Maximum number of iterations
    double[][] particles; // Particle positions
    double[][] velocities; // Particle velocities
    double[][] pbest; // Personal best positions
    double[] gbest; // Global best position
    double gbestFitness = Double.NEGATIVE_INFINITY; // Global best fitness

    public OptimizerFinder(NeuralNetwork neunet) {
        this.neunet = neunet;
        this.particles = new double[numParticles][2]; // 2D for alpha and threshold
        this.velocities = new double[numParticles][2];
        this.pbest = new double[numParticles][2];
        initializeParticles();
    }

    private void initializeParticles() {
        Random rand = new Random();
        for (int i = 0; i < numParticles; i++) {
            // Initialize particle positions and velocities
            particles[i][0] = rand.nextDouble(); // Alpha in (0, 1)
            particles[i][1] =rand.nextDouble() ; // Threshold in (1, 100)
            velocities[i][0] = rand.nextDouble() * 0.5; // Small random velocity
            velocities[i][1] = rand.nextDouble() * 0.5; // Small random velocity
            pbest[i] = particles[i].clone(); // Set personal best to initial position
          //  System.out.println("initial location "+particles[i][0]+" "+particles[i][1]);
        }
    }

    public double[] findBest() {
        for (int iteration = 0; iteration < maxIterations; iteration++) {
            for (int i = 0; i < numParticles; i++) {
                double fitness = predictFitness(particles[i][0], particles[i][1]);
                // Update personal best
                if (fitness > predictFitness(pbest[i][0], pbest[i][1])) {
                    pbest[i] = particles[i].clone();
                }

                // Update global best
                if (fitness > gbestFitness) {
                    gbestFitness = fitness;

                    gbest = particles[i].clone();
                }
            }

            // Update velocities and positions
            for (int i = 0; i < numParticles; i++) {
                double w = 0.5; // Inertia weight
                double c1 = 0.5; // Cognitive coefficient 
                double c2 = 0.5; // Social coefficient
                Random rand = new Random();

                // Update velocity
                velocities[i][0] = w * velocities[i][0] +
                        c1 * rand.nextDouble() * (pbest[i][0] - particles[i][0]) +
                        c2 * rand.nextDouble() * (gbest[0] - particles[i][0]);

                velocities[i][1] = w * velocities[i][1] +
                        c1 * rand.nextDouble() * (pbest[i][1] - particles[i][1]) +
                        c2 * rand.nextDouble() * (gbest[1] - particles[i][1]);

                // Update position
                particles[i][0] += velocities[i][0];
                particles[i][1] += velocities[i][1];

                // Ensure particles stay within bounds
                particles[i][0] = Math.max(0.1, Math.min(1.0, particles[i][0])); // Keep alpha in (0, 1)
                particles[i][1] = Math.max(0, Math.min(1.0, particles[i][1])); // Keep threshold in (1, 100)
            }
        }
        return gbest; // Return the best parameters found
    }

    public double predictFitness(double alpha, double threshold) {
        double[] input = new double[]{alpha, threshold};
        double output = neunet.predict(input); // Replace with your prediction function
        return output; // Return the predicted fitness
    }

    public static void main(String[] args) {
       
    }
}