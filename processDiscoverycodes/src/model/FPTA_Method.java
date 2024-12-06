package model;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;


public class FPTA_Method extends Model{

 
    public FPTA_Method(double alpha,HashMap<String, Long> eventLog) {
    	super(alpha,eventLog);    
    }
    /*+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-*/   
    public FPTA run() {
    	createFPTA(getEventLog());
    	
    	return returnFPTA();
    }
    /*+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-*/   
    public FPTA returnFPTA() {
    	return getFpta();
    }
    
    public void mergeModel(FPTA fpta2)
    {
    	
   		 stochasticFold(getFpta(),"",fpta2,"","");  
   		 getFpta().setInitialFrequency("", getFpta().getInitialFrequencies().get("")+fpta2.getInitialFrequencies().get(""));
   		 
    }
    /*+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-*/
    public static void main(String[] args) {
        // Create a sample S
      
         	
    }
    /*+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-*/
	@Override
	public void stochasticFold(FPTA fpta1, String q, FPTA fpta2, String qPrime, String pattern) {
        fpta1.setFinalFrequency(q, fpta1.getFinalFrequency(q) + fpta2.getFinalFrequency(qPrime));
        for (String a : fpta2.alphabet) {
            if (fpta2.getTransitionFunction().get(qPrime + a) != null) {
                if (fpta1.getTransitionFunction().get(q + a) != null) {
                    String nextStateQ = fpta1.getTransitionFunction().get(q + a);   
                    String nextStateQPrime = fpta2.getTransitionFunction().get(qPrime + a);
                    fpta1.setTransitionFrequency(q, a, nextStateQ, fpta1.getTransitionFrequency(q, a, nextStateQ) + fpta2.getTransitionFrequency(qPrime, a, nextStateQPrime));
                    stochasticFold(fpta1,nextStateQ, fpta2,nextStateQPrime,pattern);
                } else {
                    fpta1.getStates().add(fpta2.getTransitionFunction().get(qPrime + a));
                	if(!fpta1.alphabet.contains(a))
                		fpta1.alphabet.add(a);
                	fpta1.setTransitionFunction(q, a,""+fpta2.getTransitionFunction().get(qPrime + a));   
                	fpta1.setTransitionFrequency(q, a, fpta2.getTransitionFunction().get(qPrime + a), fpta2.getTransitionFrequency(qPrime, a, fpta2.getTransitionFunction().get(qPrime + a)));
                	stochasticFold(fpta1,fpta2.getTransitionFunction().get(qPrime + a), fpta2,fpta2.getTransitionFunction().get(qPrime + a),pattern);
                }

            }
        }				
	}
	@Override
	public void mergeModel(FPTA fpta1, FPTA fpta2) {
		// TODO Auto-generated method stub
		
	}
}