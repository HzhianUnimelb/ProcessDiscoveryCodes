package optimization;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class KolmogorovArnoldNetwork {
    private List<Layer> layers;
    private double learningRate;

    public KolmogorovArnoldNetwork(int numLayers, int numBasisFunctionsPerLayer, double learningRate) {
        this.learningRate = learningRate;
        layers = new ArrayList<>(numLayers);
        for (int i = 0; i < numLayers; i++) {
            layers.add(new Layer(numBasisFunctionsPerLayer));
        }
    }

    public double evaluate(double x1, double x2) {
        double output = 0.0;
        for (Layer layer : layers) {
            output += layer.evaluate(x1, x2);
        }
        return clamp(output);
    }

    public void train(double[][] inputs, double[] targets, int epochs) {
        for (int epoch = 0; epoch < epochs; epoch++) {
            for (int i = 0; i < inputs.length; i++) {
                double x1 = inputs[i][0];
                double x2 = inputs[i][1];
                double target = targets[i];

                // Forward pass
                double output = evaluate(x1, x2);

                // Backward pass (gradient descent)
                double error = target - output;
                for (Layer layer : layers) {
                    layer.updateWeights(x1, x2, error, learningRate);
                }
            }
            // Optional: Print loss every 100 epochs
            if (epoch % 100 == 0) {
                double totalError = 0;
                for (int i = 0; i < inputs.length; i++) {
                    double pred = evaluate(inputs[i][0], inputs[i][1]);
                    totalError += Math.pow(targets[i] - pred, 2);
                }
                System.out.println("Epoch " + epoch + ", Loss: " + (totalError / inputs.length));
            }
        }
    }

    private double clamp(double value) {
        return Math.max(0, Math.min(value, 1));
    }

    private static class Layer {
        private List<BasisFunction> basisFunctions;

        public Layer(int numBasisFunctions) {
            basisFunctions = new ArrayList<>(numBasisFunctions);
            for (int i = 0; i < numBasisFunctions; i++) {
                basisFunctions.add(new BasisFunction());
            }
        }

        public double evaluate(double x1, double x2) {
            double output = 0.0;
            for (BasisFunction bf : basisFunctions) {
                output += bf.evaluate(x1, x2);
            }
            return output;
        }

        public void updateWeights(double x1, double x2, double error, double learningRate) {
            for (BasisFunction bf : basisFunctions) {
                bf.updateWeight(x1, x2, error, learningRate);
            }
        }
    }

    private static class BasisFunction {
        private double weight;
        private Random random;

        public BasisFunction() {
            this.random = new Random();
            this.weight = randomWeight();
        }

        private double randomWeight() {
            return (random.nextDouble() - 0.5) * 0.1; // Initialize weights in a small range
        }

        public double evaluate(double x1, double x2) {
            // Using a simple linear combination for the basis function
            return weight * (x1 * x2);
        }

        public void updateWeight(double x1, double x2, double error, double learningRate) {
            // Simple weight update rule
            weight += learningRate * error * (x1 * x2);
        }
    }

    public static void main(String[] args) {
        KolmogorovArnoldNetwork kan = new KolmogorovArnoldNetwork(3, 10, 0.00001); // Increased layers and basis functions
        
        // Training data
        double[][] inputs = {
            {0.2846016633852489, 91.93463859013288},
            {0.8498718349346006, 67.41974192994454},
            {0.4542509061467461, 6.856212182071619},
            {0.910882903740856, 93.83041525253077},
            {0.02510887087527243, 91.31224944880168},
            {0.577073440730412, 29.354001347464436},
            {0.19122335596021708, 65.27356448141015},
            {0.11157176743154194, 66.83980823100741},
            {0.5363023603178999, 9.934283860417805},
            {0.2539141198136807, 44.60018766492143},
            {0.2931804758371554, 37.56690392162506},
            {0.21354907939228965, 64.93961887369103},
            {0.7843826301446319, 46.622501195564084},
            {0.2990974214439298, 64.9774229869939},
            {0.6025596426268481, 60.65625129824277},
            {0.7311370429990028, 33.353596101154},
            {0.2489658706476717, 80.45041227415689},
            {0.13098861374542695, 21.12103654776047},
            {0.5631673449042844, 30.09270700661815},
            {0.08831057635395964, 17.935127772883945}
        };
        double[] targets = {
            0.5249123888725578,
            0.5788434619427143,
            0.6314763370164275,
            0.5049601857826554,
            0.4435341608879158,
            0.42665200421917715,
            0.4748776804475374,
            0.5169172267825975,
            0.5892668077239296,
            0.5169243094058241,
            0.5109462315698047,
            0.459818721221912,
            0.49140127931543065,
            0.4734218940843029,
            0.48092127254840267,
            0.5185075829871371,
            0.49147210554769644,
            0.5833285696435597,
            0.41471975240052805,
            0.5682988262387438
        };

        // Normalize inputs
        for (int i = 0; i < inputs.length; i++) {
            inputs[i][0] = Math.min(1, Math.max(0, inputs[i][0])); // Normalize x1 to [0, 1]
            inputs[i][1] = (inputs[i][1] - 1) / 99; // Normalize x2 to [0, 1] from [1, 100]
        }

        // Train the network with the provided data
        kan.train(inputs, targets, 30000); // Train for 1000 epochs

        // Test the network with an example input
        double testX1 = 0.08031673449042844; // Example input
        double testX2 = 17.9; // Original input
        double normalizedTestX2 = (testX2 - 1) / 99; // Normalize test input to [0, 1]
        double output = kan.evaluate(testX1, normalizedTestX2);
        System.out.println("Output for (" + testX1 + ", " + normalizedTestX2 + "): " + output);
    }
}
