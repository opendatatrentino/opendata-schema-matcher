package eu.trentorise.opendata.schemamatcher.implementation.services;

/*
 *  The MIT License
 *
 *  Copyright 2010 Prasanna Velagapudi <psigen@gmail.com>.
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to deal
 *  in the Software without restriction, including without limitation the rights
 *  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included in
 *  all copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 *  THE SOFTWARE.
 */
/**
 * The Hungarian method is a combinatorial optimization algorithm which solves
 * the assignment problem in polynomial time. It takes a matrix of non-negative
 * numbers defining either the costs or values for a set of executors to
 * complete a set of jobs.
 * 
* It was developed and published by Harold Kuhn in 1955, who gave the name
 * "Hungarian method" because the algorithm was largely based on the earlier
 * works of two Hungarian mathematicians: Dénes Kőnig and Jenő Egerváry. James
 * Munkres reviewed the algorithm in 1957 and observed that it is (strongly)
 * polynomial. Since then the algorithm has been known also as Kuhn-Munkres
 * algorithm or Munkres assignment algorithm.
 * 
* Source: http://en.wikipedia.org/wiki/Hungarian_algorithm Source:
 * http://www.ams.jhu.edu/~castello/362/Handouts/hungarian.pdf
 * 
* @author Prasanna Velagapudi <psigen@gmail.com>
 */
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;

public class KuhnMunkres {

    public static int[][] computeAssignments(double[][] matrix) {

        // subtract minumum value from rows and columns to create lots of zeroes
        reduceMatrix(matrix);

        // non negative values are the index of the starred or primed zero in the row or column
        int[] sByRow = new int[matrix.length];
        Arrays.fill(sByRow, -1);
        int[] sByCol = new int[matrix[0].length];
        Arrays.fill(sByCol, -1);
        int[] pByRow = new int[matrix.length];
        Arrays.fill(pByRow, -1);

        // 1s mean covered, 0s mean not covered
        int[] coveredRows = new int[matrix.length];
        int[] coveredCols = new int[matrix[0].length];

        // star any zero that has no other starred zero in the same row or column
        initStars(matrix, sByRow, sByCol);
        coverColumnsOfStarredZeroes(sByCol, coveredCols);

        while (!allAreCovered(coveredCols)) {
            int[] primedZero = primeSomeUncoveredZero(matrix, pByRow, coveredRows, coveredCols);

            while (primedZero == null) {
                // keep making more zeroes until we find something that we can prime (i.e. a zero that is uncovered)
                makeMoreZeroes(matrix, coveredRows, coveredCols);
                primedZero = primeSomeUncoveredZero(matrix, pByRow, coveredRows, coveredCols);
            }

            // check if there is a starred zero in the primed zero's row
            int columnIndex = sByRow[primedZero[0]];
            if (-1 == columnIndex) {

                // if not, then we need to increment the zeroes and start over
                incrementSetOfStarredZeroes(primedZero, sByRow, sByCol, pByRow);
                Arrays.fill(pByRow, -1);
                Arrays.fill(coveredRows, 0);
                Arrays.fill(coveredCols, 0);
                coverColumnsOfStarredZeroes(sByCol, coveredCols);
            } else {

                // cover the row of the primed zero and uncover the column of the starred zero in the same row
                coveredRows[primedZero[0]] = 1;
                coveredCols[columnIndex] = 0;
            }
//            nrTimes++;
        }

        // ok now we should have assigned everything
        // take the starred zeroes in each column as the correct assignments
        int[][] retval = new int[matrix.length][];
        for (int i = 0; i < sByCol.length; i++) {
            retval[i] = new int[]{sByCol[i], i};
        }
        return retval;

    }

    private static boolean allAreCovered(int[] coveredCols) {
        for (int covered : coveredCols) {
            if (0 == covered) {
                return false;
            }
        }
        return true;
    }

    /**
     * the first step of the hungarian algorithm is to find the smallest element
     * in each row and subtract it's values from all elements in that row
     *
     * @return the next step to perform
     */
    private static void reduceMatrix(double[][] matrix) {

        for (int i = 0; i < matrix.length; i++) {

            // find the min value in the row
            double minValInRow = Float.MAX_VALUE;
            for (int j = 0; j < matrix[i].length; j++) {
                if (minValInRow > matrix[i][j]) {
                    minValInRow = matrix[i][j];
                }
            }

            // subtract it from all values in the row
            for (int j = 0; j < matrix[i].length; j++) {
                matrix[i][j] -= minValInRow;
            }
        }

        for (int i = 0; i < matrix[0].length; i++) {
            double minValInCol = Float.MAX_VALUE;
            for (int j = 0; j < matrix.length; j++) {
                if (minValInCol > matrix[j][i]) {
                    minValInCol = matrix[j][i];
                }
            }

            for (int j = 0; j < matrix.length; j++) {
                matrix[j][i] -= minValInCol;
            }

        }

    }

