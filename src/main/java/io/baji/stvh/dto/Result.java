package io.baji.stvh.dto;

import io.baji.stvh.enums.ResponseStateEnum;
import lombok.Data;

/**
 * 封装相应结果
 */
@Data
public class Result<T> {

    private int code = 0;
    private String msg = "";
    private T data = null;
    private PageInfo pageInfo;

    public Result() {}

    public Result(T data) {
        this.data = data;
        this.code = 200;
        this.msg = "success";
    }
    public Result(int code, String msg, T data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }
    public Result(int code, String msg, T data, PageInfo pageInfo) {
        this.code = code;
        this.msg = msg;
        this.data = data;
        this.pageInfo = pageInfo;
    }
    public Result(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    /**
     * 成功
     */
    public static <T> Result<T> success(T data) {
        Result<T> result = new Result<T>();
        result.setCode(ResponseStateEnum.SUCCESS.getCode());
        result.setMsg(ResponseStateEnum.SUCCESS.getMsg());
        result.setData(data);
        return result;
    }

    public static <T> Result<T> success(T data, PageInfo pageInfo) {
        Result<T> result = new Result<T>();
        result.setCode(ResponseStateEnum.SUCCESS.getCode());
        result.setMsg(ResponseStateEnum.SUCCESS.getMsg());
        result.setData(data);
        result.setPageInfo(pageInfo);
        return result;
    }

    public static <T> Result<T> success() {
        return new Result(ResponseStateEnum.SUCCESS.getCode(), ResponseStateEnum.SUCCESS.getMsg());
    }

    public static <T> Result<T> success(ResponseStateEnum responseState) {
        return new Result(responseState.getCode(), responseState.getMsg());
    }

    public static <T> Result<T> success(ResponseStateEnum responseState, T data) {
        return new Result(responseState.getCode(), responseState.getMsg(), data);
    }

    /**
     * 失败
     */

    public static <T> Result<T> error(String message) {
        return new Result(message);
    }

    public static <T> Result<T> error(int code, String message) {
        return new Result(code, message);
    }

    public static <T> Result<T> error(ResponseStateEnum responseState) {
        return new Result(responseState.getCode(), responseState.getMsg());
    }

}
