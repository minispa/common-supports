package common.supports;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Throwables extends RuntimeException {

    private String code;
    private String msg;
    private String[] args;

    public Throwables(String code) {
        this.code = code;
    }

    public Throwables(String code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    @Override
    public String getMessage() {
        StringBuilder sb = new StringBuilder();
        if(code != null) {
            sb.append(" code: [").append(code).append("]");
        }
        if(msg != null) {
            sb.append(" msg: [").append(String.format(msg, args)).append("]");
        }
        return sb.toString();
    }

    public void throwEx() {
        throw this;
    }

    public static void throwEx(String code) {
        new Throwables(code, null).throwEx();
    }

    public static void throwEx(String code, String msg) {
        new Throwables(code, msg).throwEx();
    }

    public static void throwEx(String code, String msg, String... args) {
        new Throwables(code, msg, args).throwEx();
    }

}
