package process2;

import java.util.*;
import java.util.Map.Entry;

public class FrequencyBasedFiltering {
    public HashMap<String, Long> filterEventLog(HashMap<String, Long> eventLog, double filteringThreshold) {
        // Calculate frequency of each trace
       

        // Calculate number of unique traces to keep
    	
        int numUniqueTracesToKeep = (int) Math.ceil((double)eventLog.size() * (double)filteringThreshold);
    //  System.out.println("numUniqueTracesToKeep-->"+ numUniqueTracesToKeep+" "+eventLog.size());
        List<Map.Entry<String, Long>> entryList = new ArrayList<>(eventLog.entrySet());
        
        // Sort the List in descending order based on the values of the entries
       Collections.sort(entryList, (e1, e2) -> e2.getValue().compareTo(e1.getValue()));

        // Create a new LinkedHashMap to store the sorted entries
        LinkedHashMap<String, Long> sortedEventLog = new LinkedHashMap<>();

        // Add the sorted entries to the LinkedHashMap
       for (Map.Entry<String, Long> entry : entryList) {
            sortedEventLog.put(entry.getKey(), entry.getValue());
        }
        HashMap<String, Long> filteredLog=new HashMap<String, Long>();
        // Print the sorted eventLog
        int index=0;
        for (String s: sortedEventLog.keySet()) {
        	if(numUniqueTracesToKeep >index)
        	{
       // 	if(eventLog.get(s)>filteringThreshold)
       // 	{
        		filteredLog.put(s,sortedEventLog.get(s));
       // 	}
        		index++;
        	}
        }
       // System.out.println(filteredLog.size()+" Event Size");
        return filteredLog;
    }

    public static void main(String[] args) {
        HashMap<String, Integer> eventLog = new HashMap<String, Integer>();
        eventLog.put("abcd", 12);
        eventLog.put("abcdd", 1);
        eventLog.put("abcdcc", 10);
        eventLog.put("ssss", 5);

       

        double filteringThreshold = 0.5;
        FrequencyBasedFiltering filtering = new FrequencyBasedFiltering();
       // eventLog=filtering.filterEventLog(eventLog, filteringThreshold);
    }
}