package com.github.devholic.SOMAReport;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeRequestUrl;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;

@Path("/")
public class View_GoogleDrive {
	@GET
	@Path("/api/drive")
	public Response driveTestA(@QueryParam("code") String code)
			throws URISyntaxException, IOException {
		if (code != null) {
			GoogleAuthorizationCodeFlow flow = API_GoogleDrive.getFlow();
			GoogleTokenResponse resp = flow.newTokenRequest(code)
					.setRedirectUri(API_GoogleDrive.REDIRECT_URI).execute();
			System.out.println(resp.toString());
			return Response.seeOther(new URI("http://localhost:8080")).build();
		} else {
			GoogleAuthorizationCodeFlow flow = API_GoogleDrive.getFlow();
			GoogleAuthorizationCodeRequestUrl urlBuilder = flow
					.newAuthorizationUrl().setRedirectUri(
							API_GoogleDrive.REDIRECT_URI);
			urlBuilder.set("user_id", "1");
			String gUrl = urlBuilder.build();
			return Response.seeOther(new URI(gUrl)).build();
		}
	}
}
