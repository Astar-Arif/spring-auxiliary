package com.astar.spring.library.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import jakarta.annotation.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class JacksonUtility {

    private static final Logger LOGGER = LoggerFactory.getLogger(JacksonUtility.class);
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    /**
     * @param obj
     * @param isPrettyPrint
     * @return
     */
    public static String objectToJSONString(
            Object obj,
            boolean isPrettyPrint,
            @Nullable Class<?> viewLevel
    ) {

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
            LOGGER.debug("Serialization exception:", e);
            return null;
        }
    }


    public static <T> T stringToObject(
            String requestBody, Class<T> clazz) throws JsonProcessingException {
        return OBJECT_MAPPER.readValue(requestBody, clazz);
    }
}
