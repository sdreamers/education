package com.sixing.base.utils;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion;
import org.codehaus.jackson.type.JavaType;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//import org.apache.commons.collections4.map.ListOrderedMap;

public class JsonUtils {

    /**
     * 对象转换成JSON字符串
     * 
     * @param obj 对象实例
     * @return JSON字符串
     */
    public static <T> String toJson(T obj) {
        if (obj == null) {
            return null;
        }
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.setSerializationInclusion(Inclusion.NON_NULL);
            return objectMapper.writeValueAsString(obj);
        }
        catch (JsonGenerationException e) {
            e.printStackTrace();
        }
        catch (JsonMappingException e) {
            e.printStackTrace();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * JSON字符串转换成对象
     * 
     * @param jsonString JSON字符串
     * @param element 要转换成的对象
     * @return 转换成功的对象实例
     */
    public static <T> T toObject(String jsonString, Class<T> element) {
        if (StringUtils.isBlank(jsonString)) {
            return null;
        }
        try {
            return new ObjectMapper().readValue(jsonString, element);
        }
        catch (JsonParseException e) {
            e.printStackTrace();
        }
        catch (JsonMappingException e) {
            e.printStackTrace();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Object toObject(String jsonString, Class<?> collection, Class<?> element) {
        if (StringUtils.isBlank(jsonString)) {
            return null;
        }
        ObjectMapper objectMapper = new ObjectMapper();
        JavaType javaType = objectMapper.getTypeFactory().constructParametricType(collection, element);
        try {
            return objectMapper.readValue(jsonString, javaType);
        }
        catch (JsonParseException e) {
            e.printStackTrace();
        }
        catch (JsonMappingException e) {
            e.printStackTrace();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     *
     * json转换map
     * @param jsonStr json字符串
     * @return Map<String,Object> 集合
     */
    public static Map<String, Object> toMap(String jsonStr){
        Map map = new HashMap();
        JSONObject json = JSONObject.fromObject(jsonStr);
        //解析最外层
        for(Object key : json.keySet()){
            Object val = json.get(key);
            //如果是数组，继续解析
            if(val instanceof JSONArray){
                List<Map<String, Object>> list = new ArrayList<Map<String,Object>>();
                for (Object json2 : ((JSONArray) val)) {
                    list.add(toMap(json2.toString()));
                }
                map.put(key.toString(), list);
            } else {
                map.put(key.toString(), val);
            }
        }
        return map;
    }

}