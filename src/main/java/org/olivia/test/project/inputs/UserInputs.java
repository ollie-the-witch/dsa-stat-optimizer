package org.olivia.test.project.inputs;

public class UserInputs {
    public UserInputs(SolverSettingsInput solverSettingsInput, CharacterStatInput characterStatInput, CriticalBoundsInput criticalBoundsInput, AssumptionsInput assumptionsInput) {
        this.solverSettingsInput = solverSettingsInput;
        this.characterStatInput = characterStatInput;
        this.criticalBoundsInput = criticalBoundsInput;
        this.assumptionsInput = assumptionsInput;
    }

    SolverSettingsInput solverSettingsInput = null;
    CharacterStatInput characterStatInput = null;
    CriticalBoundsInput criticalBoundsInput = null;
    AssumptionsInput assumptionsInput = null;
}