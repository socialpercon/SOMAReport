package com.github.devholic.SOMAReport;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;

import org.glassfish.grizzly.http.server.Request;
import org.glassfish.grizzly.http.server.Session;
import org.glassfish.jersey.server.mvc.Viewable;

@Path("/")
public class Mypage {

	@Context
	UriInfo uri;

	@GET
	@Path("/mypage")
	public Response View_Mypage(@Context Request request) {
		Session session = request.getSession();
		UriBuilder builder = UriBuilder.fromUri(uri.getBaseUri());
		if (session.getAttribute("user_id") != null) {
			return Response.status(200)
					.entity(new Viewable("/new/new_mypage.mustache")).build();
		} else {
			builder.path("login");
			return Response.seeOther(builder.build()).build();
		}
	}

}
