package optimization;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import model.FPTA;
import nodes.EdgeNode;
import nodes.OptimizerEdgeNode;
import performance.PerformanceEstimator;
import java.math.*;
public class SurrogateModel extends Optimization {
    private List<double[]> trainingInputs; // Stores input pairs [alpha, filtering threshold]
    private List<Double> trainingOutputs;   // Stores corresponding fitness scores
    protected OptimizerEdgeNode edgeNode;
	private double inputParameter[];
	private NeuralNetwork neuralNet;
    private KolmogorovArnoldNetwork kan ; // 5 basis functions

    public static double calculateExpression(double x1, double x2) {
        double term1 = (25 * x1) / 50;
        double term2 = - (8 * x2) / 25;
        double term3 = - (8 * Math.sin((626 * x1 / 231) - (509.0 / 84.0))) / 89;
        double term4 = (3 * Math.sin((12 * x2 / 5) + (407.0 / 102.0))) / 20;
        double term5 = (17 * Math.sin((7 * x1 / 10) + (13 * x2 / 261) + (667.0 / 84.0))) / 25;
        double term6 = - (3 * Math.sin((586 * x1 / 383) + (13 * x2 / 261) + (1037.0 / 100.0))) / 10;
        double term7 = - (3.0 / 25.0);

        // Summing all terms
        double result = term1 + term2 + term3 + term4 + term5 + term6 + term7;
        
        return result;
    }
    /*-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+*/
    public SurrogateModel(double noiseVariance,int maxItr) {
    	super(0,1,maxItr);
    	setEdgeNode(new OptimizerEdgeNode(id,actions.size(),eventLog));
    	inputParameter= new double[dimensions];
        this.trainingInputs = new ArrayList<>();
        this.trainingOutputs = new ArrayList<>();
       
        //neuralNet = new NeuralNetwork(); // 2 inputs, 10 hidden neurons, 1 outp
       // kan = new KolmogorovArnoldNetwork(3, 10, 0.005); // 5 layers, 10 basis functions each
;
    }
    /*-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+*/ 
    /*-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+*/
    public double evaluatePrediction(double alpha,double filteringThreshold) {
    	inputParameter[0] = alpha; // Random alpha between 0.1 and 0.8
    	inputParameter[1] = filteringThreshold; // Random threshold between 1 and 100
        // Evaluate the fitness score
        double fitness[] = optimizationFunction(null); // Replace with actual fitness evaluation
        return fitness[0];
    }
    /*-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+*/
   public void generateTrainingData(int maxIter) {
    	try {
			FileWriter trainInputFWiter = new FileWriter("input-ER.txt");
			FileWriter EROutputFWiter = new FileWriter("output-ER.txt");
			FileWriter SIZEOutputFWiter = new FileWriter("output-SIZE.txt");
			FileWriter totalEOutputFWiter = new FileWriter("output-total.txt");
		int indexX=1;
		int indexY=1;
    	double solution[] = new double[3];
    	for(double i=0.1;i<=1.0;i+=0.05)
    	{
    		for(double j=0.1;j<=1;j+=0.05)
    		{	
    			for(double h=0.01;h<=0.1;h+=0.01)
        		{
    				solution[0] = i; // Select alpha
    				solution[1] = j; // Select threshold
    				solution[2] = h;
    				double result[] = calculateFitnss(solution);
                		//calculateFitnss(solution); // Replace with actual fitness evaluation
    				trainInputFWiter.write("("+solution[0]+","+solution[1]+")\n");
    				EROutputFWiter.write((String.format("%.3f",solution[0]))+"	"+(String.format("%.3f",solution[1]))+"	"+(String.format("%.3f",solution[2]))+"	"+String.format("%.3f", result[0])+"\n");
                
                	
    				SIZEOutputFWiter.write((String.format("%.3f",solution[0]))+"	"+(String.format("%.3f",solution[1]))+"	"+(String.format("%.3f",solution[2]))+"	"+String.format("%.5f", result[1])+"\n");
    				totalEOutputFWiter.write((String.format("%.3f",solution[0]))+"	"+(String.format("%.3f",solution[1]))+"	"+(String.format("%.3f",solution[2]))+"	"+String.format("%.5f", result[2])+"\n");
    				System.out.println(solution[0]+" "+solution[1]+" <----position");
    				indexY++;
        		}
    		}
    	}
    	trainInputFWiter.close();
    	EROutputFWiter.close();
    	SIZEOutputFWiter.close();
    	totalEOutputFWiter.close();
    	} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    
    }
/*  public void generateTrainingData(int maxIter) {
        Random rand = new Random(System.currentTimeMillis());
        try {
			FileWriter trainInputFWiter = new FileWriter("input.txt");
			FileWriter testInputFWiter = new FileWriter("input1.txt");
			FileWriter trainOutputFWiter = new FileWriter("output.txt");
			FileWriter testOutputFWiter = new FileWriter("output1.txt");
			boolean flag = false,flag1;
			
       // Set a maximum number of iterations
        int sampleCount = 0; // To keep track of the number of samples

        // Define possible values for alpha and threshold
       
        // Loop until maxIter or a condition is met
        for (int i = 0; i < maxIter; i++) {
            // Sample alpha and threshold equally
        	double solution[] = new double[2];
        	solution[0] = 0.1+rand.nextDouble()*0.9; // Select alpha
            solution[1] = 0.1+rand.nextDouble()*(0.9); // Select threshold
            // Evaluate the fitness score
            double fitness = calculateFitnss(solution); // Replace with actual fitness evaluation
            if(i>0.8*maxIter)
            {
            	if(!flag)
            	{
                	testInputFWiter.write("[["+solution[0]+",  "+solution[1]+"],\n");
                	flag=true;
                	testOutputFWiter.write("[["+fitness+"],\n");
            	}
            	else
            	{
                	testInputFWiter.write("["+solution[0]+",  "+solution[1]+"],\n");
                	testOutputFWiter.write("["+fitness+"],\n");
            	}
            }
            if(i==0)
            {
            	trainInputFWiter.write("[["+solution[0]+",  "+solution[1]+"],\n");
            	trainOutputFWiter.write("[["+fitness+"],\n");
            }
            else
            {
            	trainInputFWiter.write("["+solution[0]+",  "+solution[1]+"],\n");
            	trainOutputFWiter.write("["+fitness+"],\n");

            }
            

            // Store the training data
            addTrainingData(solution[0],solution[1], fitness);
        }
        trainInputFWiter.close();
        testInputFWiter.close();
        trainOutputFWiter.close();
        testOutputFWiter.close();
        testInputFWiter.close();
          //  trainNeuralNetwork();
        } catch (IOException e) {
			// TODO Auto-generatsed catch block
			e.printStackTrace();
		}
    }*/
    /*-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+*/
    public double[] calculateFitnss(double input[])
    {
    	FPTA fpta =	getEdgeNode().runModel(input[0],input[1]*100,input[2],"ALEEGIA");
    	double []result= new double[3];
     	HashMap<String,Double> matric = getEdgeNode().getPerformanceEstimator().calculatePerformanceMetrics(fpta,getEdgeNode().getEventLog(),getEdgeNode().getActionList());
    	double metric1 = matric.get("Entropic Relevance")/getEdgeNode().getPerformanceEstimator().getUnpperBoundEntropicRelevance(); ;// Example metric 1
    	result[1] = matric.get("Size");
        double metric2 = result[1]/getEdgeNode().getPerformanceEstimator().getUpperBoundSize();  // Example metric 2
    	double fit = (0.2*(1-metric1) + 0.8*(1-metric2));
        // Adjust this as needed for your optimization goal
        	result[0] = (metric1);
        	result[2] = fit;
        	return result;
    }
    /*-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+*/
    public void trainNeuralNetwork() {
        double[][] inputArray = new double[trainingInputs.size()][2];
        double[][] outputArray = new double[trainingOutputs.size()][1];
        double[] outputArray1 = new double[trainingOutputs.size()];
        for (int i = 0; i < trainingInputs.size(); i++) {
            inputArray[i] = trainingInputs.get(i);
            outputArray[i][0] = trainingOutputs.get(i);
            outputArray1[i]=trainingOutputs.get(i);
        }
        neuralNet.train(inputArray,outputArray);
        kan.train(inputArray, outputArray1, 1000);
    }
    /*-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+*/
    /*-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+*/
    // Method to add training data
    public void addTrainingData(double alpha, double threshold, double fitness) {
        trainingInputs.add(new double[]{alpha, threshold});
        trainingOutputs.add(fitness);
    }
    /*-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+*/

