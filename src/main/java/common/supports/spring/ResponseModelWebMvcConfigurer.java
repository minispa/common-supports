package common.supports.spring;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import common.supports.DataModel;
import common.supports.StatusCode;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.support.HandlerMethodReturnValueHandler;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.handler.HandlerExceptionResolverComposite;
import org.springframework.web.servlet.mvc.method.annotation.*;

import javax.annotation.Resource;
import javax.servlet.RequestDispatcher;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Configuration
public class ResponseModelWebMvcConfigurer implements InitializingBean {

    @Resource
    private RequestMappingHandlerAdapter requestMappingHandlerAdapter;

    @Resource
    private HandlerExceptionResolver handlerExceptionResolver;

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
        List<HandlerMethodReturnValueHandler> returnValueHandlers = new ArrayList<>(preHandlers);
        returnValueHandlers.add(new ResponseModelMethodReturnValueHandler(requestMappingHandlerAdapter.getMessageConverters(), String.valueOf(StatusCode.OK.value())));
        returnValueHandlers.addAll(lastHandlers);
        requestMappingHandlerAdapter.setReturnValueHandlers(returnValueHandlers);

        if (handlerExceptionResolver instanceof HandlerExceptionResolverComposite) {
            HandlerExceptionResolverComposite handlerExceptionResolverComposite = (HandlerExceptionResolverComposite) this.handlerExceptionResolver;
            List<HandlerExceptionResolver> exceptionResolvers = handlerExceptionResolverComposite.getExceptionResolvers();
            for (HandlerExceptionResolver resolver : exceptionResolvers) {
                if (resolver instanceof ExceptionHandlerExceptionResolver) {
                    ((ExceptionHandlerExceptionResolver) resolver).setReturnValueHandlers(returnValueHandlers);
                }
            }
        }
    }

    /**
     * 改写默认springboot报错的报文格式
     *
     * @return
     */
//    @Bean
//    @Order(Ordered.HIGHEST_PRECEDENCE)
//    public DefaultErrorAttributes defaultErrorAttributes() {
//        return new DefaultErrorAttributes() {
//            @Override
//            public Map<String, Object> getErrorAttributes(WebRequest webRequest, ErrorAttributeOptions options) {
//                Object errorCode = webRequest.getAttribute(RequestDispatcher.ERROR_STATUS_CODE, RequestAttributes.SCOPE_REQUEST);
//                DataModel<?> dataModel = new DataModel<>();
//                dataModel.setCode(Objects.toString(errorCode, String.valueOf(HttpStatus.INTERNAL_SERVER_ERROR.value())));
//                dataModel.setMessage(HttpStatus.valueOf(Integer.parseInt(dataModel.getCode())).getReasonPhrase());
//                return new Gson().fromJson(new Gson().toJson(dataModel), new TypeToken<Map<String, Object>>(){}.getType());
//            }
//        };
//    }

}
