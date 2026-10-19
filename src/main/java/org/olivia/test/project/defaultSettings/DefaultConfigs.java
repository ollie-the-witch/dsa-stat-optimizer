package org.olivia.test.project.defaultSettings;

import org.olivia.test.project.config.*;
import org.olivia.test.project.config.enumerations.StatCategory;
import org.olivia.test.project.config.enumerations.StatVariableName;
import org.olivia.test.project.inputs.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DefaultConfigs {


    public static StatIncreases defaultStatIncreases() {
        Map<StatVariableName, int[]> main = new HashMap<>();
        main.put(StatVariableName.ATTACK_POWER_UP, new int[]{2386, 3408, 3068, 2386, 2386});
        main.put(StatVariableName.CRITICAL_RATE, new int[]{1164, 1664, 1500, 1164, 1164});
        main.put(StatVariableName.CRITICAL_DAMAGE, new int[]{1872, 2676, 2408, 1872, 1872});

        Map<StatVariableName, int[]> sub = new HashMap<>();
        sub.put(StatVariableName.ATTACK, new int[]{25, 35, 32, 25, 25});
        sub.put(StatVariableName.ATTACK_POWER_UP, new int[]{318, 454, 409, 318, 318});
        sub.put(StatVariableName.CRITICAL_RATE, new int[]{155, 222, 200, 155, 155});
        sub.put(StatVariableName.CRITICAL_DAMAGE, new int[]{250, 357, 321, 250, 250});
        sub.put(StatVariableName.PHYSICAL_ATTACK, new int[]{175, 250, 225, 175, 175});
        sub.put(StatVariableName.ELEMENTAL_ATTACK, new int[]{87, 125, 112, 87, 87});
        sub.put(StatVariableName.STATUS_ATTACK_POWER_UP, new int[]{388, 555, 500, 388, 388});

        Map<StatVariableName, Integer> rune = new HashMap<>();
        rune.put(StatVariableName.ATTACK, 140);
        rune.put(StatVariableName.CRITICAL_RATE, 850);
        rune.put(StatVariableName.CRITICAL_DAMAGE, 1400);

        StatIncreases statIncreases = new StatIncreases(main, sub, rune);
        System.out.println("defaultStatIncreases(): " + statIncreases);
        return statIncreases;
    }

    public static BaseStats defaultBaseStats() {

        UserInputs userInputs = DefaultUserInput.createSampleInput();

        OptimizationConfigurations configurations = new OptimizationConfigurations();
        int scale = configurations.getScale();

        CharacterStatInput characterStatInput = userInputs.characterStatInput();
        int totalAttack = characterStatInput.characterSheetAttack();
        double totalAttackUp = characterStatInput.sumOptimizationAttackPowerUp();
        int baseAttack = (int) (totalAttack / (1 + (totalAttackUp / 100)));

        double totalCriticalRate = characterStatInput.sumCriticalRate();
        double totalCriticalDamage = characterStatInput.sumCriticalDamage();

        int physicalAdditionalAttackPower = characterStatInput.statusAttackPower().physicalAdditionalAttackPower();
        int elementalAdditionalAttackPower = characterStatInput.statusAttackPower().elementalAdditionalAttackPower();
        double existingStatusAttackPowerUp = characterStatInput.statusAttackPower().existingStatusAttackPowerUp();

        Map<StatVariableName, Integer> baseStatsMap = new HashMap<>();
        baseStatsMap.put(StatVariableName.ATTACK, baseAttack);
        baseStatsMap.put(StatVariableName.ATTACK_POWER_UP, (int) (totalAttackUp/100 * scale));
        baseStatsMap.put(StatVariableName.CRITICAL_RATE, (int) (totalCriticalRate/100 * scale));
        baseStatsMap.put(StatVariableName.CRITICAL_DAMAGE, (int) (totalCriticalDamage/100 * scale));
        baseStatsMap.put(StatVariableName.PHYSICAL_ATTACK, physicalAdditionalAttackPower);
        baseStatsMap.put(StatVariableName.ELEMENTAL_ATTACK, elementalAdditionalAttackPower);
        baseStatsMap.put(StatVariableName.STATUS_ATTACK_POWER_UP, (int) (existingStatusAttackPowerUp/100 * scale));

        BaseStats baseStats = new BaseStats(baseStatsMap);
        System.out.println("defaultBaseStats(): " + baseStats);
        return baseStats;
    }

    public static SelectCount defaultSelectCount() {
        Map<StatCategory, Integer> count = new HashMap<>();
        count.put(StatCategory.MAIN, 1);
        count.put(StatCategory.SUBSTAT, 4);

        SelectCount selectCount = new SelectCount(count);
        System.out.println("defaultSelectCount(): " + selectCount);
        return selectCount;
    }

    public static StatLimitConfiguration defaultStatBoundsConfiguration() {
        Map<StatVariableName, Integer> statCaps = Map.of(
                StatVariableName.CRITICAL_RATE, 10000
        );

        Map<StatVariableName, Integer> statLowerBounds = Map.of(
                StatVariableName.CRITICAL_RATE, 5000
        );

        Map<StatVariableName, Integer> statUpperBounds = Map.of(
                StatVariableName.CRITICAL_RATE, 9500
        );

        StatLimitConfiguration statLimitConfiguration = new StatLimitConfiguration(statCaps, statLowerBounds, statUpperBounds);

        System.out.println("defaultStatBoundsConfiguration(): " + statLimitConfiguration);
        return statLimitConfiguration;
    }

    static class DefaultUserInput {
        private static CharacterStatInput getCharacterStatInput() {

            List<CharacterStatInput.AttackPowerUpSources> attackPowerUpSourcesList = List.of(new CharacterStatInput.AttackPowerUpSources("title", 11.6, true),
                    new CharacterStatInput.AttackPowerUpSources("karma", 15, true),
                    new CharacterStatInput.AttackPowerUpSources("battle will", 300, false));
            List<CharacterStatInput.CriticalRateSources> criticalRateSourcesList = List.of(new CharacterStatInput.CriticalRateSources("character sheet", 32),
                    new CharacterStatInput.CriticalRateSources("awakening", 10));
            List<CharacterStatInput.CriticalDamageSources> criticalDamageSourcesList = List.of(new CharacterStatInput.CriticalDamageSources("character sheet", 82.3));
            CharacterStatInput.AdditionalDamageBonus additionalDamageBonus = new CharacterStatInput.AdditionalDamageBonus(15, 35);
            CharacterStatInput.StatusAttackPower statusAttackPower = new CharacterStatInput.StatusAttackPower(100, 425, 10);

            return new CharacterStatInput(3757, statusAttackPower, additionalDamageBonus, attackPowerUpSourcesList, criticalRateSourcesList, criticalDamageSourcesList);
        }

        private static UserInputs createSampleInput() {
            AssumptionsInput assumptionsInput = new AssumptionsInput(10, 80);


            CharacterStatInput characterStatInput = getCharacterStatInput();

            CriticalBoundsInput criticalBoundsInput = new CriticalBoundsInput(10d, 90d);

            SolverSettingsInput solverSettingsInput = new SolverSettingsInput(5, 30, 4);

            UserInputs userInputs = new UserInputs(solverSettingsInput, characterStatInput, criticalBoundsInput, assumptionsInput);

            System.out.println("createSampleInput(): " + userInputs);

            return userInputs;
        }
    }
}
