package performance;

import java.util.HashMap;
import java.util.Set;

import model.FPTA;

public class PerformanceEstimator {

    private PerformanceAnalyser performanceAnalyser;
    private EntropicRelevanceCalculator entropicRelevanceCalculator;
    private HashMap<String,Double>performanceMetric;
    private double upperBoundSize;
    private double UnpperBoundEntropicRelevance;
    public PerformanceEstimator(FPTA fpta, HashMap<String, Long> eventLog,int actionList) {
    	performanceMetric = new HashMap<String, Double>();
    	performanceMetric.put("Fitness", 0.0);
    	performanceMetric.put("Entropic Relevance", 0.0);
    	performanceMetric.put("Size", 0.0);
    	performanceAnalyser = new PerformanceAnalyser();
    	entropicRelevanceCalculator = new EntropicRelevanceCalculator();
    	setUpperBoundValues(fpta,eventLog,actionList);
    }
	/*+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-*/
    public void setUpperBoundValues(FPTA model, HashMap<String, Long> eventLog, int actionSize) {
    	setUpperBoundSize(performanceAnalyser.calculateSize(model));
    	setUnpperBoundEntropicRelevance(entropicRelevanceCalculator.calculateEntropic(new FPTA(), eventLog, actionSize));	
    }
	/*+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-*/
    public HashMap<String,Double> calculatePerformanceMetrics(FPTA model, HashMap<String, Long> eventLog,int actionSize) {
    	performanceMetric.put("Fitness", performanceAnalyser.calculateFitness1(model, eventLog));
    //	System.out.println("Fitness-->"+performanceAnalyser.calculateFitness1(model, eventLog));
    	performanceMetric.put("Entropic Relevance", entropicRelevanceCalculator.calculateEntropic(model, eventLog, actionSize));
    	//System.out.println(entropicRelevanceCalculator.calculateEntropic(model, eventLog, actionSize)+" ER");
    	entropicRelevanceCalculator.calculateEntropic(model, eventLog, actionSize);
    	performanceMetric.put("Size",(double)performanceAnalyser.calculateSize(model));
    	//model.show(model, "sss");
    	return performanceMetric;
    }
    
    /*+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-*/

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}
	/*+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-*/
	public PerformanceAnalyser getPerformanceAnalyser() {
		return performanceAnalyser;
	}
	/*+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-*/
	public void setPerformanceAnalyser(PerformanceAnalyser performanceAnalyser) {
		this.performanceAnalyser = performanceAnalyser;
	}
	/*+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-*/
	public EntropicRelevanceCalculator getEntropicRelevanceCalculator() {
		return entropicRelevanceCalculator;
	}
	/*+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-*/
	public void setEntropicRelevanceCalculator(EntropicRelevanceCalculator entropicRelevanceCalculator) {
		this.entropicRelevanceCalculator = entropicRelevanceCalculator;
	}
	/*+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-*/

	public double getUpperBoundSize() {
		return upperBoundSize;
	}
	/*+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-*/

	public void setUpperBoundSize(double upperBoundSize) {
		this.upperBoundSize = upperBoundSize;
	}
	/*+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-*/

	public double getUnpperBoundEntropicRelevance() {
		return UnpperBoundEntropicRelevance;
	}
	/*+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-*/

	public void setUnpperBoundEntropicRelevance(double unpperBoundEntropicRelevance) {
		UnpperBoundEntropicRelevance = unpperBoundEntropicRelevance;
	}
	/*+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-*/

}
