package com.github.minispa;

import lombok.*;
import lombok.experimental.Accessors;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DataModel<T> implements Serializable {

    private String code;
    private String message;

    private T data;

}
