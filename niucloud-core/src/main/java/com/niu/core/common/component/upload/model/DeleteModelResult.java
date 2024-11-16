package com.niu.core.common.component.upload.model;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class DeleteModelResult {

    private boolean result;

    public DeleteModelResult() {
        this.result = true;
    }

}
