package commons.support.spring;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ResponseModelControllerAdviceConfigurer {

    @Bean
    public ResponseModelRestControllerAdvice responseModelRestControllerAdvice() {
        return new ResponseModelRestControllerAdvice();
    }

}
