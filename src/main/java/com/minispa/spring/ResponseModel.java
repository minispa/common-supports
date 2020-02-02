package com.minispa.spring;

import org.springframework.web.bind.annotation.ResponseBody;

import java.lang.annotation.*;

@ResponseBody
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ResponseModel {
}
