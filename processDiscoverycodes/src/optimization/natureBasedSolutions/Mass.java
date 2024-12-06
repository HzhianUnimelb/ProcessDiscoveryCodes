package optimization.natureBasedSolutions;

import java.util.HashMap;
import java.util.Random;

import org.apache.jena.base.Sys;

import nodes.OptimizerEdgeNode;
import optimization.BasicObject;

class Mass  extends BasicObject{
    public double[] velocity;
    public double[] acceleration;
    public double massValue;
    public double fitness;
    // Add other necessary fields and methods

    public Mass(int id, HashMap<String, Long> eventLog, HashMap<String, Character> action) {
    	super();
        this.velocity = new double[2];
        this.acceleration = new double[2];
        this.fitness =0;
        setEdgeNode(new OptimizerEdgeNode(id,action.size(),eventLog));
        // Initialize solution and velocity
        Random rand = new Random(System.currentTimeMillis());
        this.solution[0] = 0.1 + (0.99 - 0.1) * rand.nextDouble();
        this.solution[1] = 0.1 + (0.99 - 0.1) * rand.nextDouble();
    }
    
    // Add methods like getEdgeNode, etc., if needed
}