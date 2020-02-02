package commons.support;

import lombok.*;

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
