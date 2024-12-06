package model;

import java.util.*;
import java.util.stream.Collectors;

import javax.swing.JFrame;

import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.view.mxGraph;

public class DFFA {
    public Set<String> states;
    public Map<String,String> aliasName;
    public Set<String> alphabet;
    public Map<String, Long> initialFrequencies;
    public Map<String, Long> finalFrequencies;
    public Map<String, Map<String, Map<String, Long>>> transitionFrequencies;
    public    Map<String, String> transitionFunction;
    public    Map<String, String> mergeState;   

    public void setInitialFrequencies(Map<String, Long> initialFrequencies) {
    	this.initialFrequencies = new HashMap<>();
    	this.initialFrequencies.putAll(initialFrequencies);
    }
    
    public void setFinalFrequencies(Map<String, Long> finalFrequencies) {
    	this.finalFrequencies = new HashMap<>();
    	this.finalFrequencies.putAll(finalFrequencies);
    }
    public void setTransitionFrequencies(Map<String, Map<String, Map<String, Long>>> transitionFrequencies)
    {
    	this.transitionFrequencies = new HashMap<>();
    	this.transitionFrequencies.putAll(transitionFrequencies);
    }
    public void setTransitionFunction(Map<String, String> transitionFunction)
    {
    	this.transitionFunction = new HashMap<>();
    	this.transitionFunction.putAll(transitionFunction);
    }
    public void setAliasName(Map<String, String> aliasName) {
    	this.aliasName = new HashMap<String, String>();
    	this.aliasName.putAll(aliasName);
    }
    public void setStates(Set<String> states)
    {
    	this.states = new HashSet<>();
    	this.states.addAll(states);
    }
    public void setAlphabet(Set<String> alphabet)
    {
    	this.alphabet = new HashSet<>();
    	this.alphabet.addAll(alphabet);
    }
    public void copy(DFFA dffa)
    {
    	dffa.setAlphabet(alphabet);
    	dffa.setStates(states);
    	dffa.setFinalFrequencies(finalFrequencies);
    	dffa.setInitialFrequencies(initialFrequencies);
    	dffa.setTransitionFrequencies(transitionFrequencies);
    	dffa.setTransitionFunction(transitionFunction); 	
    }
    public DFFA(Set<String> states, Set<String> alphabet) {
        this.states = states;
        this.alphabet = alphabet;
        this.initialFrequencies = new HashMap<>();
        this.finalFrequencies = new HashMap<>();
        this.transitionFrequencies = new HashMap<>();
        this.transitionFunction = new HashMap<>();
        this.mergeState = new HashMap<>();
    }
    public DFFA() {
    	this.initialFrequencies = new HashMap<>();
        this.finalFrequencies = new HashMap<>();
        this.transitionFrequencies = new HashMap<>();
        this.transitionFunction = new HashMap<>();
        this.mergeState = new HashMap<>();
    }
   

	public void setInitialFrequency(String state, Long frequency) {
        initialFrequencies.put(state, frequency);
    }

    public void setFinalFrequency(String state, Long frequency) {
        finalFrequencies.put(state, frequency);
    }

    public void setTransitionFrequency(String fromState, String symbol, String toState, Long frequency) {
        transitionFrequencies.computeIfAbsent(fromState, k -> new HashMap<>())
                .computeIfAbsent(symbol, k -> new HashMap<>())
                .put(toState, frequency);
    }

    public void setTransitionFunction(String fromState, String symbol, String toState) {
    	
        transitionFunction.put(fromState + symbol, toState);
       
    }
    public Long getTransitionFrequency(String fromState, String symbol, String toState) {
        return transitionFrequencies.computeIfAbsent(fromState, k -> new HashMap<>())
                .computeIfAbsent(symbol, k -> new HashMap<>())
                .getOrDefault(toState, (long) 0);
    }

    public Long getFinalFrequency(String state) {
        return finalFrequencies.getOrDefault(state, (long) 0);
    }

    // getters
    public Set<String> getStates() {
        return states;
    }

    public Set<String> getAlphabet() {
        return alphabet;
    }

    public Map<String, Long> getInitialFrequencies() {
        return initialFrequencies;
    }

    public Map<String, Long> getFinalFrequencies() {
        return finalFrequencies;
    }

    public Map<String, Map<String, Map<String, Long>>> getTransitionFrequencies() {
        return transitionFrequencies;
    }

