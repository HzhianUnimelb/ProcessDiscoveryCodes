package model;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.swing.JFrame;

import org.jgrapht.Graph;
import org.jgrapht.ext.*;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultEdge;

import com.mxgraph.model.mxGeometry;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.view.mxGraph;

import org.jgrapht.*;
import org.graphstream.graph.*;
import org.graphstream.graph.implementations.*;
import org.apache.commons.math3.fraction.Fraction;
import java.io.StringWriter;
import java.io.Writer;

public class DPFA extends PFA {

	public DPFA()
	{
		 
	}
    public DPFA(Set<String> states, Set<String> alphabet) {
        super(states, alphabet);
    }

    public boolean isDPFA() {
        // Check the conditions of a PFA
        if (!checkPFA()) {
            return false;
        }

        // Check if there is a unique initial state with probability 1
        int count = 0;
        for (Fraction prob : initialProbabilities.values()) {
            if (prob.equals(new Fraction(1))) {
                count++;
            }
        }
        if (count != 1) {
            return false;
        }

        // Check if there are no λ-transitions
        for (Map<String, Map<String, Fraction>> symbolTransitions : transitionProbabilities.values()) {
            if (symbolTransitions.containsKey("")) {
                return false;
            }
        }

        // Check if for each state and symbol, there is at most one transition with probability > 0
        for (Map<String, Map<String, Fraction>> symbolTransitions : transitionProbabilities.values()) {
            for (Map<String, Fraction> transitions : symbolTransitions.values()) {
            	  int countTransitions = 0;
                  for (Fraction prob : transitions.values()) {
                      if (prob.compareTo(new Fraction(0)) < 0 || prob.compareTo(new Fraction(1)) > 0) {
                          return false; // Probability is not between 0 and 1
                      }
                      if (prob.compareTo(new Fraction(0)) > 0) {
                          countTransitions++;
                      }
                  }
                  if (countTransitions > 1) {
                      return false;
                  }
              }
        }

        return true;
    }
    public static void show(DPFA dpfa) {
        mxGraph graph = new mxGraph();
        Object parent = graph.getDefaultParent();

        // Show DFFA
       
        // Show DPFA
        for (String state : dpfa.states) {
            String vertexName = state + ":FP(" + dpfa.finalProbabilities.get(state) + ")";
            if (dpfa.initialProbabilities.containsKey(state) && dpfa.initialProbabilities.get(state).doubleValue() > 0) {
                vertexName = "IP(" + dpfa.initialProbabilities.get(state) + "):" + vertexName;
            }
            graph.insertVertex(parent, null, vertexName, 0, 0, 100, 30);
        }

        for (String fromState : dpfa.transitionProbabilities.keySet()) {
            for (String symbol : dpfa.transitionProbabilities.get(fromState).keySet()) {
                for (String toState : dpfa.transitionProbabilities.get(fromState).get(symbol).keySet()) {
                    String fromVertexName = fromState + ":FP(" + dpfa.finalProbabilities.get(fromState) + ")";
                    if (dpfa.initialProbabilities.containsKey(fromState) && dpfa.initialProbabilities.get(fromState).doubleValue() > 0) {
                        fromVertexName = "IP(" + dpfa.initialProbabilities.get(fromState) + "):" + fromVertexName;
                    }
                    String toVertexName = toState + ":FP(" + dpfa.finalProbabilities.get(toState) + ")";
                    if (dpfa.initialProbabilities.containsKey(toState) && dpfa.initialProbabilities.get(toState).doubleValue() > 0) {
                        toVertexName = "IP(" + dpfa.initialProbabilities.get(toState) + "):" + toVertexName;
                    }
                    String edgeName = symbol + ":" + dpfa.transitionProbabilities.get(fromState).get(symbol).get(toState) ;
                    Object fromVertex = getVertex(graph, parent, fromVertexName);
                    Object toVertex = getVertex(graph, parent, toVertexName);
                    graph.insertEdge(parent, null, edgeName, fromVertex, toVertex);
                }
            }
        }

        setVertexPositions(graph, parent);
        mxGraphComponent graphComponent = new mxGraphComponent(graph);
        JFrame frame = new JFrame("Graph");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(graphComponent);
        frame.setSize(800, 600);
        frame.setVisible(true);
    }
    private static Object getVertex(mxGraph graph, Object parent, String vertexName) {
        for (Object vertex : graph.getChildVertices(parent)) {
            if (graph.getModel().getValue(vertex).equals(vertexName)) {
                return vertex;
            }
        }
        return null;
    }
    public static DPFA convertFromDFFA(DFFA dffa) {
        DPFA dpfa = new DPFA(dffa.getStates(), dffa.getAlphabet());

        // Calculate FREQ for each state
        Map<String, Long> freq = new HashMap<>();
        for (String state : dffa.getStates()) {
            freq.put(state, (long)dffa.getFinalFrequencies().getOrDefault(state, (long) 0));
            for (String symbol : dffa.getAlphabet()) {
                String nextState = dffa.getTransitionFunction().get(state + symbol);
                if (nextState != null) {
                    freq.put(state, freq.get(state) + dffa.getTransitionFrequencies().get(state).get(symbol).get(nextState));
                }
            }
        }

        // Set initial probabilities
        for (String state : dffa.getStates()) {
            long numerator = dffa.getInitialFrequencies().getOrDefault(state, (long)0);
            long denominator = freq.get(state);
            if(denominator==0)
            	denominator=1;
            dpfa.setInitialProbability(state, new Fraction((int)numerator, (int)denominator));
            
        }

        // Set final probabilities
        for (String state : dffa.getStates()) {
            long numerator = dffa.getFinalFrequencies().getOrDefault(state,(long) 0);
            long denominator = freq.get(state);
            if (denominator==0)
            	denominator=1;
            dpfa.setFinalProbability(state, new Fraction((int)numerator, (int)denominator));
        }

        // Set transition probabilities
        for (String state : dffa.getStates()) {
            for (String symbol : dffa.getAlphabet()) {
                String nextState = dffa.getTransitionFunction().get(state + symbol);
                if (nextState != null) {
                    long numerator = dffa.getTransitionFrequencies().get(state).get(symbol).get(nextState);
                    long denominator = freq.get(state);
                    if(denominator==0)
                    	denominator=1;
                    dpfa.setTransitionProbability(state, symbol, nextState, new Fraction((int)numerator, (int)denominator));
                    dpfa.setTransitionFunction(state, symbol, nextState);
                }
            }
        }

        return dpfa;
    }
    private static void setVertexPositions(mxGraph graph, Object parent) {
        int numVertices = graph.getChildVertices(parent).length;
        int levels = (int) Math.log(numVertices) + 1;
        int xSpacing = 150;
        int ySpacing = 100;
        int xOffset = 50;
        int yOffset = 50;

        int level = 0;
        int index = 0;

        for (Object vertex : graph.getChildVertices(parent)) {
            int x = xOffset + index * xSpacing;
            int y = yOffset + level * ySpacing;

            graph.getModel().setGeometry(vertex, new mxGeometry(x, y, 100, 30));
            
            index++;
            if (index >= Math.pow(2, level)) {
                index = 0;
                level++;
            }
        }
    }
    public static void main(String[] args) {
    /*	Set<String> states = new HashSet<>(Arrays.asList("q1", "q2", "q3", "q4"));
    	Set<String> alphabet = new HashSet<>(Arrays.asList("a", "b"));

    	DPFA dpfa = new DPFA(states, alphabet);
    	dpfa.setInitialProbability("q1", 1.0);
    	dpfa.setInitialProbability("q2", 0.0);
    	dpfa.setInitialProbability("q3", 0.0);
    	dpfa.setInitialProbability("q4", 0.0);
    	dpfa.setFinalProbability("q1", 0.0);
    	dpfa.setFinalProbability("q2", 0.0);
    	dpfa.setFinalProbability("q3", 0.9);
    	dpfa.setFinalProbability("q4", 0.5);
    	dpfa.setTransitionProbability("q1", "a", "q2", 1);
    	
    	dpfa.setTransitionProbability("q2", "b", "q4",1);
    	dpfa.setTransitionProbability("q3", "b", "q4", 0.1);
    	dpfa.setTransitionProbability("q4", "b", "q3", 0.5);

    	if (dpfa.isDPFA()) {
    	    System.out.println("The PFA is a DPFA");
    	 //   System.out.println(dpfa.generateString()+"<--          ");
    	} else {
    	    System.out.println("The PFA is not a DPFA");
    	}
    	String x ="ab";
    	System.out.println( dpfa.computeStringProbability(x));
    	double forwardProbability = dpfa.computeStringProbability(x);
        double backwardProbability = dpfa.computeStringProbabilityWithBackward(x);
        assert Math.abs(forwardProbability - backwardProbability) < 1e-6;
        System.out.println("Probability of string '" + x + "' (FORWARD): " + forwardProbability);
        System.out.println("Probability of string '" + x + "' (BACKWARD): " + backwardProbability);*/

    	    // Print the result
    	 Set<String> states = new HashSet<>(Arrays.asList("q1", "q2", "q3", "q4"));
         Set<String> alphabet = new HashSet<>(Arrays.asList("a", "b"));

      
    	
    }
}