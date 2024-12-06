package optimization.swarmBasedSolutions;

import java.util.HashMap;
import java.util.Random;

import nodes.OptimizerEdgeNode;
import optimization.BasicObject;

class Particle extends BasicObject{
    double[] velocity;
    double[] bestPosition;
    double bestValue;


    public Particle(int id, HashMap<String, Long> eventLog, HashMap<String, Character> action) {
        solution = new double[2];
        velocity = new double[2];
        bestPosition = new double[2];
        Random rand = new Random(System.currentTimeMillis());
        setEdgeNode(new OptimizerEdgeNode(id,action.size(),eventLog));
        // Initialize position and velocity randomly
        solution[0] = rand.nextDouble(0.1,0.99); // Random position in [0, 1]
        velocity[0] = rand.nextDouble(0.0,0.2); // Random velocity
        solution[1] = rand.nextDouble(0.1,0.99); // Random position in [0, 1]
        velocity[1] = rand.nextDouble(0.0,0.2); // Random velocity
        bestPosition = solution.clone();
        bestValue = 0;
    }

}