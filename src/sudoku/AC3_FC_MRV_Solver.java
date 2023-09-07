package sudoku;

import java.util.Map;
import java.util.List;
import java.util.Queue;
import java.util.LinkedList;

public class AC3_FC_MRV_Solver extends FC_MRV_Solver {
    Map<Variable, List<Variable>> adjacencyList;

    public AC3_FC_MRV_Solver(int n, int[][] sudoku) {
        super(n, sudoku);
        adjacencyList = constraintGraphs(variables);
    }

    @Override
    public void solve() {
        long start = System.currentTimeMillis();
        preprocess_AC3(variables);
        super.solve();
        solveDuration = System.currentTimeMillis() - start;
    }

    protected void preprocess_AC3(List<Variable> baseVariables) {
        Queue<Variable> queue = new LinkedList<>();

        // add all variables to queue for becoming consistent
        for(Variable v : baseVariables){
            if(! v.isSet){
                v.calcDomain(n, sudoku);
                queue.add(v);
            }
        }

        // applying 2-Consistency on all variables
        while (!queue.isEmpty()) {
            Variable variable = queue.poll();

            boolean domainChanged = false;
            for (Variable neighbor : adjacencyList.get(variable)) {
                if(! neighbor.isSet) {
                    boolean changed = makeConsistence(variable, neighbor);
                    domainChanged = domainChanged || changed;
                }
            }

            if(domainChanged){ // if domain of variable is changed
                // add all neighbors to the queue to checked again
                for (Variable neighbor : adjacencyList.get(variable))
                    if(! neighbor.isSet)
                        queue.add(neighbor);
            }
        }
    }

    protected boolean makeConsistence(Variable v1, Variable v2) {
        boolean changed = false;

        for (int i = 1; i <= n2; i++) {
            if(v1.domain[i]){
                //checkout consistency of value i for v1
                boolean isConsistent = false;
                for (int j = 1; j <= n2; j++) {
                    if(v2.domain[j] && j != i){
                        // value i for v1 is consistent with some value for v2
                        isConsistent = true;
                        break;
                    }
                }

                if(! isConsistent) { //if value i for v1 is not is consistent
                    v1.domain[i] = false; //remove i from domain v1
                    v1.domainSize --;
                    changed = true;
                }
            }
        }

        return changed;
    }

}