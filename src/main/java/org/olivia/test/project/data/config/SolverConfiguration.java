package org.olivia.test.project.data.config;

import org.olivia.test.project.inputs.dto.SolverSettingsTranslationDto;

public record SolverConfiguration(int resultCount, int timeLimit, int workerCount) {
    public static SolverConfiguration configuration(SolverSettingsTranslationDto defaultSolverSettings) {
        return new SolverConfiguration(defaultSolverSettings.resultCount(), defaultSolverSettings.timeLimit(), defaultSolverSettings.workerCount());
    }
}
