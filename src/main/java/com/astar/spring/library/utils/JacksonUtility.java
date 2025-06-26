package com.astar.spring.library.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import jakarta.annotation.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

public abstract class JacksonUtility {

    private static final Logger LOGGER = LoggerFactory.getLogger(JacksonUtility.class);
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private static final XmlMapper XML_MAPPER = new XmlMapper();

    /**
     * @param obj
     * @param isPrettyPrint
     * @return
     */
    public static String objectToJSONString(
            Object obj,
            boolean isPrettyPrint,
            @Nullable Class<?> viewLevel
    ) throws JsonProcessingException {

        try {
            ObjectWriter writer;

            if (viewLevel != null) {
                writer = isPrettyPrint
                        ? OBJECT_MAPPER.writerWithView(viewLevel).withDefaultPrettyPrinter()
                        : OBJECT_MAPPER.writerWithView(viewLevel);
            } else {
                writer = isPrettyPrint
                        ? OBJECT_MAPPER.writerWithDefaultPrettyPrinter()
                        : OBJECT_MAPPER.writer();
            }

            return "\n" + writer.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            LOGGER.error("Failed to convert object of type {} to JSON",
                         obj != null ? obj.getClass().getName() : "null");
            LOGGER.debug("<1> Serialization exception:", e);
            throw e;
        }
    }

    public static String objectToXMLString(Object obj) throws JsonProcessingException {
        try {
            return XML_MAPPER.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            LOGGER.error("Failed to convert object of type {} to XML",
                         obj != null ? obj.getClass().getName() : "null");
            LOGGER.debug("<2> Serialization exception:", e);
            throw e;
        }
    }

    public static byte[] objectToXMLBytes(Object obj) throws JsonProcessingException {
        return XML_MAPPER.writeValueAsBytes(obj);
    }


    public static <T> T JSONStringToObject(
            String requestBody, Class<T> clazz) throws JsonProcessingException {
        return OBJECT_MAPPER.readValue(requestBody, clazz);
    }
}
