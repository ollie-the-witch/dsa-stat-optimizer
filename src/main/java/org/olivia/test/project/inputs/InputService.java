package org.olivia.test.project.inputs;

import org.codehaus.plexus.util.StringUtils;
import org.olivia.test.project.inputs.dto.CharacterStatInputDto;
import org.olivia.test.project.inputs.dto.CriticalBoundsInputDto;
import org.olivia.test.project.inputs.dto.SolverSettingsInputDto;
import org.olivia.test.project.inputs.dto.UptimeAssumptionsInputDto;

import java.util.List;
import java.util.stream.IntStream;

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

    private static <T> List<T> readSourceRows(String label,
            List<CharacterStatInputUiData.SourceRow> rows,
            SourceFactory<T> factory) {
        if (rows == null) {
            throw new IllegalArgumentException(label + " sources are required; use an empty list for no sources.");
        }

        return IntStream.range(0, rows.size()).mapToObj(index -> {
            CharacterStatInputUiData.SourceRow row = rows.get(index);
            String rowLabel = label + " source row " + (index + 1);
            if (row == null) {
                throw new IllegalArgumentException(rowLabel + " is missing.");
            }
            String source = row.source() == null ? "" : row.source().trim();
            if (source.isEmpty()) {
                throw new IllegalArgumentException(rowLabel + " name cannot be blank.");
            }
            double percent = parseNonNegativeDouble(rowLabel + " percentage", row.percent());
            return factory.create(source, percent);
        }).toList();
    }

    private static int parseNonNegativeInt(String label,
            String value) {
        if (StringUtils.isBlank(value)) {
            throw new IllegalArgumentException(label + " is required.");
        }
        try {
            int parsed = Integer.parseInt(value.trim());
            if (parsed < 0) {
                throw new IllegalArgumentException(label + " cannot be negative.");
            }
            return parsed;
        } catch (NumberFormatException exception) {
            throw new IllegalArgumentException(label + " must be a non-negative whole number.", exception);
        }
    }

    private static double parseNonNegativeDouble(String label,
            String value) {
        if (StringUtils.isBlank(value)) {
            throw new IllegalArgumentException(label + " is required.");
        }
        try {
            double parsed = Double.parseDouble(value.trim());
            if (!Double.isFinite(parsed) || parsed < 0) {
                throw new IllegalArgumentException(label + " must be finite and non-negative.");
            }
            return parsed;
        } catch (NumberFormatException exception) {
            throw new IllegalArgumentException(label + " must be a finite, non-negative number.", exception);
        }
    }

    public Inputs defaultInputs() {
        return new Inputs();
    }

    public CharacterStatInputDto readCharacterStatInputDto(CharacterStatInputUiData uiData,
            Inputs inputs) {
        if (uiData == null) {
            throw new IllegalArgumentException("Character stat form data is required.");
        }
        if (inputs == null) {
            throw new IllegalArgumentException("Inputs state is required.");
        }

        int characterSheetAttack = parseNonNegativeInt("Character-sheet attack", uiData.characterSheetAttack());
        int physicalAdditionalAttackPower = parseNonNegativeInt("Physical additional attack", uiData.physicalAdditionalAttackPower());
        int elementalAdditionalAttackPower = parseNonNegativeInt("Elemental additional attack", uiData.elementalAdditionalAttackPower());
        double physicalAdditionalDamageBonus = parseNonNegativeDouble("Physical additional damage bonus", uiData.physicalAdditionalDamageBonus());
        double elementalAdditionalDamageBonus = parseNonNegativeDouble("Elemental additional damage bonus", uiData.elementalAdditionalDamageBonus());

        List<CharacterStatInputDto.AttackPowerUpSources> attackPowerUpSources = readSourceRows("Attack power-up", uiData.attackPowerUpSources(), CharacterStatInputDto.AttackPowerUpSources::new);
        List<CharacterStatInputDto.StatusAttackPowerUpSources> statusAttackPowerUpSources = readSourceRows("Status attack power-up", uiData.statusAttackPowerUpSources(), CharacterStatInputDto.StatusAttackPowerUpSources::new);
        List<CharacterStatInputDto.CriticalRateSources> criticalRateSources = readSourceRows("Critical rate", uiData.criticalRateSources(), CharacterStatInputDto.CriticalRateSources::new);
        List<CharacterStatInputDto.CriticalDamageSources> criticalDamageSources = readSourceRows("Critical damage", uiData.criticalDamageSources(), CharacterStatInputDto.CriticalDamageSources::new);

        CharacterStatInputDto result = new CharacterStatInputDto(characterSheetAttack, attackPowerUpSources, new CharacterStatInputDto.StatusAttackPower(physicalAdditionalAttackPower, elementalAdditionalAttackPower, statusAttackPowerUpSources), new CharacterStatInputDto.StatusAdditionalDamageBonus(physicalAdditionalDamageBonus, elementalAdditionalDamageBonus), criticalRateSources, criticalDamageSources);

        inputs.setCharacterStatInputDto(result);
        return result;
    }

    public UptimeAssumptionsInputDto readUptimeAssumptionsInputDto(Object uiData,
            Inputs inputs) {
        inputs.setUptimeAssumptionsInputDto(null);
        return inputs.getUptimeAssumptionsInputDto();
    }

    public CriticalBoundsInputDto readCriticalBoundsInputDto(Object uiData,
            Inputs inputs) {
        inputs.setCriticalBoundsInputDto(null);
        return inputs.getCriticalBoundsInputDto();
    }

    public SolverSettingsInputDto readSolverSettingsInputDto(Object uiData,
            Inputs inputs) {
        inputs.setSolverSettingsInputDto(null);
        return inputs.getSolverSettingsInputDto();
    }

    @FunctionalInterface
    private interface SourceFactory<T> {
        T create(String source,
                double percent);
    }


}
