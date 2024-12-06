package process2;

import java.util.*;
import java.util.stream.Collectors;

public class PTA {
	

    Set<String> alphabet;
    Set<String> SPlus;
    Set<String> SMinus;
     Set<String> states;
     String initialState;
    Set<String> finalAcceptingStates;
     Set<String> finalRejectingStates;
     Map<String, Map<String, String>> transitionFunction;
     Set<String> RED;
     Set<String> BLUE;

    public PTA(Set<String> alphabet, Set<String> SPlus, Set<String> SMinus) {
        this.alphabet = alphabet;
        this.SPlus = SPlus;
        this.SMinus = SMinus;
        this.states = new HashSet<>();
        this.finalAcceptingStates = new HashSet<>();
        this.finalRejectingStates = new HashSet<>();
        this.transitionFunction = new HashMap<>();
        this.RED = new LinkedHashSet<>();
        this.BLUE = new LinkedHashSet<>();

        buildPTA();
    }

    private void buildPTA() {
        // Compute the prefix set of SPlus and SMinus
        Set<String> prefixSet = new HashSet<>();
        for (String s : SPlus) {
            for (int i = 0; i <= s.length(); i++) {
                prefixSet.add(s.substring(0, i));
            }
        }
       /* for (String s : SMinus) {
            for (int i = 0; i <= s.length(); i++) {
                prefixSet.add(s.substring(0, i));
            }
        }*/

        // Create the states
        states.add("qλ");
        for (String prefix : prefixSet) {
            if (!prefix.isEmpty()) {
                states.add("q" + prefix);
            }
        }

        // Create the initial state
        initialState = "qλ";

        // Create the transition function
        for (String state : states) {
            if (state.equals("qλ")) {
                for (String a : alphabet) {
                    String nextState = "q" + a;
                    if (states.contains(nextState)) {
                        if (!transitionFunction.containsKey(state)) {
                            transitionFunction.put(state, new HashMap<>());
                        }
                        transitionFunction.get(state).put(a, nextState);
                    }
                }
            } else {
                String prefix = state.substring(1);
                for (String a : alphabet) {
                    String nextState = "q" + prefix + a;
                    if (states.contains(nextState)) {
                        if (!transitionFunction.containsKey(state)) {
                            transitionFunction.put(state, new HashMap<>());
                        }
                        transitionFunction.get(state).put(a, nextState);
                    }
                }
            }
        }

        // Create the final accepting and rejecting states
        for (String s : SPlus) {
            finalAcceptingStates.add("q" + s);
        }
        for (String s : SMinus) {
            finalRejectingStates.add("q" + s);
        }

        // Initialize RED and BLUE
        RED.add(initialState);
    }

    public void rpniPromote(String qu) {
    
            RED.add(qu);
       
            for (String a : alphabet) {
                if (transitionFunction.containsKey(qu) && transitionFunction.get(qu).containsKey(a)) {
                    BLUE.add(transitionFunction.get(qu).get(a));
                }
            }
        
    }

    public boolean rpniCompatible(boolean flag) {
    	boolean flag1=false;
        for (String w : SMinus) {
            String currentState = initialState;
            for (char a : w.toCharArray()) {            	
            	if(transitionFunction.get(currentState)!=null)
            	{
            		
            		currentState = transitionFunction.get(currentState).get(String.valueOf(a));
            		flag1 = true;
            	}
            }
            if (finalAcceptingStates.contains(currentState) && flag1) {
                return false;
            }
            flag1=false;
        }
        return true;
    }

    public void rpniMerge(String q, String qPrime,int value) {
        for (String state : states) {
        	if(transitionFunction.containsKey(state))
            for (String a : alphabet) {
                if ( transitionFunction.get(state).containsKey(a) && transitionFunction.get(state).get(a).equals(qPrime)) {
                    transitionFunction.get(state).put(a, q);
                    rpniFold(q, qPrime);
                  
                    break;
                }
            }
        }
        
    }

    public void rpniFold(String q, String qPrime) {
    	
       
        for (String a : alphabet) {
        
            if ( transitionFunction.get(qPrime)!=null && transitionFunction.get(qPrime).containsKey(a)) {
          
            	if (transitionFunction.get(q)!=null && transitionFunction.get(q).containsKey(a)) {
            
            		rpniFold(transitionFunction.get(q).get(a), transitionFunction.get(qPrime).get(a));
                } else {
                	
                		transitionFunction.get(q).put(a, transitionFunction.get(qPrime).get(a));
                		
                        if(finalAcceptingStates.contains(qPrime)) 
                        	finalAcceptingStates.add(q);       
                	}
                }
            }
        
        markSuspended(qPrime);
    }

