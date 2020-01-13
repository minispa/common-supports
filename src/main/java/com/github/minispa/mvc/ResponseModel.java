package com.github.minispa.mvc;

import org.springframework.web.bind.annotation.ResponseBody;

import java.lang.annotation.*;

@ResponseBody
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ResponseModel {
}
