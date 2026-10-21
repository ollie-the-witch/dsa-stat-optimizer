package org.olivia.test.project.optimizationModel;

import com.google.ortools.sat.CpModel;
import com.google.ortools.sat.IntVar;
import com.google.ortools.sat.LinearExpr;
import org.olivia.test.project.data.StatusUptimeParameters;
import org.olivia.test.project.data.config.OptimizationConfigurations;
import org.olivia.test.project.data.enumerations.StatVariable;

import java.util.Map;

import static org.olivia.test.project.data.enumerations.StatVariable.*;
import static org.olivia.test.project.optimizationModel.OptimizationObjective.LongBounds.*;
import static org.olivia.test.project.optimizationModel.OptimizationObjective.ProductTerm.addExactProduct;

public class OptimizationObjective {


    public static IntVar addObjective(CpModel model,
            Map<StatVariable, IntVar> stats,
            Map<StatVariable, StatBoundsCalculator.StatBounds> bounds,
            StatusUptimeParameters parameters) {

        final long UPTIME_SCALE = getUptimeScale();
        IntVar uptimeScaleConstant = model.newConstant(UPTIME_SCALE);

        /*
        Item to maximize: Damage

        Formula (pre-scaled):
        Damage = (Attack * (1 + Attack Power Up)) * (1 + (Critical Damage * Critical Rate))
        Damage = ((Attack * (1 + Attack Power Up)) + (Status Attack * (1 + Status Attack Power Up))) * (1 + (Critical Damage * Critical Rate))
        Damage = ((Attack * (1 + Attack Power Up)) + ((Physical + Element Status Attack) * (1 + Status Attack Power Up))) * (1 + (Critical Damage * Critical Rate))
        Damage = ((Attack * (1 + Attack Power Up)) + ((Physical + Element Status Attack) * (1 + Status Attack Power Up))) * (1 + (Critical Damage * Critical Rate))

        Then add uptime components to Physical and Elemental attack modifiers because how much they actually do depend on how often the status they need are actually on:
        Thus, uptime.

        Scale percentages by 10000 to do integer operations for stats that need optimization
        Percentages:
        - Attack Power Up
        - Status Attack Power Up
        - Critical Damage
        - Critical Rate

        Attack Component: (Attack * (10000 + Attack Power Up)) + ((PhysAttack * PhysUptime) + (EleAttack * EleUptime)) * (10000 + Status Attack Power Up) = P
        Critical Component: 100000000 * (Critical Damage * Critical Rate) = Q

        Objective = R = P * Q

        Operations will exceed Integer maxes so convert to longs first
         */

        LongBounds attackLongBounds = asLongBounds(bounds.get(ATTACK));
        LongBounds attackUpLongBounds = asLongBounds(bounds.get(ATTACK_POWER_UP));
        LongBounds critRateLongBounds = asLongBounds(bounds.get(CRITICAL_RATE));
        LongBounds critDamageLongBounds = asLongBounds(bounds.get(CRITICAL_DAMAGE));
        LongBounds physAttackLongBounds = asLongBounds(bounds.get(PHYSICAL_ATTACK));
        LongBounds eleAttackLongBounds = asLongBounds(bounds.get(ELEMENTAL_ATTACK));
        LongBounds statusAttackUpLongBounds = asLongBounds(bounds.get(STATUS_ATTACK_POWER_UP));

        long physicalScaledUptime = parameters.physicalStatusUptimeScaledLong();
        long elementalScaledUptime = parameters.elementalStatusUptimeScaledLong();

        /*
        ========================= Calculate P element ===========================
         */

        /*
        Attack * Attack Power
         */
        LongBounds positiveAttackUpBounds = shiftPercentPositive(attackUpLongBounds, UPTIME_SCALE);
        IntVar attackUpVar = model.newIntVar(positiveAttackUpBounds.lower(), positiveAttackUpBounds.upper(), "one_plus_w_scaled");
        model.addEquality(attackUpVar, LinearExpr.newBuilder().add(stats.get(ATTACK_POWER_UP)).add(UPTIME_SCALE).build());
        ProductTerm attackProduct = addExactProduct(model, "attack_times_one_plus_w", stats.get(ATTACK), attackLongBounds, attackUpVar, positiveAttackUpBounds);


        /*
        Status Attack & Uptime: Update bounds because lower uptime makes limit values smaller
        Status Attack * Status Attack Power Up
        Adding Bounds to Status component (divide by uptime scale to remove the effects of scaling up uptime earlier)
         */
        LongBounds physicalWeightedBounds = productBounds(physAttackLongBounds, new LongBounds(physicalScaledUptime, physicalScaledUptime));
        LongBounds elementalWeightedBounds = productBounds(eleAttackLongBounds, new LongBounds(elementalScaledUptime, elementalScaledUptime));
        LongBounds statusWeightedBounds = sum(physicalWeightedBounds, elementalWeightedBounds);

        IntVar weightedStatusAttackVar = model.newIntVar(statusWeightedBounds.lower(), statusWeightedBounds.upper(), "uptime_weighted_status_attack");
        model.addEquality(weightedStatusAttackVar, LinearExpr.newBuilder().addTerm(stats.get(PHYSICAL_ATTACK), physicalScaledUptime).addTerm(stats.get(ELEMENTAL_ATTACK),
                elementalScaledUptime).build());

        LongBounds positiveStatusAttackUpBounds = shiftPercentPositive(statusAttackUpLongBounds, UPTIME_SCALE);
        IntVar statusAttackUpFactor = model.newIntVar(positiveStatusAttackUpBounds.lower(), positiveStatusAttackUpBounds.upper(), "one_plus_t_scaled");
        model.addEquality(statusAttackUpFactor, LinearExpr.newBuilder().add(stats.get(STATUS_ATTACK_POWER_UP)).add(UPTIME_SCALE).build());

        ProductTerm statusProduct = addExactProduct(model, "status_product", weightedStatusAttackVar, statusWeightedBounds, statusAttackUpFactor, positiveStatusAttackUpBounds);
        LongBounds statusComponentBounds = new LongBounds(statusProduct.bounds().lower() / UPTIME_SCALE, statusProduct.bounds().upper() / UPTIME_SCALE);
        IntVar statusComponentVar = model.newIntVar(statusComponentBounds.lower(), statusComponentBounds.upper(), "status_component");
        model.addDivisionEquality(statusComponentVar, statusProduct.variable(), uptimeScaleConstant);

        /*
        Sum up P by adding Attack and Status components
         */
        LongBounds pComponentBounds = sum(attackProduct.bounds(), statusComponentBounds);
        IntVar pComponentVar = model.newIntVar(pComponentBounds.lower(), pComponentBounds.upper(), "attack_component");
        model.addEquality(pComponentVar, LinearExpr.newBuilder().add(attackProduct.variable()).add(statusComponentVar).build());


        /*
        ========================= Calculate Q element ===========================
         */
        /*
        Multiply Critical Rate and Critical Damage, then multiply the bounds by scale twice
        (since Q is the product of Critical Rate and Critical Damage which are both scaled)
         */
        ProductTerm criticalProduct = addExactProduct(model, "critical_rate_times_damage", stats.get(CRITICAL_RATE), critRateLongBounds, stats.get(CRITICAL_DAMAGE),
                critDamageLongBounds);

        long scaleSquared = UPTIME_SCALE * UPTIME_SCALE;
        LongBounds qBounds = shiftPercentPositive(criticalProduct.bounds(), scaleSquared);

        IntVar q = model.newIntVar(qBounds.lower(), qBounds.upper(), "critical_component");
        model.addEquality(q, LinearExpr.newBuilder().add(criticalProduct.variable()).add(scaleSquared).build());

        /*
        ========================= P * Q ===========================
        R = P * Q; maximize R.
         */
        ProductTerm objective = addExactProduct(model, "scaled_damage_objective", pComponentVar, pComponentBounds, q, qBounds);

        model.maximize(objective.variable());

        return objective.variable();
    }

