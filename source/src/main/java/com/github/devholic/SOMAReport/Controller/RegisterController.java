package com.github.devholic.SOMAReport.Controller;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.json.JSONArray;
import org.json.JSONObject;

public class RegisterController {

	private final static Logger Log = Logger.getLogger(UserController.class);

	DatabaseController db;

	private Workbook workbook;
	private Sheet sheet;

	public RegisterController(InputStream is) {
		try {
			workbook = new XSSFWorkbook(is);
			db = new DatabaseController();
		} catch (FileNotFoundException e) {
			Log.error(e.getLocalizedMessage());
		} catch (IOException e) {
			Log.error(e.getLocalizedMessage());
		}
	}

	public JSONObject registerMentor() {

		sheet = workbook.getSheet("mentor");
		if (sheet == null) {
			Log.error("Mentor sheet does not exists.");
			return null;
		}
		JSONObject registerDoc = new JSONObject();
		Row row;
		Cell cell;

		// 등록연도 가져오기 (기수 정보)
		row = sheet.getRow(4);
		cell = row.getCell(1);
		int year = (int) cell.getNumericCellValue();
		int updateNum = 0, insertNum = 0;
		JSONArray updated = new JSONArray(), inserted = new JSONArray();

		for (int rowIndex = 7; rowIndex <= sheet.getLastRowNum(); rowIndex++) {
			row = sheet.getRow(rowIndex);

			if (row != null) {

				registerDoc = new JSONObject();
				registerDoc.put("type", "account");
				registerDoc.put("role", "mentor");
				registerDoc.put("years", new int[] { year });
				// registerDoc.put("salt", StringFactory.createSalt());

				// temporary config for test - password = "password"
				registerDoc.put("salt",
						"uKBH+w9OaZYwTaXM9VD6T6DHgGzn23plYWawbbbOqAs=");
				registerDoc
						.put("password",
								"3d424d257f6a1e15cffc45b493e3f77c54852889e494fbb69c301802e3ebe75a");

				cell = row.getCell(0);
				registerDoc.put("name", cell.getStringCellValue());
				cell = row.getCell(1);
				registerDoc.put("email", cell.getStringCellValue());
				cell = row.getCell(2);
				registerDoc.put("phone_num", cell.getStringCellValue());
				cell = row.getCell(3);
				registerDoc.put("belong", cell.getStringCellValue());
				cell = row.getCell(4);
				registerDoc.put("section", cell.getStringCellValue());
				Map<String, Object> m = db.createDoc(registerDoc);
				registerDoc.put("_id", m.get("_id").toString());
				registerDoc.put("_rev", m.get("_rev"));
				inserted.put(registerDoc);
				Log.info(registerDoc.toString());
				insertNum++;

			}
		}

		JSONObject registered = new JSONObject();
		registered.put("updated", updated);
		registered.put("inserted", inserted);
		Log.debug(updateNum + " mentors are updated and " + insertNum
				+ " mentors are inserted.");
		return registered;
	}

	public JSONArray registerMentee() {

		sheet = workbook.getSheet("mentee");
		if (sheet == null) {
			Log.error("Mentee sheet does not exits");
			return null;
		}
		JSONObject registerDoc = new JSONObject();
		Row row;
		Cell cell;

		// 등록연도 가져오기 (기수 정보)
		row = sheet.getRow(4);
		cell = row.getCell(1);
		int year = (int) cell.getNumericCellValue();
		int num = 0;
		JSONArray inserted = new JSONArray();

		for (int rowIndex = 7; rowIndex <= sheet.getLastRowNum(); rowIndex++) {
			row = sheet.getRow(rowIndex);
			if (row != null) {
				registerDoc = new JSONObject();
				registerDoc.put("type", "account");
				registerDoc.put("role", "mentee");
				registerDoc.put("year", year);
				// registerDoc.put("salt", StringFactory.createSalt());

				// temporary config for test
				registerDoc.put("salt",
						"uKBH+w9OaZYwTaXM9VD6T6DHgGzn23plYWawbbbOqAs=");
				registerDoc
						.put("password",
								"3d424d257f6a1e15cffc45b493e3f77c54852889e494fbb69c301802e3ebe75a");

				cell = row.getCell(0);
				registerDoc.put("name", cell.getStringCellValue());
				cell = row.getCell(1);
				registerDoc.put("email", cell.getStringCellValue());
				cell = row.getCell(2);
				registerDoc.put("phone_num", cell.getStringCellValue());
				cell = row.getCell(3);
				registerDoc.put("belong", cell.getStringCellValue());
				Map<String, Object> m = db.createDoc(registerDoc);
				registerDoc.put("_id", m.get("_id"));
				registerDoc.put("_rev", m.get("_rev"));
				Log.info(registerDoc);
				inserted.put(registerDoc);
				num++;
			}
		}
		Log.debug("Total " + num + " mentees are registered in DB.");
		return inserted;
	}

