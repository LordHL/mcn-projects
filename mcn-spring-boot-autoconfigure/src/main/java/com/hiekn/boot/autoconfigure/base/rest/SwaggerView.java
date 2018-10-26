package com.hiekn.boot.autoconfigure.base.rest;

import com.hiekn.boot.autoconfigure.base.util.CommonHelper;
import com.hiekn.boot.autoconfigure.jersey.JerseySwaggerProperties;
import org.glassfish.jersey.server.mvc.Template;
import org.springframework.beans.factory.annotation.Autowired;

import javax.inject.Singleton;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Singleton
@Path("/")
@Produces(MediaType.TEXT_HTML)
public class SwaggerView {

    @Autowired
    private JerseySwaggerProperties jerseySwaggerProperties;

    @Path("/Swagger.html")
    @GET
    @Template(name = "/index")
    public Map<String, Object> indexView() {
        Map<String, Object> map = new HashMap<>();
        if(Objects.nonNull(jerseySwaggerProperties.getHost())) {
            map.put("host",jerseySwaggerProperties.getHost());
        }else{
            map.put("host",jerseySwaggerProperties.getIp()+":"+jerseySwaggerProperties.getPort());
        }
        map.put("path",CommonHelper.parsePath(jerseySwaggerProperties.getBasePath()));
        map.put("cdn","/swagger/");
        return map;
    }

}
