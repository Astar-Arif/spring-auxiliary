package com.astar.spring.library.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import jakarta.annotation.Nullable;
import org.modelmapper.Condition;
import org.modelmapper.ModelMapper;
import org.modelmapper.spi.MappingContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public abstract class ObjectMappingUtility {

    private static final Logger LOGGER = LoggerFactory.getLogger(ObjectMappingUtility.class);
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private static final ModelMapper MODEL_MAPPER = new ModelMapper();
    private static final XmlMapper XML_MAPPER = new XmlMapper();

    /**
     * @param obj
     * @param isPrettyPrint
     * @return
     */
    public static String objectToJSONString(Object obj, boolean isPrettyPrint, @Nullable Class<?> viewLevel
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
            String JSONString, Class<T> clazz) throws JsonProcessingException {
        return OBJECT_MAPPER.readValue(JSONString, clazz);
    }


    public static <T> T copy(Object from, T target, char choice) {
        if (choice == 'd') MODEL_MAPPER.map(from, target);
        else BeanUtils.copyProperties(from, target);
        return target;
    }

//    TODO STRESS TEST THIS TO SEE IF WANT TO USE JACKSON TO JSON THEN JSON TO OBJECT WHICH IS FASTER
    public static <S, T> T deepCopy(S from, T target, Set<String> ignoredProperties) {
        if (from == null) {
            LOGGER.warn("Source object for deepCopy (with ignoredProperties) is null. Returning target as is.");
            return target;
        }
        if (ignoredProperties == null || ignoredProperties.isEmpty()) {
            return deepCopy(from, target);
        }
        ModelMapper tempModelMapper = new ModelMapper();
        Condition<Object, Object> ignoreCondition = new Condition<Object, Object>() {
            @Override
            public boolean applies(MappingContext<Object, Object> context) {
                String destinationPropertyName = context.getMapping().getDestinationProperties().get(0).getName();
                return !ignoredProperties.contains(destinationPropertyName.toLowerCase());
            }
        };
        tempModelMapper.createTypeMap((Class<S>) from.getClass(), (Class<T>) target.getClass())
                .setPropertyCondition(ignoreCondition);
        tempModelMapper.map(from, target);
        return target;
    }

    public static <T> T deepCopy(Object from, T target,String... ignoredProperties){
        Set<String> ignoredPropertiesSet = new HashSet<>(List.of(ignoredProperties));
        return deepCopy(from, target, ignoredPropertiesSet);
    }

    public static <T> T shallowCopy(Object from, T target, List<String> ignoredProperties) {
        BeanUtils.copyProperties(from, target, ignoredProperties.toArray(new String[0]));
        return target;
    }

    public static <T> T shallowCopy(Object from, T target,String... ignoredProperties){
        BeanUtils.copyProperties(from, target, ignoredProperties);
        return target;
    }
}