    public Map<String, String> getTransitionFunction() {
        return transitionFunction;
    }
    public void show(DFFA dffa,String name) {
        mxGraph graph = new mxGraph();
        Object parent = graph.getDefaultParent();

        ;
        List<String> sortedStates = dffa.getStates().stream()
                .sorted(Comparator.comparingInt(String::length))
                .collect(Collectors.toList());
 /*       for(String s:sortedStates)
        {
        	if(s.charAt(0)=='λ')
        	{
        		sortedStates.remove(s);
        		sortedStates.add(0, s);
        		break;
        	}
        }*/
        
        int x = 500;
        int y = 100;
        int level = 0;
        int vertexWidth = 80;
        int vertexHeight = 20;
        int horizontalGap = 20;
        int verticalGap = 20;

        Map<String, Object> vertices = new HashMap<>();

        for (String state : sortedStates) {
            String vertexName = (state.isEmpty() ? "λ" : state) + ":" + dffa.getFinalFrequencies().getOrDefault(state, (long)0) + "";
            if (dffa.getInitialFrequencies().containsKey(state)) {
                vertexName = "IP(" + dffa.getInitialFrequencies().get(state) + "):" + vertexName;
            }
           
            if (state.length() == 0) {
                Object vertex = graph.insertVertex(parent, null, vertexName, x, y, vertexWidth, vertexHeight);
                vertices.put(state, vertex);
            } else  {
                String prefix = state.substring(0, state.length() - 1);
                Object prefixVertex = vertices.get(prefix);
                if(prefixVertex==null)
                	prefixVertex = vertices.get("");
                	
                	int prefixVertexWidth = (int) graph.getCellGeometry(prefixVertex).getWidth();
                	int prefixVertexHeight = (int) graph.getCellGeometry(prefixVertex).getHeight();
                	int currentVertexWidth = Math.max(vertexWidth, vertexName.length() * 10);
                	int currentVertexHeight = vertexHeight;             
                	Object vertex = graph.insertVertex(parent, null, vertexName, (int) graph.getCellGeometry(prefixVertex).getX() + (prefixVertexWidth + horizontalGap), (int) graph.getCellGeometry(prefixVertex).getY() + (prefixVertexHeight + verticalGap), currentVertexWidth, currentVertexHeight);
                    vertices.put(state, vertex);      
                
            }
        }

        for (String fromState : dffa.getStates()) {

            for (String symbol : dffa.getAlphabet()) {          	
                String nextState = dffa.getTransitionFunction().get(fromState + symbol);
                if (nextState != null) {
                    String fromVertexName = (fromState.isEmpty() ? "λ" : fromState) + ":" + dffa.getFinalFrequencies().getOrDefault(fromState, (long)0) + "";
                    if (dffa.getInitialFrequencies().containsKey(fromState)) {
                        fromVertexName = "IP(" + dffa.getInitialFrequencies().get(fromState) + "):" + fromVertexName;
                    }
                    String toVertexName = (nextState.isEmpty() ? "λ" : nextState) + ":" + dffa.getFinalFrequencies().getOrDefault(nextState, (long)0) + "";
                    if (dffa.getInitialFrequencies().containsKey(nextState)) {
                        toVertexName = "IP(" + dffa.getInitialFrequencies().get(nextState) + "):" + toVertexName;
                    }
                    String edgeName = symbol + ":" + dffa.getTransitionFrequencies().get(fromState).get(symbol).get(nextState);
                    Object fromVertex = getVertex(graph, parent, fromVertexName);
                    Object toVertex = getVertex(graph, parent, toVertexName);
                    graph.insertEdge(parent, null, edgeName, fromVertex, toVertex);
                }
            }
        }

        mxGraphComponent graphComponent = new mxGraphComponent(graph);
       
        JFrame frame = new JFrame(name);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(graphComponent);
        frame.setSize(800, 600);
        frame.setVisible(true);
    }
    private static Object getVertex(mxGraph graph, Object parent, String vertexName) {
        for (Object vertex : graph.getChildVertices(parent)) {
            if (vertexName.equals(graph.getLabel(vertex))) {
                return vertex;
            }
        }
        return null;
    }
    public boolean isConsistent() {
        for (String state : states) {
            long leftSide = initialFrequencies.getOrDefault(state, (long)0);
            for (String nextState : states) {
                for (String symbol : alphabet) {
                    if (transitionFrequencies.containsKey(nextState) && transitionFrequencies.get(nextState).containsKey(symbol) && transitionFrequencies.get(nextState).get(symbol).containsKey(state)) {
                        leftSide += transitionFrequencies.get(nextState).get(symbol).get(state);
                    }
                }
            }

            long rightSide = finalFrequencies.getOrDefault(state, (long)0);
            for (String symbol : alphabet) {
                for (String nextState : states) {
                    if (transitionFrequencies.containsKey(state) && transitionFrequencies.get(state).containsKey(symbol) && transitionFrequencies.get(state).get(symbol).containsKey(nextState)) {
                        rightSide += transitionFrequencies.get(state).get(symbol).get(nextState);
                    }
                }
            }

            if (leftSide != rightSide) {
            	System.out.println(leftSide+" "+rightSide+" "+state);
                return false;
            }
        }

        return true;
    }
    
    public String getMerge(String state)
    {
    	return mergeState.get(state);
    }
    public long getFrequency(String state) {
    	 long rightSide = finalFrequencies.getOrDefault(state, (long)0);
         for (String symbol : alphabet) {
             for (String nextState : states) {
                 if (transitionFrequencies.containsKey(state) && transitionFrequencies.get(state).containsKey(symbol) && transitionFrequencies.get(state).get(symbol).containsKey(nextState)) {
                     rightSide += transitionFrequencies.get(state).get(symbol).get(nextState);
                 }
             }
         }
         long leftSide = initialFrequencies.getOrDefault(state, (long)0);
         for (String nextState : states) {
             for (String symbol : alphabet) {
                 if (transitionFrequencies.containsKey(nextState) && transitionFrequencies.get(nextState).containsKey(symbol) && transitionFrequencies.get(nextState).get(symbol).containsKey(state)) {
                     leftSide += transitionFrequencies.get(nextState).get(symbol).get(state);
                 }
             }
         }
         return rightSide;
    }

    public long getTransitionFrequency(String state, String symbol) {
        if (getTransitionFrequencies().containsKey(state) && getTransitionFrequencies().get(state).containsKey(symbol)) {
            Map<String, Long> transitions = getTransitionFrequencies().get(state).get(symbol);
            for (String nextState : transitions.keySet()) {
                return transitions.get(nextState);
            }
        }
        return 0;
    }
    public static void main(String[] args) {
      
    }
    
}