package com.github.devholic.SOMAReport.Database;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

public class RegistrationUtil {

	private final Logger logger = Logger.getLogger(RegistrationUtil .class);
	
	DocumentUtil doc_util = new DocumentUtil("");
	Workbook workbook;
	Sheet sheet;

	public RegistrationUtil(FileInputStream fileInput) {
		try {
			workbook = new XSSFWorkbook(fileInput);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}	
	}
	
	public void registerMentor() {

		sheet = workbook.getSheet("mentor");
		if (sheet == null)
			return;
		JsonObject registerDoc = new JsonObject();
		Row row;
		Cell cell;

		// 등록연도 가져오기 (기수 정보)
		row = sheet.getRow(4);
		cell = row.getCell(1);
		int year = (int) cell.getNumericCellValue();
		int updateNum = 0, insertNum = 0;

		for (int rowIndex = 7; rowIndex <= sheet.getLastRowNum(); rowIndex++) {
			row = sheet.getRow(rowIndex);

			if (row != null) {
				cell = row.getCell(1);
				registerDoc = doc_util.getUserDoc(cell.getStringCellValue());

				if (registerDoc != null) {
					// 이미 등록된 멘토
					// "year" 항목에 현재 연도를 추가하여 update
					JsonArray years;
					try {
						years = registerDoc.get("years").getAsJsonArray();
					} catch (IllegalStateException e) {
						years = new JsonArray();
						years.add(new JsonPrimitive(registerDoc.get("years").getAsInt()));
					}
					years.add(new JsonPrimitive(year));
					registerDoc.add("years", years);

					logger.debug(doc_util.updateDoc(registerDoc).toString());
					updateNum++;
				}

				else {
					// 신규 등록
					registerDoc = new JsonObject();
					registerDoc.addProperty("type", "mentor");
					registerDoc.addProperty("years", year);

					cell = row.getCell(0);
					registerDoc.addProperty("name", cell.getStringCellValue());
					cell = row.getCell(1);
					registerDoc.addProperty("account", cell.getStringCellValue());
					cell = row.getCell(2);
					registerDoc.addProperty("phone_num", cell.getStringCellValue());
					cell = row.getCell(3);
					registerDoc.addProperty("belong", cell.getStringCellValue());
					cell = row.getCell(4);
					registerDoc.addProperty("section", cell.getStringCellValue());

					logger.debug(doc_util.putDoc(registerDoc));
					insertNum++;
				}
			}
		}
		logger.debug(updateNum +" mentors are updated and " + insertNum + " mentors are inserted.");
	}

	public void registerMentee() {

		sheet = workbook.getSheet("mentee");
		if (sheet == null)
			return;
		JsonObject registerDoc = new JsonObject();
		Row row;
		Cell cell;

		// 등록연도 가져오기 (기수 정보)
		row = sheet.getRow(4);
		cell = row.getCell(1);
		int year = (int) cell.getNumericCellValue();
		int num = 0;

		for (int rowIndex = 7; rowIndex <= sheet.getLastRowNum(); rowIndex++) {
			row = sheet.getRow(rowIndex);
			if (row != null) {
				registerDoc = new JsonObject();
				registerDoc.addProperty("type", "mentee");
				registerDoc.addProperty("year", year);

				cell = row.getCell(0);
				registerDoc.addProperty("name", cell.getStringCellValue());
				cell = row.getCell(1);
				registerDoc.addProperty("account", cell.getStringCellValue());
				cell = row.getCell(2);
				registerDoc.addProperty("phone_num", cell.getStringCellValue());
				cell = row.getCell(3);
				registerDoc.addProperty("belong", cell.getStringCellValue());

				logger.debug(doc_util.putDoc(registerDoc));
				num++;
			}
		}
		logger.debug("Total " + num + " mentees are registered in DB.");
	}

	public void registerProject() {

		List<Sheet> projectSheets = new ArrayList<Sheet>();
		for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
			String name = workbook.getSheetName(i);
			if (name.contains("Project")) {
				projectSheets.add(workbook.getSheetAt(i));
			}
		}
		if (projectSheets.size() == 0) return;
		logger.debug(projectSheets.size() + " Sheets");
		for (Sheet projectSheet : projectSheets) {
			int num = 0;
			JsonObject registerDoc = new JsonObject();
			sheet = projectSheet;
			Row row;
			Cell cell;

			// 등록정보 가져오기 (기수, 단계, 차수)
			cell = sheet.getRow(3).getCell(1);
			int year = (int) cell.getNumericCellValue();
			cell = sheet.getRow(4).getCell(1);
			int level = (int) cell.getNumericCellValue();
			cell = sheet.getRow(5).getCell(1);
			int st = (int) cell.getNumericCellValue();

			JsonArray stage = new JsonArray();
			stage.add(new JsonPrimitive(year));
			stage.add(new JsonPrimitive(level));
			stage.add(new JsonPrimitive(st));


			for (int rowIndex = 8; rowIndex <= sheet.getLastRowNum(); rowIndex++) {
				row = sheet.getRow(rowIndex);
				if (row != null) {
					registerDoc = new JsonObject();
					registerDoc.addProperty("type", "project");
					cell = row.getCell(0);
					registerDoc.addProperty("project_type", cell.getStringCellValue());
					registerDoc.add("stage", stage);
					cell = row.getCell(1);
					registerDoc.addProperty("mentor", doc_util.getUserId(cell.getStringCellValue()));
					cell = row.getCell(2);
					registerDoc.addProperty("title", cell.getStringCellValue());
					cell = row.getCell(3);
					registerDoc.addProperty("field", cell.getStringCellValue());

					JsonArray menteeList = new JsonArray();
					for (int i = 4; i < row.getLastCellNum(); i++) {
						String name = row.getCell(i).getStringCellValue();
						menteeList.add(new JsonPrimitive(doc_util.getUserId(name)));
					}
					registerDoc.add("mentee", menteeList);
					logger.debug(rowIndex + " : " + doc_util.putDoc(registerDoc));
				}
			}
			logger.debug("Total " + num + "projects are inserted in " + stage.toString());
		}
	}

}
