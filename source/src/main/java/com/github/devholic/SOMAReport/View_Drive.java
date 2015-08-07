package com.github.devholic.SOMAReport;

import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

import org.json.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.google.api.client.auth.oauth2.BearerToken;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.auth.oauth2.Credential.AccessMethod;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeRequestUrl;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;

@Path("/")
public class View_Drive {
	@GET
	@Path("/api/drive")
	public Response driveAuth(@QueryParam("code") String code)
			throws URISyntaxException, IOException {
		if (code != null) {
			GoogleAuthorizationCodeFlow flow = API_GoogleDrive.getFlow();
			GoogleTokenResponse resp = flow.newTokenRequest(code)
					.setRedirectUri(API_GoogleDrive.REDIRECT_URI).execute();
			PrintWriter out = new PrintWriter("0.json");
			out.println(resp.toString());
			out.close();
			return Response.seeOther(new URI("http://localhost:8080")).build();
		} else {
			GoogleAuthorizationCodeFlow flow = API_GoogleDrive.getFlow();
			GoogleAuthorizationCodeRequestUrl urlBuilder = flow
					.newAuthorizationUrl().setRedirectUri(
							API_GoogleDrive.REDIRECT_URI);
			urlBuilder.set("user_id", "0");
			String gUrl = urlBuilder.build();
			return Response.seeOther(new URI(gUrl)).build();
		}
	}

	@GET
	@Path("/api/drive/{id}")
	public Response driveQ(@QueryParam("code") String code,
			@PathParam("id") String id) throws URISyntaxException, IOException,
			ParseException {
		Credential c = API_GoogleDrive
				.fuckFuck(
						"ya29.yAF8kUZ_J3uUzJPbQvPYv-sFlM6qjP9FyHKOvgRON09Hrj7OFxxmJWbRkdoPjc20wgZH",
						"1/yXfCfi7fAiPmVzqJ6NrtkZxaDuyH2yqiKU_5aoK1yCw");
		Drive drive = API_GoogleDrive.buildService(c);
		FileList fl = drive.files().list().setMaxResults(3).execute();
		List<File> fll = fl.getItems();
		for (int i = 0; i < fll.size(); i++) {
			System.out.println("Fll : " + fll.get(i).getTitle());
		}
		return Response.ok().build();
	}
}
