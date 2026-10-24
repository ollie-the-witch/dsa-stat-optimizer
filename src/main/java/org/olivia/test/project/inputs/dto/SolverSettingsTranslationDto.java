package org.olivia.test.project.inputs.dto;

public record SolverSettingsTranslationDto(int resultCount, int timeLimit, int workerCount) {

    public static SolverSettingsTranslationDto defaultSolverSettings() {
        return new SolverSettingsTranslationDto(1, 30, 8);
    }
}
