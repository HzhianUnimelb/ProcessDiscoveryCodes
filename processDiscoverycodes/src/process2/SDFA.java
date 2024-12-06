package process2;

import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.view.mxGraph;

import javax.swing.*;
import java.util.*;

public class SDFA {
    private Set<String> states;
    private Set<String> actions;
    private Map<String, Map<String, String>> transitionFunction;
    private Map<String, Map<String, Double>> transitionProbabilityFunction;
    private String initialState;

    public SDFA(Set<String> states, Set<String> actions, Map<String, Map<String, String>> transitionFunction, Map<String, Map<String, Double>> transitionProbabilityFunction, String initialState) {
        this.states = states;
        this.actions = actions;
        this.transitionFunction = transitionFunction;
        this.transitionProbabilityFunction = transitionProbabilityFunction;
        this.initialState = initialState;
    }
    private double calculateLanguageProbability(String state, List<String> trace) {
        if (trace.isEmpty()) {
            return 1.0;
        } else {
            String action = trace.get(0);
            List<String> remainingTrace = trace.subList(1, trace.size());
            String nextState = transitionFunction.get(state).get(action);
            double probability = transitionProbabilityFunction.get(state).get(action);
            System.out.println(probability);
            return probability * calculateLanguageProbability(nextState, remainingTrace);
        }
    }
    public boolean isSDFA() {
        for (String state : states) {
            double probabilitySum = 0.0;
            for (String action : actions) {
                probabilitySum += transitionProbabilityFunction.get(state).get(action);
            }
            if (probabilitySum > 1.0) {
                return false;
            }
        }
        return true;
    }
    public void showSDFA() {
        mxGraph graph = new mxGraph();
        Object parent = graph.getDefaultParent();

        Map<String, Object> stateVertices = new HashMap<>();
        for (String state : states) {
            stateVertices.put(state, graph.insertVertex(parent, null, state, 20, 20, 80, 30));
        }

        for (String state : states) {
            for (String action : actions) {
                String targetState = transitionFunction.get(state).get(action);
                graph.insertEdge(parent, null, action + ": " + transitionProbabilityFunction.get(state).get(action), stateVertices.get(state), stateVertices.get(targetState));
            }
        }

        mxGraphComponent graphComponent = new mxGraphComponent(graph);
        JFrame frame = new JFrame("SDFA");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(graphComponent);
        frame.pack();
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        Set<String> states = new HashSet<>(Arrays.asList("s0", "s1", "s2"));
        Set<String> actions = new HashSet<>(Arrays.asList("a", "b"));

        Map<String, Map<String, String>> transitionFunction = new HashMap<>();
        transitionFunction.put("s0", new HashMap<String, String>() {{
            put("a", "s1");
            put("b", "s2");
        }});
        transitionFunction.put("s1", new HashMap<String, String>() {{
            put("a", "s2");
            put("b", "s1");
        }});
        transitionFunction.put("s2", new HashMap<String, String>() {{
            put("a", "s1");
            put("b", "s2");
        }});

        Map<String, Map<String, Double>> transitionProbabilityFunction = new HashMap<>();
        transitionProbabilityFunction.put("s0", new HashMap<String, Double>() {{
            put("a", 0.6);
            put("b", 0.4);
        }});
        transitionProbabilityFunction.put("s1", new HashMap<String, Double>() {{
            put("a", 0.1);
            put("b", 0.7);
        }});
        transitionProbabilityFunction.put("s2", new HashMap<String, Double>() {{
            put("a", 0.5);
            put("b", 0.5);
        }});

        SDFA sdka = new SDFA(states, actions, transitionFunction, transitionProbabilityFunction, "s0");

        List<String> trace = new ArrayList<>(Arrays.asList("a", "b", "a"));
        double probability = sdka.calculateLanguageProbability("s0",trace);
        System.out.println("Probability of trace " + trace + ": " + probability);
        assert sdka.isSDFA();

        sdka.showSDFA();
    }
}