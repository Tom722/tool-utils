package com.scc.toolutils.es.config;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.Assert;

import java.util.Arrays;
import java.util.Objects;

/**
 * @author : scc
 * @date : 2023/01/05
 **/
@Configuration
@ConfigurationProperties(prefix = "es")
@Slf4j
@Data
public class ElasticsearchConfig {
    private String hosts;
    private String username;
    private String password;
    private int connectTimeout;
    private int socketTimeout;
    private int connectionRequestTimeout;

    @Bean
    public RestHighLevelClient client() {
        log.info("elasticsearch init start ");
        Assert.hasLength(hosts, "elasticsearch host is null");
        HttpHost[] httpHosts = Arrays.stream(hosts.split(",")).map(host -> {
            Assert.hasLength(host, "elasticsearch host is null");
            String[] h = host.split(":");
            if (h.length != 2) throw new RuntimeException("host ip or port formate is error");
            return new HttpHost(h[0], Integer.parseInt(h[1]));
        }).filter(Objects::nonNull).toArray(HttpHost[]::new);
        RestClientBuilder builder = RestClient.builder(httpHosts);
        CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
        credentialsProvider.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials(username, password));
        builder.setHttpClientConfigCallback(f -> f.setDefaultCredentialsProvider(credentialsProvider))
                .setRequestConfigCallback(r -> {
                    r.setConnectTimeout(connectTimeout);
                    r.setSocketTimeout(socketTimeout);
                    r.setConnectionRequestTimeout(connectionRequestTimeout);
                    return r;
                });
        log.info("elasticsearch init stop ");
        return new RestHighLevelClient(builder);
    }
}