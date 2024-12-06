package process2;

import gurobi.*;

public class MaximumCliqueSolverWithGurobi {
    public static void main(String[] args) {
        try {
            GRBEnv env = new GRBEnv(); // Create a Gurobi environment

            // Create an empty model
            GRBModel model = new GRBModel(env);

            int[][] adjacencyMatrix = {
                    {0, 1, 1, 1, 0},
                    {1, 0, 1, 1, 0},
                    {1, 1, 0, 1, 0},
                    {1, 1, 1, 0, 0},
                    {0, 0, 0, 0, 0}
            };

            int numVertices = adjacencyMatrix.length;

            // Create binary decision variables for each vertex
            GRBVar[] vertices = new GRBVar[numVertices];
            for (int i = 0; i < numVertices; i++) {
                vertices[i] = model.addVar(0.0, 1.0, 0.0, GRB.BINARY, "x_" + i);
            }

            // Update model to integrate new variables
            model.update();

            // Add constraints: Non-adjacent vertices cannot both be in the clique
            for (int i = 0; i < numVertices; i++) {
                for (int j = i + 1; j < numVertices; j++) {
                    if (adjacencyMatrix[i][j] == 0) {
                        GRBLinExpr expr = new GRBLinExpr();
                        expr.addTerm(1.0, vertices[i]);
                        expr.addTerm(1.0, vertices[j]);
                        model.addConstr(expr, GRB.LESS_EQUAL, 1.0, "nonAdjacency_" + i + "_" + j);
                    }
                }
            }

            // Set objective: Maximize the size of the clique
            GRBLinExpr objExpr = new GRBLinExpr();
            for (int i = 0; i < numVertices; i++) {
                objExpr.addTerm(1.0, vertices[i]);
            }
            model.setObjective(objExpr, GRB.MAXIMIZE);

            // Optimize the model
            model.optimize();

            // Output the binary solution for the maximum clique
            System.out.println("Binary Solution for Maximum Clique (Maximizing Objective):");
            for (int i = 0; i < numVertices; i++) {
                System.out.println("x_" + i + " = " + Math.round(vertices[i].get(GRB.DoubleAttr.X)));
            }

            // Dispose of model and environment
            model.dispose();
            env.dispose();

        } catch (GRBException e) {
            System.out.println("Error code: " + e.getErrorCode() + ". " + e.getMessage());
        }
    }
}