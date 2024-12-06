package model;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import ch.qos.logback.core.status.Status;
import de.invation.code.toval.types.HashList;


public class ALERGIA extends Model{
	private Set<String> visitedPatterns;
    public ALERGIA(double alpha,HashMap<String, Long> eventLog) {
    	super(alpha, eventLog);
    	createFPTA(getEventLog());
    	visitedPatterns = new HashSet<>();
    }
    /*+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-*/
    public ALERGIA(FPTA model)
    {
    	super(model);
    	
    }
    /*+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-*/
    public ALERGIA(double alpha, double filteringThreshold,HashMap<String, Long> eventLog) {
    	super(alpha, filteringThreshold,eventLog);
    	visitedPatterns = new HashSet<>();
    }
    /*+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-*/

    public ALERGIA(FPTA mode,double alpha)
    {
    	super(mode, alpha);
    	
    }
    /*+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-*/

    public boolean alergiaTest(long f1, long n1, long f2, long n2, double alpha) {
        double gamma = (double)(Math.abs(f1 * n2  - f2 * n1))/(n1*n2);
        double threshold = (Math.sqrt(1.0 / n1) + Math.sqrt(1.0 / n2)) * Math.sqrt(0.5 * Math.log(2/alpha));
      //s  System.out.println("gamma and threshold "+ gamma+" "+threshold);
      if (gamma < threshold)  
    	  return true;
      else 
    	  return false;
    }
    /*+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-*/
    public boolean alergiaCompatible(String qu, String qv, double alpha) {
        boolean correct = true;
        try {  	
        	if (!alergiaTest(fpta.getFinalFrequencies().get(qu), fpta.getFrequency(qu), fpta.getFinalFrequencies().get(qv), fpta.getFrequency(qv), alpha)) {
        		return false;
        	}
        }catch(Exception e)
        {
        	return false;
        }
        for (String a : getFpta().getAlphabet()) {
            if (!alergiaTest(fpta.getTransitionFrequency(qu, a), fpta.getFrequency(qu), fpta.getTransitionFrequency(qv, a), fpta.getFrequency(qv), alpha)) {
                return false;
            }
        }
        return correct;
    }
    /*+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-*/
    public static FPTA extractModel(FPTA model,double alpha) {
    	ALERGIA alergia = new ALERGIA(model, alpha);
    	FPTA fpta = alergia.run();
    	return fpta;
    }
    /*+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-*/
    public FPTA run() {
    	int i =0;
        int t0 = computeT0(fpta.getStates().size(), getAlpha());
        for(String s:fpta.alphabet)
        {
        	if(fpta.states.contains(s))
        		fpta.BLUE.add(s);
        }
        fpta.BLUE.remove("");
        while (hasUnmarkedState(fpta.BLUE, t0)) {
            String qb = chooseUnmarkedState(fpta.BLUE, t0);
            String qr = findCompatibleState(qb, getAlpha());
            fpta.BLUE.remove(qb);
            if (qr!=null) {           
            	fpta.merge(qr, qb);
            	if(fpta.getFinalFrequency(qb)!=null)
            		fpta.setFinalFrequency(qb, (long)0);
            	fpta.mergeState.put(qb,qr);
               //fpta.removeSuspendedStates();
            } else {
            	fpta.statePromote(qb);      

            }            
            fpta.changeColor();
            fpta.removeSuspendedStates();
        }     
        return fpta;
    }
    /*+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-*/
    public int computeT0(int n, double alpha) {
        // implementing the calculation of t0 is a question
        // for simplicity, let's assume t0 is 30 for now
       return (int)Filterring;
    }
    /*+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-*/
    public boolean hasUnmarkedState(Set<String> blue, int t0) {
    	for(String s:fpta.states)
        for (String state : blue) {
        	
            if (fpta.getFrequency(state) >= t0) {
            
                return true;
            }
        }
        return false;
    }
    /*+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-*/
    public String chooseUnmarkedState(Set<String> blue, int t0) {
        for (String state : blue) {
            if (fpta.getFrequency(state) >= t0) {
                return state;
            }
        }
        return null;
    }
    /*+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-*/
    private String findCompatibleState(String qb, double alpha) {
       for(String red :getFpta().RED)
       {
    	   if(alergiaCompatible(red,qb,alpha)==true)
    	   {
    		   return red;
    	   }
       }
        return null;
    }
    
