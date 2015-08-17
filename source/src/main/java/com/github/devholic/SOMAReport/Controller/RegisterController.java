package com.github.devholic.SOMAReport.Controller;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
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

import com.github.devholic.SOMAReport.Utilities.StringFactory;

public class RegisterController {

	private final static Logger Log = Logger.getLogger(UserController.class);

	public final static int SHEET_USER = 0;
	public final static int SHEET_PROJECT = 1;

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

	public JSONArray registerMentor() {
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
		JSONArray inserted = new JSONArray();

		for (int rowIndex = 7; rowIndex <= sheet.getLastRowNum(); rowIndex++) {
			row = sheet.getRow(rowIndex);

			if (row != null) {
				String email = row.getCell(1).getStringCellValue();
				if (UserController.isAlreadyRegistered(email)) {
					// 이미 등록된 멘토는 연도를 업데이트
					registerDoc = UserController.getUserByEmail(email);
					JSONArray years = registerDoc.getJSONArray("years");
					years.put(year);
					registerDoc.put("years", years);
					insertNum++;
				} else {
					// 신규 멘토 등
					registerDoc = new JSONObject();
					registerDoc.put("type", "account");
					registerDoc.put("role", "mentor");
					registerDoc.put("years", new int[] { year });

					String salt = StringFactory.createSalt();
					registerDoc.put("salt", salt);
					String password = StringFactory.encryptPassword("password",
							salt);
					registerDoc.put("password", password);

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
		}

		Log.debug(updateNum + " mentors are updated and " + insertNum
				+ " mentors are inserted.");
		return inserted;
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

				String salt = StringFactory.createSalt();
				registerDoc.put("salt", salt);
				String password = StringFactory.encryptPassword("password",
						salt);
				registerDoc.put("password", password);

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
		if (projectSheets.size() == 0) {
			Log.error("Project sheet does not exists");
			return null;
		}
		Log.debug("There are " + projectSheets.size() + " Sheets");

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
			if (st != 0)
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
					registerDoc.put("mentor", UserController.getIdbyName(cell
							.getStringCellValue()));
					cell = row.getCell(2);
					registerDoc.put("title", cell.getStringCellValue());
					cell = row.getCell(3);
					registerDoc.put("field", cell.getStringCellValue());

					JSONArray menteeList = new JSONArray();
					for (int i = 4; i < row.getLastCellNum(); i++) {
						String name = row.getCell(i).getStringCellValue();
						menteeList.put(UserController.getIdbyName(name));
					}
					registerDoc.put("mentee", menteeList);
					Map<String, Object> m = db.createDoc(registerDoc);
					registerDoc.put("_id", m.get("_id"));
					registerDoc.put("_rev", m.get("_rev"));
					array.put(registerDoc);
				}
			}
			JSONObject stageInfo = new JSONObject();
			stageInfo.put("type", "stageInfo");
			stageInfo.put("stage", stage);
			String stageString;
			if (stage.length() == 2 || stage.getInt(2) == 0)
				stageString = stage.get(0) + "기 " + stage.get(1) + "단계 프로젝트";
			else
				stageString = stage.get(0) + "기 " + stage.get(1) + "단계 " + stage.get(2) + "차 프로젝트";
			stageInfo.put("stageString", stageString);
			JSONArray projects = new JSONArray();
			for (int i=0; i<array.length(); i++) {
				projects.put(array.getJSONObject(i).get("_id"));
			}
			stageInfo.put("projects", projects);
			Map<String, Object> m = db.createDoc(stageInfo);
			Log.info(m.get("_id"));
			registered.put(sheet.getSheetName(), array);
			Log.debug("Total " + num + "projects are inserted in "
					+ stage.toString());
		}
		return registered;
	}
}
