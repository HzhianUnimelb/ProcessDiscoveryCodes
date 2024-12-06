package process2;

import java.awt.Color;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.JFrame;

import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultEdge;

import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.view.mxGraph;

public class SDAG {
    private Graph<String, DefaultEdge> graph;
    private Map<String, String> nodeActions;
    private Map<DefaultEdge, Double> edgeProbabilities;
    private Map<String, Integer> nodeFrequencies;
    private int nodeCount;
    private Set<String> traces;

    public SDAG() {
        graph = new DefaultDirectedGraph<>(DefaultEdge.class);
        nodeActions = new HashMap<>();
        edgeProbabilities = new HashMap<>();
        nodeFrequencies = new HashMap<>();
        nodeCount = 0;
        traces = new HashSet<>();
    }

    public void addTrace(String trace) {
        String[] parts = trace.split(" \\^ ");
        String[] actions = parts[0].split(",");
        traces.add(trace);
        int frequency = Integer.parseInt(parts[1]);

        String currentNode = "i";
        graph.addVertex(currentNode);
        nodeFrequencies.put(currentNode, nodeFrequencies.getOrDefault(currentNode, 0) + frequency);

        for (int i = 0; i < actions.length; i++) {
            String action = actions[i];
            String nextNode = null;

            // Check if a node with the same action already exists in the graph
            for (String node : graph.vertexSet()) {
                if (nodeActions.containsKey(node) && nodeActions.get(node).equals(action)) {
                	//if(node.compareTo(currentNode)==0)
                	//{
                		nextNode = node;
                		break;
                	//}
                }
            }

            // If no node with the same action is found, check for substrings
            if (nextNode == null && i < actions.length - 1) {
                for (int j = i + 1; j < actions.length; j++) {
                	
                    String subTrace = String.join(",", Arrays.copyOfRange(actions, i, j + 1));
                    for (String node : graph.vertexSet()) {
                        if (nodeActions.containsKey(node) && nodeActions.get(node).equals(subTrace)) {
                            if (j - i > 0) { // Check if the length of the matched substring is more than 1
                                nextNode = node;
                                i = j;
                                break;
                            }
                        }
                    }
                    if (nextNode != null) {
                        break;
                    }
                }
            }

            // If no node with the same action or substring is found, create a new node
            if (nextNode == null) {
                nextNode = action + "  n" + nodeCount++;
                graph.addVertex(nextNode);
                nodeActions.put(nextNode, action);
            }

            DefaultEdge edge;
            if (graph.containsEdge(currentNode, nextNode)) {
                edge = graph.getEdge(currentNode, nextNode);
                edgeProbabilities.put(edge, edgeProbabilities.getOrDefault(edge, 0.0) + frequency);
            } else {
                edge = graph.addEdge(currentNode, nextNode);
                edgeProbabilities.put(edge, (double) frequency);
            }
            nodeFrequencies.put(nextNode, nodeFrequencies.getOrDefault(nextNode, 0) + frequency);
            currentNode = nextNode;
        }

        if (!graph.containsVertex("o")) {
            graph.addVertex("o");
        }
        DefaultEdge edge;
        if (graph.containsEdge(currentNode, "o")) {
            edge = graph.getEdge(currentNode, "o");
            edgeProbabilities.put(edge, edgeProbabilities.getOrDefault(edge, 0.0) + frequency);
        } else {
            edge = graph.addEdge(currentNode, "o");
            edgeProbabilities.put(edge, (double) frequency);
        }
        nodeFrequencies.put("o", nodeFrequencies.getOrDefault("o", 0) + frequency);
    }
    private String getNextNode(String currentNode, String action) {
        for (DefaultEdge edge : graph.edgesOf(currentNode)) {
            if (graph.getEdgeTarget(edge).equals(currentNode)) {
                String target = graph.getEdgeTarget(edge);
                if (nodeActions.containsKey(target) && nodeActions.get(target).equals(action)) {
                    return target;
                }
            }
        }
        // If no edge with the given action is found, create a new node
        String newNode = action + "  n" + nodeCount++;
        graph.addVertex(newNode);
        nodeActions.put(newNode, action);
        return newNode;
    }

    public void calculateProbabilities() {
    	DecimalFormat df = new DecimalFormat("#.#");
        for (String node : graph.vertexSet()) {
            int totalFrequency = 0;
            for (DefaultEdge edge : graph.edgesOf(node)) {
                if (graph.getEdgeSource(edge).equals(node)) {
                	System.out.println(node + " "+edgeProbabilities.get(edge));
                    totalFrequency += edgeProbabilities.get(edge);
                }
            }
            for (DefaultEdge edge : graph.edgesOf(node)) {
                if (graph.getEdgeSource(edge).equals(node)) {
                	double temp =Double.parseDouble(df.format(edgeProbabilities.get(edge)/ totalFrequency));
                   edgeProbabilities.put(edge, temp);
                }
            }
        }
    }

