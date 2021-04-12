package common.supports.spring;

import common.supports.DataModel;
import common.supports.StatusCode;
import common.supports.Throwables;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;

@Slf4j
@RestControllerAdvice
@ResponseStatus(HttpStatus.OK)
public class ResponseModelRestControllerAdvice {

    @ExceptionHandler(Throwables.class)
    public Object throwables(Throwables e) {
        log.error("", e);
        String message = null;
        if (StringUtils.hasLength(e.getMsg())) {
            message = String.format(e.getMsg(), e.getArgs());
        }
        return DataModel.builder().code(e.getCode()).message(message).build();
    }

    @ExceptionHandler(Exception.class)
    public Object exception(Exception e) {
        log.error("", e);
        if (e instanceof Throwables) {
            return throwables((Throwables) e);
        }
        return DataModel.builder().code(String.valueOf(StatusCode.INTERNAL_SERVER_ERROR.value())).message(e.getMessage()).build();
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Object methodArgumentNotValidException(MethodArgumentNotValidException e) {
        log.error("", e);
        BindingResult result = e.getBindingResult();
        StringBuilder sb = new StringBuilder();
        boolean first = true;
        for(FieldError error : result.getFieldErrors()) {
            if(!first) {
                sb.append(';');
            }
            sb.append('[').append(error.getField()).append(']').append(error.getDefaultMessage());
            first = false;
        }
        return DataModel.builder().code(String.valueOf(StatusCode.INTERNAL_SERVER_ERROR.value())).message(sb.toString()).build();
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public Object constraintViolationException(ConstraintViolationException e) {
        log.error("", e);
        StringBuilder sb = new StringBuilder();
        boolean first = true;
        for (ConstraintViolation<?> violation : e.getConstraintViolations()) {
            if(!first) {
                sb.append(';');
            }
            sb.append("[").append(violation.getInvalidValue()).append("]").append(violation.getMessage());
            first = false;
        }
        return DataModel.builder().code(String.valueOf(StatusCode.INTERNAL_SERVER_ERROR.value())).message(sb.toString()).build();
    }

}
