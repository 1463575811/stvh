package io.baji.stvh.exception;


import io.baji.stvh.dto.Result;
import io.baji.stvh.enums.ResponseStateEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(SystemException.class)
    public Result systemExceptionHandler(SystemException e) {
        log.error("出现了异常! {}", e);
        return Result.error(e.getCode(), e.getMsg());
    }
    @ExceptionHandler(Exception.class)
    public Result exceptionHandler(Exception e) {
        log.error("出现了异常! {}", e);
        return Result.error(ResponseStateEnum.SYSTEM_ERROR.getCode(), e.getMessage());
    }

}
