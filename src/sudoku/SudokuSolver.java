package sudoku;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class SudokuSolver {
    protected int n;
    protected int n2;
    protected int variablesCount;
    protected int[][] sudoku;

    protected long solveDuration;

    public SudokuSolver(int n, int[][] sudoku) {
        this.setSudoku(n, sudoku);
    }

    abstract public void solve();

    public void setSudoku(int n, int[][] sudoku) {
        this.n = n;
        this.n2 = n * n;
        this.variablesCount = 0;

        // take copy of sudoku into an internal variable
        this.sudoku = new int[n2][n2];
        for (int i = 0; i < n2; i++) {
            for (int j = 0; j < n2; j++) {
                this.sudoku[i][j] = sudoku[i][j];

                if (sudoku[i][j] == 0)
                    variablesCount++;
            }
        }
    }

    public int[][] getSudoku() {
        int[][] sudoku = new int[n2][n2];
        for (int i = 0; i < n2; i++) {
            for (int j = 0; j < n2; j++) {
                sudoku[i][j] = this.sudoku[i][j];
            }
        }
        return sudoku;
    }

    public List<Variable> extractVariables() {
        List<Variable> variables = new ArrayList<>(variablesCount);
        for (int i = 0; i < n2; i++) {
            for (int j = 0; j < n2; j++) {
                if (sudoku[i][j] == 0) { // if value is zero then it's a variable
                    Variable v = new Variable(i, j);
                    v.calcDomain(n, sudoku);
                    variables.add(v);
                }
            }
        }

        return variables;
    }

    public Map<Variable, List<Variable>> constraintGraphs(List<Variable> variables) {
        Map<Variable, List<Variable>> adjacencyList = new HashMap<>();

        // fill adjacency list
        for (int i = 0; i < variablesCount; i++) {
            Variable v1 = variables.get(i);
            List<Variable> neighbors = new ArrayList<>();

            for (int j = 0; j < variablesCount; j++) {
                if (i == j)
                    continue;

                Variable v2 = variables.get(j);

                boolean incident;
                incident = (v1.r == v2.r) // if v1 and v2 are in the same row
                        || (v1.c == v2.c) // if v1 and v2 are in the same column
                        || ((v1.r / n) * n == (v2.r / n) * n) && ((v1.c / n) * n == (v2.c / n) * n); //if v1 and v2 are in the same square

                if (incident)
                    neighbors.add(v2);
            }

            adjacencyList.put(v1, neighbors);
        }

        return adjacencyList;
    }

    public boolean isSolved() {
        boolean[] values = new boolean[n2 + 1];

        // check each row
        for (int i = 0; i < n2; i++) {
            for (int j = 1; j <= n2; j++) // reset values
                values[j] = false;

            for (int j = 0; j < n2; j++) // set flag for each present value
                values[sudoku[i][j]] = true;

            for (int j = 1; j <= n2; j++) // check all values to be present
                if (!values[j])
                    return false;
        }

        // check each column
        for (int i = 0; i < n2; i++) {
            for (int j = 1; j <= n2; j++) // reset values
                values[j] = false;

            for (int j = 0; j < n2; j++) // set flag for each present value
                values[sudoku[j][i]] = true;

            for (int j = 1; j <= n2; j++) // check all values to be present
                if (!values[j])
                    return false;
        }

        // check each square
        for (int start_i = 0; start_i < n2; start_i += n) {
            for (int start_j = 0; start_j < n2; start_j += n) {
                // check square with starting point (start_i, start_j)

                for (int j = 1; j <= n2; j++) // reset values
                    values[j] = false;

                for (int i = start_i; i < n + start_i; i++) // set flag for each present value
                    for (int j = start_j; j < n + start_j; j++)
                        values[sudoku[i][j]] = true;

                for (int j = 1; j <= n2; j++) // check all values to be present
                    if (!values[j])
                        return false;
            }
        }

        return true;
    }

    /**
     * this method takes a path creates that file and
     * stores sudoku 2D-array in it
     */
    public void saveToFile(String path) throws IOException {
        StringBuilder str = new StringBuilder();
        for (int i = 0; i < n2; i++) {
            for (int j = 0; j < n2; j++) {
                str.append(String.format("%d ", sudoku[i][j]));
            }
            str.append('\n');
        }


        FileWriter fileWriter = new FileWriter(path);
        fileWriter.write(str.toString());
        fileWriter.write('\n');
        fileWriter.write("Elapsed time to solve: ");
        fileWriter.write(Long.toString(solveDuration));
        fileWriter.write("ms \n");
        fileWriter.close();
    }
}
