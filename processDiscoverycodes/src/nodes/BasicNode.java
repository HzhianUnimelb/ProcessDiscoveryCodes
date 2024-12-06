package nodes;

import java.util.HashMap;

import model.ALERGIA;
import model.FPTA_Method;
import model.Model;
import process2.FrequencyBasedFiltering;
import unilities.LogParser;

public class BasicNode {
	private Model model;
    private Object output;
    private LogParser logParser;
    private FrequencyBasedFiltering filtering;
    private int id;
    private HashMap<String, Long> log;
    protected double filteringThreshold; 
    /*-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+*/
    public BasicNode(int id,String fileName) {
    	logParser = new LogParser(fileName);
    	this.setId(id);
    }
    /*-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+*/
    public BasicNode() {

    }
    /*-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+*/
    public BasicNode(int id,double filteringThreshold,HashMap<String, Long> eventLog,String algorithmName) {
    	this.setId(id);
    	filtering = new FrequencyBasedFiltering();
    	log = filtering.filterEventLog(eventLog, filteringThreshold);
    	if(algorithmName.compareTo("ALERGIA")==0)
    	{
    		this.model = new ALERGIA(filteringThreshold,log);
    	}
    	this.filteringThreshold = filteringThreshold;
    }
    /*-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+*/
    public BasicNode(int id,double alpha,String fileName,String algorithmName) {
    	this.setId(id);
    	logParser = new LogParser(fileName);
    	
    	log = logParser.extractEvent(logParser.readMapList("actionMap.txt"));
    	 
    }
    /*-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+*/
    void InitialFPTA() {
    	filtering = new FrequencyBasedFiltering();
    	log = filtering.filterEventLog(log, filteringThreshold);
    	this.model = new ALERGIA(filteringThreshold,log); 
    }
    /*-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+*/
    public BasicNode(double alpha,int num,String algorithmName) {
    	HashMap<String, Long> log = new HashMap<String, Long>() ;
    	if(num==0)
    	{
    		log.put("",  (long)1);
    		log.put("a",  (long)4);
    		log.put("aa",  (long)4);
    		log.put("b",  (long)2);
    		///log.put("b",  (long)10);
    		//log.put("bb",  (long)10);

    		
    	//	filtering = new FrequencyBasedFiltering();
    	//log = filtering.filterEventLog(log, 1);
    	}
    	else if(num==1)
    	{
    		log.put("",  (long)4);
    		log.put("a",  (long)2);
    		log.put("b",  (long)4);
    		log.put("bb",  (long)4);

    		//log.put("b",  (long)40);
    	//	log.put("bb",  (long)40);
    		
    //		filtering = new FrequencyBasedFiltering();
    //		log = filtering.filterEventLog(log, 0.50);
    	}
    	else if(num==2)
    	{
    		log.put("",  (long)10);
    		log.put("ac",  (long)10);
    		log.put("cb",  (long)10);
    		log.put("cbb",  (long)10);
    		
    	/*	log.put("", 2);
    		log.put("a", 2);
    		log.put("bb", 2);
    		log.put("abc", 4);
    		log.put("abb", 4);*/
    //		filtering = new FrequencyBasedFiltering();
    //		log = filtering.filterEventLog(log, 0.50);
    	}
    	if(algorithmName.compareTo("ALERGIA")==0)
    		this.model = new ALERGIA(alpha,log);
    	else if(algorithmName.compareTo("FPTA_Method")==0)
    		this.model = new FPTA_Method(alpha,log);       
    }
    
    public void runModel() {
        this.output = model.run();
    }

    public Object getOutput() {
        return this.output;
    }
    public Model getModel() {
    	return model;
    }

	public LogParser getLogParser() {
		return logParser;
	}

	public void setLogParser(LogParser logParser) {
		this.logParser = logParser;
	}

	public FrequencyBasedFiltering getFiltering() {
		return filtering;
	}

	public void setFiltering(FrequencyBasedFiltering filtering) {
		this.filtering = filtering;
	}
    /*-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+*/
	public static void main(String[] args) {
		// TODO Auto-generated method stub

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

	public double getFilteringThreshold() {
		return filteringThreshold;
	}
    /*-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+*/
	public HashMap<String, Long> getLog(){
		return log;
	}

}
