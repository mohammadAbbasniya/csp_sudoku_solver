package sudoku;

public class MAC_MCV_Solver extends AC3_FC_MRV_Solver {
    public MAC_MCV_Solver(int n, int[][] sudoku) {
        super(n, sudoku);
    }

    @Override
    public void solve() {
        long start = System.currentTimeMillis();
        preprocess_AC3(variables);
        solve_rc(getMCV_variable());
        solveDuration = System.currentTimeMillis() - start;
    }

    @Override
    protected boolean solve_rc(Variable variable) {
        while (variable.setValue(sudoku)) { // while we can set new value to variable
            // run AC3 after setting value to variable
            // for maintaining arc consistency
            preprocess_AC3(adjacencyList.get(variable));

            Variable nextVariable = getMCV_variable();
            if (nextVariable != null){ // still there are some unset variables
                if(solve_rc(nextVariable)) {
                    // next variables set correctly and everything is OK
                    return true;
                }else{
                    variable.unsetValue(sudoku);
                    // there is a problem in future with this value
                }
            }else{
                // all variables are set correctly, there is no more unset variable
                return true;
            }
        }
        return false;
    }

    protected Variable getMCV_variable() {
        Variable mcv = null;
        for (Variable v : variables) {
            if (!v.isSet) {
                if (mcv == null)
                    mcv = v;
                else if (adjacencyList.get(mcv).size() < adjacencyList.get(v).size())
                    mcv = v;
            }
        }
        return mcv;
    }
}
