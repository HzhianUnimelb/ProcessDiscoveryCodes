package nodes;

import java.util.HashMap;

import model.ALERGIA;
import model.FPTA;
import model.Model;
import performance.PerformanceEstimator;
import process2.FrequencyBasedFiltering;


public class OptimizerEdgeNode extends BasicNode{

	private FPTA fixFPTA,currentFPTA;
	private HashMap<String, Long>  eventLog;
	private HashMap<String, Long>  filterEventLog;
	private PerformanceEstimator performanceEstimator;
	private int actionList;
	
	/**/
	public PerformanceEstimator getPerformanceEstimator() {
		return performanceEstimator;
	}
	
	public int getActionList() {
		return actionList;
	}
    /*+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-*/
	 public FPTA runModel(double alpha,double T0,double filteringThreshold,String algorithmName) {
		 FrequencyBasedFiltering filtering = new FrequencyBasedFiltering();
	
		 filterEventLog = filtering.filterEventLog(eventLog, filteringThreshold);
		 fixFPTA = FPTA.constructFPTA(filterEventLog);
		 //fixFPTA.show(fixFPTA, "first model");
		 
		 Model model1= new ALERGIA(alpha, T0,filterEventLog);
         currentFPTA = model1.run(); 
         return currentFPTA;
	 }
    /*+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-*/
	public OptimizerEdgeNode(int type,int size,HashMap<String, Long> eventLog) {
		super();
		this.eventLog = eventLog;
        actionList = size;
		fixFPTA = FPTA.constructFPTA(eventLog);
        performanceEstimator = new PerformanceEstimator(fixFPTA, eventLog, actionList);
        
       
	/*	else
		{
			eventLog = new HashMap<String, Long>();
			eventLog.put("",  (long)40);
			eventLog.put("b",  (long)10);
			eventLog.put("bb",  (long)10);
			eventLog.put("a",  (long)30);
			eventLog.put("aa",  (long)10);
    		actionList = 2;
		}*/
		
		// TODO Auto-generated constructor stub
	}
    /*+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-*/
	
    /*+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-*/
	public static void main(String[] args) {
		// TODO Auto-generated method stub
	//	OptimizerEdgeNode i = new OptimizerEdgeNode(0, "chunk_1.xes",1);
		//i.performanceEstimator.calculatePerformanceMetrics(i.runModel(0.5, 30, "ALERGIA"), i.eventLog, i.actionList);	
		//i.performanceEstimator.calculatePerformanceMetrics(i.runModel(0.2, 2, "ALERGIA"), i.eventLog, i.actionList);	
	}
    /*+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-*/
	public HashMap<String, Long> getEventLog() {
		return eventLog;
	}
    /*+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-*/
	public void setEventLog(HashMap<String, Long> eventLog) {
		this.eventLog = eventLog;
	}
    /*+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-*/
	public FPTA getCurrentFPTA() {
		return currentFPTA;
	}
}
