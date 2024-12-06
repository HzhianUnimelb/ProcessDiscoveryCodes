package model;

import java.util.*;

import org.graphstream.graph.*;
import org.graphstream.graph.implementations.*;
import org.apache.commons.math3.fraction.Fraction;

public class PFA {
    protected Set<String> states;
    private Set<String> alphabet;
    protected Map<String, Fraction> initialProbabilities;
    public Map<String, Fraction> finalProbabilities;
    public Map<String, Map<String, Map<String, Fraction>>> transitionProbabilities;
    protected HashMap<String, String> transitionFunction;

    public PFA(Set<String> states, Set<String> alphabet) {
        this.states = states;
        this.alphabet = alphabet;
        this.initialProbabilities = new HashMap<>();
        this.finalProbabilities = new HashMap<>();
        this.transitionProbabilities = new HashMap<>();
        this.transitionFunction = new HashMap<String, String>();
    }
    public PFA() {
    	this.initialProbabilities = new HashMap<>();
        this.finalProbabilities = new HashMap<>();
        this.transitionProbabilities = new HashMap<>();
        this.transitionFunction = new HashMap<String, String>();
        this.states= new HashSet<>();
    }
    public Map<String, String> getTransitionFunction() {
        return transitionFunction;
    }
    
    public  Map<String, Map<String, Map<String, Fraction>>> getTransitionProbabilities()
    {
    	return transitionProbabilities;
    }
    public void setTransitionFunction(String fromState, String symbol, String toState) {
        transitionFunction.put(fromState + symbol, toState);
       
    }
    
    public void setInitialProbability(String state, Fraction probability) {
        initialProbabilities.put(state, probability);
    }

    public void setFinalProbability(String state, Fraction probability) {
        finalProbabilities.put(state, probability);
    }

    public void setTransitionProbability(String fromState, String symbol, String toState, Fraction probability) {
        transitionProbabilities.computeIfAbsent(fromState, k -> new HashMap<>())
                .computeIfAbsent(symbol, k -> new HashMap<>())
                .put(toState, probability);
    }

    public boolean checkPFA() {
        // Check if the sum of initial state probabilities is 1
        Fraction sumInitialProbabilities = initialProbabilities.values().stream().reduce(new Fraction(0), (a, b) -> a.add(b));
        if (!sumInitialProbabilities.equals(new Fraction(1))) {
            return false;
        }

        // Check if the sum of transition probabilities and final state probability for each state is 1
        for (String state : states) {
            Fraction sumProbabilities = finalProbabilities.getOrDefault(state, new Fraction(0));
            for (Map<String, Fraction> symbolTransitions : transitionProbabilities.getOrDefault(state, Collections.emptyMap()).values()) {
                sumProbabilities = sumProbabilities.add(symbolTransitions.values().stream().reduce(new Fraction(0), (a, b) -> a.add(b)));
            }
            
            if (!sumProbabilities.equals(new Fraction(1))) {
                System.out.println(sumProbabilities + " " + state);
                return false;
            }
        }

        return true;
    }

    public void displayGraph() {
        Graph graph = new SingleGraph("PFA");
        graph.setStrict(false);
        graph.setAutoCreate(true);

        for (String state : states) {
            String vertexName = "IP(" + initialProbabilities.get(state) + "):" + state + ":FP(" + finalProbabilities.get(state) + ")";
            Node node = graph.addNode(vertexName);
            node.addAttribute("ui.label", vertexName);
            node.addAttribute("ui.style", "text-size: 24px;");
        }

        for (String fromState : transitionProbabilities.keySet()) {
            String fromVertexName = "IP(" + initialProbabilities.get(fromState) + "):" + fromState + ":FP(" + finalProbabilities.get(fromState) + ")";
            for (String symbol : transitionProbabilities.get(fromState).keySet()) {
                for (String toState : transitionProbabilities.get(fromState).get(symbol).keySet()) {
                    if (graph.getEdge(fromVertexName + toState) == null) {
                        String toVertexName = "IP(" + initialProbabilities.get(toState) + "):" + toState + ":FP(" + finalProbabilities.get(toState) + ")";
                        String edgeName = symbol + ":" + transitionProbabilities.get(fromState).get(symbol).get(toState) + ":" + fromState + ":" + toState;
                        Edge edge = graph.addEdge(edgeName, fromVertexName, toVertexName, true);
                        if (edge != null) {
                            edge.addAttribute("ui.label", edgeName);
                            edge.addAttribute("ui.style", "text-size: 24px;");
                            if (symbol.equals("a")) {
                                edge.addAttribute("ui.style", "fill-color: blue; text-size: 24px;");
                            } else if (symbol.equals("b")) {
                                edge.addAttribute("ui.style", "fill-color: red; text-size: 24px;");
                            }
                        }
                    }
                }
            }
        }

        graph.display();
    }

