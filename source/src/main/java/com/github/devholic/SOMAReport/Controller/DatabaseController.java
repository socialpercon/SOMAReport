package com.github.devholic.SOMAReport.Controller;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.ektorp.CouchDbConnector;
import org.ektorp.CouchDbInstance;
import org.ektorp.DocumentNotFoundException;
import org.ektorp.UpdateConflictException;
import org.ektorp.ViewQuery;
import org.ektorp.http.HttpClient;
import org.ektorp.http.StdHttpClient;
import org.ektorp.impl.StdCouchDbConnector;
import org.ektorp.impl.StdCouchDbInstance;
import org.json.JSONArray;
import org.json.JSONObject;

import com.github.devholic.SOMAReport.Utilities.JSONFactory;
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

	public InputStream getByView(String docId, String viewName,
			boolean includeDocs, boolean descending, boolean reduce) {
		// view 결과를 조회
		// key 없음
		// includeDocs (true : 문서 통째로 리턴) / (false : value만 리턴)
		// descending (true : key역순) / (false : key순)
		ViewQuery q;
		if (reduce) {
			q = new ViewQuery().designDocId(docId).viewName(viewName)
					.reduce(reduce).includeDocs(includeDocs)
					.descending(descending).group(true);
		} else {
			q = new ViewQuery().designDocId(docId).viewName(viewName)
					.reduce(reduce).includeDocs(includeDocs)
					.descending(descending);
		}
		return db.queryForStream(q);
	}

	public InputStream getByView(String docId, String viewName, Object key,
			boolean includeDocs, boolean descending, boolean reduce) {
		// view 결과를 조회
		// single key
		// includeDocs (true : 문서 통째로 리턴) / (false : value만 리턴)
		// descending (true : key역순) / (false : key순)
		ViewQuery q;
		if (reduce) {
			q = new ViewQuery().designDocId(docId).viewName(viewName).key(key)
					.reduce(reduce).includeDocs(includeDocs)
					.descending(descending).group(true);
		} else {
			q = new ViewQuery().designDocId(docId).viewName(viewName).key(key)
					.reduce(reduce).includeDocs(includeDocs)
					.descending(descending);
		}

		return db.queryForStream(q);
	}

	public InputStream getByView(String docId, String viewName,
			Object startKey, Object endKey, boolean includeDocs,
			boolean descending, boolean reduce) {
		// view 결과를 조회
		// start-end 범위 검색 (key 설정시 descending에 따른 순서 주의)
		// includeDocs (true : 문서 통째로 리턴) / (false : value만 리턴)
		// descending (true : key역순) / (false : key순)
		ViewQuery q;
		if (reduce) {
			q = new ViewQuery().designDocId(docId).viewName(viewName)
					.startKey(startKey).endKey(endKey).reduce(reduce)
					.includeDocs(includeDocs).descending(descending)
					.group(true);
		} else {
			q = new ViewQuery().designDocId(docId).viewName(viewName)
					.startKey(startKey).endKey(endKey).reduce(reduce)
					.includeDocs(includeDocs).descending(descending);
		}
		return db.queryForStream(q);
	}
	
	public InputStream getByView(String docId, String viewName,
			Object startKey, Object endKey, boolean includeDocs,
			boolean descending, boolean reduce, int groupLevel) {
		// view 결과를 조회
		// start-end 범위 검색 (key 설정시 descending에 따른 순서 주의)
		// includeDocs (true : 문서 통째로 리턴) / (false : value만 리턴)
		// descending (true : key역순) / (false : key순)
		// groupLevel (grouping level)
		ViewQuery q;
		if (reduce) {
			q = new ViewQuery().designDocId(docId).viewName(viewName)
					.startKey(startKey).endKey(endKey).reduce(reduce)
					.includeDocs(includeDocs).descending(descending)
					.groupLevel(groupLevel).group(true);
		} else {
			q = new ViewQuery().designDocId(docId).viewName(viewName)
					.startKey(startKey).endKey(endKey).reduce(reduce)
					.includeDocs(includeDocs).descending(descending);
		}
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
		try {
			return db.getAsStream(id);
		} catch (DocumentNotFoundException e) {
			return null;
		}
	}

	public JSONArray getAllDocs() {
		List<String> allIds = db.getAllDocIds();
		JSONArray docs = new JSONArray();
		for(String id : allIds) {
			JSONObject doc = JSONFactory.inputStreamToJson(getDoc(id));
			docs.put(doc);
		}
		return docs;
	}
	
}
