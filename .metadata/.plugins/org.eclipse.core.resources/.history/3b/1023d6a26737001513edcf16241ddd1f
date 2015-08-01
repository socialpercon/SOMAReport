package org.gradle;

import static org.junit.Assert.*;

import java.io.FileNotFoundException;
import java.util.Scanner;

import org.junit.Test;

import com.google.gson.JsonIOException;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;

public class CouchDocUtilsTest {

	@Test
	public void testInsertDoc() throws JsonIOException, JsonSyntaxException, FileNotFoundException {
		CouchDocUtils insert = new CouchDocUtils();
		
		Scanner keybd = new Scanner(System.in);
		System.out.print("File to insert: ");
		String fileName = keybd.nextLine();
		keybd.close();
		
		JsonObject resultDoc = insert.db.find(JsonObject.class, 
				insert.insertDoc(fileName));	//insert it and find inserted one
		JsonObject inputDoc = insert.inputDoc;		// get input data
		System.out.println("input data: "+inputDoc.toString());
		
//		String[] input = {inputDoc.get("type").getAsString(), inputDoc.get("name").getAsString()};
//		String[] result = {resultDoc.get("type").getAsString(), resultDoc.get("name").getAsString()};
		
		//Compare input and result
//		assertEquals(input, result);
		assertEquals(inputDoc.get("name").getAsString(), resultDoc.get("name").getAsString());
	}

	@Test
	public void testViewAllLists() {
		CouchDocUtils views = new CouchDocUtils();
		assertTrue(views.viewAllLists());
	}


}
