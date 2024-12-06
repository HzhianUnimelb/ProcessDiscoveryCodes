package optimization;

import java.util.Random;

class SplineFunction {
    private double[] parameters; // Parameters for the spline function
    private int degree; // Degree of the spline

    public SplineFunction(int degree) {
        this.degree = degree;
        this.parameters = new double[degree + 1]; // Initialize parameters for polynomial of given degree
        Random rand = new Random();
        for (int i = 0; i <= degree; i++) {
            parameters[i] = rand.nextDouble() * 0.1; // Initialize with small random values
        }
    }

    // Evaluate the spline function at a given x value
    public double evaluate(double x) {
        double result = 0.0;
        // Evaluate polynomial using Horner's method
        for (int i = degree; i >= 0; i--) {
            result = result * x + parameters[i];
        }
        return result; // Return the evaluated result
    }

    // Update parameters based on input, error, and learning rate
    public void updateParameters(double input, double error, double learningRate) {
        // Update each parameter based on the input and error
        for (int i = 0; i <= degree; i++) {
            parameters[i] += learningRate * error * Math.pow(input, i); // Gradient update for polynomial
        }
    }
}