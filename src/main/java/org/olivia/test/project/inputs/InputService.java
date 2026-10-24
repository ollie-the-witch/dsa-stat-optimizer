package org.olivia.test.project.inputs;

import org.codehaus.plexus.util.StringUtils;
import org.olivia.test.project.inputs.dto.CharacterStatTranslationDto;
import org.olivia.test.project.inputs.dto.CriticalBoundsTranslationDto;
import org.olivia.test.project.inputs.dto.SolverSettingsTranslationDto;
import org.olivia.test.project.inputs.dto.UptimeTranslationDto;
import org.olivia.test.project.ui.inputs.dto.CharacterStatInputUiDto;
import org.olivia.test.project.ui.inputs.dto.CriticalBoundsInputUiDto;
import org.olivia.test.project.ui.inputs.dto.SolverSettingsInputUiDto;
import org.olivia.test.project.ui.inputs.dto.UptimeAssumptionsInputUiDto;

import java.util.List;
import java.util.stream.IntStream;

public class InputService {

    private static <T> List<T> readSourceRows(String label,
            List<CharacterStatInputUiDto.SourceRow> rows,
            SourceFactory<T> factory) {
        if (rows == null) {
            throw new IllegalArgumentException(label + " sources are required; use an empty list for no sources.");
        }

        return IntStream.range(0, rows.size()).mapToObj(index -> {
            CharacterStatInputUiDto.SourceRow row = rows.get(index);
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

    public CharacterStatTranslationDto readCharacterStatInputDto(CharacterStatInputUiDto uiData,
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

        List<CharacterStatTranslationDto.AttackPowerUpSources> attackPowerUpSources = readSourceRows("Attack power-up",
                uiData.attackPowerUpSources(),
                CharacterStatTranslationDto.AttackPowerUpSources::new);
        List<CharacterStatTranslationDto.StatusAttackPowerUpSources> statusAttackPowerUpSources = readSourceRows("Status attack power-up",
                uiData.statusAttackPowerUpSources(),
                CharacterStatTranslationDto.StatusAttackPowerUpSources::new);
        List<CharacterStatTranslationDto.CriticalRateSources> criticalRateSources = readSourceRows("Critical rate",
                uiData.criticalRateSources(),
                CharacterStatTranslationDto.CriticalRateSources::new);
        List<CharacterStatTranslationDto.CriticalDamageSources> criticalDamageSources = readSourceRows("Critical damage",
                uiData.criticalDamageSources(),
                CharacterStatTranslationDto.CriticalDamageSources::new);

        CharacterStatTranslationDto result = new CharacterStatTranslationDto(characterSheetAttack,
                attackPowerUpSources,
                new CharacterStatTranslationDto.StatusAttackPower(physicalAdditionalAttackPower, elementalAdditionalAttackPower, statusAttackPowerUpSources),
                new CharacterStatTranslationDto.StatusAdditionalDamageBonus(physicalAdditionalDamageBonus, elementalAdditionalDamageBonus),
                criticalRateSources,
                criticalDamageSources);

        inputs.setCharacterStatInputDto(result);
        return result;
    }

    public UptimeTranslationDto readUptimeAssumptionsInputDto(UptimeAssumptionsInputUiDto uiData,
            Inputs inputs) {

        if (uiData == null) {
            throw new IllegalArgumentException("Uptime assumptions form data is required.");
        }
        if (inputs == null) {
            throw new IllegalArgumentException("Inputs state is required.");
        }

        double physicalStatusUptime = parseNonNegativeDouble("Physical status uptime", uiData.physicalStatusUptime());
        double elementalStatusUptime = parseNonNegativeDouble("Elemental status uptime", uiData.elementalStatusUptime());

        UptimeTranslationDto result = new UptimeTranslationDto(physicalStatusUptime, elementalStatusUptime);

        inputs.setUptimeAssumptionsInputDto(result);
        return inputs.getUptimeAssumptionsInputDto();
    }

    public void readArmorBonusInputDto() {

    }

    public void readStatLocksInputDto() {

    }

    public void readStatIncrementInputDto() {

    }

    public CriticalBoundsTranslationDto readCriticalBoundsInputDto(CriticalBoundsInputUiDto uiData,
            Inputs inputs) {

        double minimum = parseNonNegativeDouble("Minimum Desired Critical Rate", uiData.minimum());
        double maximum = parseNonNegativeDouble("Maximum Desired Critical Rate", uiData.maximum());
        double cap = parseNonNegativeDouble("Hard Critical Rate Cap", uiData.cap());


        CriticalBoundsTranslationDto result = new CriticalBoundsTranslationDto(minimum, maximum, cap);

        inputs.setCriticalBoundsInputDto(result);
        return inputs.getCriticalBoundsInputDto();
    }

    public SolverSettingsTranslationDto readSolverSettingsInputDto(SolverSettingsInputUiDto uiData,
            Inputs inputs) {

        int resultCount = parseNonNegativeInt("Number of Results to Return", uiData.resultCount());
        int timeLimit = parseNonNegativeInt("Optimizer Time Limit per Solution", uiData.timeLimit());
        int workerCount = parseNonNegativeInt("Number of Workers", uiData.workerCount());

        SolverSettingsTranslationDto result = new SolverSettingsTranslationDto(resultCount, timeLimit, workerCount);
        inputs.setSolverSettingsInputDto(result);
        return inputs.getSolverSettingsInputDto();
    }

    @FunctionalInterface
    private interface SourceFactory<T> {
        T create(String source,
                double percent);
    }


}
