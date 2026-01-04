package com.value.decision.process.common;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class HttpUtil {

    public static String getUrl(String url, Integer userId, String authorizationToken) {

        // 构建完整 URL（包含 Query 参数）
        String completeUrl = url + "?userId=" + userId;

        String responseBody = null;
        // 创建 HttpClient 实例
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {

            // 创建 GET 请求
            HttpGet request = new HttpGet(completeUrl);

            // 设置请求头
            request.setHeader("Authorization", authorizationToken);

            // 发送请求并获取响应
            HttpResponse response = httpClient.execute(request);

            // 获取响应状态码和内容
            int statusCode = response.getStatusLine().getStatusCode();
            responseBody = EntityUtils.toString(response.getEntity());

        } catch (IOException e) {
            e.printStackTrace();
        }
        return responseBody;
    }
}