    private static long getUptimeScale() {
        return OptimizationConfigurations.getScale();
    }

    /*
    Add McCormick Envelope to further restrict the valid values of a linear equation.
    Adds "four linear inequalities are added based on the upper and lower bounds of the original variables"
    Basically by stating that the product is within a certain upper and lower bound of the variable products
     */
    private static void addMcCormickEnvelope(CpModel model,
            IntVar product,
            IntVar aVar,
            IntVar bVar,
            LongBounds aBounds,
            LongBounds bBounds) {

        long aLowerBound = aBounds.lower();
        long aUpperBound = aBounds.upper();
        long bLowerBound = bBounds.lower();
        long bUpperBound = bBounds.upper();

        model.addGreaterOrEqual(LinearExpr.newBuilder().add(product).addTerm(bVar, -aLowerBound).addTerm(aVar, -bLowerBound).build(), -aLowerBound * bLowerBound);

        model.addGreaterOrEqual(LinearExpr.newBuilder().add(product).addTerm(bVar, -aUpperBound).addTerm(aVar, -bUpperBound).build(), -aUpperBound * bUpperBound);

        model.addLessOrEqual(LinearExpr.newBuilder().add(product).addTerm(bVar, -aUpperBound).addTerm(aVar, -bLowerBound).build(), -aUpperBound * bLowerBound);

        model.addLessOrEqual(LinearExpr.newBuilder().add(product).addTerm(bVar, -aLowerBound).addTerm(aVar, -bUpperBound).build(), -aLowerBound * bUpperBound);
    }

    record LongBounds(long lower, long upper) {
        static LongBounds asLongBounds(StatBoundsCalculator.StatBounds bounds) {
            return new LongBounds(bounds.lower(), bounds.upper());
        }

        static LongBounds shiftPercentPositive(LongBounds bounds,
                long offset) {
            return new LongBounds(bounds.lower() + offset, bounds.upper() + offset);
        }

        static LongBounds sum(LongBounds a,
                LongBounds b) {
            return new LongBounds(a.lower() + b.lower(), a.upper() + b.upper());
        }

        static LongBounds productBounds(LongBounds a,
                LongBounds b) {

            long p1 = Math.multiplyExact(a.lower(), b.lower());
            long p2 = Math.multiplyExact(a.lower(), b.upper());
            long p3 = Math.multiplyExact(a.upper(), b.lower());
            long p4 = Math.multiplyExact(a.upper(), b.upper());

            return new LongBounds(Math.min(Math.min(p1, p2), Math.min(p3, p4)), Math.max(Math.max(p1, p2), Math.max(p3, p4)));
        }
    }

    record ProductTerm(IntVar variable, LongBounds bounds) {
        static ProductTerm addExactProduct(CpModel model,
                String name,
                IntVar aVar,
                LongBounds aBounds,
                IntVar bVar,
                LongBounds bBounds) {

            LongBounds productBounds = productBounds(aBounds, bBounds);

            IntVar product = model.newIntVar(productBounds.lower(), productBounds.upper(), name);

            model.addMultiplicationEquality(product, aVar, bVar);

            addMcCormickEnvelope(model, product, aVar, bVar, aBounds, bBounds);
            return new ProductTerm(product, productBounds);
        }
    }
}
