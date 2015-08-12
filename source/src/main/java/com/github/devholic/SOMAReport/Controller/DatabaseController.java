package com.github.devholic.SOMAReport.Controller;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.ektorp.CouchDbConnector;
import org.ektorp.CouchDbInstance;
import org.ektorp.ViewQuery;
import org.ektorp.http.HttpClient;
import org.ektorp.http.StdHttpClient;
import org.ektorp.impl.StdCouchDbConnector;
import org.ektorp.impl.StdCouchDbInstance;

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
}
