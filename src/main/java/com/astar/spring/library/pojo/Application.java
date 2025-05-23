package com.astar.spring.library.pojo;

import jakarta.servlet.http.HttpServletRequest;
import org.apache.juli.logging.Log;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.converter.AbstractHttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;

import java.io.IOException;

public class Application extends AbstractHttpMessageConverter<Object> {

    private static final Logger LOGGER = LoggerFactory.getLogger(Application.class);

    @Override
    protected boolean supports(Class<?> clazz) {
        return false;
    }

    @Override
    protected Object readInternal(
            Class<?> clazz,
            HttpInputMessage inputMessage
    ) throws IOException, HttpMessageNotReadableException {
        return null;
    }

    @Override
    protected void writeInternal(
            Object o,
            HttpOutputMessage outputMessage
    ) throws IOException, HttpMessageNotWritableException {

    }

    public static <T> T decipherRequestBody(HttpServletRequest request, Class<T> clazz){
    return null;

    }
}
