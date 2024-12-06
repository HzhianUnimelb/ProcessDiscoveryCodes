package process2;

import java.util.Scanner;

public class Dice_A{

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        int numDice, desiredSum;
        do {
            System.out.print("Enter the number of dice (positive integer): ");
            while (!scanner.hasNextInt()) {
                System.out.println("Invalid input. Please enter a positive integer.");
                scanner.next(); // Clear invalid input
            }
            numDice = scanner.nextInt();
        } while (numDice <= 0);

        do {
            System.out.print("Enter the desired sum (positive integer): ");
            while (!scanner.hasNextInt()) {
                System.out.println("Invalid input. Please enter a positive integer.");
                scanner.next(); // Clear invalid input
            }
            desiredSum = scanner.nextInt();
        } while (desiredSum <= 0);

        boolean combinationsFound = findCombinations(desiredSum, numDice, new int[numDice], 0);

        if (!combinationsFound) {
            System.out.println("No combinations possible.");
        }
    }

    public static boolean findCombinations(int remainingSum, int diceLeft, int[] currentRolls, int index) {
        if (diceLeft == 0 && remainingSum == 0) {
            System.out.println(java.util.Arrays.toString(currentRolls));
            return true; 
        }

        if (diceLeft == 0 || remainingSum <= 0) {
            return false;
        }

        boolean found = false;
        for (int i = 1; i <= 6; i++) {
            currentRolls[index] = i;
            found |= findCombinations(remainingSum - i, diceLeft - 1, currentRolls, index + 1);
        }

        return found;
    }
}