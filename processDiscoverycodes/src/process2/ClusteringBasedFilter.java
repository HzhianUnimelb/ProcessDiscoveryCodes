package process2;

import java.util.*;

public class ClusteringBasedFilter {

    private Map<String, Integer> frequencyMap;
    private int numClusters;

    public ClusteringBasedFilter(int numClusters) {
        this.numClusters = numClusters;
        this.frequencyMap = new HashMap<>();
    }

    public void addEventLog(String eventLog, int frequency) {
        frequencyMap.put(eventLog, frequency);
    }

    public void filterEventLogs() {
        List<Map.Entry<String, Integer>> list = new ArrayList<>(frequencyMap.entrySet());
        list.sort((o1, o2) -> o2.getValue().compareTo(o1.getValue()));

        KMeansClustering kmeans = new KMeansClustering(numClusters);
        int[] clusters = kmeans.cluster(list);

        frequencyMap.clear();
        for (int i = 0; i < list.size(); i++) {
            if (clusters[i] == 0) {
                frequencyMap.put(list.get(i).getKey(), list.get(i).getValue());
            }
        }
    }

    public Map<String, Integer> getFilteredEventLogs() {
        return frequencyMap;
    }

    public static void main(String[] args) {
        ClusteringBasedFilter filter = new ClusteringBasedFilter(2); // filter out the event logs into 2 clusters

        filter.addEventLog("a,b,c", 10);
        filter.addEventLog("d,e,f", 5);
        filter.addEventLog("g,h,i", 8);
        filter.addEventLog("j,k,l", 12);
        filter.addEventLog("m,n,o", 3);

        filter.filterEventLogs();

        System.out.println(filter.getFilteredEventLogs());
    }
}

class KMeansClustering {

    private int numClusters;

    public KMeansClustering(int numClusters) {
        this.numClusters = numClusters;
    }

    public int[] cluster(List<Map.Entry<String, Integer>> list) {
        int[] clusters = new int[list.size()];
        Random random = new Random();
        for (int i = 0; i < list.size(); i++) {
            clusters[i] = random.nextInt(numClusters);
        }

        boolean changed = true;
        while (changed) {
            changed = false;
            for (int i = 0; i < list.size(); i++) {
                int closestCluster = getClosestCluster(list.get(i).getValue(), clusters, list);
                if (closestCluster != clusters[i]) {
                    clusters[i] = closestCluster;
                    changed = true;
                }
            }
        }

        return clusters;
    }

    private int getClosestCluster(int value, int[] clusters, List<Map.Entry<String, Integer>> list) {
        int closestCluster = 0;
        int minDistance = Integer.MAX_VALUE;
        for (int i = 0; i < numClusters; i++) {
            int distance = getDistance(value, clusters, list, i);
            if (distance < minDistance) {
                minDistance = distance;
                closestCluster = i;
            }
        }
        return closestCluster;
    }

    private int getDistance(int value, int[] clusters, List<Map.Entry<String, Integer>> list, int cluster) {
        int sum = 0;
        int count = 0;
        for (int i = 0; i < list.size(); i++) {
            if (clusters[i] == cluster) {
                sum += list.get(i).getValue();
                count++;
            }
        }
        return Math.abs(value - sum / count);
    }
}