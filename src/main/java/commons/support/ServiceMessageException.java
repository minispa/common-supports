package commons.support;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ServiceMessageException extends RuntimeException {

    private String code;
    private String desc;
    private String[] args;

    @Override
    public String getMessage() {
        StringBuilder sb = new StringBuilder();
        if(code != null) {
            sb.append(" code: [").append(code).append("]");
        }
        if(desc != null) {
            sb.append(" desc: [").append(String.format(desc, args)).append("]");
        }
        return sb.toString();
    }

    public void throwEx() {
        throw this;
    }

}
