package com.github.devholic.SOMAReport;


import java.io.InputStream;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.log4j.Logger;
import org.glassfish.grizzly.http.server.Request;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;
import org.json.JSONObject;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;


@Path("/api/demo")
public class API_Demo {
	
	private final Logger logger = Logger.getLogger(API_Demo .class);

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

	@GET
	@Path("/request")
	public Response demoGetRequest(@Context Request request) {
		// Session
		// Session session = request.getSession();
		// session.setAttribute("key", "value");
		// session.getAttribute("key");
		return Response.status(200).type(MediaType.TEXT_PLAIN)
				.entity("Content-Type: " + request.getContentType()).build();
	}

	// http://www.mkyong.com/webservices/jax-rs/file-upload-example-in-jersey/

	@POST
	@Path("/upload")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	public Response demoUploadFileAPI(
			@FormDataParam("file") InputStream uploadedInputStream,
			@FormDataParam("file") FormDataContentDisposition fileDetail) {
		JSONObject jo = new JSONObject();
		jo.put("fileName", fileDetail.getFileName().toString());
		logger.debug(jo.toString());
		return Response.status(200).type(MediaType.APPLICATION_JSON)
				.entity(jo.toString()).build();
	}
	
	/*
	 * private void writeToFile(InputStream uploadedInputStream, String
	 * uploadedFileLocation) {
	 * 
	 * try { OutputStream out = new FileOutputStream(new File(
	 * uploadedFileLocation)); int read = 0; byte[] bytes = new byte[1024];
	 * 
	 * out = new FileOutputStream(new File(uploadedFileLocation)); while ((read
	 * = uploadedInputStream.read(bytes)) != -1) { out.write(bytes, 0, read); }
	 * out.flush(); out.close(); } catch (IOException e) {
	 * 
	 * e.printStackTrace(); } }
	 */
}
