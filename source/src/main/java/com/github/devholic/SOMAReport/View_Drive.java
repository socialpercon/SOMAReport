package com.github.devholic.SOMAReport;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.URI;
import java.net.URISyntaxException;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

import org.json.simple.parser.ParseException;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeRequestUrl;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import com.google.api.client.http.FileContent;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpResponse;
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

	public static String driveUploadImage(String gid, String name,
			java.io.File file) throws URISyntaxException, IOException,
			ParseException {
		Credential c = API_GoogleDrive
				.fuckFuck(
						"ya29.yAF8kUZ_J3uUzJPbQvPYv-sFlM6qjP9FyHKOvgRON09Hrj7OFxxmJWbRkdoPjc20wgZH",
						"1/yXfCfi7fAiPmVzqJ6NrtkZxaDuyH2yqiKU_5aoK1yCw");
		Drive drive = API_GoogleDrive.buildService(c);
		File body = new File();
		body.setTitle(name);
		java.io.File fileContent = file;
		FileContent mediaContent = new FileContent("", fileContent);
		drive.files().insert(body, mediaContent).execute();
		return "";
	}

	@GET
	@Path("/api/drive/image")
	@Produces("image/jpeg")
	public Response getDriveImage(@QueryParam("id") String id)
			throws IOException {
		Credential c = API_GoogleDrive
				.fuckFuck(
						"ya29.yAF8kUZ_J3uUzJPbQvPYv-sFlM6qjP9FyHKOvgRON09Hrj7OFxxmJWbRkdoPjc20wgZH",
						"1/yXfCfi7fAiPmVzqJ6NrtkZxaDuyH2yqiKU_5aoK1yCw");
		Drive drive = API_GoogleDrive.buildService(c);
		FileList fl = drive.files().list().setQ("title = '" + id + "'")
				.execute();
		File file = fl.getItems().get(0);
		HttpResponse resp = drive.getRequestFactory()
				.buildGetRequest(new GenericUrl(file.getDownloadUrl()))
				.execute();
		return Response.ok(resp.getContent()).build();
	}
}
