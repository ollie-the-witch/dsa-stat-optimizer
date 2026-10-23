package org.olivia.test.project.inputs;

import org.olivia.test.project.inputs.dto.CharacterStatInputDto;
import org.olivia.test.project.inputs.dto.CriticalBoundsInputDto;
import org.olivia.test.project.inputs.dto.SolverSettingsInputDto;
import org.olivia.test.project.inputs.dto.UptimeAssumptionsInputDto;

public class Inputs {
    /*
        Things to read:
        - User Inputs for character stats,
        - uptime assumptions
        - armor Bonus
        - Stat Locks
        - Stat Increments
        - critical bounds
        - Solver settings,
         */
    private CharacterStatInputDto characterStatInputDto;
    private UptimeAssumptionsInputDto uptimeAssumptionsInputDto;
    // Armor Bonus
    // Stat Lock
    // Stat Increments
    private CriticalBoundsInputDto criticalBoundsInputDto;
    private SolverSettingsInputDto solverSettingsInputDto;

    Inputs() {
        characterStatInputDto = CharacterStatInputDto.defaultCharacterStatInput();
        uptimeAssumptionsInputDto = UptimeAssumptionsInputDto.defaultUptimeAssumptionsInputDto();
        criticalBoundsInputDto = CriticalBoundsInputDto.defaultCriticalBoundsInputSettings();
        solverSettingsInputDto = SolverSettingsInputDto.defaultSolverSettings();
    }

    public CharacterStatInputDto getCharacterStatInputDto() {
        return characterStatInputDto;
    }

    void setCharacterStatInputDto(CharacterStatInputDto characterStatInputDto) {
        this.characterStatInputDto = characterStatInputDto;
    }

    public UptimeAssumptionsInputDto getUptimeAssumptionsInputDto() {
        return uptimeAssumptionsInputDto;
    }

    void setUptimeAssumptionsInputDto(UptimeAssumptionsInputDto uptimeAssumptionsInputDto) {
        this.uptimeAssumptionsInputDto = uptimeAssumptionsInputDto;
    }

    public CriticalBoundsInputDto getCriticalBoundsInputDto() {
        return criticalBoundsInputDto;
    }

    void setCriticalBoundsInputDto(CriticalBoundsInputDto criticalBoundsInputDto) {
        this.criticalBoundsInputDto = criticalBoundsInputDto;
    }

    public SolverSettingsInputDto getSolverSettingsInputDto() {
        return solverSettingsInputDto;
    }

    void setSolverSettingsInputDto(SolverSettingsInputDto solverSettingsInputDto) {
        this.solverSettingsInputDto = solverSettingsInputDto;
    }
}
