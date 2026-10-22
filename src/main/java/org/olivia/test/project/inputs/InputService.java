package org.olivia.test.project.inputs;

import org.olivia.test.project.inputs.dto.CharacterStatInputDto;
import org.olivia.test.project.inputs.dto.CriticalBoundsInputDto;
import org.olivia.test.project.inputs.dto.SolverSettingsInputDto;
import org.olivia.test.project.inputs.dto.UptimeAssumptionsInputDto;

public class InputService {

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

    public Inputs defaultInputs() {
        return new Inputs();
    }

    public CharacterStatInputDto readCharacterStatInputDto(Inputs inputs) {
        return null;
    }

    public UptimeAssumptionsInputDto readUptimeAssumptionsInputDto(Inputs inputs) {
        return null;
    }

    public CriticalBoundsInputDto readCriticalBoundsInputDto(Inputs inputs) {
        return null;
    }

    public SolverSettingsInputDto readSolverSettingsInputDto(Inputs inputs) {
        return null;
    }


}
