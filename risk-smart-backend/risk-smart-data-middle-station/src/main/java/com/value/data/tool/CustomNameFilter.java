package com.value.data.tool;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.alibaba.fastjson2.filter.NameFilter;

import java.util.HashMap;
import java.util.Map;

public class CustomNameFilter implements NameFilter {


    private Map<String, String> fieldMapping;

    public CustomNameFilter(Map<String, String> fieldMapping) {
        this.fieldMapping = fieldMapping;
    }


//    @Override
    public String process(Object object, String name, Object value) {
        // 根据字段映射进行转换
        if (fieldMapping.containsKey(name)) {
            return fieldMapping.get(name);
        }

        // 自定义属性转换逻辑
        //if ("valueA".equals(value)) {
            //return "NewValueA";
        //}

        // 保持原样
        return name;
    }



    public static void main(String[] args) {
        // JSON数据

//        Map<String,String> param = new HashMap<>();
//        param.put("sourceFieldA","valueA");
//
//        Map<String,String> param2 = new HashMap<>();
//        param.put("sourceFieldA","valueA");
        // 配置字段映射规则
        Map<String, String> fieldMapping = new HashMap<>();
        fieldMapping.put("sourceFieldA", "targetFieldX");
        fieldMapping.put("sourceFieldB", "targetFieldY");


        // JSON数据
        String jsonString = "{\"level1\":{\"sourceFieldA\":\"valueA\",\"level2\":{\"sourceFieldA\":\"valueB\"}}}";

        String newJsonStr = "{\"level1\":{\"targetFieldX\":\"valueA\",\"level2\":{\"targetFieldX\":\"valueB\"},\"level3\":[{\"targetFieldX\":\"valueB\"}]}}";
        // 解析JSON数据
        JSONObject originalObject = JSON.parseObject(newJsonStr);


        NameFilter nameFilter = (object, name, value) -> fieldMapping.getOrDefault(name, name);

        // 应用NameFilter并生成新的JSON字符串
        String newJsonString = JSON.toJSONString(originalObject, nameFilter);
        System.out.println(newJsonString);
        // 反序列化并进行字段映射和属性转换
        String jsonStr = JSON.toJSONString(originalObject, new CustomNameFilter(fieldMapping));

        System.out.println(jsonStr);
    }
}
