package process2;
import java.util.*;
import org.graphstream.graph.Graph;
import org.graphstream.graph.implementations.SingleGraph;

public class Automata {
    // DFFA implementation
    public static class DFFA {
        private Set<String> alphabet;
        private Set<String> states;
        private Map<String, Integer> initialFrequencies;
        private Map<String, Integer> finalFrequencies;
        private Map<String, Map<String, Map<String, Integer>>> transitionFrequencies;
        private Map<String, Map<String, String>> transitions;

        public DFFA(Set<String> alphabet, Set<String> states, Map<String, Integer> initialFrequencies,
                    Map<String, Integer> finalFrequencies, Map<String, Map<String, Map<String, Integer>>> transitionFrequencies,
                    Map<String, Map<String, String>> transitions) {
            this.alphabet = alphabet;
            this.states = states;
            this.initialFrequencies = initialFrequencies;
            this.finalFrequencies = finalFrequencies;
            this.transitionFrequencies = transitionFrequencies;
            this.transitions = transitions;
        }

        public boolean isConsistent() {
            for (String state : states) {
                int incoming = initialFrequencies.getOrDefault(state, 0);
                int outgoing = finalFrequencies.getOrDefault(state, 0);

                for (String prevState : states) {
                    for (String symbol : alphabet) {
                        incoming += transitionFrequencies.getOrDefault(prevState, Collections.emptyMap())
                                .getOrDefault(symbol, Collections.emptyMap()).getOrDefault(state, 0);
                    }
                }

                for (String symbol : alphabet) {
                    for (String nextState : states) {
                        outgoing += transitionFrequencies.getOrDefault(state, Collections.emptyMap())
                                .getOrDefault(symbol, Collections.emptyMap()).getOrDefault(nextState, 0);
                    }
                }

                assert incoming >= 0;
                assert outgoing >= 0;

                if (incoming != outgoing) {
                    return false;
                }
            }

            return true;
        }
    }

    // PFA implementation
    public static class PFA {
        private Set<String> alphabet;
        private Set<String> states;
        private Map<String, Double> initialProbabilities;
        private Map<String, Double> finalProbabilities;
        private Map<String, Map<String, Map<String, Double>>> transitionProbabilities;

        public PFA(Set<String> alphabet, Set<String> states, Map<String, Double> initialProbabilities,
                   Map<String, Double> finalProbabilities, Map<String, Map<String, Map<String, Double>>> transitionProbabilities) {
            this.alphabet = alphabet;
            this.states = states;
            this.initialProbabilities = initialProbabilities;
            this.finalProbabilities = finalProbabilities;
            this.transitionProbabilities = transitionProbabilities;
        }

        public boolean isValid() {
            double sumInitialProbabilities = 0.0;
            for (double prob : initialProbabilities.values()) {
                sumInitialProbabilities += prob;
                assert prob >= 0 && prob <= 1;
            }
            assert Math.abs(sumInitialProbabilities - 1.0) < 1e-6;

            for (String state : states) {
                double sumProbabilities = finalProbabilities.getOrDefault(state, 0.0);
                assert sumProbabilities >= 0 && sumProbabilities <= 1;

                for (String symbol : alphabet) {
                    for (String nextState : states) {
                        double prob = transitionProbabilities.getOrDefault(state, Collections.emptyMap())
                                .getOrDefault(symbol, Collections.emptyMap()).getOrDefault(nextState, 0.0);
                        assert prob >= 0 && prob <= 1;
                        sumProbabilities += prob;
                    }
                }

                assert Math.abs(sumProbabilities - 1.0) < 1e-6;
            }

            return true;
        }
    }

