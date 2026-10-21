package org.olivia.test.project.inputs.dto;

import java.util.List;

public record UserInputsDto(SolverSettingsInputDto solverSettingsInputDto, CharacterStatInputDto characterStatInputDto, CriticalBoundsInputDto criticalBoundsInputDto,
                            AssumptionsInputDto assumptionsInputDto) {

    private static CharacterStatInputDto getCharacterStatInput() {
        List<CharacterStatInputDto.AttackPowerUpSources> attackPowerUpSourcesList = List.of(new CharacterStatInputDto.AttackPowerUpSources("title", 11.6, true),
                new CharacterStatInputDto.AttackPowerUpSources("karma", 15, true), new CharacterStatInputDto.AttackPowerUpSources("battle will", 0, false));
        List<CharacterStatInputDto.CriticalRateSources> criticalRateSourcesList = List.of(new CharacterStatInputDto.CriticalRateSources("character sheet", 32),
                new CharacterStatInputDto.CriticalRateSources("awakening", 10), new CharacterStatInputDto.CriticalRateSources("skills", 10));
        List<CharacterStatInputDto.CriticalDamageSources> criticalDamageSourcesList = List.of(new CharacterStatInputDto.CriticalDamageSources("character sheet", 82.3));
        CharacterStatInputDto.StatusAdditionalDamageBonus statusAdditionalDamageBonus = new CharacterStatInputDto.StatusAdditionalDamageBonus(15, 35);
        CharacterStatInputDto.StatusAttackPower statusAttackPower = new CharacterStatInputDto.StatusAttackPower(100, 425, 0);

        return new CharacterStatInputDto(3757, statusAttackPower, statusAdditionalDamageBonus, attackPowerUpSourcesList, criticalRateSourcesList, criticalDamageSourcesList);
    }

    public static UserInputsDto createSampleInput() {
        AssumptionsInputDto assumptionsInputDto = new AssumptionsInputDto(10, 80);
        CharacterStatInputDto characterStatInputDto = getCharacterStatInput();
        CriticalBoundsInputDto criticalBoundsInputDto = new CriticalBoundsInputDto(10d, 100d);
        SolverSettingsInputDto solverSettingsInputDto = new SolverSettingsInputDto(5, 30, 8);

        return new UserInputsDto(solverSettingsInputDto, characterStatInputDto, criticalBoundsInputDto, assumptionsInputDto);
    }
}