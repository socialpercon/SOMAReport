package org.gradle;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.FileNotFoundException;
import java.io.FileReader;

import org.junit.BeforeClass;
import org.junit.Test;

import com.cloudant.client.api.model.Response;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonIOException;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSyntaxException;

public class DocumentUtilTest {

	public static DocumentUtil docutil;
	
	@BeforeClass
	public static void testDocumentUtil() 
	{
		docutil = new DocumentUtil("somarecord");
		System.out.println("Connected to Cloudant :: SOMAReport");
		System.out.println("Server Version: " + docutil.client.serverVersion());
		System.out.println("DB Info. \n" + docutil.db.info().toString());
		assertTrue(true);
	}
	
	@Test
	public void testGetUserDoc() 
	{
		String account = "ppyong0@gmail.com";
		JsonObject user = docutil.getUserDoc(account);
		System.out.println(user.toString());
		assertEquals(account, user.get("account").getAsString());
	}
	
	@Test
	public void testGetUserId() 
	{
		String id = docutil.getUserId("이뿅뿅");
		assertEquals("이뿅뿅",docutil.getDoc(id).get("name").getAsString());
	}

	@Test
	public void testPutDoc() 
			throws JsonIOException, JsonSyntaxException, FileNotFoundException 
	{
		JsonParser parser = new JsonParser();
		JsonElement json = parser.parse(new FileReader("report.json"));
		JsonObject inputDoc = json.getAsJsonObject();
		System.out.println("input: "+inputDoc);
		JsonObject resultDoc = docutil.db.find(JsonObject.class, docutil.putDoc(inputDoc));
		System.out.println("result: "+resultDoc);
		
		assertEquals(inputDoc.get("project").getAsString(), resultDoc.get("project").getAsString());
	}

	@Test
	public void testDeleteDoc() 
	{
		Response res = docutil.deleteDoc("7f60b28b5ad44434b89d71206852c8a7");
		System.out.println("\n"+res.toString());
	}

	@Test
	public void testUpdateDoc() 
	{
		JsonObject doc = docutil.getUserDoc("gyakk3@gmail.com").getAsJsonObject();
		if (!doc.has("years")) {
			JsonPrimitive year = new JsonPrimitive(2014);
			JsonArray years = new JsonArray();
			years.add(year);
			doc.add("years", years);
		}
		System.out.println("\nchanged doc: "+doc.toString());
		Response res = docutil.updateDoc(doc);
		System.out.println(res.toString());
		JsonObject updoc = docutil.db.find(JsonObject.class, res.getId());
		System.out.println("updated doc: "+updoc.toString());
		
		assertTrue(updoc.has("years"));
	}

	@Test
	public void testCalWholeTime() throws JsonIOException, JsonSyntaxException, FileNotFoundException 
	{
		JsonParser parser = new JsonParser();
		JsonElement json = parser.parse(new FileReader("report_input.json"));
		JsonObject report_info = json.getAsJsonObject().get("report_info").getAsJsonObject();
		assertEquals(5,docutil.calWholeTime(report_info));
	}
	
	@Test
	public void testGetDate() throws JsonIOException, JsonSyntaxException, FileNotFoundException {
		JsonParser parser = new JsonParser();
		JsonElement json = parser.parse(new FileReader("report_input.json"));
		JsonObject report_info = json.getAsJsonObject().get("report_info").getAsJsonObject();
		System.out.println(docutil.getDate(report_info));
	}
	
	@Test
	public void testPutReportDoc() throws JsonIOException, JsonSyntaxException, FileNotFoundException {
		JsonParser parser = new JsonParser();
		JsonElement json = parser.parse(new FileReader("report_input.json"));
		JsonObject report_input = json.getAsJsonObject();
		docutil.putReportDoc(report_input);
	}
}
