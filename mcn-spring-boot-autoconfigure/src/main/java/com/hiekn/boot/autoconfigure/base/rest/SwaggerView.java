package com.hiekn.boot.autoconfigure.base.rest;

import com.hiekn.boot.autoconfigure.base.util.CommonHelper;
import com.hiekn.boot.autoconfigure.jersey.JerseySwaggerProperties;
import org.glassfish.jersey.server.mvc.Template;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.HashMap;
import java.util.Map;

@Controller
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
        map.put("ip",jerseySwaggerProperties.getIp());
        map.put("port",jerseySwaggerProperties.getPort());
        map.put("path",CommonHelper.parsePath(jerseySwaggerProperties.getBasePath()));
        return map;
    }

}
