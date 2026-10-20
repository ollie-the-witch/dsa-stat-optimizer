package org.olivia.test.project.optimizationModel;

import com.google.ortools.sat.BoolVar;
import com.google.ortools.sat.CpModel;
import com.google.ortools.sat.IntVar;
import org.olivia.test.project.data.enumerations.EquipmentSlots;
import org.olivia.test.project.data.enumerations.StatVariable;

import java.util.Map;

public record OptimizationModelBundle(CpModel model, Map<EquipmentSlots, Map<StatVariable, BoolVar>> mainStatChoicesBySlot,
                                      Map<EquipmentSlots, Map<StatVariable, BoolVar>> substatChoicesBySlot, Map<StatVariable, IntVar> runeCountsByStat,
                                      Map<StatVariable, IntVar> finalStats, Map<StatVariable, StatBoundsCalculator.StatBounds> statBounds, IntVar objective) {
}
