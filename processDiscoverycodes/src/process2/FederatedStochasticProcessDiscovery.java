package process2;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import model.ALERGIA;
import model.FPTA;
import optimization.NonOpt;
import optimization.Optimization;
import optimization.evolutionBasedSolutions.DifferentialEvolution;
import optimization.evolutionBasedSolutions.Genetic;
import optimization.natureBasedSolutions.BlackHole;
import optimization.natureBasedSolutions.GravitationalSearch;
import optimization.stochastic.SimulatedAnnealing;
import optimization.swarmBasedSolutions.ArtificialBeeColony;
import optimization.swarmBasedSolutions.PSO;
import performance.EntropicRelevanceCalculator;
import performance.PerformanceAnalyser;
import unilities.LogParser;


public class FederatedStochasticProcessDiscovery {
    private int numEdgeNodes;
    private List<Optimization> optEdgeNodes;
    private PerformanceAnalyser performanceAnalyser;
    private EntropicRelevanceCalculator entropicRelevanceCalculator;
    private LogParser logParser;
    private SimpleDateFormat sdf ;
    private boolean ParetoFront;
    private boolean OPTFlag;
    /*-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+*/
    public FederatedStochasticProcessDiscovery(int iteration,int population,int numEdgeNodes,String optName,boolean ParetoFront,boolean OPTFlag) {
    	sdf = new SimpleDateFormat("hh:mm:ss:SSS");
    	this.ParetoFront = ParetoFront;
    	this.OPTFlag = OPTFlag;
    	performanceAnalyser = new PerformanceAnalyser();
        this.numEdgeNodes = numEdgeNodes;
        this.optEdgeNodes = new ArrayList<Optimization>();
        
        String fileName="Hospital Billing - Event Log.xes";
        logParser = new LogParser(fileName);
        System.out.println("Extracting Log Started... "+ sdf.format(new Date()));
        for (int i = 0; i < numEdgeNodes; i++) {
        	if(OPTFlag)
        	{
        		if(optName.compareTo("PSO")==0)
        			optEdgeNodes.add(new PSO(i,iteration,population));
        		else if(optName.compareTo("GEN")==0)
        			optEdgeNodes.add(new Genetic(i,iteration,population,0.8, 0.0));
        		else if(optName.compareTo("BEE")==0)
        			optEdgeNodes.add(new ArtificialBeeColony(i,iteration,population,5));
        		else if(optName.compareTo("DE")==0)
        			optEdgeNodes.add(new DifferentialEvolution(i,iteration,population,0.5,0.9));
        		else if(optName.compareTo("SA")==0)
        			optEdgeNodes.add(new SimulatedAnnealing(i,1000, 0.95));
        		else if(optName.compareTo("GSA")==0)
        			optEdgeNodes.add(new GravitationalSearch(i,iteration,population,100));
        		else if(optName.compareTo("BLH")==0)
        			optEdgeNodes.add(new BlackHole(i,iteration,population,0.1));
        	}
        	else
        	{
        		optEdgeNodes.add(new NonOpt(i,0,0));
        	    
        	}
        		//edgeNodes.add(new EdgeNode(i,0.5,"chunk_"+i+".xes","ALERGIA"));
        	//edgeNodes.add(new EdgeNode(0.5,i,"FPTA_Method"));
        }
    }
    /*-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+*/
    public void extractModel() {
        ExecutorService executor = Executors.newFixedThreadPool(numEdgeNodes);
        System.out.println("Extracting Models Started... "+ sdf.format(new Date()));
        for (int i = 0; i < numEdgeNodes; i++) {
            Optimization opt = optEdgeNodes.get(i);
            executor.execute(new Runnable() {
                @Override
                public void run() {
                	opt.run();
                }
            });
        }
        executor.shutdown();
        while (!executor.isTerminated()) {

        }
        System.out.println("Finished all threads");
       // unionModels(edgeNodes);
   }
    /*-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+*/
    public List<FPTA> rotateList(List<FPTA> fptaList){
    	Collections.rotate(fptaList, 1);
    	return fptaList;
    }
    /*-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+*/
    public List<FPTA> FilterModles(List<Optimization> nodeList){
    	List<FPTA> result = new ArrayList<FPTA>();
    	Collections.sort(nodeList, Comparator.comparingDouble(Optimization ::getGlobalBestValue));
    	
		result.add(nodeList.get(nodeList.size()-1).getBestModel());
		double bestmetric[] = nodeList.get(nodeList.size()-1).getBestMetric();
	
		for(int i =0;i< nodeList.size()-1; i++)
		{
			Optimization node = nodeList.get(i);
			double metric[] = node.getBestMetric();
			if(bestmetric[0]<= metric[0] && bestmetric[1]<= metric[1])
				continue;
			else
			{
				result.add(node.getBestModel());
			}
		}
		return result;
    }
    /*-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+*/
    public List<FPTA> selectModels(List<Optimization> nodeList){
    	List<FPTA> result = new ArrayList<FPTA>();
    	if(ParetoFront)
    	{
    		return FilterModles(nodeList);
    	}
    	for(Optimization opt : nodeList)
    	{
    		result.add(opt.getBestModel());
    	}	
    	return result;
    }
    /*-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+*/
    public int unionModels(List<FPTA> fptas) {
    	FPTA mergedDffa = fptas.get(0);
    	mergedDffa.show(mergedDffa, null);
    	ALERGIA alergia = new ALERGIA(mergedDffa);
    	for(int i = 0; i< fptas.size(); i++)
    	{
    		fptas.get(i).show(fptas.get(i), "model "+i);
    		System.out.println(i+" merged");
    	}
    	
    	for(int i = 1; i< fptas.size(); i++)
    	{
    		alergia.mergeModel(mergedDffa, fptas.get(i));
    		System.out.println(i+" merged");
    	}
    	System.out.println("Merged finished");
    	mergedDffa.show(mergedDffa, "merge model");
//	mergedDffa
    	
		//log.put("", 2);
    //	log.put("abb", 1);
    //	log.put("c", 10000);
	//	log.put("a", 1);
	//	log.put("aa", 4);
	//	log.put("aba", 3);
	//	log.put("aaa", 1);
	//	log.put("aaaa", 1);
	//	log.put("aaaaa", 1);
	//	log.put("aaaaaa", 1);
	//	log.put("aaaaaaa", 1);
	//	log.put("aaaaaaaa", 1);
	//	log.put("aaaaaaaaa", 1);
	//	log.put("aaaaaaaaaa", 1);
		//log.put("aa", 4);
		//log.put("ab", 3);
		//log.put("aba", 3);
    	//mergedDffa = ALERGIA.extractModel(mergedDffa,nodeList.get(0).getModel().getAlpha());
    	//mergedDffa.show(mergedDffa, "after ALERGIA");
    	return 0;
    }   
    /*-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+*/
    public static void main(String[] args) {
        // Create a sample S
    	FederatedStochasticProcessDiscovery fspd= new FederatedStochasticProcessDiscovery(5,10,1,"GEN",true,true);
      
    	HashMap<String, Long> log = new HashMap<String,Long>() ;
    	String fileName="Hospital Billing - Event Log.xes";
/*	log.put("",  (long)80);
	log.put("b",  (long)40);
	log.put("a",  (long)20);
	log.put("bc",  (long)40);*/
      	LogParser logParser = new LogParser(fileName);

      /*	log.put("",  (long)80);
		log.put("b",  (long)70);
		log.put("bb",  (long)40);
		log.put("a",  (long)30);
		log.put("aa",  (long)20);
		log.put("baba",  (long)30);
		log.put("bbaa",  (long)30);
		log.put("bbaaa",  (long)30);
		log.put("bc",  (long)40);*/
   	   log = logParser.extractEvent();
    	//log.put("",  (long)60);
		//log.put("a",  (long)30);
	/*	log.put("bb",  (long)140);
		log.put("bba",  (long)40);
		log.put("bbaa",  (long)40);
		log.put("bbaaa",  (long)40);
		log.put("aa", (long) 100);
		log.put("bbabba",  (long)40);*/
        fspd.FindBestSolution(fspd.optEdgeNodes,log);
   
    	//fspd.extractALERGIAfromFPTS(log);
    }
    
   
    /*-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+*/
    public void FindBestSolution(List<Optimization> nodeList, HashMap<String, Long> log) {
    	extractModel();
    	//mergedDffa = ALERGIA.extractModel(mergedDffa,nodeList.get(0).getModel().getAlpha());

    	System.out.println("Models Extracted... "+ sdf.format(new Date()));
    	List<FPTA> models = new ArrayList<FPTA>();

    	int index = calculateMergedModels(nodeList);
		printReport(nodeList.get(index).getBestModel(),log);
  /*  	for(int i=0;i<nodeList.size();i++)
    	{
    		List<FPTA>fptas = new ArrayList<FPTA>();
    		for(int j=0;j<models.size();j++)
    		{
    			FPTA fpta = new FPTA();
    			models.get(j).copy(fpta);
    			fptas.add(fpta);
    		}
    		calculateMergedModels(fptas);
    		fptas.get(0).show(fptas.get(0), "after merging"+i);
    		printReport(fptas.get(0),log);
    		models = rotateList(models);		
    	}*/
        
    }
    /*-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+*/
    public void printReport(FPTA mergedDffa, HashMap<String, Long> log) {
    	System.out.println("Performance Analysing Started... "+ sdf.format(new Date()));
    	System.out.println("*-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+");
   // 	System.out.println("Fitness is: "+ performanceAnalyser.calculateFitness(mergedDffa,log));
    	
    	
    	
    //	System.out.println("Generaliation is: "+ performanceAnalyser.calculateGeneraliation(mergedDffa, log));
    	//FrequencyBasedFiltering filtering = new FrequencyBasedFiltering();
    	//log = filtering.filterEventLog(log, 0.001);
    	System.out.println("Percision is: "+ performanceAnalyser.calculatePercision(mergedDffa, log));
		System.out.println("Size is "+performanceAnalyser.calculateSize(mergedDffa));
		System.out.println("fitness1 is: "+ performanceAnalyser.calculateFitness1(mergedDffa, log));
		HashMap<String, Character> readMapList = logParser.readMapList("actionMap.txt");
    	int actionListSize = readMapList.size();
		entropicRelevanceCalculator = new EntropicRelevanceCalculator(mergedDffa,log,actionListSize);
		System.out.println("ER is: "+ entropicRelevanceCalculator.getER());

    }
    /*-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+*/
    public int calculateMergedModels(List<Optimization> nodeList) {
      return  unionModels(selectModels(nodeList));
    }
    /*-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+*/
	public PerformanceAnalyser getPerformanceAnalyser() {
		return performanceAnalyser;
	}
	public void setPerformanceAnalyser(PerformanceAnalyser performanceAnalyser) {
		this.performanceAnalyser = performanceAnalyser;
	}
	public boolean isOPTFlag() {
		return OPTFlag;
	}
	public void setOPTFlag(boolean oPTFlag) {
		OPTFlag = oPTFlag;
	}
}