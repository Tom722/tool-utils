package com.scc.toolutils.json;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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
    
    // 测试用例--> Json字符串中有某个对象的值为集合，并且集合类型为Object
    public void test1(){
        JSONObject jsonObject = new JSONObject();
        List<Object> datas = new ArrayList<>();
        datas.add("2022-03-01");
        datas.add("实际出勤人工时");
        datas.add(20.9);
        jsonObject.put("datas", datas);
        try {
            TestObject myJsonObject = objectMapper.readValue(jsonObject.toString(), TestObject.class);
            System.out.println("解析后的Java对象： " + myJsonObject);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    // 测试用例-> 数组类型的Json字符串解析
    public void test2(){
        String jsonArrayStr = "[\"1\",\"2\",\"3\"]";
        String json
                = "[{\r\n"
                + "\"name\" : \"Jack Ryan\",\r\n"
                + "\"id\" : \"2019071075\",\r\n"
                + "\"school\" : \"St. Jude's School\",\r\n"
                + "\"section\" : \"B\"\r\n"
                + "}]";
        List<Student> list = JsonArrayStrToJavaObjectList(json, Student.class);
        for (Object o : list) {
            System.out.println("类型：" + o.getClass().getName());
            System.out.println("值：" + o);
        }
    }

    /**
     * JsonArray类型字符串转为Java对象集合
     * @param jsonArrayStr json字符串
     * @param tClass 对象类型
     * @param <T> 定义对象泛型
     * @return
     */
    public static  <T> List<T> JsonArrayStrToJavaObjectList(String jsonArrayStr, Class<T> tClass){
        return JSONArray.parseArray(jsonArrayStr, tClass);
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
class TestObject{
    List<Object> datas;

    public List<Object> getDatas() {
        return datas;
    }

    public void setDatas(List<Object> datas) {
        this.datas = datas;
    }
}