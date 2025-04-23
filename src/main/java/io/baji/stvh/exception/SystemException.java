package io.baji.stvh.exception;

import io.baji.stvh.enums.ResponseStateEnum;
import lombok.Data;

@Data
public class SystemException extends RuntimeException {
    private int code;
    private String msg;

    public SystemException(ResponseStateEnum responseState) {
        super(responseState.getMsg());
        this.code = responseState.getCode();
        this.msg = responseState.getMsg();
    }
}