    public static void main(String[] args) {
        Set<String> states = new HashSet<>(Arrays.asList("q1", "q2", "q3", "q4"));
        Set<String> alphabet = new HashSet<>(Arrays.asList("a", "b"));

        PFA pfa = new PFA(states, alphabet);
        pfa.setInitialProbability("q1", new Fraction(1, 4));
        pfa.setInitialProbability("q2", new Fraction(1, 4));
        pfa.setInitialProbability("q3", new Fraction(1, 4));
        pfa.setInitialProbability("q4", new Fraction(1, 4));
        pfa.setFinalProbability("q1", new Fraction(1, 5));
        pfa.setFinalProbability("q2", new Fraction(1, 5));
        pfa.setFinalProbability("q3", new Fraction(3, 10));
        pfa.setFinalProbability("q4", new Fraction(3, 10));
        pfa.setTransitionProbability("q1", "a", "q2", new Fraction(2, 5));
        pfa.setTransitionProbability("q1", "a", "q3", new Fraction(2, 5));
        pfa.setTransitionProbability("q2", "b", "q4", new Fraction(4, 5));
        pfa.setTransitionProbability("q3", "b", "q4", new Fraction(7, 10));
        pfa.setTransitionProbability("q4", "a", "q1", new Fraction(7, 10));

        assert pfa.checkPFA();
        pfa.displayGraph();
    }

    public String generateString() {
        String x = "";
        String q = getRandomInitialState();
        String[] transition = getRandomTransition(q);
        String a = transition[0];
        String qPrime = transition[1];

        while (!a.equals("")) {
            x += a;
            q = qPrime;
            transition = getRandomTransition(q);
            a = transition[0];
            qPrime = transition[1];
        }

        return x;
    }

 public Fraction getFinalFraction(String state) {
    	Fraction res = finalProbabilities.get(state);
    	return res;
    }
 public boolean addState(String state)
 {
	 if(!states.contains(state))
	 {
		 this.states.add(state);
		 return true;
	 }
	 return false;
 }
 public String getNextState(String state,String symbol) {
	 return transitionFunction.get(state+symbol);
 }
    public String getRandomInitialState() {
        double random = Math.random();
        double cumulativeProbability = 0.0;
        for (String state : initialProbabilities.keySet()) {
            cumulativeProbability += initialProbabilities.get(state).doubleValue();
            if (random < cumulativeProbability) {
                return state;
            }
        }
        return null; // Should not happen
    }

    public String[] getRandomTransition(String state) {
        double random = Math.random();
        double cumulativeProbability = 0.0;
        for (String symbol : transitionProbabilities.get(state).keySet()) {
            for (String nextState : transitionProbabilities.get(state).get(symbol).keySet()) {
                cumulativeProbability += transitionProbabilities.get(state).get(symbol).get(nextState).doubleValue();
                if (random < cumulativeProbability) {
                    return new String[]{symbol, nextState};
                }
            }
        }
        return new String[]{"", ""}; // No transition found, return a dummy transition
    }

    public double[][] forward(String x) {
        int n = x.length();
        int numStates = states.size();
        double[][] F = new double[n + 1][numStates];

        // Initialize F[0][j] with the initial probabilities
        int j = 0;
        for (String state : states) {
            F[0][j++] = initialProbabilities.get(state).doubleValue();
        }

        // Initialize F[i][j] to 0 for all i, j
        for (int i = 1; i <= n; i++) {
            for (j = 0; j < numStates; j++) {
                F[i][j] = 0;
            }
        }

        // Compute F[i][j] for all i, j
        for (int i = 1; i <= n; i++) {
            String symbol = x.substring(i - 1, i);
            for (j = 0; j < numStates; j++) {
                String state = (String) states.toArray()[j];
                for (int k = 0; k < numStates; k++) {
                    String prevState = (String) states.toArray()[k];
                    if (transitionProbabilities.containsKey(prevState) && transitionProbabilities.get(prevState).containsKey(symbol) && transitionProbabilities.get(prevState).get(symbol).containsKey(state)) {
                        F[i][j] += F[i - 1][k] * transitionProbabilities.get(prevState).get(symbol).get(state).doubleValue();
                    }
                }
            }
        }

        return F;
    }

    public double computeStringProbability(String x) {
        double[][] F = forward(x);
        int n = x.length();
        double T = 0;

        int j = 0;
        for (String state : states) {
            T += F[n][j++] * finalProbabilities.get(state).doubleValue();
        }

        return T;
    }

    public Set<String> getAlphabet(){
    	return alphabet;
    }
    public double[][] backward(String x) {
        int n = x.length();
        int numStates = states.size();
        double[][] B = new double[n + 1][numStates];

        // Initialize B[n][j] with the final probabilities
        int j = 0;
        for (String state : states) {
            B[n][j++] = finalProbabilities.get(state).doubleValue();
        }

        // Initialize B[i][j] to 0 for all i, j
        for (int i = 0; i < n; i++) {
            for (j = 0; j < numStates; j++) {
                B[i][j] = 0;
            }
        }

        // Compute B[i][j] for all i, j
        for (int i = n - 1; i >= 0; i--) {
            String symbol = x.substring(i, i + 1);
            for (j = 0; j < numStates; j++) {
                String state = (String) states.toArray()[j];
                for (int k = 0; k < numStates; k++) {
                    String nextState = (String) states.toArray()[k];
                    if (transitionProbabilities.containsKey(state) && transitionProbabilities.get(state).containsKey(symbol) && transitionProbabilities.get(state).get(symbol).containsKey(nextState)) {
                        B[i][j] += B[i + 1][k] * transitionProbabilities.get(state).get(symbol).get(nextState).doubleValue();
                    }
                }
            }
        }

        return B;
    }

    public double computeStringProbabilityWithBackward(String x) {
        double[][] B = backward(x);
        double T = 0;
        int j = 0;
        for (String state : states) {
            T += B[0][j++] * initialProbabilities.get(state).doubleValue();
        }
        return T;
    }
}