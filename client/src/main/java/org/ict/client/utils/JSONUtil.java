package org.ict.client.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;

import java.util.List;

public class JSONUtil {
    private final static ObjectMapper objectMapper = new ObjectMapper();
    public static <T> T parse(String message, Class<T> clazz) throws JsonProcessingException {
        return objectMapper.readValue(message, clazz);
    }

    public static String stringify(Object object) throws JsonProcessingException {
        return objectMapper.writeValueAsString(object);
    }

    public static <T> List<T> parseList(String message, Class<T> clazz) throws JsonProcessingException {
        TypeFactory typeFactory = objectMapper.getTypeFactory();
        return objectMapper.readValue(message, typeFactory.constructCollectionType(List.class, clazz));
    }
}
