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

    public static  DataModel<Void> of(String code) {
        return DataModel.<Void>builder().code(code).build();
    }

    public static  DataModel<Void> of(String code, String message) {
        return DataModel.<Void>builder().code(code).message(message).build();
    }

    public static <T>  DataModel<T> of(String code, String message, T data) {
        return DataModel.<T>builder().code(code).message(message).data(data).build();
    }
}
