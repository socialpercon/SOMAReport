package com.github.devholic.SOMAReport;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

@Path("/api/demo")
public class API_Demo {

	@GET
	@Path("/couch")
	public Response demo() {
		try {
			HttpResponse<String> response = Unirest.get(
					"http://localhost:5984/test").asString();
			return Response.status(200).type(MediaType.APPLICATION_JSON)
					.entity(response.getBody().toString()).build();
		} catch (UnirestException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return Response.status(500).type(MediaType.APPLICATION_JSON)
				.entity("{'error':'error'}").build();
	}

}
