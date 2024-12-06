package optimization.swarmBasedSolutions;

import java.util.HashMap;

import nodes.OptimizerEdgeNode;
import optimization.BasicObject;


public class FoodSource extends BasicObject {
    private int limit; // To keep track of abandonment
            

    public FoodSource(double[] solution,int id, HashMap<String, Long> eventLog, HashMap<String, Character> action) {
    	this.solution = solution;
    	this.limit = 0;
    	setEdgeNode(new OptimizerEdgeNode(id,action.size(),eventLog));
    }
    /*-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+*/

    public void resetFoodSource(double[] solution) {
    	this.solution = solution;
    	this.limit = 0;
    }
    /*-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+*/
	public int getLimit() {
		return limit;
	}
    /*-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+*/

	public void incrementLimit() {
		limit++;
	}
    /*-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+*/

	public void resetLimit() {
    	limit = 0;
    }
    /*-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+*/

}