package com.scc.toolutils.json;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

/**
 * @author : scc
 * @date : 2023/05/26
 **/
public class JsonUtils {
    public static ObjectMapper objectMapper = new ObjectMapper();

    public JsonUtils() {
    }

    public void init() {
        // 配置Json字符串中属性，在要转化对象的不存在时不报错
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        // 配置时间序列（不使用时间序列）
        objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
    }

    /**
     * 转换Json到指定的Java对象-------待验证
     * @param tClass
     * @param <T>
     * @return
     * @throws JsonProcessingException
     */
    public static <T> T JsonToObject(Class<T> tClass) throws JsonProcessingException {
        // JSON string
        String json
                = "{\r\n"
                + "\"name\" : \"Jack Ryan\",\r\n"
                + "\"id\" : \"2019071075\",\r\n"
                + "\"school\" : St. Jude's School,\r\n"
                + "\"section\" : B\r\n"
                + "}";
        Student effectiveJava
                = objectMapper.readValue(json, Student.class);
        System.out.println("Input json string: " + json);
        System.out.println("Generated java class: " + effectiveJava);
        return (T)effectiveJava;
    }
}

class Student {
    private String name;
    private String id;
    private String school;

    public Student() {

    }

    public Student(String name, String id, String school) {
        this.name = name;
        this.id = id;
        this.school = school;
    }

    // getters and setters
    public String getID() {
        return id;
    }

    public void setID(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String id) {
        this.name = name;
    }

    public String getSchool() {
        return school;
    }

    public void setSchool(String id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "Student{"
                + "name='" + name + '\'' + ", id='" + id + '\''
                + ", school='" + school + '\'' + '}';
    }
}
