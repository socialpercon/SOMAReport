package com.github.devholic.SOMAReport.Controller;

import java.io.FileInputStream;
import java.io.InputStream;
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
}
