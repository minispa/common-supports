package commons.support.spring;

import org.hibernate.validator.HibernateValidator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.validation.Validation;
import javax.validation.Validator;

import static org.hibernate.validator.BaseHibernateValidatorConfiguration.FAIL_FAST;

@Configuration
public class ValidatorConfigurer {

    @Bean
    public Validator validator() {
        return Validation.byProvider(HibernateValidator.class)
                .configure()
                .addProperty(FAIL_FAST, "true")
                .buildValidatorFactory().getValidator();
    }

}
