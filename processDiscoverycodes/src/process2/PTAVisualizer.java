package process2;

import com.mxgraph.model.mxGeometry;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.view.mxGraph;
import org.jgrapht.Graph;
import org.jgrapht.ext.JGraphXAdapter;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultEdge;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class PTAVisualizer {
    public static void main(String[] args) {
        Set<String> alphabet = new HashSet<>(Arrays.asList("a", "b"));
        Set<String> SPlus = new HashSet<>(Arrays.asList("aaa", "aaba", "bba", "bbaba"));
        Set<String> SMinus = new HashSet<>(Arrays.asList("a", "bb", "aab", "aba"));
        PTA pta = new PTA(alphabet, SPlus, SMinus);
        RPNI rpni = new RPNI(pta);
        rpni.run();

        // Create a directed graph
        Graph<String, DefaultEdge> graph = new DefaultDirectedGraph<>(DefaultEdge.class);

        // Add vertices
        for (String state : rpni.getPTA().getStates()) {
            graph.addVertex(state);
        }

        // Add edges
        for (Map.Entry<String, Map<String, String>> entry : rpni.getPTA().getTransitionFunction().entrySet()) {
            for (Map.Entry<String, String> innerEntry : entry.getValue().entrySet()) {
                graph.addEdge(entry.getKey(), innerEntry.getValue());
            }
        }

        // Create a visualization using JGraphX
        JGraphXAdapter<String, DefaultEdge> jgxAdapter = new JGraphXAdapter<>(graph);
        mxGraphComponent component = new mxGraphComponent(jgxAdapter);
        component.setPreferredSize(new Dimension(500, 500));

        // Set the position of the vertices
        jgxAdapter.getModel().beginUpdate();
        Object parent = jgxAdapter.getDefaultParent();
        int x = 20;
        int y = 20;
        int level = 0;
        for (String state : rpni.getPTA().getStates()) {
            Object v = jgxAdapter.getVertexToCellMap().get(state);
            jgxAdapter.getModel().setGeometry(v, new mxGeometry(x, y, 50, 50));
            x += 100;
            if (x > 500) {
                x = 20;
                y += 100;
                level++;
            }
        }
        jgxAdapter.getModel().endUpdate();

        // Make edges curvy
        Map<String, Object> edgeStyle = new HashMap<>();
        edgeStyle.put("edgeStyle", "orthogonalEdgeStyle");
        edgeStyle.put("curved", "1");
        jgxAdapter.getStylesheet().putCellStyle("curvy", edgeStyle);
        for (DefaultEdge edge : graph.edgeSet()) {
            jgxAdapter.getModel().setStyle(jgxAdapter.getEdgeToCellMap().get(edge), "curvy");
        }

        // Display the graph
        JFrame frame = new JFrame("PTA Graph");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().add(component);
        frame.pack();
        frame.setVisible(true);
    }
}