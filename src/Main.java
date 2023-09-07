import sudoku.*;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;

public class Main {
    public static int[][] sudoku;
    public static int n;

    public static void main(String[] args) throws IOException {
        String inputFile = "inputs/in7_9x9.txt";
        String outputFile = "outputs/out7_%d.txt";

        readFromFile(inputFile);

        /* solve with BT + FC + MRV */
        SudokuSolver sudokuSolver_1 = new FC_MRV_Solver(n, sudoku);
        sudokuSolver_1.solve();
        sudokuSolver_1.saveToFile(String.format(outputFile, 1));
        System.out.println("FC_MRV solved sudoku completely: " + sudokuSolver_1.isSolved());

        /* solve with AC3 + BT + FC + MRV */
        SudokuSolver sudokuSolver_2 = new AC3_FC_MRV_Solver(n, sudoku);
        sudokuSolver_2.solve();
        sudokuSolver_2.saveToFile(String.format(outputFile, 2));
        System.out.println("AC3_FC_MRV solved sudoku completely: " + sudokuSolver_2.isSolved());

        /* solve with Min-conflicts local search */
        SudokuSolver sudokuSolver_3 = new MinConf_LocalSearch_Solver(n, sudoku, 50000, System.currentTimeMillis());
        sudokuSolver_3.solve();
        sudokuSolver_3.saveToFile(String.format(outputFile, 3));
        System.out.println("MinConf_LocalSearch solved sudoku completely: " + sudokuSolver_3.isSolved());

        /* solve with Maintaining Arc consistency + MCV */
        SudokuSolver sudokuSolver_4 = new MAC_MCV_Solver(n, sudoku);
        sudokuSolver_4.solve();
        sudokuSolver_4.saveToFile(String.format(outputFile, 4));
        System.out.println("MAC_MCV solved sudoku completely: " + sudokuSolver_4.isSolved());
    }

    /**
     * this method reads a file with the given path
     * and stores sudoku data into a 2-D array
     * and then returns it
     */
    static void readFromFile(String path) throws IOException {
        Scanner fileReader = new Scanner(new File(path));

        n = fileReader.nextInt();
        int n2 = n * n;
        sudoku = new int[n2][n2];
        for (int i = 0; i < n2; i++) {
            for (int j = 0; j < n2; j++) {
                sudoku[i][j] = fileReader.nextInt();
            }
        }
    }
}