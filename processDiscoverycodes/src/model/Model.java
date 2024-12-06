package model;

import java.util.HashMap;

public abstract class Model {
    /*+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-*/
	protected FPTA fpta;
	protected double alpha;
	private HashMap<String, Long> eventLog;
	protected double Filterring;
    /*+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-*/
	abstract public FPTA run();
	abstract public void stochasticFold(FPTA dffa1,String q, FPTA dffa2,String qPrime,String pattern);
	
	
	public Model(FPTA fpta)
	{
		this.fpta = fpta.cloneFPTA();
	}
	/*+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-*/
	public Model(FPTA fpta,double alpha)
	{
		this.fpta = fpta.cloneFPTA();
		this.alpha = alpha;
		Filterring = 30;
	}
	/*+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-*/
	public Model(double alpha,double Filterring,HashMap<String, Long> eventLog)
	{
		this.fpta = FPTA.constructFPTA(eventLog);
		this.alpha = alpha;
		this.Filterring = Filterring;
	}
	/*+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-*/
	public Model(double alpha,HashMap<String, Long> eventLog) {
	    	
	        this.alpha = alpha;
	        this.setEventLog(eventLog);    
	        Filterring = 30;
	}
	
	/*+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-*/

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}
    /*+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-*/
	abstract public void mergeModel(FPTA fpta2); 
	
	abstract public void mergeModel(FPTA fpta1, FPTA fpta2); 
    /*+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-*/
	public FPTA getFpta() {
		return fpta;
	}
    /*+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-*/

	public void setFpta(FPTA fpta) {
		this.fpta = fpta;
	}
    /*+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-*/

	public double getAlpha() {
		return alpha;
	}
    /*+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-*/
	public void setAlpha(double alpha) {
		this.alpha = alpha;
	}
    /*+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-*/
	protected void createFPTA(HashMap<String, Long> S) {       
        setFpta(FPTA.constructFPTA(S));     
    }
    /*+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-*/
    public FPTA returnFPTA() {
    	return getFpta();
    }
    /*+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-*/

    public void showModel(String text) {
    	getFpta().show(getFpta(),text);
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
	public double getFilterring() {
		return Filterring;
	}
	public void setFilterring(double filterring) {
		Filterring = filterring;
	}

}
