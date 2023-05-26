package com.scc.toolutils.http;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.http.Header;
import org.apache.http.HeaderElement;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.IOException;

/**
 * @author : scc
 * @date : 2023/05/26
 **/
public class HttpUtils {

    /**
     * 获取response header中Content-Disposition中的filename值
     *
     * @param response
     * @return
     */
    public static String getFileName(HttpResponse response) {
        Header contentHeader = response.getFirstHeader("Content-Disposition");
        String filename = null;
        if (contentHeader != null) {
            HeaderElement[] values = contentHeader.getElements();
            if (values.length == 1) {
                NameValuePair param = values[0].getParameterByName("filename");
                if (param != null) {
                    try {
                        //此处根据具体编码来设置
                        filename = new String(param.getValue().toString().getBytes("ISO-8859-1"), "GBK");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return filename;
    }

    /**
     * 发送post请求，参数类型为Json
     * @return
     */
    public static JSONObject sendPostWithJson() {
        //定义发送数据
        JSONObject param = new JSONObject();
        param.put("username", "test");
        param.put("age", "18");
        //定义接收数据
        JSONObject result = new JSONObject();

        String url = "xxxxxxx";
        HttpPost httpPost = new HttpPost(url);
        CloseableHttpClient client = HttpClients.createDefault();
        //请求参数转JOSN字符串
        StringEntity entity = new StringEntity(param.toString(), "UTF-8");
        entity.setContentEncoding("UTF-8");
        entity.setContentType("application/json");
        httpPost.setEntity(entity);
        try {
            HttpResponse response = client.execute(httpPost);
            if (response.getStatusLine().getStatusCode() == 200) {
                result = JSON.parseObject(EntityUtils.toString(response.getEntity(), "UTF-8"));
            }
        } catch (IOException e) {
            e.printStackTrace();
            result.put("error", "连接错误！");
        }
        //关闭连接
        try {
            client.close();
        } catch (Exception e) {
            System.out.println(e.getMessage());

        }
        return result;
    }
}
