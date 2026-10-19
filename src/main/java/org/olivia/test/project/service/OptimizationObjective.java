package org.olivia.test.project.service;

import com.google.ortools.sat.CpModel;
import com.google.ortools.sat.IntVar;
import com.google.ortools.sat.LinearExpr;
import org.olivia.test.project.config.OptimizationConfigurations;
import org.olivia.test.project.config.enumerations.StatVariableName;

import java.util.Map;

import static org.olivia.test.project.config.enumerations.StatVariableName.*;
import static org.olivia.test.project.service.OptimizationObjective.LongBounds.*;
import static org.olivia.test.project.service.OptimizationObjective.ProductTerm.addExactProduct;

public class OptimizationObjective {


    public static IntVar addObjective(CpModel model,
            Map<StatVariableName, IntVar> stats,
            Map<StatVariableName, StatBoundsCalculator.StatBounds> bounds,
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

        LongBounds xBounds = asLongBounds(bounds.get(ATTACK));
        LongBounds wBounds = asLongBounds(bounds.get(ATTACK_POWER_UP));
        LongBounds yBounds = asLongBounds(bounds.get(CRITICAL_RATE));
        LongBounds zBounds = asLongBounds(bounds.get(CRITICAL_DAMAGE));
        LongBounds uBounds = asLongBounds(bounds.get(PHYSICAL_ATTACK));
        LongBounds vBounds = asLongBounds(bounds.get(ELEMENTAL_ATTACK));
        LongBounds tBounds = asLongBounds(bounds.get(STATUS_ATTACK_POWER_UP));

        long scale = parameters.scale();
        long physicalUptime = Math.round(parameters.physicalStatusUptime() * UPTIME_SCALE);
        long elementalUptime = Math.round(parameters.elementalStatusUptime() * UPTIME_SCALE);

        /*
        Calculate P element
         */

        /*
        Attack * Attack Power
         */
        LongBounds wFactorBounds = shiftPercentPositive(wBounds, scale);
        IntVar wFactor = model.newIntVar(wFactorBounds.lower(), wFactorBounds.upper(), "one_plus_w_scaled");
        model.addEquality(wFactor, LinearExpr.newBuilder().add(stats.get(ATTACK_POWER_UP)).add(scale).build());
        ProductTerm attackProduct = addExactProduct(model, "attack_times_one_plus_w", stats.get(ATTACK), xBounds, wFactor, wFactorBounds);


        /*
        Status Attack & Uptime: Update bounds because lower uptime makes limit values smaller
        Status Attack * Status Attack Power Up
        Adding Bounds to Status component (divide by uptime scale to remove the effects of scaling up uptime earlier)
         */
        LongBounds physicalWeightedBounds = productBounds(uBounds, new LongBounds(physicalUptime, physicalUptime));
        LongBounds elementalWeightedBounds = productBounds(vBounds, new LongBounds(elementalUptime, elementalUptime));
        LongBounds weightedBounds = sum(physicalWeightedBounds, elementalWeightedBounds);

        IntVar weightedStatuses = model.newIntVar(weightedBounds.lower(), weightedBounds.upper(), "uptime_weighted_status_attack");
        model.addEquality(weightedStatuses,
                LinearExpr.newBuilder().addTerm(stats.get(PHYSICAL_ATTACK), physicalUptime).addTerm(stats.get(ELEMENTAL_ATTACK), elementalUptime).build());

        LongBounds tFactorBounds = shiftPercentPositive(tBounds, scale);
        IntVar tFactor = model.newIntVar(tFactorBounds.lower(), tFactorBounds.upper(), "one_plus_t_scaled");
        model.addEquality(tFactor, LinearExpr.newBuilder().add(stats.get(STATUS_ATTACK_POWER_UP)).add(scale).build());


        ProductTerm statusProduct = addExactProduct(model, "status_product", weightedStatuses, weightedBounds, tFactor, tFactorBounds);

        LongBounds statusComponentBounds = new LongBounds(statusProduct.bounds().lower() / UPTIME_SCALE, statusProduct.bounds().upper() / UPTIME_SCALE);
        IntVar statusComponent = model.newIntVar(statusComponentBounds.lower(), statusComponentBounds.upper(), "status_component");
        model.addDivisionEquality(statusComponent, statusProduct.variable(), uptimeScaleConstant);

        /*
        Sum up P by adding Attack and Status components
         */
        LongBounds pBounds = sum(attackProduct.bounds(), statusComponentBounds);
        IntVar p = model.newIntVar(pBounds.lower(), pBounds.upper(), "attack_component");
        model.addEquality(p, LinearExpr.newBuilder().add(attackProduct.variable()).add(statusComponent).build());


        return null;
    }

    private static long getUptimeScale() {
        return OptimizationConfigurations.getScale();
    }


    /**
     * @param lower
     * @param upper
     */
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
                IntVar left,
                LongBounds leftBounds,
                IntVar right,
                LongBounds rightBounds) {

            LongBounds productBounds = productBounds(leftBounds, rightBounds);

            IntVar product = model.newIntVar(productBounds.lower(), productBounds.upper(), name);

            model.addMultiplicationEquality(product, left, right);


            return new ProductTerm(product, productBounds);
        }
    }


    /**
     * Record for passing information about damage uptime
     *
     * @param scale
     * @param physicalStatusUptime
     * @param elementalStatusUptime
     */
    public record StatusUptimeParameters(int scale, double physicalStatusUptime, double elementalStatusUptime) {
    }
}
