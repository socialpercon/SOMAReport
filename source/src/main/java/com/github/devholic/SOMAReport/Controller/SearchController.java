package com.github.devholic.SOMAReport.Controller;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Properties;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.log4j.Logger;
import org.json.JSONObject;

import com.cloudant.client.api.model.SearchResult;
import com.github.devholic.SOMAReport.Utilities.DocumentUtil;
import com.github.devholic.SOMAReport.Utilities.ReferenceUtil;
import com.google.gson.JsonObject;

@Path("/search")
public class SearchController {

	private final Logger logger = Logger.getLogger(SearchController.class);

	ReferenceUtil ref_util = new ReferenceUtil("");
	DocumentUtil doc_util = new DocumentUtil("");
				
	@GET
	@Path("/report/{query}")
	@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8") 
	public ArrayList<JsonObject> searchReport_cloudantsearch(@PathParam("query") String query){

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
	public ArrayList<JsonObject> searchProject_cloudantsearch(@PathParam("query") String query){

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
	public ArrayList<JsonObject> searchUser_cloudantsearch(@PathParam("query") String query){

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
	 * couchDB에 넣는 JSONObject(couchDB에 들어가는 Json) 을 넣어주면된다.
	 * 
	 * ex. localhost:8080/elastic_index/report/주제입니다만
	 * 
	 * @param JSONObject jo : json 형식으로 doc을 추가한다.
	 * @param target => report,user,project 중 하나를 입력
	 * @return JSONObject가 없거나, target이 잘못 입력되면 status 500을 return 한다.
	 *************************************************/
	@POST
	@Path("/elastic_index/{target}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8") 
	public Response elastic_index(@PathParam("target") String target,
            				      JSONObject jo){
		logger.debug("elastic_index invoked..");
		
		try{
			//target parameter valid check
			ArrayList<String> valid_target = new ArrayList<String>();
			valid_target.add("user");
			valid_target.add("report");
			valid_target.add("project");
			
			if(!valid_target.contains(target)|| target.equals("") || target == null || jo == null){
				return Response.status(500).type(MediaType.APPLICATION_JSON).build();
			}
			
			// get config value
			Properties prop = new Properties();
			FileInputStream fileInput = new FileInputStream("config.xml");
			prop.loadFromXML(fileInput);
			String elastic_base_url = prop.getProperty("elasticsearch_base");
			
			String url = elastic_base_url+"/somareport/"+target+"/";
			URL obj = new URL(url);
			HttpURLConnection con = (HttpURLConnection) obj.openConnection();
			con.setDoOutput(true);
			con.setDoInput(true);
			con.setRequestProperty("Content-Type", "application/json");
			con.setRequestProperty("Accept", "application/json");
			con.setRequestMethod("POST");

			// Send post request
			OutputStreamWriter wr = new OutputStreamWriter(con.getOutputStream());
			wr.write(jo.toString());
			wr.flush();

			System.out.println("\nSending 'POST' request to URL : " + url);
			System.out.println("Post parameters : " + jo.toString());
			
			StringBuilder sb = new StringBuilder();  
			int HttpResult = con.getResponseCode(); 
			if(HttpResult == HttpURLConnection.HTTP_OK){
			    BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream(),"utf-8"));  
			    String line = null;  
			    while ((line = br.readLine()) != null) {  
			        sb.append(line + "\n");  
			    }  

			    br.close();  

			    System.out.println(""+sb.toString());  

			}else{
			    System.out.println(con.getResponseMessage());  
			}  
			
		}catch(Exception e){
			e.printStackTrace();
		}
		
		//CORS header 추가
		return Response.status(200).type(MediaType.APPLICATION_JSON)
				.header("Access-Control-Allow-Origin", "*")
				.header("Access-Control-Allow-Methods", "GET, POST, DELETE, PUT")
				.build();
	}
	
	/**************************************************************************
	 * 키워드를 query에 넣어주면 report topic 기준으로 검색을 해서 결과를 보내준다.
	 * 
	 * @param query - report :topic / user :name / project :title 기준으로 검색을 한다.- 전체단어검색 
	 * @param target => report,user,project 중 하나를 입력
	 * @return query가 없거나, target이 잘못 입력되면 status 500을 return 한다.
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
			
			String url = "";
			if(target.equals("report")){
				url = elastic_base_url
						+"/somareport/"+target+"/_search?q=_source.topic="
						+URLEncoder.encode(query, "utf-8");
			}else if(target.equals("user")){
				url = elastic_base_url
						+"/somareport/"+target+"/_search?q=_source.name="
						+URLEncoder.encode(query, "utf-8");
			}else if(target.equals("project")){
				url = elastic_base_url
						+"/somareport/"+target+"/_search?q=_source.title="
						+URLEncoder.encode(query, "utf-8");
			}
			

			URL obj = new URL(url);
			HttpURLConnection con = (HttpURLConnection) obj.openConnection();

			// optional default is GET
			con.setRequestMethod("GET");
			//add request header

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
		
		//CORS header 추가
		return Response.status(200).type(MediaType.APPLICATION_JSON)
				.header("Access-Control-Allow-Origin", "*")
				.header("Access-Control-Allow-Methods", "GET, POST, DELETE, PUT")
				.build();
	}
	
	/*************************************************
	 * couchDB에 넣는 JSONObject(couchDB에 들어가는 Json) 을 넣어주면된다.
	 * 
	 * ex. localhost:8080/elastic_update/report/주제입니다만
	 * 
	 * @param JSONObject jo : 수정할 doc의 json을 넣어준다.
	 * @param target => report,user,project 중 하나를 입력
	 * @return JSONObject가 없거나, target이 잘못 입력되면 status 500을 return 한다.
	 *************************************************/
	@POST
	@Path("/elastic_update/{target}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8") 
	public Response elastic_update(@PathParam("target") String target,
            				      JSONObject jo, String _id){
		logger.debug("elastic_update invoked..");
		
		try{
			//target parameter valid check
			ArrayList<String> valid_target = new ArrayList<String>();
			valid_target.add("user");
			valid_target.add("report");
			valid_target.add("project");
			
			if(!valid_target.contains(target)|| target.equals("") || target == null || jo == null){
				return Response.status(500).type(MediaType.APPLICATION_JSON).build();
			}
			
			// get config value
			Properties prop = new Properties();
			FileInputStream fileInput = new FileInputStream("config.xml");
			prop.loadFromXML(fileInput);
			String elastic_base_url = prop.getProperty("elasticsearch_base");
			
			String url = elastic_base_url+"/somareport/"+target+"/"+_id+"/_update";
			URL obj = new URL(url);
			HttpURLConnection con = (HttpURLConnection) obj.openConnection();
			con.setDoOutput(true);
			con.setDoInput(true);
			con.setRequestProperty("Content-Type", "application/json");
			con.setRequestProperty("Accept", "application/json");
			con.setRequestMethod("POST");

			// Send post request
			OutputStreamWriter wr = new OutputStreamWriter(con.getOutputStream());
			JSONObject update_jo = new JSONObject();
			update_jo.put("doc", jo);
			System.out.println("update_jo:"+update_jo.toString());
			wr.write(update_jo.toString());
			wr.flush();

			System.out.println("\nSending 'POST' request to URL : " + url);
			System.out.println("Post parameters : " + jo.toString());
			
			StringBuilder sb = new StringBuilder();  
			int HttpResult = con.getResponseCode(); 
			if(HttpResult == HttpURLConnection.HTTP_OK){
			    BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream(),"utf-8"));  
			    String line = null;  
			    while ((line = br.readLine()) != null) {  
			        sb.append(line + "\n");  
			    }  

			    br.close();  

			    System.out.println(""+sb.toString());  

			}else{
			    System.out.println(con.getResponseMessage());  
			}  
			
		}catch(Exception e){
			e.printStackTrace();
		}
		
		//CORS header 추가
		return Response.status(200).type(MediaType.APPLICATION_JSON)
				.header("Access-Control-Allow-Origin", "*")
				.header("Access-Control-Allow-Methods", "GET, POST, DELETE, PUT")
				.build();
	}
}
