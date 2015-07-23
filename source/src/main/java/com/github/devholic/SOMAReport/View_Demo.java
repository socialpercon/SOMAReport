package com.github.devholic.SOMAReport;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import org.glassfish.jersey.server.mvc.Viewable;
import org.json.JSONObject;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

@Path("/demo")
public class View_Demo {

	@GET
	@Path("/welcome/{username}")
	@Produces("text/html")
	public Viewable demoUrl(@PathParam("username") String username) {
		JSONObject jo1 = new JSONObject();
		jo1.put("name", username);
		Map<String, Object> retMap = new Gson().fromJson(jo1.toString(),
				new TypeToken<HashMap<String, Object>>() {
				}.getType());
		return new Viewable("/demo.mustache", retMap);
	}

	@GET
	@Path("/form")
	@Produces("text/html")
	public Viewable demoFormGet() {
		return new Viewable("/demoform.mustache");
	}

	@POST
	@Path("/form")
	@Produces("text/html")
	public Viewable demoFormPost(@FormParam("username") String username) {
		JSONObject jo1 = new JSONObject();
		jo1.put("name", username);
		Map<String, Object> retMap = new Gson().fromJson(jo1.toString(),
				new TypeToken<HashMap<String, Object>>() {
				}.getType());
		return new Viewable("/demo.mustache", retMap);
	}

	@GET
	@Path("/redirect")
	public Response demoRedirect() {
		URL url;
		URI uri = null;
		try {
			url = new URL("http://www.google.com");
			uri = url.toURI();
			return Response.temporaryRedirect(uri).build(); // 307
			// return Response.seeOther(""); // 302
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return Response.seeOther(uri).build(); // 302
	}

	@GET
	@Path("/upload")
	public Viewable demoUpload() {
		return new Viewable("/demoupload.mustache");
	}
}
