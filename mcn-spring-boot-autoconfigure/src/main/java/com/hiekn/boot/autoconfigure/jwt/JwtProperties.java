package com.hiekn.boot.autoconfigure.jwt;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.security.Security;

@ConfigurationProperties("jwt")
public class JwtProperties {
    private String secretKey = "SECRET";
    private String issuer = "hiekn";
    private Long refreshInterval = 24*60*60*1000L;
    private Integer expireDate = 7;
    private Security security = new Security();

    public String getSecretKey() {
        return secretKey;
    }

    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }

    public String getIssuer() {
        return issuer;
    }

    public void setIssuer(String issuer) {
        this.issuer = issuer;
    }

    public Long getRefreshInterval() {
        return refreshInterval;
    }

    public void setRefreshInterval(Long refreshInterval) {
        this.refreshInterval = refreshInterval;
    }

    public Integer getExpireDate() {
        return expireDate;
    }

    public void setExpireDate(Integer expireDate) {
        this.expireDate = expireDate;
    }

    public Security getSecurity() {
        return security;
    }

    public void setSecurity(Security security) {
        this.security = security;
    }

    public static class Security{
        private Boolean login = false;
        private String[] ignoreUrls = {"/api/swagger.json"};

        public Boolean getLogin() {
            return login;
        }

        public void setLogin(Boolean login) {
            this.login = login;
        }

        public String[] getIgnoreUrls() {
            return ignoreUrls;
        }

        public void setIgnoreUrls(String[] ignoreUrls) {
            this.ignoreUrls = ignoreUrls;
        }
    }
}
