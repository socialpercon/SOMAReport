package com.github.devholic.SOMAReport.Controller;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.ektorp.CouchDbConnector;
import org.ektorp.CouchDbInstance;
import org.ektorp.UpdateConflictException;
import org.ektorp.ViewQuery;
import org.ektorp.http.HttpClient;
import org.ektorp.http.StdHttpClient;
import org.ektorp.impl.StdCouchDbConnector;
import org.ektorp.impl.StdCouchDbInstance;
import org.json.JSONArray;
import org.json.JSONObject;

import com.github.devholic.SOMAReport.Utilities.MustacheHelper;

public class DatabaseController {

	private final static Logger Log = Logger.getLogger(UserController.class);

	HttpClient couchClient;
	CouchDbInstance dbInstance;
	CouchDbConnector db;

	public DatabaseController() {
		try {
			Properties prop = new Properties();
			FileInputStream fileInput = new FileInputStream("config.xml");
			prop.loadFromXML(fileInput);
			couchClient = new StdHttpClient.Builder()
					.url(prop.getProperty("couchdb_url"))
					.username(prop.getProperty("couchdb_id"))
					.password(prop.getProperty("couchdb_password")).build();
			dbInstance = new StdCouchDbInstance(couchClient);
			db = new StdCouchDbConnector(prop.getProperty("couchdb_name"),
					dbInstance);
		} catch (Exception e) {
			Log.error(e.getMessage());
		}
	}

	public InputStream getByView(String docId, String viewName, String key) {
		ViewQuery q = new ViewQuery().designDocId(docId).viewName(viewName)
				.key(key);
		return db.queryForStream(q);
	}

	public InputStream getByViewIncludeDoc(String docId, String viewName,
			String key) {
		ViewQuery q = new ViewQuery().designDocId(docId).viewName(viewName)
				.key(key).includeDocs(true);
		return db.queryForStream(q);
	}

	public Map<String, Object> createDoc(JSONObject jo) {
		Map<String, Object> doc = MustacheHelper.toMap(jo);
		db.create(doc);
		if (doc.get("_id") != null) {
			return doc;
		} else {
			return null;
		}
	}

	public Map<String, Object> updateDoc(JSONObject jo) {
		Map<String, Object> doc = MustacheHelper.toMap(jo);
		Log.info(doc);
		try {
			db.update(doc);
			Log.info(doc);
			return doc;
		} catch (UpdateConflictException e) {
			Log.error(e.getMessage());
			return null;
		}
	}

	public boolean deleteDoc(String id, String rev) {
		try {
			db.delete(id, rev);
			return true;
		} catch (UpdateConflictException e) {
			Log.error(e.getMessage());
			return false;
		}
	}
	
	public InputStream getDoc(String id) {
		// id로 문서를 가져온다
		return db.getAsStream(id);
	}
	
	public boolean isAlreadyRegistered(String email) {
		// 이메일을 통해 이미 등록된 사용자인지를 확인한다
		BufferedReader is = new BufferedReader(new InputStreamReader(getByView("_design/user", "search_by_email", email)));
		try {
			String str, doc = "";
			while ((str = is.readLine())!= null) {	doc += str;	}
			JSONArray results = new JSONObject(doc).getJSONArray("rows");
			if (results.length() == 0) 
				return false;
			else
				return true;
		} catch (IOException e) {
			Log.error(e.getLocalizedMessage());
			return false;
		}
	}
	
	public String getIdbyName(String name) {
		// 이름을 통해 해당 사용자 문서의 _id를 가져온다
		BufferedReader is = new BufferedReader(new InputStreamReader(getByView("_design/user", "search_by_name", name)));
		try {
			String str, doc = "";
			while ((str = is.readLine())!= null) {	doc += str;	}
			JSONArray results = new JSONObject(doc).getJSONArray("rows");
			if (results.length() == 0)	throw new Exception();
			return results.getJSONObject(0).getString("value");
		} catch (IOException e) {
			Log.error(e.getLocalizedMessage());
			return null;
		} catch (Exception e) {
			Log.error(e.getLocalizedMessage());
			return null;
		}
	}
	
	public void getMyProject(String id) {
		// 사용자가 속한 프로젝트의 정보를 불러온다
		// key: 사용자 문서의 _id
		// return: [{title, mentor, mentee[]}]
		
		//BufferedReader reader = new BufferedReader(new InputStreamReader(getByBiew("_design/")))
	}
}
