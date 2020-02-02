package commons.support.spring;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodReturnValueHandler;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@Configuration
public class ResponseModelWebMvcConfigurer implements InitializingBean {

    @Resource
    private RequestMappingHandlerAdapter requestMappingHandlerAdapter;

    @Bean
    public ResponseModelMethodReturnValueHandler responseModelMethodReturnValueHandler() {
        return new ResponseModelMethodReturnValueHandler(requestMappingHandlerAdapter.getMessageConverters());
    }

    @Override
    public void afterPropertiesSet() {
        List<HandlerMethodReturnValueHandler> returnValueHandlers = new ArrayList<>();
        returnValueHandlers.add(responseModelMethodReturnValueHandler());
        returnValueHandlers.addAll(requestMappingHandlerAdapter.getReturnValueHandlers());
        requestMappingHandlerAdapter.setReturnValueHandlers(returnValueHandlers);
    }
}
