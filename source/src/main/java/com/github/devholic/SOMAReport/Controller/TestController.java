package com.github.devholic.SOMAReport.Controller;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.log4j.Logger;

import com.github.devholic.SOMAReport.Database.ElasticSearchUtil;

@Path("/to_test")
public class TestController {

	// Log4j setting
	private final Logger logger = Logger.getLogger(TestController.class);

	@GET
	@Path("/xmlpropertiesTest")
	@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
	public Response xmlpropertiesTest() {
		logger.debug("xmlpropertiesTest invoked..");

		try {

			Properties prop = new Properties();
			FileInputStream fis = new FileInputStream("config.xml");
			prop.loadFromXML(fis);
			logger.debug("\n URL: " + prop.getProperty("test"));

			ReportsController rc = new ReportsController();
			rc.renderDocx_mentoringReport();
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}

		return Response
				.status(200)
				.type(MediaType.APPLICATION_JSON)
				.header("Access-Control-Allow-Origin", "*")
				.header("Access-Control-Allow-Methods",
						"GET, POST, DELETE, PUT").build();
	}

	@GET
	@Path("/elastic_search")
	@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
	public Response elastic_search() {
		logger.debug("elastic_search invoked..");

		try {

			ElasticSearchUtil el = new ElasticSearchUtil();

			Map<String, Object> test_data = new HashMap<String, Object>();

			test_data.put("type", "admin");
			test_data.put("account", "admin@admin.com");
			test_data.put("salt", "qawsedrf");
			test_data.put("password", "somasoma");

			el.index(test_data, "1");
			Map<String, Object> result = el.search("1");
			System.out.println(result.toString());

		} catch (Exception e) {
			e.printStackTrace();
		}

		return Response
				.status(200)
				.type(MediaType.APPLICATION_JSON)
				.header("Access-Control-Allow-Origin", "*")
				.header("Access-Control-Allow-Methods",
						"GET, POST, DELETE, PUT").build();
	}

	@GET
	@Path("/elastic_search_urltest")
	@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
	public Response elastic_search_urltest() {
		logger.debug("elastic_search invoked..");

		try {

			String url = "http://172.16.101.101:9200/somareport/users/1";

			URL obj = new URL(url);
			HttpURLConnection con = (HttpURLConnection) obj.openConnection();

			// optional default is GET
			con.setRequestMethod("GET");

			// add request header
			// con.setRequestProperty("User-Agent", USER_AGENT);

			int responseCode = con.getResponseCode();

			logger.debug("\nSending 'GET' request to URL : " + url);
			logger.debug("Response Code : " + responseCode);

			BufferedReader in = new BufferedReader(new InputStreamReader(
					con.getInputStream()));
			String inputLine;
			StringBuffer response = new StringBuffer();

			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			in.close();

			// print result
			System.out.println(response.toString());

		} catch (Exception e) {
			e.printStackTrace();
		}

		return Response
				.status(200)
				.type(MediaType.APPLICATION_JSON)
				.header("Access-Control-Allow-Origin", "*")
				.header("Access-Control-Allow-Methods",
						"GET, POST, DELETE, PUT").build();
	}

	@GET
	@Path("/elastic_search_urltest_post")
	@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
	public Response elastic_search_urltest_post() {
		logger.debug("elastic_search invoked..");

		try {

			String url = "http://172.16.101.101:9200/somareport/users/1/_update";
			URL obj = new URL(url);
			URLConnection con = (URLConnection) obj.openConnection();

			// add reuqest header
			con.setDoOutput(true);
			con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");

			String urlParameters = "{\"doc\": " + "{" + "\"type\":\"mento\","
					+ "\"account\":\"admin@gmail.com\","
					+ "\"salt\":\"qawsedrf\"," + "\"password\":\"somasoma2\""
					+ "}" + "}";

			// Send post request
			con.setDoOutput(true);
			DataOutputStream wr = new DataOutputStream(con.getOutputStream());
			wr.writeBytes(urlParameters);
			wr.flush();
			wr.close();

			logger.debug("\nSending 'POST' request to URL : " + url);
			logger.debug("Post parameters : " + urlParameters);

			BufferedReader in = new BufferedReader(new InputStreamReader(
					con.getInputStream()));
			String inputLine;
			StringBuffer response = new StringBuffer();

			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			in.close();

			// print result
			System.out.println(response.toString());

		} catch (Exception e) {
			e.printStackTrace();
		}

		return Response
				.status(200)
				.type(MediaType.APPLICATION_JSON)
				.header("Access-Control-Allow-Origin", "*")
				.header("Access-Control-Allow-Methods",
						"GET, POST, DELETE, PUT").build();
	}
	
}
