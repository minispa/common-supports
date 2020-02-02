package com.minispa.spring;

import com.minispa.DataModel;
import com.minispa.StatusCode;
import org.springframework.core.MethodParameter;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.method.support.HandlerMethodReturnValueHandler;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.springframework.web.servlet.HandlerMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;

public class ResponseModelMethodReturnValueHandler implements HandlerMethodReturnValueHandler {

    private List<HttpMessageConverter<?>> messageConverters;
    private List<MediaType> allSupportedMediaType;
    private String defaultDataModelCode = String.valueOf(StatusCode.OK.value());

    public ResponseModelMethodReturnValueHandler(List<HttpMessageConverter<?>> messageConverters) {
        this.messageConverters = messageConverters;
        this.allSupportedMediaType = getAllSupportedMediaTypes(messageConverters);
    }

    public ResponseModelMethodReturnValueHandler(List<HttpMessageConverter<?>> messageConverters, String defaultDataModelCode) {
        this.messageConverters = messageConverters;
        this.allSupportedMediaType = getAllSupportedMediaTypes(messageConverters);
        this.defaultDataModelCode = defaultDataModelCode;
    }

    @Override
    public boolean supportsReturnType(MethodParameter returnType) {
        return (AnnotatedElementUtils.hasAnnotation(returnType.getContainingClass(), ResponseModel.class) ||
                returnType.hasMethodAnnotation(ResponseModel.class));
    }


    @Override
    public void handleReturnValue(Object returnValue, MethodParameter returnType, ModelAndViewContainer mavContainer, NativeWebRequest webRequest) throws Exception {
        mavContainer.setRequestHandled(true);
        returnValue = DataModel.builder().code(defaultDataModelCode).data(returnValue).build();
        this.writeMessageConverters(returnValue, returnType, webRequest);
    }

    private <T> void writeMessageConverters(T returnValue, MethodParameter returnType, NativeWebRequest webRequest) throws IOException, HttpMediaTypeNotAcceptableException {
        ServletServerHttpRequest inputMessage = createInputMessage(webRequest);
        ServletServerHttpResponse outputMessage = createOutputMessage(webRequest);

        Class<?> returnValueClass = returnValue.getClass();
        final HttpServletRequest servletRequest = inputMessage.getServletRequest();

        List<MediaType> requestedMediaTypes = getAcceptableMediaTypes(servletRequest);
        List<MediaType> producibleMediaTypes = getProducibleMediaTypes(servletRequest, returnValueClass);

        Set<MediaType> compatibleMediaTypes = new LinkedHashSet<>();
        for(MediaType requestedType : requestedMediaTypes) {
            for (MediaType producibleType : producibleMediaTypes) {
                if(requestedType.isCompatibleWith(producibleType)) {
                    compatibleMediaTypes.add(getMostSpecificMediaType(requestedType, producibleType));
                }
            }
        }

        if(compatibleMediaTypes.isEmpty()) {
            throw new HttpMediaTypeNotAcceptableException(producibleMediaTypes);
        }

        List<MediaType> mediaTypes = new ArrayList<>(compatibleMediaTypes);
        MediaType.sortBySpecificityAndQuality(mediaTypes);

        MediaType selectedMediaType = null;
        for (MediaType mediaType : mediaTypes) {
            if(mediaType.isConcrete()) {
                selectedMediaType = mediaType;
                break;
            } else if(mediaType.equals(MediaType.ALL) || mediaType.equals("application")) {
                selectedMediaType = MediaType.APPLICATION_OCTET_STREAM;
                break;
            }
        }

        if(selectedMediaType != null) {
            selectedMediaType = selectedMediaType.removeQualityValue();
            for (HttpMessageConverter<?> messageConverter : messageConverters) {
                if(messageConverter.canWrite(returnValueClass, selectedMediaType)) {
                    ((HttpMessageConverter<T>) messageConverter).write(returnValue, selectedMediaType, outputMessage);
                    return;
                }
            }
        }
        throw new HttpMediaTypeNotAcceptableException(allSupportedMediaType);

    }

    private MediaType getMostSpecificMediaType(MediaType acceptType, MediaType produceType) {
        MediaType produceTypeToUse = produceType.copyQualityValue(acceptType);
        return (MediaType.SPECIFICITY_COMPARATOR.compare(acceptType, produceTypeToUse) <= 0 ? acceptType : produceTypeToUse);
    }

    private List<MediaType> getProducibleMediaTypes(HttpServletRequest request, Class<?> returnValueClass) {
        Set<MediaType> mediaTypes = (Set<MediaType>) request.getAttribute(HandlerMapping.PRODUCIBLE_MEDIA_TYPES_ATTRIBUTE);
        if(!CollectionUtils.isEmpty(mediaTypes)) {
            return new ArrayList<>(mediaTypes);
        } else if(!getAllSupportedMediaTypes(messageConverters).isEmpty()) {
            List<MediaType> result = new ArrayList<>();
            for (HttpMessageConverter<?> converter : messageConverters) {
                if(converter.canWrite(returnValueClass, null)) {
                    result.addAll(converter.getSupportedMediaTypes());
                }
            }
            return result;
        } else {
            return Collections.singletonList(MediaType.ALL);
        }
    }

    private List<MediaType> getAcceptableMediaTypes(HttpServletRequest request) throws HttpMediaTypeNotAcceptableException {
        List<MediaType> mediaTypes = resolveMediaTypes(new ServletWebRequest(request));
        return (mediaTypes.isEmpty() ? Collections.singletonList(MediaType.ALL) : mediaTypes);
    }

    private List<MediaType> resolveMediaTypes(NativeWebRequest webRequest) throws HttpMediaTypeNotAcceptableException {
        String acceptHeader = webRequest.getHeader("Accept");
        try {
            if(StringUtils.hasText(acceptHeader)) {
               List<MediaType> mediaTypes = MediaType.parseMediaTypes(acceptHeader);
               MediaType.sortBySpecificityAndQuality(mediaTypes);
               return mediaTypes;
            }
        } catch (Exception e) {
            throw new HttpMediaTypeNotAcceptableException("Could not parse accept header [" + acceptHeader + "]: " + e.getMessage());
        }
        return Collections.emptyList();
    }

    private ServletServerHttpResponse createOutputMessage(NativeWebRequest webRequest) {
        final HttpServletResponse servletResponse = webRequest.getNativeResponse(HttpServletResponse.class);
        return new ServletServerHttpResponse(servletResponse);
    }

    private ServletServerHttpRequest createInputMessage(NativeWebRequest webRequest) {
        final HttpServletRequest servletRequest = webRequest.getNativeRequest(HttpServletRequest.class);
        return new ServletServerHttpRequest(servletRequest);
    }

    private static List<MediaType> getAllSupportedMediaTypes(List<HttpMessageConverter<?>> messageConverters) {
        Set<MediaType> allSupportedMediaTypes = new LinkedHashSet<>();
        final Iterator<HttpMessageConverter<?>> iterator = messageConverters.iterator();
        while(iterator.hasNext()) {
            final HttpMessageConverter<?> next = iterator.next();
            allSupportedMediaTypes.addAll(next.getSupportedMediaTypes());
        }

        List<MediaType> mediaTypes = new ArrayList<>(allSupportedMediaTypes);
        MediaType.sortBySpecificity(mediaTypes);
        return Collections.unmodifiableList(mediaTypes);
    }
}
