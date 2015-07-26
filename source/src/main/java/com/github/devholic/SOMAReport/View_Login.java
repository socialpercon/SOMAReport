package com.github.devholic.SOMAReport;

import java.net.URI;
import java.net.URISyntaxException;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

import org.glassfish.jersey.server.mvc.Viewable;

@Path("/")
public class View_Login {

	@GET
	@Path("/login")
	public Response loginView(@QueryParam("token") String token) throws URISyntaxException {
		if(token==null){
			return Response.status(200).entity(new Viewable("/login.mustache"))
					.build();
		}else{
			return Response.seeOther(new URI("http://localhost:8080/project/list")).build();
		}
	}

}
