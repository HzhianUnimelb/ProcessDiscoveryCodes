package process2;

public class Entropy {
    public static void main(String[] args) {
        double probabilityE = 0.8;
        double probabilityNotE = 1 - probabilityE;
        
        double costE = -Math.log(probabilityE) / Math.log(2);
        double costNotE = -Math.log(probabilityNotE) / Math.log(2);
        
        System.out.println("Cost of representing 'e': " + costE + " bits");
        System.out.println("Cost of representing 'not e': " + costNotE + " bits");
        
        // Let's assume we have two symbols 'a' and 'b' each with a probability of 0.1
        double probabilityA = 0.1;
        double probabilityB = 0.1;
        
        // Scale the probabilities so that they add up to 1
        double scaledProbabilityA = probabilityA / (probabilityA + probabilityB);
        double scaledProbabilityB = probabilityB / (probabilityA + probabilityB);
        
        double costA = -Math.log(scaledProbabilityA) / Math.log(2);
        double costB = -Math.log(scaledProbabilityB) / Math.log(2);
        
        System.out.println("Cost of representing 'a': " + costA + " bits");
        System.out.println("Cost of representing 'b': " + costB + " bits");
    }
}