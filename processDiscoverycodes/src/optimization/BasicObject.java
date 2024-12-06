package optimization;

import nodes.OptimizerEdgeNode;

public class BasicObject {

    protected OptimizerEdgeNode edgeNode;
    protected double fitness;
    protected double metrics[];
    public double[] solution;
    
    public BasicObject() {
    	this.solution = new double[3];
    	this.metrics = new double[2];
    	fitness = -1;
    }
    /*-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+*/
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}
    /*-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+*/	
	public OptimizerEdgeNode getEdgeNode() {
		return edgeNode;
	}
    /*-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+*/
	public void setEdgeNode(OptimizerEdgeNode edgeNode) {
		this.edgeNode = edgeNode;
	}
    /*-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+*/

	public void setFitness(double fitness) {
		this.fitness = fitness;
	}
    /*-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+*/
	public double getFitness() {
		return fitness;
	}
    /*-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+*/
	public double[] getSolution() {
		return  solution;
	}
    /*-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+*/
	public double[] getMetrics() {
		return metrics;
	}
    /*-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+*/
	public void setMetrics(double []metrics)
	{
		this.metrics = metrics;
	}
}