    private Set<String> suspendedStates = new HashSet<>();

    private void markSuspended(String state) {
        suspendedStates.add(state);
    }

    void removeSuspendedStates() {
    
        states.removeAll(suspendedStates);
       
        transitionFunction.keySet().removeAll(suspendedStates);
       
        suspendedStates.clear();
    }

    public Set<String> getStates() {
        return states;
    }

    public String getInitialState() {
        return initialState;
    }

    public Set<String> getFinalAcceptingStates() {
        return finalAcceptingStates;
    }

    public Set<String> getFinalRejectingStates() {
        return finalRejectingStates;
    }

    public Map<String, Map<String, String>> getTransitionFunction() {
        return transitionFunction;
    }

    public Set<String> getRED() {
        return RED;
    }

    public Set<String> getBLUE() {
        return BLUE;
    }
    public void AlphabeticalSort(){
    	BLUE = BLUE.stream()
      		  .sorted()
      		  .collect(Collectors.toCollection(LinkedHashSet::new));
    	
    }
    public void LexLengthSort()
    {
    	BLUE = BLUE.stream()
                .sorted((s1, s2) -> {
                    if (s1.length() == s2.length()) {
                        return s1.compareTo(s2);
                    } else {
                        return Integer.compare(s1.length(), s2.length());
                    }
                })
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }
    public void getNewBlueStates(String qr) {
        for (String a : alphabet) {
            if (transitionFunction.containsKey(qr) && transitionFunction.get(qr).containsKey(a)) {
                String nextState = transitionFunction.get(qr).get(a);
                if (!RED.contains(nextState)) {
                    BLUE.add(nextState);
                }
            }
        }
      
    }

    
    public String choose() {
        // Implement the CHOOSE function to select a state from BLUE
        // For example, return the first element in the set
        return BLUE.iterator().next();
    }

    public void markRejectingStates() {
        for (String qr : RED) {
            if (isRejectingState(qr)) {
                finalRejectingStates.add(qr);
            }
        }
    }
    public void initialBlue()
    {
    	 for (String s :SPlus) {
             for (int i = 0; i <= s.length(); i++) {
                 String prefix = s.substring(0, i);
                 if (!prefix.isEmpty()) {
                     BLUE.add("q" + prefix);
                 }
             }
         }
         LexLengthSort();
    }
    public void removeBlue(String qb) {
    	BLUE.remove(qb);
    }
    public boolean isRejectingState(String qr) {
        // Implement the check for rejecting states
        // For example, return true if the state is not accepting
        return finalAcceptingStates.contains(qr);
    }
    public PTA copy() {
        PTA backup = new PTA(alphabet, SPlus, SMinus);
        
        backup.states = new HashSet<>(states);
        backup.initialState = initialState;
        backup.finalAcceptingStates = new HashSet<>(finalAcceptingStates);
        backup.finalRejectingStates = new HashSet<>(finalRejectingStates);
        backup.transitionFunction = new HashMap<>();
        
        for (String state : transitionFunction.keySet()) {
            backup.transitionFunction.put(state, new HashMap<>(transitionFunction.get(state)));
        }
        
        backup.RED = new LinkedHashSet<>(RED);
        backup.BLUE = new LinkedHashSet<>(BLUE);
        
        return backup;
    }
    public static void main(String[] args) {
        Set<String> alphabet = new HashSet<>(Arrays.asList("a", "b"));
        Set<String> SPlus = new HashSet<>(Arrays.asList("aaa", "aaba", "bba", "bbaba"));
        Set<String> SMinus = new HashSet<>(Arrays.asList("a", "bb", "aab", "aba"));

        PTA pta = new PTA(alphabet, SPlus, SMinus);

        System.out.println("States: " + pta.getStates());
        System.out.println("Initial State: " + pta.getInitialState());
        System.out.println("Final Accepting States: " + pta.getFinalAcceptingStates());
        System.out.println("Final Rejecting States: " + pta.getFinalRejectingStates());
        System.out.println("Transition Function: " + pta.getTransitionFunction());

       // System.out.println("Is Compatible: " + pta.rpniCompatible());

       // pta.rpniMerge("qλ", "qa");
        System.out.println("States: " + pta.getStates());
        System.out.println("Transition Function: " + pta.getTransitionFunction());
    }

}