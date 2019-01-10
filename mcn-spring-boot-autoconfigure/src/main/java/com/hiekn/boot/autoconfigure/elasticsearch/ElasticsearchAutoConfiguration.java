package com.hiekn.boot.autoconfigure.elasticsearch;

import com.google.common.collect.Lists;
import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

import java.util.List;

@Configuration
@ConditionalOnClass(RestClientBuilder.class)
@EnableConfigurationProperties({ElasticsearchProperties.class})
public class ElasticsearchAutoConfiguration {

    private ElasticsearchProperties elasticsearchProperties;

    public ElasticsearchAutoConfiguration(ElasticsearchProperties elasticsearchProperties) {
        this.elasticsearchProperties = elasticsearchProperties;
    }

    @Bean
    @ConditionalOnClass(RestHighLevelClient.class)
    @ConditionalOnMissingBean(type = {"org.elasticsearch.client.RestHighLevelClient"})
    public RestHighLevelClient restHighClient(){
        return new RestHighLevelClient(restClientBuilder());
    }

    @Bean
    @ConditionalOnMissingBean(type = {"org.elasticsearch.client.RestClientBuilder"})
    public RestClientBuilder restClientBuilder(){
        List<HttpHost> hosts = Lists.newArrayList();
        for (String host : elasticsearchProperties.getHost()) {
            String[] ipPort = StringUtils.tokenizeToStringArray(host,":");
            hosts.add(new HttpHost(ipPort[0], Integer.valueOf(ipPort[1])));
        }
        return RestClient.builder(hosts.toArray(new HttpHost[hosts.size()]));
    }

    @Bean
    @ConditionalOnClass(name = {"org.elasticsearch.client.RestClient"})
    @ConditionalOnMissingBean(type = {"org.elasticsearch.client.RestClient"})
    public RestClient restClient(){
        return restClientBuilder().build();
    }

}