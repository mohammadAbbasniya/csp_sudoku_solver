package sudoku;

import java.util.List;


public class FC_MRV_Solver extends SudokuSolver {
    List<Variable> variables;

    public FC_MRV_Solver(int n, int[][] sudoku) {
        super(n, sudoku);
        variables = extractVariables();
    }

    @Override
    public void solve() {
        long start = System.currentTimeMillis();
        solve_rc(getMRV_variable());
        solveDuration = System.currentTimeMillis() - start;
    }

    protected boolean solve_rc(Variable variable){
        while (variable.setValue(sudoku)){ // while we can set new value to variable
            boolean thereIsUnsetVariable = false;
            boolean forwardCheckFailed = false;

            // recalculate variables domains and forward checking
            for(Variable v : variables) {
                if (!v.isSet) {
                    v.calcDomain(n, sudoku);

                    if(v.domainSize == 0){
                        // v domain became empty
                        forwardCheckFailed = true;
                        break;
                    }

                    thereIsUnsetVariable = true;
                }
            }

            if(forwardCheckFailed){ // forward checking detected a failure in future
                variable.unsetValue(sudoku);
            } else {
                if (thereIsUnsetVariable) { // still there are some unset variables
                    if (solve_rc(getMRV_variable())) {
                        // next variables set correctly and everything is OK
                        return true;
                    } else {
                        // there is a problem in future with this value
                        variable.unsetValue(sudoku);
                    }
                } else { // all variables are set correctly
                    return true;
                }
            }
        }
        return false; // unable to find any value for this value
    }

    protected Variable getMRV_variable(){
        Variable mrv_variable = null;

        for (Variable variable : variables){
            if(! variable.isSet){ // if variable is not set
                if(mrv_variable == null || variable.domainSize < mrv_variable.domainSize){
                    mrv_variable = variable;
                }
            }
        }
        return mrv_variable;
    }
}
