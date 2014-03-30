/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sequencealgorithms;

import com.mycompany.tiralabra_maven.InputReader;

/**
 * Pairwise Sequence Alignment. Superclass that reads two input sequences from a
 * file.
 *
 * @author jtthaavi@cs
 */
abstract class PSA {

    /**
     * The two input sequences.
     */
    char[] input1, input2;
    /**
     * The alphabet used in the input sequences.
     */
    char[] alphabet;
    /**
     * The alignment matrix where the alignment scores of all possible
     * alignments are stored.
     */
    AlignmentMatrix m;
    /**
     * The scoring matrix that holds the current scoring scheme.
     */
    ScoringMatrix s;
    /**
     * The alignment that solves the problem.
     */
    char[][] solution;

    public PSA(String filename, char[] alphabet) {
        char[][] input = InputReader.readInput(filename);
        input1 = input[0];
        input2 = input[1];

        s = new ScoringMatrix(alphabet);
        m = new AlignmentMatrix(input1.length, input2.length);
        solution = null;

        setUpScoringMatrix();
    }

    /**
     * Makes sure that the scoring matrix is not empty after constructor
     * finishes.
     */
    abstract void setUpScoringMatrix();

    /**
     * Calculates the alignment scores and fills the alignment matrix.
     */
    public void calculateAlignment() {
        for (int i = 1; i < input1.length + 1; i++) {
            for (int j = 1; j < input2.length + 1; j++) {
                double[] scores = possibleScores(i, j);
                int indexOfMax = indexOfMax(scores);
                m.setScore(i, j, scores[indexOfMax]);
                m.setPath(i, j, indexOfMax);
            }
        }
    }

    /**
     * Calculates all possible alignment scores depending on the direction of
     * arrival.
     *
     * @param i The coordinates of the target entry
     * @param j The coordinates of the target entry
     * @return A double array consisting of the possible scores with indexing 0
     * - diagonal, 1 - vertical, 2 - horizontal
     */
    public double[] possibleScores(int i, int j) {
        double[] scores = new double[3];
        scores[0] = m.get(i - 1, j - 1) + s.getScore(input1[i - 1], input2[j - 1]);
        scores[1] = m.get(i - 1, j) + s.getScore('-', input1[i - 1]);
        scores[2] = m.get(i, j - 1) + s.getScore(input2[j - 1], '-');
        return scores;

    }

    /**
     * Finds the index of the largest number in double array.
     *
     * @param numbers The array to be searched.
     * @return The index of the largest number.
     */
    private int indexOfMax(double[] numbers) {
        double max = numbers[0];
        int maxIndex = 0;
        for (int i = 1; i < numbers.length; i++) {
            if (numbers[i] > max) {
                max = numbers[i];
                maxIndex = i;
            }
        }
        return maxIndex;
    }

    public void findSolution() {
        char[][] preSolution = new char[2][input1.length + input2.length];
        int p = findSolutionStartX();
        int q = findSolutionStartY();
        int length = 0;
        while (solutionContinueCondition(p, q)) {
            switch (m.getPath(p, q)) {
                case 0:
                    preSolution[0][length] = input1[p - 1];
                    preSolution[1][length] = input2[q - 1];
                    p--;
                    q--;
                    break;
                case 1:
                    preSolution[0][length] = input1[p - 1];
                    preSolution[1][length] = '-';
                    p--;
                    break;
                case 2:
                    preSolution[0][length] = '-';
                    preSolution[1][length] = input2[q - 1];
                    q--;
                    break;
                default:
                    System.out.println("error");
                    break;
            }
            length++;
        }

        System.out.println("PRINT PRESOLUTION");
        for (int i=0; i<preSolution[0].length; i++) {
            System.out.print(preSolution[0][i]);
        }
        System.out.println("");
        for (int i=0; i<preSolution[1].length; i++) {
            System.out.print(preSolution[1][i]);
        }
        System.out.println("\nLength on " + length);

        setSolution(preSolution, length);
    }

    abstract int findSolutionStartX();

    abstract int findSolutionStartY();

    abstract boolean solutionContinueCondition(int p, int q);

    abstract void setSolution(char[][] preSolution, int length);

    /**
     * Getter for the solution.
     *
     * @return The solution
     */
    public char[][] getSolution() {
        return solution;
    }

    /**
     * Prints the solution. For debugging purposes.
     */
    public void printSolution() {
        for (int l = solution[0].length - 1; l >= 0; l--) {
            System.out.print(solution[0][l]);
        }
        System.out.println("");

    }

}