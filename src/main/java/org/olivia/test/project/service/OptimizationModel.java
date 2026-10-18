package org.olivia.test.project.service;

import com.google.ortools.sat.CpModel;
import com.google.ortools.sat.IntVar;

public class OptimizationModel {

    CpModel model = new CpModel();

    int num = 3;

    IntVar x = model.newIntVar(0, num - 1, "x");
    IntVar y = model.newIntVar(0, num - 1, "y");
    IntVar z = model.newIntVar(0, num - 1, "z");

}
