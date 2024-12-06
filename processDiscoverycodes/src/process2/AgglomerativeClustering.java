package process2;

import java.util.ArrayList;
import java.util.List;

public class AgglomerativeClustering {
    private static final int MAX_CLUSTERS = 10; // Maximum number of clusters to evaluate
    private static final double THRESHOLD = 0.001; // Threshold for significant change in silhouette score

    public static void main(String[] args) {
        // Sample data points (2D for simplicity)
        double[][] dataPoints = {
            {1.0, 2.0},
            {1.5, 1.8},
            {5.0, 8.0},
            {8.0, 8.0},
            {1.0, 0.6},
            {9.0, 11.0},
            {20.0, 30.0},
            {21.0, 30.0},
            {20.0, 300.0},
            {100.0,1100,0}
        };

        // Perform agglomerative clustering and find the optimal number of clusters
        int optimalClusters = findOptimalClusters(dataPoints);
        System.out.println("Optimal number of clusters: " + optimalClusters);
    }

    private static int findOptimalClusters(double[][] data) {
        double bestSilhouette = -1;
        int bestClusterCount = 2; // Start from 2 clusters
        double previousSilhouette = -1;

        for (int k = 2; k <= MAX_CLUSTERS; k++) {
            List<List<double[]>> clusters = agglomerativeClustering(data, k);
            double silhouetteScore = computeSilhouetteScore(clusters, data);
            System.out.println("Silhouette score for " + k + " clusters: " + silhouetteScore);

            // Check if the change in silhouette score is significant
            if (previousSilhouette != -1 && (previousSilhouette - silhouetteScore) > THRESHOLD) {
                System.out.println("Significant decrease in silhouette score detected. Stopping at " + (k - 1) + " clusters.");
                break;
            }

            if (silhouetteScore > bestSilhouette) {
                bestSilhouette = silhouetteScore;
                bestClusterCount = k;
            }
            previousSilhouette = silhouetteScore; // Update the previous score
        }

        return bestClusterCount;
    }

    private static List<List<double[]>> agglomerativeClustering(double[][] data, int k) {
        // Initialize clusters
        List<List<double[]>> clusters = new ArrayList<>();
        for (double[] point : data) {
            List<double[]> cluster = new ArrayList<>();
            cluster.add(point);
            clusters.add(cluster);
        }

        // Merge clusters until we reach the desired number of clusters
        while (clusters.size() > k) {
            double minDistance = Double.MAX_VALUE;
            int cluster1Index = -1;
            int cluster2Index = -1;

            // Find the two closest clusters
            for (int i = 0; i < clusters.size(); i++) {
                for (int j = i + 1; j < clusters.size(); j++) {
                    double distance = computeClusterDistance(clusters.get(i), clusters.get(j));
                    if (distance < minDistance) {
                        minDistance = distance;
                        cluster1Index = i;
                        cluster2Index = j;
                    }
                }
            }

            // Merge the closest clusters
            if (cluster1Index != -1 && cluster2Index != -1) {
                clusters.get(cluster1Index).addAll(clusters.get(cluster2Index));
                clusters.remove(cluster2Index);
            }
        }

        return clusters;
    }

    private static double computeClusterDistance(List<double[]> cluster1, List<double[]> cluster2) {
        double minDistance = Double.MAX_VALUE;
        for (double[] point1 : cluster1) {
            for (double[] point2 : cluster2) {
                double distance = euclideanDistance(point1, point2);
                if (distance < minDistance) {
                    minDistance = distance; // Using minimum distance (single-linkage)
                }
            }
        }
        return minDistance; // Return minimum distance between the two clusters
    }

    private static double computeSilhouetteScore(List<List<double[]>> clusters, double[][] data) {
        double totalSilhouetteScore = 0.0;
        int totalPoints = 0;

        for (List<double[]> cluster : clusters) {
            for (double[] point : cluster) {
                double a = computeAverageDistance(point, cluster);
                double b = computeNearestClusterDistance(point, clusters, cluster);
                if (a == 0 && b == 0) {
                    continue; // Avoid division by zero
                }

                double s = (b - a) / Math.max(a, b);
                totalSilhouetteScore += s;
                totalPoints++;
            }
        }

        return totalPoints > 0 ? totalSilhouetteScore / totalPoints : 0;
    }

    private static double computeAverageDistance(double[] point, List<double[]> cluster) {
        double totalDistance = 0.0;
        for (double[] other : cluster) {
            totalDistance += euclideanDistance(point, other);
        }
        return totalDistance / cluster.size();
    }

    private static double computeNearestClusterDistance(double[] point, List<List<double[]>> clusters, List<double[]> ownCluster) {
        double minDistance = Double.MAX_VALUE;

        for (List<double[]> cluster : clusters) {
            if (cluster.equals(ownCluster) || cluster.isEmpty()) continue; // Skip the same cluster
            double distance = computeAverageDistance(point, cluster);
            if (distance < minDistance) {
                minDistance = distance;
            }
        }
        return minDistance;
    }

    private static double euclideanDistance(double[] a, double[] b) {
        return Math.sqrt(Math.pow(a[0] - b[0], 2) + Math.pow(a[1] - b[1], 2));
    }
}