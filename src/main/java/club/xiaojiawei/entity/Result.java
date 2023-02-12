package club.xiaojiawei.entity;

import club.xiaojiawei.enums.ResultStatusEnum;
import lombok.Data;

import static club.xiaojiawei.enums.ResultStatusEnum.*;

/**
 * @author 肖嘉威
 * @date 2022/12/4 14:33
 */
@Data
public class Result<T> {

    private ResultStatusEnum resultStatus;

    private T data;

    public Result(ResultStatusEnum resultStatus, T data) {
        this.resultStatus = resultStatus;
        this.data = data;
    }

    public static Result<Object> ofSuccess(Object data){
        return new Result<>(SUCCESS, data);
    }

    public static Result<Object> ofError(Object data){
        return new Result<>(ERROR, data);
    }

    public static Result<Object> ofFail(Object data){
        return new Result<>(FAIL, data);
    }
}
