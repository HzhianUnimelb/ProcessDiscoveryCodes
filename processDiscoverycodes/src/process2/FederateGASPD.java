package process2;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import model.ALERGIA;
import model.FPTA;
import optimization.BasicObject;
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


public class FederateGASPD {
    private int numEdgeNodes;
    private List<Optimization> optEdgeNodes;
    private PerformanceAnalyser performanceAnalyser;
    private EntropicRelevanceCalculator entropicRelevanceCalculator;
    private LogParser logParser;
    private SimpleDateFormat sdf ;
    private boolean ParetoFront;
    private boolean OPTFlag;
    private KMeans clustring;
    /*-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+*/
    public FederateGASPD(int iteration,int population,int numEdgeNodes,String optName,boolean ParetoFront,boolean OPTFlag) {
    	sdf = new SimpleDateFormat("hh:mm:ss:SSS");
    	this.ParetoFront = ParetoFront;
    	this.OPTFlag = OPTFlag;
    	performanceAnalyser = new PerformanceAnalyser();
        this.numEdgeNodes = numEdgeNodes;
        this.optEdgeNodes = new ArrayList<Optimization>();
        
        String fileName="BPI_Challenge_2013_incidents.xes";
        logParser = new LogParser(fileName);
        System.out.println("Extracting Log Started... "+ sdf.format(new Date()));

        ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        List<Future<Void>> futures = new ArrayList<>();

        for (int i = 0; i < numEdgeNodes; i++) {
            final int index = i; // Need to make i effectively final for use in lambda
            Callable<Void> task = () -> {
                if (OPTFlag) {
                    if (optName.compareTo("PSO") == 0)
                        optEdgeNodes.add(new PSO(index, iteration, population));
                    else if (optName.compareTo("GEN") == 0)
                        optEdgeNodes.add(new Genetic(index, iteration, population, 0.8, 0.0));
                    else if (optName.compareTo("BEE") == 0)
                        optEdgeNodes.add(new ArtificialBeeColony(index, iteration, population, 5));
                    else if (optName.compareTo("DE") == 0)
                        optEdgeNodes.add(new DifferentialEvolution(index, iteration, population, 0.5, 0.9));
                    else if (optName.compareTo("SA") == 0)
                        optEdgeNodes.add(new SimulatedAnnealing(index, 1000, 0.95));
                    else if (optName.compareTo("GSA") == 0)
                        optEdgeNodes.add(new GravitationalSearch(index, iteration, population, 100));
                    else if (optName.compareTo("BLH") == 0)
                        optEdgeNodes.add(new BlackHole(index, iteration, population, 0.1));
                } else {
                    optEdgeNodes.add(new NonOpt(index, 0, 0));
                }
                return null; // Callable must return a value
            };
            futures.add(executor.submit(task));
        }

        // Wait for all tasks to complete
        for (Future<Void> future : futures) {
            try {
                future.get(); // This will block until the task is complete
            } catch (Exception e) {
                e.printStackTrace(); // Handle exceptions as needed
            }
        }

        executor.shutdown();
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
    public List<List<Optimization>> clusterModels(List<Optimization> nodeList,int type){
    	if(ParetoFront)
    	{
    		List<Optimization> removelist = new ArrayList<Optimization>();
        	for(Optimization node:nodeList)
        	{
        		for(Optimization node1:nodeList)
        		{
        			if(node.equals(node1)==false)
        			if(node1.getBestMetric()[0]>node.getBestMetric()[0])
        				if(node1.getBestMetric()[1]>node.getBestMetric()[1])
        				{
        					removelist.add(node1);
        					continue;
        				}
        		}
        	}
        	nodeList.removeAll(removelist);
    	}
    	if(type==0)
    		clustring = new KMeans(1, nodeList);
    	else
    		clustring = new KMeans(8, nodeList);
    	List<List<Optimization>> clusters= clustring.fit(100);
    /*	Collections.sort(nodeList, Comparator.comparingDouble(Optimization ::getGlobalBestValue));
    	
		result.add(nodeList.get(nodeList.size()-1).getBestModel());
		HashMap<String,Double> bestmetric = nodeList.get(nodeList.size()-1).getBestMetric();
	
		for(int i =0;i< nodeList.size()-1; i++)
		{
			Optimization node = nodeList.get(i);
			HashMap<String,Double> metric = node.getBestMetric();
			if(bestmetric.get("Fitness")>= metric.get("Fitness") && bestmetric.get("Size")<= metric.get("Size"))
				continue;
			else
			{
				result.add(node.getBestModel());
			}
		}*/
		return clusters;
    }
    /*-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+*/

    /*-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+*/
    public List<FPTA> unionModels(List<List<Optimization>> clusters) {
    	List<FPTA> mergedModel = new ArrayList<FPTA>();
    	for(int i=0;i<clusters.size();i++)
    	{
    		if(clusters.get(i).size()>0)
    		{
    			System.out.println("Cluster number->"+i);;
    			FPTA mergedDffa = clusters.get(i).get(0).getBestModel();
    			ALERGIA alergia = new ALERGIA(mergedDffa);	
    			for(int j = 1; j< clusters.get(i).size(); j++)
    	    	{				
    			//	FPTA tree = mergedDffa.reverseDFFA( clusters.get(i).get(j).getBestModel());
    				alergia.mergeModel(mergedDffa, clusters.get(i).get(j).getBestModel());
    	    		System.out.println("merged "+i);    	    	}
    				mergedModel.add(mergedDffa);
    			}
    	}
    	return mergedModel;
    
    }   
    /*-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+*/
    public static void main(String[] args) {
        // Create a sample S
    	int cluster_type=0;
    	for(int i=0;i<20;i++)
    	{
    		FederateGASPD fspd= new FederateGASPD(20,5,4,"GEN",true,true);
    		HashMap<String, Long> log = new HashMap<String,Long>() ;
    		String fileName="BPI_Challenge_2013_incidents.xes";
    		LogParser logParser = new LogParser(fileName);
    		log = logParser.extractEvent();
    		if(i<10)
    			cluster_type =0;
    		else
    		{
    			cluster_type =1;
    		}
    			fspd.FindBestSolution(fspd.optEdgeNodes,log,cluster_type);

    		System.out.println("*****************ROUND number*******************");
    	}
    	//fspd.extractALERGIAfromFPTS(log);
    }
    
   
    /*-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+*/
    public void FindBestSolution(List<Optimization> nodeList, HashMap<String, Long> log,int clusterType) {
    	extractModel();
    	System.out.println("Models Extracted... "+ sdf.format(new Date()));
    	List<FPTA> mrgedmodels1 = calculateMergedModels(nodeList,clusterType);
    	for(FPTA fpta:mrgedmodels1)
    		printReport(fpta,log);
		
        
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
    public List<FPTA> calculateMergedModels(List<Optimization> nodeList,int type) {
      return unionModels(clusterModels(nodeList,type));
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