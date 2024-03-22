package com.example.demo1.util.extractor;

import com.example.demo1.util.response.BodyResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.IOException;
import java.io.Reader;

@Data
@AllArgsConstructor
public class Json<T> implements BodyResponse {
    T inner;

    public static <T> Json<T> fromReader(Reader reader, Class<T> objectClass) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        return new Json<>(mapper.readValue(reader, objectClass));
    }

    @Override
    public String getContentType() {
        return "application/json";
    }

    @Override
    public byte[] getContent() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());

        byte[] jsonValue;
        try {
            jsonValue = mapper.writeValueAsBytes(inner);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        return jsonValue;
    }
}
