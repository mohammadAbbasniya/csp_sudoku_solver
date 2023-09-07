package sudoku;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MinConf_LocalSearch_Solver extends SudokuSolver {
    int max_steps;
    List<Variable> variables;
    Random random;

    public MinConf_LocalSearch_Solver(int n, int[][] sudoku, int max_steps, long randomSeed) {
        super(n, sudoku);
        this.max_steps = max_steps;
        this.variables = extractVariables();
        this.random = new Random(randomSeed);
    }

    @Override
    public void solve() {
        long start = System.currentTimeMillis();

        // set random values to each variable from its domain
        for (int i = 0; i < variablesCount; i++) {
            Variable v = variables.get(i);
            int randomValueIndex = random.nextInt(v.domainSize);
            for (int value = 1; value <= n2; value++) {
                if (v.domain[value]) {
                    if (randomValueIndex == 0) {
                        sudoku[v.r][v.c] = value;
                        v.isSet = true;
                        break;
                    } else {
                        randomValueIndex--;
                    }
                }
            }
        }

        // try to change the value of a random conflicted variable for max_steps items
        for (int step = 0; step < max_steps; step++) {
            // collect conflicted variables
            List<Variable> conflictedVariables = getConflictedVariables();

            // check if there is any conflicted variable
            if(conflictedVariables.size() == 0)
                break;

            // pick a random conflicted variable
            int randomVariableIndex = random.nextInt(conflictedVariables.size());
            Variable rcv = conflictedVariables.get(randomVariableIndex); // r: random c: conflicted v:variable

            // get new value with min-conflict for this variable
            int value = getMinConf_value(rcv);
            sudoku[rcv.r][rcv.c] = value;
        }

        solveDuration = System.currentTimeMillis() - start;
    }

    protected List<Variable> getConflictedVariables(){
        List<Variable> conflictedVariables = new ArrayList<>();
        for(Variable variable : variables){
            if(variable.isConflicted(n ,sudoku))
                conflictedVariables.add(variable);
        }
        return conflictedVariables;
    }

    protected int getMinConf_value(Variable variable) {
        int minConflictValue = 0; // best value (value having minimum number of conflicts)
        int minConflicts = Integer.MAX_VALUE; // number of conflicts of best value

        int r = variable.r;
        int c = variable.c;
        int currentValue = sudoku[r][c];

        for (int value = 1; value <= n2; value++) {
            int valueConflictsCount = 0;

            // conflicts in row
            for (int j = 0; j < n2; j++)
                if(j != c &&  sudoku[r][j] == value)
                    valueConflictsCount ++;

            // conflicts in column
            for (int i = 0; i < n2; i++)
                if(i != r &&  sudoku[i][c] == value)
                    valueConflictsCount ++;

            // conflicts in square
            for (int i = (r / n) * n; i < ((r / n) * n + n); i++)
                for (int j = (c / n) * n; j < ((c / n) * n + n); j++)
                    if (r != i && c != j && sudoku[i][j] == value)
                        valueConflictsCount ++;

            if(valueConflictsCount < minConflicts){
                minConflicts = valueConflictsCount;
                minConflictValue = value;
            }
        }

        return minConflictValue;
    }

    @Override
    public void saveToFile(String path) throws IOException {
        super.saveToFile(path);

        List<Variable> conflictedVariables=  getConflictedVariables();
        if(conflictedVariables.size() > 0) {
            FileWriter fileWriter = new FileWriter(path, true);
            fileWriter.write("\n");
            fileWriter.write("This is not a complete solution, just best in local search.\n");
            fileWriter.write("There are still ");
            fileWriter.write(Integer.toString(conflictedVariables.size()));
            fileWriter.write(" variables with conflict.");
            fileWriter.close();
        }
    }
}
