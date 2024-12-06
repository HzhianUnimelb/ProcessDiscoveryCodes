package optimization;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import model.FPTA;
import unilities.LogParser;

public abstract class Optimization {
	
	protected FPTA bestModel;
    protected double globalBestValue;    
    protected double[] bestMetric;
    protected int id;
    protected HashMap<String, Long>  eventLog;
    protected LogParser logParser;
    protected HashMap<String, Character> actions;
    protected int maxIter;
    protected int populationSize;
    protected int dimensions;
    protected double[] bestSolution;
    protected List<BasicObject> frontier;
    /*-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+*/
    public Optimization(int id,int size,int maxIter) {
    	dimensions = 3;
    	String fileName = "chunk_" + id + ".xes";
        logParser = new LogParser(fileName);
        actions = getLogParser().readMapList("actionMap.txt");
        eventLog = logParser.extractEvent(actions);
        this.setId(id);
        this.populationSize = size;
        this.maxIter = maxIter;
        bestSolution = new double[dimensions];
        bestMetric = new double[2];
        frontier = new ArrayList<BasicObject>();
	}
    public Optimization(double alpha, int id2, String algorithmName) {
		// TODO Auto-generated constructor stub
	}
	/*-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+*/
	public abstract void run();
    /*-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+*/	
	public double[] optimizationFunction(BasicObject obj) { 
		
		FPTA fpta = obj.getEdgeNode().runModel(obj.solution[0],obj.solution[1]*100,obj.solution[2],"ALEEGIA");
    	//fpta.show(fpta, "after run");
    	HashMap<String,Double> matric = obj.getEdgeNode().getPerformanceEstimator().calculatePerformanceMetrics(fpta, obj.getEdgeNode().getEventLog(), obj.getEdgeNode().getActionList());
    	double metric1 = matric.get("Entropic Relevance")/obj.getEdgeNode().getPerformanceEstimator().getUnpperBoundEntropicRelevance();  // Example metric 1

    	double metric2 = matric.get("Size")/obj.getEdgeNode().getPerformanceEstimator().getUpperBoundSize();  // Example metric 2
    	double fit= (0.3*(1-metric1) + 0.7*(1-metric2));
        double [] value= {metric1,metric2};
        double result[] = {fit,metric1,metric2};
        obj.setMetrics(value);
       
        // System.out.println("pos("+obj.solution[0]+","+obj.solution[1]+") fit("+fit+")");
        return result; // Adjust this as needed for your optimization goal
	}
    /*-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+*/	
	public abstract void optimize();
    /*-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+*/
	public static void main(String[] args) {
		// TODO Auto-generated method stub
	}
    /*-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+*/
    public FPTA getBestModel() {
    	return bestModel;
    }
    /*-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+*/
	public void setBestModel(FPTA bestModel) {
		this.bestModel = bestModel;
	}
    /*-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+*/
    public double getGlobalBestValue() {
        return globalBestValue;
    }
    /*-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+*/
    public double[] getBestMetric() {
        return bestMetric;
    }
    /*-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+*/
    public void setBestMetric(double[] bestMetric) {
		this.bestMetric = bestMetric;
	}
    /*-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+*/
	public int getId() {
		return id;
	}
    /*-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+*/
	public void setId(int id) {
		this.id = id;
	}
    /*-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+*/
	public HashMap<String, Long> getEventLog() {
		return eventLog;
	}
    /*-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+*/
	public void setEventLog(HashMap<String, Long> eventLog) {
		this.eventLog = eventLog;
	}
    /*-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+*/
	public LogParser getLogParser() {
		return logParser;
	}
    /*-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+*/
	public void setLogParser(LogParser logParser) {
		this.logParser = logParser;
	}
    /*-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+*/
    public void setGlobalBestValue(double globalBestValue) {
        this.globalBestValue = globalBestValue;
    }

    /*-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+*/
    public int getDimensions() {
        return dimensions;
    }
    /*-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+*/
    public void setDimensions(int dimensions) {
        this.dimensions = dimensions;
    }
    /*-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+*/
    public List<BasicObject> getFrontier() {
    	return frontier;
    }
}
