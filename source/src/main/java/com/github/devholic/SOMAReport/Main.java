package com.github.devholic.SOMAReport;

import java.net.URISyntaxException;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;

import org.glassfish.grizzly.http.server.Request;
import org.glassfish.grizzly.http.server.Session;

@Path("/")
public class Main {

	@Context
	UriInfo uri;

	@GET
	public Response mainView(@Context Request request)
			throws URISyntaxException {
		Session session = request.getSession();
		UriBuilder builder = UriBuilder.fromUri(uri.getBaseUri());
		if (session.getAttribute("user_id") != null) {
			builder.path("project");
			return Response.seeOther(builder.build()).build();
		} else {
			builder.path("login");
			return Response.seeOther(builder.build()).build();
		}
	}

}
