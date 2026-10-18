package org.olivia.test.project.service;

import com.google.ortools.Loader;
import com.google.ortools.sat.CpModel;
import com.google.ortools.sat.CpSolver;
import com.google.ortools.sat.CpSolverStatus;
import com.google.ortools.sat.IntVar;

public class OptimizerService {


    public static void solveOptimizationProblem() {

        Loader.loadNativeLibraries();
        CpModel model = new CpModel();

        int num = 3;

        IntVar x = model.newIntVar(0, num - 1, "x");
        IntVar y = model.newIntVar(0, num - 1, "y");
        IntVar z = model.newIntVar(0, num - 1, "z");

        model.addDifferent(x, y);

        CpSolver solver = new CpSolver();
        CpSolverStatus status = solver.solve(model);

        if (status == CpSolverStatus.OPTIMAL || status == CpSolverStatus.FEASIBLE) {
            System.out.println("x = " + solver.value(x));
            System.out.println("y = " + solver.value(y));
            System.out.println("z = " + solver.value(z));
        } else {
            System.out.println("No solution found.");
        }
    }

    private void buildModel() {

    }
}
