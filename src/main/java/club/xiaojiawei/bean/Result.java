package club.xiaojiawei.bean;

import club.xiaojiawei.enums.ResultStatusEnum;
import lombok.Data;

import static club.xiaojiawei.enums.ResultStatusEnum.*;

/**
 * @author 肖嘉威
 * @date 2022/12/4 14:33
 */
@Data
public class Result<T> {

    private ResultStatusEnum status;

    private T data;

    public Result(ResultStatusEnum status, T data) {
        this.status = status;
        this.data = data;
    }

    public static<T> Result <T>ofSuccess(T data){
        return new Result<>(SUCCESS, data);
    }
    public static<T> Result <T>ofSuccess(){
        return new Result<>(SUCCESS, null);
    }
    public static Result<Object> ofError(){
        return ofError(null);
    }
    public static Result<Object> ofError(Object data){
        return new Result<>(ERROR, data);
    }
    public static Result<Object> ofFail(){
        return ofFail(null);
    }
    public static Result<Object> ofFail(Object data){
        return new Result<>(FAIL, data);
    }
}