    /*-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+*/
    public static void main(String[] args) {
        SurrogateModel gp = new SurrogateModel(0.1,200); // Set noise variance
        gp. optimize();
    }
    /*-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+*/
	@Override
	public void run() {
		// TODO Auto-generated method stub
		
	}
    /*-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+*/
	@Override
	public void optimize() {
		

		// TODO Auto-generated method stub
		generateTrainingData(1000);
	   // double predictedfit = kan.evaluate(inputParameter[0], (inputParameter[1]-1)/99);
	   // double fitness = optimizationFunction(null);
	   // System.out.println(inputParameter[0]+" "+inputParameter[1]+" --- "+"real Value--> "+fitness+" predicted Value--> "+predictedfit);

	  // System.out.println("real--> "+fitness+" predicted value "+  neuralNet.predict(inputParameter));
	   
	    //OptimizerFinder pso = new OptimizerFinder(neuralNet);
	     
	   // inputParameter = pso.findBest();
       // System.out.println("Optimized Parameters: Alpha = " + inputParameter[0] + ", Threshold = " + inputParameter[1]);
	   // inputParameter = pso.findBest();
	   // double []predictedFitness= new double[1];
	  //  
	   // inputParameter[1] = (4.7-1)/99;
	 //   inputParameter[0]=0.562;
	 //   inputParameter[1]=48;
	 //   double fitness = optimizationFunction(null);
	  //  inputParameter[1]=(double) 47/(double)99;
      //  double predictedfit = neuralNet.predict(inputParameter);
      //  System.out.println(inputParameter[0]+" "+inputParameter[1]+" --- "+"real Value--> "+fitness+" predicted Value--> "+predictedfit);
       // for(double a : predictedFitness)
       // 	System.out.print(a);
       // System.out.println("\n-----------------");
	    
	}
    /*-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+*/
	public  OptimizerEdgeNode getEdgeNode() {
		return edgeNode;
	}
    /*-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+*/
	public void setEdgeNode(OptimizerEdgeNode edgeNode) {
		this.edgeNode = edgeNode;
	}
}