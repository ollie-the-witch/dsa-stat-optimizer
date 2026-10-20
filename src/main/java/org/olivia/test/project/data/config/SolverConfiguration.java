package org.olivia.test.project.data.config;

import org.olivia.test.project.inputs.dto.SolverSettingsInputDto;
import org.olivia.test.project.inputs.dto.UserInputsDto;

public record SolverConfiguration(int resultCount, int timeLimit, int workerCount) {
    public static SolverConfiguration configuration(UserInputsDto userInput) {

        SolverSettingsInputDto solverSettingsInput = userInput.solverSettingsInputDto();
        return new SolverConfiguration(solverSettingsInput.resultCount(), solverSettingsInput.timeLimit(), solverSettingsInput.workerCount());
    }
}
