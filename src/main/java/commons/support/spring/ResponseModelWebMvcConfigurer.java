package commons.support.spring;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodReturnValueHandler;
import org.springframework.web.servlet.mvc.method.annotation.AsyncTaskMethodReturnValueHandler;
import org.springframework.web.servlet.mvc.method.annotation.CallableMethodReturnValueHandler;
import org.springframework.web.servlet.mvc.method.annotation.DeferredResultMethodReturnValueHandler;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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
        List<HandlerMethodReturnValueHandler> handlers = requestMappingHandlerAdapter.getReturnValueHandlers();
        List<HandlerMethodReturnValueHandler> preHandlers = handlers.stream()
                .filter(handler -> (handler instanceof CallableMethodReturnValueHandler
                || handler instanceof AsyncTaskMethodReturnValueHandler
                || handler instanceof DeferredResultMethodReturnValueHandler))
                .collect(Collectors.toList());
        List<HandlerMethodReturnValueHandler> lastHandlers = handlers.stream()
                .filter(handler -> !(handler instanceof CallableMethodReturnValueHandler
                        || handler instanceof AsyncTaskMethodReturnValueHandler
                        || handler instanceof DeferredResultMethodReturnValueHandler))
                .collect(Collectors.toList());
        List<HandlerMethodReturnValueHandler> returnValueHandlers = new ArrayList<>();
        returnValueHandlers.addAll(preHandlers);
        returnValueHandlers.add(responseModelMethodReturnValueHandler());
        returnValueHandlers.addAll(lastHandlers);
        requestMappingHandlerAdapter.setReturnValueHandlers(returnValueHandlers);
    }
}
