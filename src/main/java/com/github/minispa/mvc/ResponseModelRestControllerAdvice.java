package com.github.minispa.mvc;

import com.github.minispa.DataModel;
import com.github.minispa.ServiceMessageException;
import org.springframework.http.HttpStatus;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@ResponseStatus(HttpStatus.OK)
public class ResponseModelRestControllerAdvice {

    @ExceptionHandler(ServiceMessageException.class)
    public Object serviceMessageException(ServiceMessageException e) {
        String message = null;
        if(StringUtils.hasLength(e.getDesc())) {
            message = String.format(e.getDesc(), e.getArgs());
        }
        return DataModel.builder().code(e.getCode()).message(message).build();
    }

    @ExceptionHandler(Exception.class)
    public Object exception(Exception e) {
        if(e instanceof ServiceMessageException) {
            return serviceMessageException((ServiceMessageException) e);
        }
        return DataModel.builder().code("-1").message(e.getMessage()).build();
    }

}
