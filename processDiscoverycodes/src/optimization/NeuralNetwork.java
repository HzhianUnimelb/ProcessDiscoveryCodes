package optimization;

import org.encog.engine.network.activation.ActivationSigmoid;
import org.encog.ml.data.MLData;
import org.encog.ml.data.MLDataPair;
import org.encog.ml.data.MLDataSet;
import org.encog.ml.data.basic.BasicMLDataSet;
import org.encog.neural.*;
/*
 * Encog(tm) Java Examples v3.4
 * http://www.heatonresearch.com/encog/
 * https://github.com/encog/encog-java-examples
 *
 * Copyright 2008-2017 Heaton Research, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *   
 * For more information on Heaton Research copyrights, licenses 
 * and trademarks visit:
 * http://www.heatonresearch.com/copyright
 */


import org.encog.Encog;
import org.encog.engine.network.activation.ActivationReLU;
import org.encog.engine.network.activation.ActivationSigmoid;
import org.encog.neural.networks.training.propagation.back.Backpropagation;
import org.encog.neural.networks.training.propagation.resilient.ResilientPropagation;
import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.layers.BasicLayer;
import org.encog.neural.networks.layers.Layer;
import org.encog.neural.networks.training.Train;

public class NeuralNetwork {
	BasicNetwork network; 
	public NeuralNetwork()
	{
		network= new BasicNetwork();
		network.addLayer(new BasicLayer(null,true,2));
		network.addLayer(new BasicLayer(new ActivationReLU(),true,10));
       // network.addLayer(new BasicLayer(new ActivationReLU(), true, 10)); // Additional hidden layer

		network.addLayer(new BasicLayer(null,false,1));
		network.getStructure().finalizeStructure();
		network.reset();
	}
	
	public void train(double[][]input,double [][]output) {
		MLDataSet trainingSet = new BasicMLDataSet(input, output);
		
		// train the neural network
		final ResilientPropagation train = new ResilientPropagation(network, trainingSet);
		for(int i=0;i<5000;i++)
		{
			train.iteration();
		}
		train.finishTraining();
	}
	public double predict(double []input)
	{

		double output[]= new double[1];
		network.compute(input,output);
		Encog.getInstance().shutdown();
		return output[0];
	}

	/**
	 * The ideal data necessary for XOR.
	 */
	
	
	/**
	 * The main method.
	 * @param args No arguments are used.
	 */
	public static void main(final String args[]) {
		
		// create a neural network, without using a factory
		

		// create training data
		
		double []output = new double[2];
		double input[] = new double[2];
		input[0]=0.6;
		input[1] = 60;
		
		// test the neural network
		System.out.println("Neural Network Results:");
	/*	for(MLDataPair pair: trainingSet ) {
			final MLData output = network.compute(pair.getInput());
			System.out.println(pair.getInput().getData(0) + "," + pair.getInput().getData(1)
					+ ", actual=" + output.getData(0) + ",ideal=" + pair.getIdeal().getData(0));
		}
		*/
		
	}
}