package org.olivia.test.project.inputs;

import org.olivia.test.project.inputs.dto.UserInputsDto;

public class InputService {

    public UserInputsDto readInput() {
        return null;
    }

    public UserInputsDto readInputMock() {
        return UserInputsDto.createSampleInput();
    }


}
