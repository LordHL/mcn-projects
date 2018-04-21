package com.hiekn.boot.autoconfigure.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTCreator;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.google.common.collect.Maps;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.UnsupportedEncodingException;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import java.util.Objects;

public class JwtToken {

    private JwtProperties jwtProperties;

    public JwtToken(JwtProperties jwtProperties) {
        this.jwtProperties = jwtProperties;
    }

    public String createToken(Object identifier) {
        Map<String,Object> data = Maps.newHashMap();
        data.put("userId",identifier.toString());
        return createToken(data);
    }

    public String createToken(Map<String,Object> data) {
        if(Objects.isNull(data) || data.isEmpty())return null;
        //签发时间
        Date iaDate = new Date();

        //过期时间
        Calendar nowTime = Calendar.getInstance();
        nowTime.add(Calendar.DATE, jwtProperties.getExpireDate());
        Date expireDate = nowTime.getTime();

        Map<String,Object> map = Maps.newHashMap();
        map.put("alg","HS256");
        map.put("typ","JWT");
        //Integer Double Date Boolean String
        JWTCreator.Builder builder = JWT.create()
                .withHeader(map)
                .withExpiresAt(expireDate)
                .withIssuedAt(iaDate)
                .withIssuer(jwtProperties.getIssuer());
        data.forEach((k,v) -> builder.withClaim(k,v.toString()));
        return builder.sign(getAlgorithm());
    }

    private String createNewToken(DecodedJWT jwt) {
        Date issuedAt = jwt.getIssuedAt();
        if(System.currentTimeMillis() - issuedAt.getTime() > jwtProperties.getRefreshInterval()){
            Map<String, Claim> claims = jwt.getClaims();
            Map<String,Object> data = Maps.newHashMap();
            claims.forEach((k,v) -> {
                if(!"iat".equals(k) && !"exp".equals(k) && !"iss".equals(k)){
                    data.put(k,v.asString());
                }
            });
            return createToken(data);
        }
        return null;
    }

    private Algorithm getAlgorithm(){
        try {
            return Algorithm.HMAC256(jwtProperties.getSecretKey());
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("The Secret Character Encoding is not supported");
        }
    }


    public DecodedJWT checkToken(String token){
        JWTVerifier verifier = JWT.require(getAlgorithm()).build();
        DecodedJWT jwt = verifier.verify(token);

        //通过之后，检查是否要返回新token
        String newToken = createNewToken(jwt);
        if(newToken != null){
            HttpServletResponse response = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getResponse();
            response.setHeader("Authorization",newToken);
        }
        return jwt;
    }

    public Integer getUserIdAsInt() {
        return getValueAsInt("userId");
    }
    public String getUserIdAsString() {
        return getValueByKey("userId");
    }
    public Integer getValueAsInt(String key) {
        return getValueByKey(key,Integer.class);
    }
    public Long getValueAsLong(String key) {
        return getValueByKey(key,Long.class);
    }
    public Double getValueAsDouble(String key) {
        return getValueByKey(key,Double.class);
    }
    public Boolean getValueAsBoolean(String key) {
        return getValueByKey(key,Boolean.class);
    }

    public String getValueByKey(String key) {
        return getValueByKey(key,String.class);
    }

    private <T> T getValueByKey(String key,Class<T> cls) {
        return getClaim(key).as(cls);
    }

    private Claim getClaim(String key) {
        return checkToken(getToken()).getClaim(key);
    }

    public String getToken(){
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        String authorization = request.getHeader("Authorization");
        if(StringUtils.isBlank(authorization)){
            return null;
        }
        String[] str = authorization.split(" ");
        return str.length == 2?str[1]:null;
    }

}
