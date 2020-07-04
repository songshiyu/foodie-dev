package com.lxk.exception;

import com.lxk.utils.ResultJSONResult;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

/**
 * @author songshiyu
 * @date 2020/7/4 22:42
 **/

@RestControllerAdvice
public class CustomExceptionHandler {

    /**
     * 上传文件超过500k，捕获异常
     * */
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResultJSONResult handlerMaxUploadFile(MaxUploadSizeExceededException ex){
        return ResultJSONResult.errorMsg("文件上传大小不能超过500K");
    }
}
