package com.github.devholic.SOMAReport;

import java.net.URI;
import java.net.URISyntaxException;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;

import org.glassfish.grizzly.http.server.Request;
import org.glassfish.grizzly.http.server.Session;
import org.glassfish.jersey.server.mvc.Viewable;

@Path("/")
public class View_Main {

	@GET
	public Response mainView(@Context Request request)
			throws URISyntaxException {
		Session session = request.getSession();
		if (session.getAttribute("user_id") != null) {
			return Response.seeOther(
					new URI("http://localhost:8080/project/list")).build();
		} else {
			return Response.ok(new Viewable("/login.mustache")).build();
		}
	}

}
