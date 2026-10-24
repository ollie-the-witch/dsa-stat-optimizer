package org.olivia.test.project.inputs;

import org.olivia.test.project.inputs.dto.CharacterStatTranslationDto;
import org.olivia.test.project.inputs.dto.CriticalBoundsTranslationDto;
import org.olivia.test.project.inputs.dto.SolverSettingsTranslationDto;
import org.olivia.test.project.inputs.dto.UptimeTranslationDto;

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
    private CharacterStatTranslationDto characterStatTranslationDto;
    private UptimeTranslationDto uptimeTranslationDto;
    // Armor Bonus
    // Stat Lock
    // Stat Increments
    private CriticalBoundsTranslationDto criticalBoundsTranslationDto;
    private SolverSettingsTranslationDto solverSettingsTranslationDto;

    Inputs() {
        characterStatTranslationDto = CharacterStatTranslationDto.defaultCharacterStatInput();
        uptimeTranslationDto = UptimeTranslationDto.defaultUptimeAssumptionsInputDto();
        criticalBoundsTranslationDto = CriticalBoundsTranslationDto.defaultCriticalBoundsInputSettings();
        solverSettingsTranslationDto = SolverSettingsTranslationDto.defaultSolverSettings();
    }

    public CharacterStatTranslationDto getCharacterStatInputDto() {
        return characterStatTranslationDto;
    }

    void setCharacterStatInputDto(CharacterStatTranslationDto characterStatTranslationDto) {
        this.characterStatTranslationDto = characterStatTranslationDto;
    }

    public UptimeTranslationDto getUptimeAssumptionsInputDto() {
        return uptimeTranslationDto;
    }

    void setUptimeAssumptionsInputDto(UptimeTranslationDto uptimeTranslationDto) {
        this.uptimeTranslationDto = uptimeTranslationDto;
    }

    public CriticalBoundsTranslationDto getCriticalBoundsInputDto() {
        return criticalBoundsTranslationDto;
    }

    void setCriticalBoundsInputDto(CriticalBoundsTranslationDto criticalBoundsTranslationDto) {
        this.criticalBoundsTranslationDto = criticalBoundsTranslationDto;
    }

    public SolverSettingsTranslationDto getSolverSettingsInputDto() {
        return solverSettingsTranslationDto;
    }

    void setSolverSettingsInputDto(SolverSettingsTranslationDto solverSettingsTranslationDto) {
        this.solverSettingsTranslationDto = solverSettingsTranslationDto;
    }
}
