package optimization;

import java.util.HashMap;

import model.FPTA;
import nodes.EdgeNode;
import performance.PerformanceEstimator;

public class NonOpt extends Optimization {

	int id;
	private EdgeNode edgeNode;
	private PerformanceEstimator performanceEstimator;
	public NonOpt(int id,int populationSize,int maxIter) {
		
		super(id,populationSize,maxIter);
		//edgeNode = new EdgeNode(id,0.1,eventLog,"ALERGIA");
		edgeNode =  new EdgeNode(0.5,id,"ALERGIA");
		//performanceEstimator = new PerformanceEstimator(FPTA.constructFPTA(edgeNode.getLog()), eventLog, actions.size());

		//edgeNodes.add(new EdgeNode(0.5,i,"ALERGIA"));
	}
	 
	@Override
	public void run() {
		// TODO Auto-generated method stub
		double fitness[]=  optimizationFunction(null);
    	globalBestValue = fitness[0];
    	double best[] = {fitness[1],fitness[2]};
		setBestMetric(best);
	}
	
	@Override
	public void optimize() {
		// Do nothing
		
	}
	public EdgeNode getEdgeNode() {
		return edgeNode;
	}
	public void setEdgeNode(EdgeNode edgeNode) {
		this.edgeNode = edgeNode;
	}
}
