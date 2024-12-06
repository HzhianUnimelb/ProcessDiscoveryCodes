package performance;

import java.util.HashMap;
import org.apache.commons.math3.fraction.Fraction;

import model.DFFA;
import model.DPFA;

	
public class EntropicRelevanceCalculator {
	
	private double overlaProbablity;
	private double costofCoding;
	private long totalevent;
	private DPFA dpfa;
	private int actionSize;
	static boolean isSet;
	private double ER;    
    /*+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-*/ 
	public EntropicRelevanceCalculator() {
		
	}
    /*+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-*/ 
	public EntropicRelevanceCalculator(DFFA dffa,HashMap<String, Long> eventLog,int actionSize) {
		isSet = false;
		dpfa = DPFA.convertFromDFFA(dffa);
		this.setActionSize(actionSize);
		setTotalevent(eventLog);
		setOverlaProbablity(calculateOveralProbablity(dpfa,eventLog));	
		calculateCostOfCoding();
		setER(calculateEntropicRelevance(dpfa,eventLog));
	}
    /*+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-*/ 
	public double calculateEntropic(DFFA dffa,HashMap<String, Long> eventLog,int actionSize) {
		isSet = false;
		dpfa = DPFA.convertFromDFFA(dffa);
		this.setActionSize(actionSize);
		setTotalevent(eventLog);
		setOverlaProbablity(calculateOveralProbablity(dpfa,eventLog));	
		calculateCostOfCoding();

		return calculateEntropicRelevance(dpfa,eventLog);
	}
    /*+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-*/ 
    public double calculateEntropicRelevance(DPFA dpfa,HashMap<String, Long> eventLog) {
    	double result = 0.0;
    	for(String event : eventLog.keySet())
    	{
    		double a = 1;
    		a = a * calculateProbablity(dpfa,event);
    		
    		if( a == 0)
    		{
    			result +=(CalculateBackgroundUniform(event,actionSize+1)*(double)eventLog.get(event));		
    		}
    		else
    		{
    			if(a<0 && !isSet)
    			{
    				isSet = true;
    			}
    			result+=calculateCost(a)*(double)eventLog.get(event);
    		}
    	}
    	result = result/(double)totalevent;
    	result += costofCoding;
    	return result;
    }
    /*+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-*/ 
    public double CalculateBackgroundUniform(String trace,int alphabetPlus) {
    	double result =0.0;
    	result = (trace.length()+1) * (Math.log(alphabetPlus)/Math.log(2));
    	return result;
    }
    /*+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-*/ 
    public double calculateProbablity(DPFA dpfa, String trace) {
    	double result = 1;
    	String current_state="";
    	String prev_state="";
    	for(char c : trace.toCharArray())
    	{
    		prev_state = current_state;
    		current_state=dpfa.getTransitionFunction().get(current_state+c);
    		if(current_state == null)
    			return 0;
    		result = result *dpfa.transitionProbabilities.get(prev_state).get(c+"").get(current_state).doubleValue();
    	}
    	try {
    	result = result* dpfa.finalProbabilities.get(current_state).doubleValue();
    	}catch(Exception e)
    	{
    		result = 0;
    		
    	}

    	return result;
    }
    /*+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-*/   
    public double calculateOveralProbablity(DPFA dpfa,HashMap<String, Long> eventLog) {
    	double result = 0.0;
    	for(String event : eventLog.keySet())
    	{
    		if(isPermittedTrace(dpfa,event))
    		{
    			result = result + eventLog.get(event);
    		}
    	}
    	
    	result = result/(double)(totalevent);
    	return result;
    }
    /*+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-*/ 
    public boolean isPermittedTrace(DPFA dpfa, String trace) {
    	String state="";
    	for(char c : trace.toCharArray())
    	{
    		state = dpfa.getTransitionFunction().get(state+c);
    		if(state == null)
    			return false;
    	}
    	return true;
    }
    /*+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-*/   
    public double calculateCost(double value) {
    	double result = -(double)(Math.log(value)/(double)Math.log(2));
    	return result;
    }
    /*+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-*/   
    public static void main(String[] args) {
   

        HashMap<String, Long> log = new HashMap<String,Long>() ;
        
      /*  log.put("a", (long) 2);
        log.put("ab", 3);
        log.put("b", 3);
        log.put("aa", 2);
        log.put("bb", 2);
        log.put("bbb", 2);
        log.put("bbbb", 2);
        FPTA fpta = FPTA.constructFPTA(log); 
        //fpta.show(fpta, "FPTA");
       
        EntropicRelevanceCalculator calculator = new EntropicRelevanceCalculator(fpta,log);
        */
    }

    /*+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-*/ 
    public void calculateCostOfCoding() {
    	if(overlaProbablity == 0 || overlaProbablity == 1 )
    		costofCoding = 0;
    	else
    	{
    		costofCoding = -overlaProbablity * (Math.log(overlaProbablity)/Math.log(2));
    		costofCoding -=(1-costofCoding)*Math.log((1-overlaProbablity))/Math.log(2);
    	}
    }
    /*+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-*/ 

	public double getOverlaProbablity() {
		return overlaProbablity;
	}
    /*+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-*/ 
	public void setOverlaProbablity(double overlaProbablity) {
		this.overlaProbablity = overlaProbablity;
	}
    /*+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-*/ 
	public double getCostofCoding() {
		return costofCoding;
	}
    /*+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-*/ 
	public void setCostofCoding(double costofCoding) {
		this.costofCoding = costofCoding;
	}
    /*+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-*/
	public long getTotalevent() {
		return totalevent;
	}
    /*+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-*/
	public void setTotalevent(HashMap<String, Long> eventLog) {
		totalevent = 0;

    	for(String event : eventLog.keySet())
    	{
    		totalevent = totalevent + eventLog.get(event);
    	}
	} 
    /*+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-*/
	public int getActionSize() {
		return actionSize;
	}
	public void setActionSize(int actionSize) {
		this.actionSize = actionSize;
	}
	public double getER() {
		return ER;
	}
	public void setER(double eR) {
		ER = eR;
	}
}