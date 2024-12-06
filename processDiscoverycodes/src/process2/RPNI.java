package process2;

import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class RPNI {
    private PTA pta;

    public RPNI(PTA pta) {
        this.pta = pta;
    }
    public PTA getPTA() {
    	return pta;
    }
    public void run() {
        // Initialize the BLUE set with the prefixes of the positive samples
         int i =0;
       pta.initialBlue();   
        while (!pta.BLUE.isEmpty()) {
            String qb = pta.choose();
            pta.removeBlue(qb);
            
            boolean merged = false;
            for (String qr : pta.getRED()) {
            	if(qr.compareTo(qb)==0)
            		continue;
            	PTA backup = pta.copy();
            	
                backup.rpniMerge(qr, qb,0);                 
                backup.removeSuspendedStates(); // Remove suspended states after merge              
            	if (backup.rpniCompatible(true)) {     
            	
            		i++;
                    pta = backup;
                    pta.getNewBlueStates(qr);
                    merged = true;
                }     	
            }
            if (!merged) {            	
            	pta.rpniPromote(qb);            
            }
        }
        pta.markRejectingStates();
    }
    public static void main(String[] args) {
        Set<String> alphabet = new HashSet<>(Arrays.asList("a", "b"));
        Set<String> SPlus = new HashSet<>(Arrays.asList("aaa", "aaba", "bba", "bbaba"));
        Set<String> SMinus = new HashSet<>(Arrays.asList("a", "bb", "aab", "aba"));
        PTA pta = new PTA(alphabet, SPlus, SMinus);
        RPNI rpni = new RPNI(pta);
        rpni.run();
        System.out.println("Final PTA:");
        System.out.println("States: " + rpni.pta.getStates());
        System.out.println("Initial State: " + rpni.pta.getInitialState());
        System.out.println("Final Accepting States: " + rpni.pta.getFinalAcceptingStates());
        System.out.println("Final Rejecting States: " + rpni.pta.getFinalRejectingStates());
        System.out.println("Transition Function: " + rpni.pta.getTransitionFunction());
    }
}