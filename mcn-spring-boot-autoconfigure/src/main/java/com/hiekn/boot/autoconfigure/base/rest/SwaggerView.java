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
        Map<String, Object> map = new HashMap();
        String host = this.jerseySwaggerProperties.getHost();
        map.put("host", Objects.nonNull(host) ? host : this.jerseySwaggerProperties.getIp() + ":" + this.jerseySwaggerProperties.getPort());
        map.put("path", CommonHelper.parsePath(this.jerseySwaggerProperties.getBasePath()));
        String cdn = this.jerseySwaggerProperties.getCdn();
        map.put("cdn", Objects.nonNull(cdn) ? cdn : "/Swagger/");
        return map;
    }

}
