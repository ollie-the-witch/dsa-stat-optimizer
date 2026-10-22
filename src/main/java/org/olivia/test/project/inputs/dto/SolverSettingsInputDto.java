package org.olivia.test.project.inputs.dto;

public record SolverSettingsInputDto(int resultCount, int timeLimit, int workerCount) {

    public static SolverSettingsInputDto defaultSolverSettings() {
        return new SolverSettingsInputDto(1, 30, 8);
    }
}