    /**
     * init starred zeroes
     *
     * for each column find the first zero if there is no other starred zero in
     * that row then star the zero, cover the column and row and go onto the
     * next column
     *
     * @param costMatrix
     * @param starredZeroes
     * @param coveredRows
     * @param coveredCols
     * @return the next step to perform
     */
    private static void initStars(double costMatrix[][], int[] starsByRow, int[] starsByCol) {

        int[] rowHasStarredZero = new int[costMatrix.length];
        int[] colHasStarredZero = new int[costMatrix[0].length];

        for (int i = 0; i < costMatrix.length; i++) {
            for (int j = 0; j < costMatrix[i].length; j++) {
                if (0 == costMatrix[i][j] && 0 == rowHasStarredZero[i] && 0 == colHasStarredZero[j]) {
                    starsByRow[i] = j;
                    starsByCol[j] = i;
                    rowHasStarredZero[i] = 1;
                    colHasStarredZero[j] = 1;
                    break; // move onto the next row
                }
            }
        }
    }

    /**
     * just marke the columns covered for any coluimn containing a starred zero
     *
     * @param starsByCol
     * @param coveredCols
     */
    private static void coverColumnsOfStarredZeroes(int[] starsByCol, int[] coveredCols) {
        for (int i = 0; i < starsByCol.length; i++) {
            coveredCols[i] = -1 == starsByCol[i] ? 0 : 1;
        }
    }

    /**
     * finds some uncovered zero and primes it
     *
     * @param matrix
     * @param primesByRow
     * @param coveredRows
     * @param coveredCols
     * @return
     */
    private static int[] primeSomeUncoveredZero(double matrix[][], int[] primesByRow,
            int[] coveredRows, int[] coveredCols) {

        // find an uncovered zero and prime it
        for (int i = 0; i < matrix.length; i++) {
            if (1 == coveredRows[i]) {
                continue;
            }
            for (int j = 0; j < matrix[i].length; j++) {
                // if it's a zero and the column is not covered
                if (0 == matrix[i][j] && 0 == coveredCols[j]) {

                    // ok this is an unstarred zero
                    // prime it
                    primesByRow[i] = j;
                    return new int[]{i, j};
                }
            }
        }
        return null;

    }

    /**
     *
     * @param unpairedZeroPrime
     * @param starsByRow
     * @param starsByCol
     * @param primesByRow
     */
    private static void incrementSetOfStarredZeroes(int[] unpairedZeroPrime, int[] starsByRow, int[] starsByCol, int[] primesByRow) {

        // build the alternating zero sequence (prime, star, prime, star, etc)
        int i, j = unpairedZeroPrime[1];

        Set<int[]> zeroSequence = new LinkedHashSet<int[]>();
        zeroSequence.add(unpairedZeroPrime);
        boolean paired = false;
        do {
            i = starsByCol[j];
            paired = -1 != i && zeroSequence.add(new int[]{i, j});
            if (!paired) {
                break;
            }

            j = primesByRow[i];
            paired = -1 != j && zeroSequence.add(new int[]{i, j});

        } while (paired);

        // unstar each starred zero of the sequence
        // and star each primed zero of the sequence
        for (int[] zero : zeroSequence) {
            if (starsByCol[zero[1]] == zero[0]) {
                starsByCol[zero[1]] = -1;
                starsByRow[zero[0]] = -1;
            }
            if (primesByRow[zero[0]] == zero[1]) {
                starsByRow[zero[0]] = zero[1];
                starsByCol[zero[1]] = zero[0];
            }
        }

    }

    private static void makeMoreZeroes(double[][] matrix, int[] coveredRows, int[] coveredCols) {

        // find the minimum uncovered value
        double minUncoveredValue = Float.MAX_VALUE;
        for (int i = 0; i < matrix.length; i++) {
            if (0 == coveredRows[i]) {
                for (int j = 0; j < matrix[i].length; j++) {
                    if (0 == coveredCols[j] && matrix[i][j] < minUncoveredValue) {
                        minUncoveredValue = matrix[i][j];
                    }
                }
            }
        }

        // add the min value to all covered rows
        for (int i = 0; i < coveredRows.length; i++) {
            if (1 == coveredRows[i]) {
                for (int j = 0; j < matrix[i].length; j++) {
                    matrix[i][j] += minUncoveredValue;
                }
            }
        }

        // subtract the min value from all uncovered columns
        for (int i = 0; i < coveredCols.length; i++) {
            if (0 == coveredCols[i]) {
                for (int j = 0; j < matrix.length; j++) {
                    matrix[j][i] -= minUncoveredValue;
                }
            }
        }
    }

}
