package com.hiekn.boot.autoconfigure.jersey;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(
        prefix = "jersey.client"
)
public class JerseyClientProperties {

    private Integer connectTimeout = 2*60000;//default 60000ms 2min
    private Integer readTimeout = 2*60000;//default 60000ms 2min

    public Integer getConnectTimeout() {
        return connectTimeout;
    }

    public void setConnectTimeout(Integer connectTimeout) {
        this.connectTimeout = connectTimeout;
    }

    public Integer getReadTimeout() {
        return readTimeout;
    }

    public void setReadTimeout(Integer readTimeout) {
        this.readTimeout = readTimeout;
    }
}