    /*+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-*/
    public static void main(String[] args) {
        // Create a sample S
    }
    
    /*+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-*/
    public long calculateIncomingArcs(FPTA dffa1,String state) {
    	int result=0;
    	for(String a: dffa1.alphabet)
    	{
    		for(String prevState:dffa1.states)
    		if( dffa1.getTransitionFunction().get(prevState + a) != null && dffa1.getTransitionFunction().get(prevState + a).compareTo(state)==0)
    		{
    			result += dffa1.getTransitionFrequencies().get(prevState).get(a).get(state);
    		}
    	}
    	result += dffa1.getInitialFrequencies().get(state)!=null? dffa1.getInitialFrequencies().get(state):0;
    	return result;
    }
    /*+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-*/
    public long printIncomingArcs(FPTA dffa1,String state) {
    	int result=0;
    	System.out.println("incoming for state "+state);
    	for(String a: dffa1.alphabet)
    	{
    		for(String prevState:dffa1.states)
    		if( dffa1.getTransitionFunction().get(prevState + a) != null && dffa1.getTransitionFunction().get(prevState + a).compareTo(state)==0)
    		{
				System.out.println(prevState+" + "+a+" --> "+state+" ("+dffa1.getTransitionFrequencies().get(prevState).get(a).get(state)+")");
    		}
    	}
    	
    	return result;
    }
    /*+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-*/
    public long calculateOutcommingArcs(FPTA dffa1,String state,int type) {
    	long result=0;
    	for(String a: dffa1.alphabet)
    	{
    		for(String nextstate:dffa1.states)
    		{
    			if(dffa1.getTransitionFunction().get(state + a)!=null&&dffa1.getTransitionFunction().get(state + a).compareTo(nextstate)==0)
    				result +=dffa1.getTransitionFrequency(state , a);
    		}
    	}
    	if(type==0)
    	result += dffa1.getFinalFrequencies().get(state)!=null? dffa1.getFinalFrequencies().get(state):0;
    	return result;
    }
    /*+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-*/
   
    public long printOutcommingArcs(FPTA dffa1,String state) {
    	long result=0;
    	System.out.println("outgoing for state "+state);
    	for(String a: dffa1.alphabet)
    	{
    		for(String nextstate:dffa1.states)
    		{
    			if(dffa1.getTransitionFunction().get(state + a)!=null&&dffa1.getTransitionFunction().get(state + a).compareTo(nextstate)==0)
    				System.out.println(state+" + "+a+" --> "+nextstate+" ("+dffa1.getTransitionFrequency(state , a)+")");
    		}
    	}
    	
    	return result;
    }
    /*+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-*/
	public void rebalanceFPTA1(FPTA dffa1,String state,Set<String> visitedStates) 
	{
		long outcoming = calculateOutcommingArcs(dffa1,state,1);
		long incoming = calculateIncomingArcs(dffa1,state);
		visitedStates.add(state);
		 for (String a : dffa1.alphabet) {
			 if(dffa1.getTransitionFunction().get(state + a) != null && dffa1.states.contains(dffa1.getTransitionFunction().get(state + a)))
			 {
				 if(!visitedStates.contains(dffa1.getTransitionFunction().get(state + a)))
					 rebalanceFPTA1(dffa1,dffa1.getTransitionFunction().get(state + a),visitedStates);
			 }
		 }
		//System.out.println("STATE "+state);	 
		long remain =Math.abs(incoming-outcoming);
		 if(remain !=0)
		 {
			 if(incoming>outcoming)
			 {
				if(dffa1.getFinalFrequency(state)==null)
					dffa1.setFinalFrequency(state,(long)0);
				dffa1.setFinalFrequency(state,remain +dffa1.getFinalFrequency(state));
			 }
			 else
			 {
				 if(state.compareTo("")==0)
					 dffa1.setInitialFrequency("",remain+ dffa1.getInitialFrequencies().get("")); 
				 else
				 {
					 if(dffa1.getFinalFrequency(state)==null)
						 dffa1.setFinalFrequency(state,(long)0);
					 if(remain<dffa1.getFinalFrequency(state))
						 dffa1.setFinalFrequency(state, dffa1.getFinalFrequency(state)-remain);
					 else
					 {
						 long remain1 = remain - dffa1.getFinalFrequency(state)+1;
						 dffa1.setFinalFrequency(state,(long)1);	
						 if(updateInComingArcs(dffa1, remain1, state, visitedStates));
					 }
				 }
			 }
		 }


		 //if(dffa1.getFinalFrequency(state) + dffa1.)
	}
    /*+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-*/
	
