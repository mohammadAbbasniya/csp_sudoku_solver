package sudoku;

public class Variable {
    public int r; // row of variable in sudoku
    public int c; // column of variable in sudoku
    public boolean[] domain;
    public int domainSize;
    public boolean isSet;

    public Variable(int r, int c) {
        this.r = r;
        this.c = c;
        this.domain = null;
        this.domainSize = 0;
        this.isSet = false;
    }

    /**
     * This method automatically set first available value to this variable.
     * @param sudoku
     * @return if successfully set the value returns true, else false
     */
    boolean setValue(int[][] sudoku) {
        if (isSet)
            throw new IllegalStateException("Unable to set variable which is currently set");

        int newValue = 0;
        for (int i = 1; i < domain.length; i++) {
            if (domain[i]) {
                newValue = i; // pick first available value
                break;
            }
        }
        if (newValue > 0) { // if new value was available
            sudoku[r][c] = newValue;
            this.isSet = true;
            return true;
        } else {
            return false; // couldn't find any new value
        }
    }

    /**
     * This method unset the value of this variable.
     * after unset value, the previous value become unavailable.
     * @param sudoku
     */
    void unsetValue(int[][] sudoku) {
        if (!isSet)
            return;

        //remove the checked value from domain in order to
        //prevent checking this value again in future
        domain[sudoku[r][c]] = false;

        // place 0 in sudoku to show this cell is free
        sudoku[r][c] = 0;

        this.isSet = false;
    }

    public void calcDomain(int n, int[][] sudoku) {
        int n2 = n * n;
        domain = new boolean[n2 + 1]; // index 0 is not used
        domainSize = n2;

        // all domains are available by default
        for (int i = 0; i <= n2; i++)
            domain[i] = true;

        // checkout row of variable and remove some domains
        for (int j = 0; j < n2; j++)
            if (c != j && sudoku[r][j] > 0)
                domain[sudoku[r][j]] = false;


        // checkout column of variable and remove some domains
        for (int i = 0; i < n2; i++)
            if (r != i && sudoku[i][c] > 0)
                domain[sudoku[i][c]] = false;


        // checkout n×n square of variable and remove some domains
        for (int i = (r / n) * n; i < ((r / n) * n + n); i++)
            for (int j = (c / n) * n; j < ((c / n) * n + n); j++)
                if (r != i && c != j && sudoku[i][j] > 0)
                    domain[sudoku[i][j]] = false;


        // decrease domainSize for each false in domain
        for (int value = 1; value <= n2; value++)
            if (!domain[value])
                domainSize--;
    }

    public boolean isConflicted(int n, int[][] sudoku) {
        int n2 = n * n;
        int value = sudoku[r][c];

        // check row conflict
        for (int j = 0; j < n2; j++)
            if (j != c && sudoku[r][j] == value)
                return true;

        // check column conflict
        for (int i = 0; i < n2; i++)
            if (i != r && sudoku[i][c] == value)
                return true;

        // check square conflict
        for (int i = (r / n) * n; i < ((r / n) * n + n); i++)
            for (int j = (c / n) * n; j < ((c / n) * n + n); j++)
                if (r != i && c != j && sudoku[i][j] == value)
                    return true;

        return false;
    }
}
