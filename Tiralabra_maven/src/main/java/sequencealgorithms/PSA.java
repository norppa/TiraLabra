package sequencealgorithms;

import com.mycompany.tiralabra_maven.InputReader;

/**
 * Pairwise Sequence Alignment. Superclass that reads two input sequences from a
 * file.
 *
 * @author jtthaavi@cs
 */
abstract class PSA implements Problem {

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
    /**
     * A value that represents negative infinity.
     */
    protected final double NEGINF = -1E100;
    /**
     * Debugging boolean. If set to true, prints all sorts of information while executing.
     */
    private boolean verbose;

    public PSA(String filename) {
        char[][] input = InputReader.readInput(filename);
        input1 = input[0];
        input2 = input[1];
        alphabet = input[2];

        s = new ScoringMatrix(alphabet);
        m = new AlignmentMatrix(input1.length, input2.length);
        solution = null;
        verbose = false;
    }

    /**
     * Formats the first row and column from the alignment matrix.
     */
    public void setUpAlignmentMatrix() {
        for (int i = 0; i < input1.length; i++) {
            m.setScore(i + 1, 0, m.get(i, 0) + s.getScore(input1[i], '-'));
            m.setPath(i + 1, 0, 1);
        }
        for (int j = 0; j < input2.length; j++) {
            m.setScore(0, j + 1, m.get(0, j) + s.getScore('-', input2[j]));
            m.setPath(0, j + 1, 2);
        }
    }

    @Override
    public void setUpScoring(double matchBonus, double mismatchPenalty, double indelPenalty, double gapPenalty) {
        s.setMatchBonus(matchBonus);
        s.setMismatchPenalty(mismatchPenalty);
        s.setIndelPenalty(indelPenalty);
        s.setGapPenalty(gapPenalty);
        
        setUpAlignmentMatrix();
    }

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

    /**
     * Finds a correct solution from the completed alignment matrix. The found solution is stored to
     * the class variable 'solution'.
     */
    public void findSolution() {
        if (verbose) {
            System.out.println("Finding solution from alignment matrix:");
            m.print();
        }

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

        setSolution(preSolution, length);
    }

    /**
     * Finds the x-coordinate of the beginning of the solution from the alignment matrix.
     * @return 
     */
    protected abstract int findSolutionStartX();

    /**
     * Finds the y-coordinate of the beginning of the solution from the alignment matrix.
     * @return 
     */
    protected abstract int findSolutionStartY();

    /**
     * Condition that gives 'false' when the end of the solution is found.
     * @param p Coordinate of the examined element.
     * @param q Coordinate of the examined element.
     * @return 
     */
    protected abstract boolean solutionContinueCondition(int p, int q);

    /**
     * Constructs and formats the final solution from the presolution.
     * @param preSolution
     * @param length 
     */
    protected abstract void setSolution(char[][] preSolution, int length);

    /**
     *
     */
    public void solve() {
        calculateAlignment();
        findSolution();
    }

    /**
     * Getter for the solution.
     *
     * @return The solution
     */
    public char[][] getSolution() {
        if (verbose) {
            System.out.println("The alignment score is " + getAlignmentScore());
        }
        return solution;
    }

    public double getAlignmentScore() {
        return m.get(input1.length, input2.length);
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