	public void updateFinalFrequency(FPTA dffa1,long reminder,String state) {
		dffa1.setFinalFrequency(state, reminder+dffa1.getFinalFrequency(state));
		
	}
    /*+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-*/
	public boolean updateInComingArcs(FPTA dffa1,long reminder,String state,Set<String> fixStates) {
		int index=0;
		List<String> stateList = new ArrayList<String>();
		List<String> symbolList = new ArrayList<String>();
		for(String a: dffa1.alphabet)
			for(String S:dffa1.states)
				if(S.compareTo(state)!=0 && dffa1.getTransitionFunction().get(S + a)!=null && dffa1.getTransitionFunction().get(S + a).compareTo(state)==0)
					if(!fixStates.contains(S))
					{
						stateList.add(S);
						symbolList.add(a);
						index++;
					}
		if(index>0)
		{
			long rest = reminder % index;
			reminder=reminder/index;
			long frequency =0;
			frequency = dffa1.getTransitionFrequency(stateList.get(0), symbolList.get(0), state);
			dffa1.setTransitionFrequency(stateList.get(0), symbolList.get(0), state, frequency+reminder+rest);

			for(int i=1; i< stateList.size();i++)
			{
				frequency = dffa1.getTransitionFrequency(stateList.get(i), symbolList.get(i), state);
				dffa1.setTransitionFrequency(stateList.get(i), symbolList.get(i), state, frequency+reminder);
			}
			return true;
		}
		else
			return false;
						
	}
    /*+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-*/
	public FPTA alphaStochasticFold(FPTA dffa1,FPTA dffa2,int limitloop) {
		FPTA result = new FPTA();
		PrintWriter writer = null;
		try {
			writer = new PrintWriter("path.txt", "UTF-8");
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		List<PairState> list = new ArrayList<PairState>();	
		Map<String, Long> stateQCount = new HashMap<String, Long>();
		Map<String, Long> stateQIncommingFreQ = new HashMap<String, Long>();
		Map<String, Long> stateQIncommingRemain = new HashMap<String, Long>();
		Map<String, String> QAliasName = new HashMap<String, String>();
		Map<String, String> path = new HashMap<String, String>();
		Set<String> visitedList = new HashSet<>();
		result.alphabet.addAll(dffa1.alphabet);
		result.alphabet.addAll(dffa2.alphabet);
		stateQIncommingFreQ.put("λ", dffa2.getInitialFrequencies().get(""));
		stateQIncommingRemain.put("λ", dffa2.getInitialFrequencies().get(""));
		list.add(new PairState("", "λ"));
		result.states.add("");
		result.setInitialFrequency("", dffa1.getInitialFrequencies().get("")+dffa2.getInitialFrequencies().get(""));
		result.setFinalFrequency("", dffa1.getFinalFrequency(""));
		path.put("", "λ");
		path.put("λ", "λ");
		QAliasName.put("λ", "");
		stateQCount.put("", 1L);
		boolean flag =false;
		while(list.size()>0)
		{
			PairState current = list.get(0);
			list.remove(0);
			String p = current.getFirst();
			String q = current.getSecond();
			visitedList.add("("+p+","+q+")");
			for(String symbol:result.alphabet)
			{
				flag = false;
				String nextPState = dffa1.getTransitionFunction().get(p+symbol);
				String nextQState = dffa2.getTransitionFunction().get(QAliasName.get(q)+symbol);
			//	if(q.compareTo("λ_1")==0)
				if(nextPState !=null)
				{
					long transitionFreq = dffa1.getTransitionFrequencies().get(p).get(symbol).get(nextPState);
					long finalFreq = dffa1.getFinalFrequency(nextPState);
					if(!result.states.contains(nextPState))
					{
						result.states.add(nextPState);
						result.setTransitionFrequency(p, symbol, nextPState, transitionFreq);
						result.setTransitionFunction(p, symbol, nextPState);
						result.setFinalFrequency(nextPState, finalFreq);
						flag = true;
					}
					else if(result.getTransitionFunction().get(p+symbol)==null)
					{
						result.setTransitionFunction(p, symbol, nextPState);					
						result.setTransitionFrequency(p, symbol, nextPState, transitionFreq);
						flag = true;
					}
				}
				if(nextQState != null)
				{
					long FREQ = dffa2.getFrequency(QAliasName.get(q));
					long incoming = stateQIncommingFreQ.get(q);				
					long transitionQFreq =  dffa2.getTransitionFrequencies().get(QAliasName.get(q)).get(symbol).get(nextQState);
					String st = nextQState;
					if(st.compareTo("")==0)
						st="λ";
					if(QAliasName.get(st)!=null)
						st = QAliasName.get(st);
					if(!detectLoop(path.get(q)+","+st, limitloop,writer))
					{
						long visitedCount = stateQCount.get(nextQState)!=null?stateQCount.get(nextQState):0;
						visitedCount++;
						if(visitedCount<500)
						{
							stateQCount.remove(nextQState);
							stateQCount.put(nextQState, visitedCount);
							String alias ="";
							if(nextQState.compareTo("")==0)
								alias = "λ"+"_"+visitedCount;
							else
								alias = nextQState+"_"+visitedCount;
							String text = nextQState;
							if(text=="")
								text="λ";	
							path.put(alias,path.get(q)+","+text);
							if(p==null)
								p=q;
							QAliasName.put(alias, nextQState);		
							if(nextPState!=null)
							{
								long transitionPFreq =  result.getTransitionFrequencies().get(p).get(symbol).get(nextPState);
								result.transitionFrequencies.get(p).get(symbol).remove(nextPState);
								result.transitionFrequencies.get(p).get(symbol).put(nextPState, transitionPFreq + ((transitionQFreq*incoming)/FREQ));
								flag = true;
							}
							else if(((transitionQFreq*incoming)/FREQ)>0)
							{
								result.states.add(alias);
								flag = true;
								result.setTransitionFrequency(p, symbol, alias,(transitionQFreq*incoming)/FREQ);
								result.setTransitionFunction(p, symbol, alias);	
							}
							stateQIncommingFreQ.put(alias, ((transitionQFreq*incoming)/FREQ));
							stateQIncommingRemain.put(alias, ((transitionQFreq*incoming)/FREQ));
							stateQIncommingRemain.put(q,stateQIncommingRemain.get(q)-(transitionQFreq*incoming)/FREQ);			
							nextQState = alias;
						}
					}
				}
				String s ="("+nextPState+","+nextQState+")";	
				
				if(!visitedList.contains(s) && flag)
				{
					list.add(new PairState(nextPState, nextQState));
				}
			}
			if(q!=null && p!=null&&stateQIncommingRemain.get(q)!=null)
			  result.setFinalFrequency(p, result.getFinalFrequency(p)+stateQIncommingRemain.get(q));
			else if(q!=null && p==null && stateQIncommingRemain.get(q)!=null)
			  result.setFinalFrequency(q, stateQIncommingRemain.get(q));

		}
		return result;
	}
    /*+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-*/

	public boolean detectLoop(String path,int count,PrintWriter write)
	{
		int length = path.length();
		write.write(path+"\n");
		if(path.length()>1000)
		{
			write.close();
			System.exit(0);
		}
    	StringTokenizer token= new StringTokenizer(path,",");
    	List<String> list=new ArrayList<String>();
    	while(token.hasMoreElements())
    	{
    		list.add(token.nextToken());
    	}
    	for(int k=1;k<list.size();k++)
    	for(int len = 1;len<=list.size()/2;len++)
    	{
    		String s="";
    		for(int j =k;j<len;j++)
    		{
    			if(j==k)
    				s=list.get(list.size()-j);
    			else
    				s=list.get(list.size()-j)+","+s;
    		}	
    		s=s+",";
    		StringBuilder repeated = new StringBuilder();
       	 	for (int j = 0; j <count; j++) {
                repeated.append(s);
            }
       	 	String finaltext = repeated.substring(0, repeated.lastIndexOf(","));
       	 	if (path.contains(finaltext)) {    

       	 		return true;
       	 	}
    	}
    	return false;
	}
    /*+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-*/

	public void betaStochasticFold(FPTA dffa1, String q, FPTA dffa2, String qPrime, String pattern,Set<String> isEffected){

	
		for (String a : dffa2.alphabet) {
	    	if (dffa2.getTransitionFunction().get(qPrime + a) != null) {
	        	if (dffa1.getTransitionFunction().get(q + a) != null) {
	            	String nextStateQ = dffa1.getTransitionFunction().get(q + a); 
	                String nextStateQPrime = dffa2.getTransitionFunction().get(qPrime + a);
	                if(dffa1.getFinalFrequency(nextStateQ)==null)
	                    dffa1.setFinalFrequency(nextStateQ,(long)0);
	                if(dffa2.getFinalFrequency(nextStateQPrime)==null)
	                    dffa2.setFinalFrequency(nextStateQPrime,(long)0);
	                dffa1.setFinalFrequency(nextStateQ, dffa1.getFinalFrequency(nextStateQ) + dffa2.getFinalFrequency(nextStateQPrime));
	                //if(!isEffected.contains(qPrime+" "+a+" "+nextStateQPrime))
	                //{
	                	dffa1.setTransitionFrequency(q, a, nextStateQ, dffa1.getTransitionFrequency(q, a, nextStateQ) + dffa2.getTransitionFrequency(qPrime, a, nextStateQPrime));
	               //  	isEffected.add(qPrime+" "+a+" "+nextStateQPrime);
	                //}
	                if(!detectLoop("("+nextStateQ+","+nextStateQPrime+")")) {
	                	betaStochasticFold(dffa1,nextStateQ, dffa2,nextStateQPrime,pattern,isEffected);
	                }
	            } 
	        	else {
	                	String mergeState = dffa2.getTransitionFunction().get(qPrime + a);
	                	if(!dffa1.alphabet.contains(a))
	                		dffa1.alphabet.add(a);       
	                	if(dffa2.getTransitionFunction().get(qPrime + a).compareTo(qPrime)==0)
	                	{
	                		mergeState=q;	
	                	}
	                	dffa1.getStates().add(mergeState);
	                	dffa1.setTransitionFunction(q, a,mergeState);  
	                	long a1 = dffa2.getTransitionFrequency(qPrime, a);
	                	dffa1.setTransitionFrequency(q, a, mergeState, a1);
	                	if(dffa1.getFinalFrequency(mergeState)==null)
	                		dffa1.setFinalFrequency(mergeState,(long)0);
	                	if(dffa2.getFinalFrequencies().get(dffa2.getTransitionFunction().get(qPrime + a))==null)
	                		dffa2.setFinalFrequency(dffa2.getTransitionFunction().get(qPrime + a),(long)0);
	                	if(mergeState.compareTo(q)!=0)
	                		dffa1.setFinalFrequency(mergeState, dffa1.getFinalFrequency(mergeState)+dffa2.getFinalFrequencies().get(dffa2.getTransitionFunction().get(qPrime + a)));
	                	if(!detectLoop("(LOOP "+qPrime+" "+a+")") && q .compareTo(mergeState)!=0) 
	                	{
	                		betaStochasticFold(dffa1,mergeState, dffa2,dffa2.getTransitionFunction().get(qPrime + a),pattern,isEffected);                  
	                	}
	                }

	            }
	        }	

	}
	/*+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-*/
	
    /*+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-*/
	public  boolean detectLoop(String text) {
      
        
        if(visitedPatterns.contains(text)==false)
        {
        	visitedPatterns.add(text);
        	return false;
        }
        else
        	return true;
    }
    /*+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-*/
	public void resetFinalFrequency(FPTA fpta1) {
		for(String state:fpta1.states)
		{
			if(fpta1.getFinalFrequency(state)!=0)
				fpta1.setFinalFrequency(state, (long)0);
			long outcoming = calculateOutcommingArcs(fpta1,state,1);
			long incoming = calculateIncomingArcs(fpta1,state);
			if(incoming<outcoming)
			{	
				System.out.println("out("+outcoming+") in("+incoming+") --->error");
				printOutcommingArcs(fpta1,state);
				printIncomingArcs(fpta1,state);
				fpta1.show(fpta1, "error");
			}
			fpta1.setFinalFrequency(state, Math.abs(outcoming-incoming));
		}
		
	}
    /*+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-*/

	@Override
	public void mergeModel(FPTA fpta2)
	{    	
		Set<String> isEffedted = new HashSet<String>();
		betaStochasticFold(getFpta(),"",fpta2,"","",isEffedted);  
		getFpta().setInitialFrequency("", getFpta().getInitialFrequencies().get("")+fpta2.getInitialFrequencies().get(""));   		 
		Set<String> visitedStates = new HashSet<String>();
		Set<String> fixStates = new HashSet<String>();
		rebalanceFPTA1(getFpta(),"",visitedStates);
	}
    /*+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-*/

	@Override
	public void mergeModel(FPTA fpta1, FPTA fpta2) {
		Set<String> isEffedted = new HashSet<String>();		
	//betaStochasticFold(getFpta(),"",fpta2,"","",isEffedted); 
		visitedPatterns = new HashSet<>();
		if(fpta1.getFinalFrequencies().get("")==null)
		{
			fpta1.setFinalFrequency("",(long)0);
		}
		if(fpta2.getFinalFrequencies().get("")==null)
		{
			fpta2.setFinalFrequency("",(long)0);
		}
		Set<String> visitedStates = new HashSet<String>();	
		Set<String> fixStates = new HashSet<String>();
		for(String state: fpta2.mergeState.keySet())
			fpta1.mergeState.put(state, fpta2.mergeState.get(state));
		fpta1 = alphaStochasticFold(fpta1, fpta2,8);
	}
	@Override
	public void stochasticFold(FPTA dffa1, String q, FPTA dffa2, String qPrime, String pattern) {
		// TODO Auto-generated method stub
		
	}		
}
class PairState{
	private String first;
	private String second;
	public PairState(String first,String second) {
		
		this.first= first;
		this.second = second;
	}
	public String getFirst() {
		return first;
	}
	public void setFirst(String first) {
		this.first = first;
	}
	public String getSecond() {
		return second;
	}
	public void setSecond(String second) {
		this.second = second;
	}
	
}