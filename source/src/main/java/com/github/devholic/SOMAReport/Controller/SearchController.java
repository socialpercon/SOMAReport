package com.github.devholic.SOMAReport.Controller;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Properties;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.log4j.Logger;

import com.cloudant.client.api.model.SearchResult;
import com.github.devholic.SOMAReport.Database.DocumentUtil;
import com.github.devholic.SOMAReport.Database.ReferenceUtil;
import com.google.gson.JsonObject;

@Path("/search")
public class SearchController {

	private final Logger logger = Logger.getLogger(SearchController.class);

	ReferenceUtil ref_util = new ReferenceUtil("");
	DocumentUtil doc_util = new DocumentUtil("");
				
	@GET
	@Path("/report/{query}")
	@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8") 
	public ArrayList<JsonObject> searchReportController(@PathParam("query") String query){

		SearchResult<JsonObject> result = new SearchResult<JsonObject>();
		JsonObject jo = new JsonObject();
		ArrayList<JsonObject> jo_list = new ArrayList<JsonObject>();
		
		try{
			result = ref_util.searchReport(query);

			for(int i=0; i<result.getTotalRows(); i++){
				jo = result.getRows().get(i).getDoc();
				jo_list.add(jo);
			}
			
		}catch(Exception e){
			e.printStackTrace();
		}
		
		return jo_list;
	}
	
	@GET
	@Path("/project/{query}")
	@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8") 
	public ArrayList<JsonObject> searchProjectController(@PathParam("query") String query){

		SearchResult<JsonObject> result = new SearchResult<JsonObject>();
		JsonObject jo = new JsonObject();
		ArrayList<JsonObject> jo_list = new ArrayList<JsonObject>();
		
		try{
			result = ref_util.searchProject(query);

			for(int i=0; i<result.getTotalRows(); i++){
				jo = result.getRows().get(i).getDoc();
				logger.info(query+"'s project searchDoc:"+result.getRows().get(i).getDoc()+"\n");
				jo_list.add(jo);
			}
			
		}catch(Exception e){
			e.printStackTrace();
		}
		
		return jo_list;
	}
	
	@GET
	@Path("/user/{query}")
	@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8") 
	public ArrayList<JsonObject> searchUserController(@PathParam("query") String query){

		SearchResult<JsonObject> result = new SearchResult<JsonObject>();
		JsonObject jo = new JsonObject();
		ArrayList<JsonObject> jo_list = new ArrayList<JsonObject>();
		
		try{
			result = ref_util.searchUser(query);

			for(int i=0; i<result.getTotalRows(); i++){
				jo = result.getRows().get(i).getDoc();
				logger.info(query+"'s project searchDoc:"+result.getRows().get(i).getDoc()+"\n");
				jo_list.add(jo);
			}
			
		}catch(Exception e){
			e.printStackTrace();
		}
		
		return jo_list;
	}
	
	/*************************************************
	 * couchDB에 넣는 json을 string으로 변환해서 넣어주면된다.
	 * @param stringParam
	 * @param target => report,user,project 중 하나를 입력
	 * @return
	 *************************************************/
	@POST
	@Path("/elastic_index")
	@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8") 
	public Response elastic_index(@QueryParam("target") String target,
            				      @QueryParam("stringParam") String stringParam){
		logger.debug("elastic_index invoked..");
		
		try{
			//target parameter valid check
			ArrayList<String> valid_target = new ArrayList<String>();
			valid_target.add("user");
			valid_target.add("report");
			valid_target.add("project");
			
			if(!valid_target.contains(target)|| target.equals("") || target == null || stringParam.equals("") || stringParam == null){
				return Response.status(500).type(MediaType.APPLICATION_JSON).build();
			}
			
			// get config value
			Properties prop = new Properties();
			FileInputStream fileInput = new FileInputStream("config.xml");
			prop.loadFromXML(fileInput);
			String elastic_base_url = prop.getProperty("elasticsearch_base");
			
			String url = elastic_base_url+"/somareport/"+target;
			URL obj = new URL(url);
			URLConnection con = (URLConnection) obj.openConnection();

			//add reuqest header
			con.setDoOutput(true);
			con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");

			stringParam = stringParam.replaceAll("\"", "\\\"");
			String urlParameters = stringParam;
			
			// Send post request
			con.setDoOutput(true);
			DataOutputStream wr = new DataOutputStream(con.getOutputStream());
			wr.writeBytes(urlParameters);
			wr.flush();
			wr.close();

			System.out.println("\nSending 'POST' request to URL : " + url);
			System.out.println("Post parameters : " + urlParameters);

			BufferedReader in = new BufferedReader(
			        new InputStreamReader(con.getInputStream()));
			String inputLine;
			StringBuffer response = new StringBuffer();

			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			in.close();
			
			//print result
			System.out.println(response.toString());
			
		}catch(Exception e){
			e.printStackTrace();
		}
		
		return Response.status(200).type(MediaType.APPLICATION_JSON).build();
	}
	
	/**************************************************************************
	 * 키워드를 query에 넣어주면 report topic 기준으로 검색을 해서 결과를 보내준다.
	 * @param query
	 * @param target => report,user,project 중 하나를 입력
	 * @return
	 *************************************************************************/
	@GET
	@Path("/elastic_search/{target}/{query}")
	@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8") 
	public Response elastic_search(@PathParam("target") String target, @PathParam("query") String query){
		logger.debug("elastic_search invoked..");
		
		try{
			//target parameter valid check
			ArrayList<String> valid_target = new ArrayList<String>();
			valid_target.add("user");
			valid_target.add("report");
			valid_target.add("project");
			
			if(!valid_target.contains(target)|| target.equals("") || target == null || query.equals("") || query == null){
				return Response.status(500).type(MediaType.APPLICATION_JSON).build();
			}
			
			// get config value
			Properties prop = new Properties();
			FileInputStream fileInput = new FileInputStream("config.xml");
			prop.loadFromXML(fileInput);
			String elastic_base_url = prop.getProperty("elasticsearch_base");
			
			
			String url = elastic_base_url
							+"/somareport/"+target+"/_search?q=_source.topic="
							+URLEncoder.encode(query, "utf-8");

			URL obj = new URL(url);
			HttpURLConnection con = (HttpURLConnection) obj.openConnection();

			// optional default is GET
			con.setRequestMethod("GET");
			//add request header
//			con.setRequestProperty("User-Agent", USER_AGENT);

			int responseCode = con.getResponseCode();
			System.out.println("\nSending 'GET' request to URL : " + url);
			System.out.println("Response Code : " + responseCode);

			BufferedReader in = new BufferedReader(
			        new InputStreamReader(con.getInputStream()));
			String inputLine;
			StringBuffer response = new StringBuffer();

			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			in.close();

			//print result
			System.out.println(response.toString());
			
		}catch(Exception e){
			e.printStackTrace();
		}
		
		return Response.status(200).type(MediaType.APPLICATION_JSON).build();
	}
}