    public void visualize() {
      calculateProbabilities();

        mxGraph mxgraph = new mxGraph();
        Object parent = mxgraph.getDefaultParent();

        mxgraph.getModel().beginUpdate();
        try {
            Object iVertex = mxgraph.insertVertex(parent, null, "i (" + nodeFrequencies.get("i") + ")", 100, 100, 80, 30);
            Object oVertex = mxgraph.insertVertex(parent, null, "o (" + nodeFrequencies.get("o") + ")", 100, 100, 80, 30);

            Map<String, Object> vertexMap = new HashMap<>();
            vertexMap.put("i", iVertex);
            vertexMap.put("o", oVertex);

            for (String node : graph.vertexSet()) {
                if (!node.equals("i") && !node.equals("o")) {
                    Object vertex = mxgraph.insertVertex(parent, null, nodeActions.get(node), 100, 100, 80, 30);
                    vertexMap.put(node, vertex);
                }
            }

            for (DefaultEdge edge : graph.edgeSet()) {
                String source = graph.getEdgeSource(edge);
                String target = graph.getEdgeTarget(edge);
                mxgraph.insertEdge(parent, null, edgeProbabilities.get(edge), vertexMap.get(source), vertexMap.get(target));
            }
        } finally {
            mxgraph.getModel().endUpdate();
        }

        JFrame frame = new JFrame();
        frame.setSize(800, 600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mxGraphComponent graphComponent = new mxGraphComponent(mxgraph);
        graphComponent.getViewport().setOpaque(true);
        graphComponent.getViewport().setBackground(Color.WHITE);
        frame.getContentPane().add(graphComponent);
        frame.setVisible(true);
    }
    
    public void mergeNodes() {
        Map<String, List<String>> actionNodes = new HashMap<>();
        for (String node : graph.vertexSet()) {
            if (!node.equals("i") && !node.equals("o")) {
                String action = nodeActions.get(node);
                if (!actionNodes.containsKey(action)) {
                    actionNodes.put(action, new ArrayList<>());
                }
                actionNodes.get(action).add(node);
            }
        }

        for (List<String> nodes : actionNodes.values()) {
            if (nodes.size() > 1) {
                String newNode = nodes.get(0);
                for (int i = 1; i < nodes.size(); i++) {
                    String nodeToRemove = nodes.get(i);
                    for (DefaultEdge edge : graph.edgesOf(nodeToRemove)) {
                        String source = graph.getEdgeSource(edge);
                        String target = graph.getEdgeTarget(edge);
                        if (source.equals(nodeToRemove)) {
                            source = newNode;
                        }
                        if (target.equals(nodeToRemove)) {
                            target = newNode;
                        }
                        DefaultEdge newEdge = graph.addEdge(source, target);
                        if (newEdge != null) {
                            edgeProbabilities.put(newEdge, edgeProbabilities.get(edge));
                        } else {
                            newEdge = graph.getEdge(source, target);
                            edgeProbabilities.put(newEdge, edgeProbabilities.get(newEdge) + edgeProbabilities.get(edge));
                        }
                    }
                    graph.removeVertex(nodeToRemove);
                }
            }
        }

        // Normalize probabilities
        for (String node : graph.vertexSet()) {
            int totalFrequency = 0;
            for (DefaultEdge edge : graph.edgesOf(node)) {
                if (graph.getEdgeSource(edge).equals(node)) {
                    totalFrequency += edgeProbabilities.get(edge);
                }
            }
         /*   for (DefaultEdge edge : graph.edgesOf(node)) {
                if (graph.getEdgeSource(edge).equals(node)) {
                    edgeProbabilities.put(edge, (double) edgeProbabilities.get(edge) / totalFrequency);
                }
            }*/
        }
    }
    public void updateFrequencies() {
        // Clear all frequencies
        nodeFrequencies.clear();
        edgeProbabilities.clear();

        // Iterate through each trace
        for (String trace : traces) {
            String[] parts = trace.split(" \\^ ");
            String[] actions = parts[0].split(",");
            int frequency = Integer.parseInt(parts[1]);

            // Walk through the SDAG
            String currentNode = "i";
            nodeFrequencies.put(currentNode, nodeFrequencies.getOrDefault(currentNode, 0) + frequency);
            for (String action : actions) {
                String nextNode = getNextNode(currentNode, action);
                DefaultEdge edge = graph.getEdge(currentNode, nextNode);
                edgeProbabilities.put(edge, edgeProbabilities.getOrDefault(edge, 0.0) + frequency);
                nodeFrequencies.put(nextNode, nodeFrequencies.getOrDefault(nextNode, 0) + frequency);
                currentNode = nextNode;
            }
            DefaultEdge edge = graph.getEdge(currentNode, "o");
            edgeProbabilities.put(edge, edgeProbabilities.getOrDefault(edge, 0.0) + frequency);
            nodeFrequencies.put("o", nodeFrequencies.getOrDefault("o", 0) + frequency);
        }

        // Normalize probabilities
        for (String node : graph.vertexSet()) {
            int totalFrequency = 0;
            for (DefaultEdge edge : graph.edgesOf(node)) {
                if (graph.getEdgeSource(edge).equals(node)) {
                    totalFrequency += edgeProbabilities.get(edge);
                }
            }
            for (DefaultEdge edge : graph.edgesOf(node)) {
                if (graph.getEdgeSource(edge).equals(node)) {
                    edgeProbabilities.put(edge, (double) edgeProbabilities.get(edge) );
                }
            }
        }

        // Update node labels
        for (String node : graph.vertexSet()) {
            if (!node.equals("i") && !node.equals("o")) {
                String action = nodeActions.get(node);
                int frequency = nodeFrequencies.get(node);
                nodeActions.put(node, action + " (" + frequency + ")");
            }
        }
    }
    public static void main(String[] args) {
        SDAG sdag = new SDAG();
        
       sdag.addTrace("a,c,e,c ^ 1057");
        sdag.addTrace("a,b,c,e ^ 272");
       sdag.addTrace("a,b,b,c ^ 164");
      //  sdag.addTrace("b,b ^ 164");
       // sdag.mergeNodes();
      //  sdag.updateFrequencies();
        sdag.visualize();
    }
}