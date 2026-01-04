package com.value.data.common.jackson;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;

import java.io.IOException;

/**
 * @author Vida
 * @date 2025年04月24日 9:21
 * @description
 */
public class JSON2StringDeserializer extends JsonDeserializer<String>{
    @Override
    public String deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        final JsonNode jsonNode = ctxt.readTree(p);
        return jsonNode.toString();
    }
}
