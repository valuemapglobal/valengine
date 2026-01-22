package com.value.data.common.jackson;

import cn.hutool.json.JSONUtil;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.json.JsonMapper;

import java.io.IOException;

/**
 * 字符串转JSON序列化器
 *
 * @author vlauemap team
 * @since 2026/01/22
 */
public class String2JSONSerializer extends JsonSerializer<String> {
    private final JsonMapper jsonMapper = new JsonMapper();
    @Override
    public void serialize(String value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        if (!JSONUtil.isJson(value)) {
            throw new RuntimeException("json类型异常");
        }
        final JsonNode jsonNode = jsonMapper.readTree(value);
        gen.writeTree(jsonNode);
    }
}