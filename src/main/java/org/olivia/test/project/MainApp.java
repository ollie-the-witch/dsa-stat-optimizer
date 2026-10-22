package org.olivia.test.project;


import org.olivia.test.project.inputs.dto.UserInputsDto;

public class MainApp {
    public static void main(String[] args) {


        OptimizerOrchestrator orchestrator = new OptimizerOrchestrator();
        UserInputsDto userInputsDto = orchestrator.readAndLoadInputInput();
        orchestrator.solveOptimizationProblem(userInputsDto);
    }
}