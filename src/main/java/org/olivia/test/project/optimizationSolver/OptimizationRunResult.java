package org.olivia.test.project.optimizationSolver;

import java.util.List;

public record OptimizationRunResult(int requestedResultCount, List<OptimizationResult> solutions, String stopReason) {
    public OptimizationRunResult {
        solutions = List.copyOf(solutions);
    }
}
