package com.github.devholic.SOMAReport;

import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import org.glassfish.jersey.server.mvc.Viewable;
import org.json.JSONArray;
import org.json.JSONObject;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

@Path("/")
public class View_Demo {

	@GET
	@Path("/demo")
	@Produces("text/html")
	public Viewable test() {
		JSONObject jo1, jo2;
		JSONArray ja;
		jo1 = new JSONObject();
		ja = new JSONArray();
		jo2 = new JSONObject();
		jo2.put("name", "test1");
		ja.put(jo2);
		jo2 = new JSONObject();
		jo2.put("name", "test2");
		ja.put(jo2);
		jo2 = new JSONObject();
		jo2.put("name", "test3");
		ja.put(jo2);
		jo1.put("objects", ja);
		Map<String, Object> retMap = new Gson().fromJson(jo1.toString(),
				new TypeToken<HashMap<String, Object>>() {
				}.getType());
		return new Viewable("/demo.mustache", retMap);
	}

}
