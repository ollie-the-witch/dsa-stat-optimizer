package org.olivia.test.project.data.config;

import org.olivia.test.project.inputs.dto.SolverSettingsInputDto;

public record SolverConfiguration(int resultCount, int timeLimit, int workerCount) {
    public static SolverConfiguration configuration(SolverSettingsInputDto defaultSolverSettings) {
        return new SolverConfiguration(defaultSolverSettings.resultCount(), defaultSolverSettings.timeLimit(), defaultSolverSettings.workerCount());
    }
}
