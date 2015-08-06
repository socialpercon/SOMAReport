package com.github.devholic.SOMAReport.Model;


import static org.junit.Assert.*;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;

import org.apache.log4j.Logger;
import org.junit.Test;

import com.github.devholic.SOMAReport.Database.DocumentUtil;
import com.github.devholic.SOMAReport.Database.ReferenceUtil;
import com.google.gson.JsonObject;

public class Testtesttest {

	private final Logger logger = Logger.getLogger(Testtesttest .class);
	
//	@Test
//	public void testGetProjectInfo() {
//		ReferenceUtil ref = new ReferenceUtil("somarecord");
//		JsonObject info = ref.getProjectInfo("36be054d83f701154adfdd0cf1733874");
//		logger.debug(info.toString());
//	}

//	@Test
//	public void testUdateDoc() {
//		DocumentUtil doc = new DocumentUtil("somarecord");
//		ReferenceUtil ref = new ReferenceUtil("somarecord");
//		
//		List<JsonObject> getlist = ref.getAllMentee();
//		getlist.addAll(ref.getAllMentor());
//		List<String> uuids = doc.getUUID(getlist.size());
//		for(int i=0; i<getlist.size(); i++) {
//			getlist.get(i).addProperty("salt", uuids.get(i));
//			String pwd = "lalala";
//			getlist.get(i).addProperty("password", getSecurePassword(pwd, uuids.get(i)));
//			doc.updateDoc(getlist.get(i));
//		}
//		logger.info(getlist.size());
//	}
//	
//    private static String getSecurePassword(String passwordToHash, String salt)
//    {
//        String generatedPassword = null;
//        try {
//            MessageDigest md = MessageDigest.getInstance("SHA-256");
//            md.update(salt.getBytes());
//            byte[] bytes = md.digest(passwordToHash.getBytes());
//            StringBuilder sb = new StringBuilder();
//            for(int i=0; i< bytes.length ;i++)
//            {
//                sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
//            }
//            generatedPassword = sb.toString();
//        }
//        catch (NoSuchAlgorithmException e) {
//            e.printStackTrace();
//        }
//        return generatedPassword;
//    }
    
    @Test
    public void testUserAuthentication() {
    	DocumentUtil doc = new DocumentUtil("somarecord");
    	assertTrue(doc.userAuthentication("ppyong0@gmail.com", "lalala"));
    	assertFalse(doc.userAuthentication("ppyong@gmail.com", "lalala"));
    	assertFalse(doc.userAuthentication("ppyong0@gmail.com", "lalalala"));
    	
    }
}