    public static void main(String[] args) {
        // DFFA example
        Set<String> alphabet = new HashSet<>(Arrays.asList("a", "b"));
        Set<String> states = new HashSet<>(Arrays.asList("q1", "q2", "q3"));
        Map<String, Integer> initialFrequencies = new HashMap<>();
        initialFrequencies.put("q1", 1);
        Map<String, Integer> finalFrequencies = new HashMap<>();
        finalFrequencies.put("q3", 1);
        Map<String, Map<String, Map<String, Integer>>> transitionFrequencies = new HashMap<>();
        Map<String, Map<String, String>> transitions = new HashMap<>();

        // Initialize transition frequencies and transitions
        transitionFrequencies.put("q1", new HashMap<>());
        transitionFrequencies.get("q1").put("a", new HashMap<>());
        transitionFrequencies.get("q1").get("a").put("q2", 1);
        transitions.put("q1", new HashMap<>());
        transitions.get("q1").put("a", "q2");

        transitionFrequencies.put("q2", new HashMap<>());
        transitionFrequencies.get("q2").put("b", new HashMap<>());
        transitionFrequencies.get("q2").get("b").put("q3", 1);
        transitions.put("q2", new HashMap<>());
        transitions.get("q2").put("b", "q3");

        DFFA dffa = new DFFA(alphabet, states, initialFrequencies, finalFrequencies, transitionFrequencies, transitions);

        assert dffa.isConsistent();

        // PFA example
        Map<String, Double> initialProbabilities = new HashMap<>();
        initialProbabilities.put("q1", 0.5);
        initialProbabilities.put("q2", 0.5);
        Map<String, Double> finalProbabilities = new HashMap<>();
        finalProbabilities.put("q3", 1.0);
        Map<String, Map<String, Map<String, Double>>> transitionProbabilities = new HashMap<>();

        // Initialize transition probabilities
        transitionProbabilities.put("q1", new HashMap<>());
        transitionProbabilities.get("q1").put("a", new HashMap<>());
        transitionProbabilities.get("q1").get("a").put("q2", 0.5);
        transitionProbabilities.get("q1").get("a").put("q3", 0.5);

        transitionProbabilities.put("q2", new HashMap<>());
        transitionProbabilities.get("q2").put("b", new HashMap<>());
        transitionProbabilities.get("q2").get("b").put("q3", 1.0);

        PFA pfa = new PFA(alphabet, states, initialProbabilities, finalProbabilities, transitionProbabilities);

        assert pfa.isValid() : "is not valid";
        Graph graph = new SingleGraph("PFA");
        graph.setStrict(false);
        graph.setAutoCreate(true);

        // Add nodes with initial and final probabilities
        graph.addNode("q1");
        graph.getNode("q1").setAttribute("ui.label", "IP: 0.4 : q1 : FP: 0.0");
        graph.getNode("q1").setAttribute("ui.style", "text-size: 16;");

        graph.addNode("q2");
        graph.getNode("q2").setAttribute("ui.label", "IP: 0.3 : q2 : FP: 0.0");
        graph.getNode("q2").setAttribute("ui.style", "text-size: 16;");

        graph.addNode("q3");
        graph.getNode("q3").setAttribute("ui.label", "IP: 0.2 : q3 : FP: 0.5");
        graph.getNode("q3").setAttribute("ui.style", "text-size: 16;");

        graph.addNode("q4");
        graph.getNode("q4").setAttribute("ui.label", "IP: 0.1 : q4 : FP: 0.5");
        graph.getNode("q4").setAttribute("ui.style", "text-size: 16;");

        // Add directed edges with transition probabilities
        graph.addEdge("q1_q2", "q1", "q2", true);
        graph.getEdge("q1_q2").setAttribute("ui.label", "a : 0.4");
        graph.getEdge("q1_q2").setAttribute("ui.style", "text-size: 16; fill-color: blue;");

        graph.addEdge("q1_q3", "q1", "q3", true);
        graph.getEdge("q1_q3").setAttribute("ui.label", "b : 0.6");
        graph.getEdge("q1_q3").setAttribute("ui.style", "text-size: 16; fill-color: blue;");

        graph.addEdge("q2_q3", "q2", "q3", true);
        graph.getEdge("q2_q3").setAttribute("ui.label", "a : 0.7");
        graph.getEdge("q2_q3").setAttribute("ui.style", "text-size: 16; fill-color: red;");

        graph.addEdge("q2_q4", "q2", "q4", true);
        graph.getEdge("q2_q4").setAttribute("ui.label", "b : 0.3");
        graph.getEdge("q2_q4").setAttribute("ui.style", "text-size: 16; fill-color: red;");

        graph.addEdge("q3_q4", "q3", "q4", true);
        graph.getEdge("q3_q4").setAttribute("ui.label", "a : 0.5");
        graph.getEdge("q3_q4").setAttribute("ui.style", "text-size: 16; fill-color: green;");

        // Display the graph
        graph.display();
    }
}