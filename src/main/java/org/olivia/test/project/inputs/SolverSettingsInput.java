package org.olivia.test.project.inputs;

public class SolverSettingsInput {

    public SolverSettingsInput(int resultCount, int timeLimit, int workerCount) {
        this.resultCount = resultCount;
        this.timeLimit = timeLimit;
        this.workerCount = workerCount;
    }

    int resultCount = 10;
    int timeLimit = 30;
    int workerCount = 4;
}
