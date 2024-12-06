package optimization.evolutionBasedSolutions;

import java.util.HashMap;
import java.util.Random;

import nodes.OptimizerEdgeNode;
import optimization.BasicObject;

public class CandidateSolution extends BasicObject{
    public CandidateSolution(int id,int actionSize, HashMap<String, Long> eventLog) {
        this.solution = new double[2];
        Random rand = new Random(System.currentTimeMillis());
        edgeNode = new OptimizerEdgeNode(id,actionSize, eventLog);
        // Initialize genes randomly
        solution[0] = rand.nextDouble(0.1, 0.99);
        solution[1] = rand.nextDouble(0.1, 1.0);
    }
    public CandidateSolution(OptimizerEdgeNode edgeNode,double[] position) {
    	this.edgeNode = edgeNode;
    	solution = position;
    }
    /*-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+*/
    public double[] getSolution() {
        return solution;
    }
    /*-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+*/
    public void setSolution(double[] solution) {
        this.solution = solution;
    }
    /*-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+*/
}