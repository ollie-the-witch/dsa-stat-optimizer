package org.olivia.test.project.optimizationModel;

import com.google.ortools.sat.BoolVar;
import com.google.ortools.sat.CpModel;
import com.google.ortools.sat.IntVar;
import org.olivia.test.project.config.enumerations.EquipmentSlots;
import org.olivia.test.project.config.enumerations.StatVariableName;

import java.util.Map;

public record OptimizationModelBundle(CpModel model, Map<EquipmentSlots, Map<StatVariableName, BoolVar>> mainStatChoicesBySlot,
                                      Map<EquipmentSlots, Map<StatVariableName, BoolVar>> substatChoicesBySlot, Map<StatVariableName, IntVar> runeCountsByStat,
                                      Map<StatVariableName, IntVar> finalStats, IntVar objective, Map<StatVariableName, StatBoundsCalculator.StatBounds> statBounds) {
}
