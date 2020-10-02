package com.ken.forum_server.common;

import com.ken.forum_server.exception.CustomException;
import com.ken.forum_server.exception.CustomExceptionCode;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
public class Result<T> implements Serializable {

    private int code;
    private String msg;
    private T data;

    public Result(int code,String msg){
        this.code = code;
        this.msg = msg;
    }

    public Result(int code,String msg,T data){
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    public Result(CustomException e) {
        this.code = e.getCode();
        this.msg = e.getMessage();
    }

    public Result(CustomExceptionCode e) {
        this.code = e.getCode();
        this.msg = e.getMessage();
    }

    public Result success(String msg,T data){
        return new Result(200,msg,data);
    }

    public Result success(String msg,String data){
        return new Result(200,msg,data);
    }

    public Result success(String msg){
        return new Result(200,msg);
    }

    public Result success(T data){
        return new Result(200,"",data);
    }

    public Result success(Integer data){
        return new Result(200,"",data);
    }

    public Result fail(String msg){
        return new Result(105,msg);
    }
}
