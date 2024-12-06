package optimization.natureBasedSolutions;

import java.util.HashMap;
import java.util.Random;

import nodes.OptimizerEdgeNode;
import optimization.BasicObject;

public class Star extends BasicObject {
	
	public Star(int id, HashMap<String, Long> eventLog, HashMap<String, Character> action) {
    	super();
    	this.fitness =0;
        setEdgeNode(new OptimizerEdgeNode(id,action.size(),eventLog));
        setRandomPosition();
	}
	public void setRandomPosition() {
		Random rand = new Random(System.currentTimeMillis());
		this.solution[0] = 0.1 + (0.99 - 0.1) * rand.nextDouble();
	    this.solution[1] = 0.1 + (0.99 - 0.1) * rand.nextDouble();
	}
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