	public JSONObject registerProject() {

		List<Sheet> projectSheets = new ArrayList<Sheet>();
		for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
			String name = workbook.getSheetName(i);
			if (name.contains("Project")) {
				projectSheets.add(workbook.getSheetAt(i));
			}
		}
		if (projectSheets.size() == 0)
			return null;
		Log.debug(projectSheets.size() + " Sheets");

		JSONObject registered = new JSONObject();
		for (Sheet projectSheet : projectSheets) {
			int num = 0;
			JSONObject registerDoc = new JSONObject();
			sheet = projectSheet;
			Row row;
			Cell cell;

			JSONArray array = new JSONArray();

			// 등록정보 가져오기 (기수, 단계, 차수)
			cell = sheet.getRow(3).getCell(1);
			int year = (int) cell.getNumericCellValue();
			cell = sheet.getRow(4).getCell(1);
			int level = (int) cell.getNumericCellValue();
			cell = sheet.getRow(5).getCell(1);
			int st = (int) cell.getNumericCellValue();

			JSONArray stage = new JSONArray();
			stage.put(year);
			stage.put(level);
			stage.put(st);

			for (int rowIndex = 8; rowIndex <= sheet.getLastRowNum(); rowIndex++) {
				row = sheet.getRow(rowIndex);
				if (row != null) {
					registerDoc = new JSONObject();
					registerDoc.put("type", "project");
					cell = row.getCell(0);
					registerDoc.put("project_type", cell.getStringCellValue());
					registerDoc.put("stage", stage);
					cell = row.getCell(1);
					registerDoc.put("mentor",
							getIdbyName(cell.getStringCellValue()));
					cell = row.getCell(2);
					registerDoc.put("title", cell.getStringCellValue());
					cell = row.getCell(3);
					registerDoc.put("field", cell.getStringCellValue());

					JSONArray menteeList = new JSONArray();
					for (int i = 4; i < row.getLastCellNum(); i++) {
						String name = row.getCell(i).getStringCellValue();
						menteeList.put(getIdbyName(name));
					}
					registerDoc.put("mentee", menteeList);
					Map<String, Object> m = db.createDoc(registerDoc);
					registerDoc.put("_id", m.get("_id"));
					registerDoc.put("_rev", m.get("_rev"));
					array.put(registerDoc);
				}
			}
			registered.put(sheet.getSheetName(), array);
			Log.debug("Total " + num + "projects are inserted in "
					+ stage.toString());
		}
		return registered;
	}

	public String getIdbyName(String name) {
		// 이름을 통해 해당 사용자 문서의 _id를 가져온다
		BufferedReader is = new BufferedReader(new InputStreamReader(
				db.getByView("_design/user", "search_by_name", name, false,
						false)));
		try {
			String str, doc = "";
			while ((str = is.readLine()) != null) {
				doc += str;
			}
			JSONArray results = new JSONObject(doc).getJSONArray("rows");
			if (results.length() == 0)
				throw new Exception();
			return results.getJSONObject(0).getString("value");
		} catch (IOException e) {
			Log.error(e.getLocalizedMessage());
			return null;
		} catch (Exception e) {
			Log.error(e.getLocalizedMessage());
			return null;
		}
	}
}
