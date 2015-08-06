package org.gradle;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

public class RegistrationUtil {

	DocumentUtil doc_util = new DocumentUtil("registtest");
	Workbook workbook;
	Sheet mentor;
	Sheet mentee;

	public RegistrationUtil() {
		try {
			FileInputStream fileInput = new FileInputStream("example.xlsx");
			workbook = new XSSFWorkbook(fileInput);
			mentor = workbook.getSheet("mentor");
			mentee = workbook.getSheet("mentee");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}	
	}
	
	public void registerMentor() {

		JsonObject registerDoc = new JsonObject();
		Row row;
		Cell cell;

		// 등록연도 가져오기 (기수 정보)
		row = mentor.getRow(4);
		cell = row.getCell(1);
		int year = (int) cell.getNumericCellValue();
		int num = mentor.getLastRowNum();

		for (int rowIndex = 7; rowIndex <= num; rowIndex++) {
			row = mentor.getRow(rowIndex);
			
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
					
					System.out.println(doc_util.updateDoc(registerDoc).toString());
				} 
				
				else {
					// 신규 등록
					registerDoc = new JsonObject();
					registerDoc.addProperty("type", "mentee");
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
					
					System.out.println(doc_util.putDoc(registerDoc));
				}
			}
		}
	}

	public void registerMentee() {

		JsonObject registerDoc = new JsonObject();
		Row row;
		Cell cell;

		// 등록연도 가져오기 (기수 정보)
		row = mentee.getRow(4);
		cell = row.getCell(1);
		int year = (int) cell.getNumericCellValue();
		int num = mentee.getLastRowNum();

		for (int rowIndex = 7; rowIndex <= num; rowIndex++) {
			row = mentee.getRow(rowIndex);
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

				System.out.println(doc_util.putDoc(registerDoc));
			}
		}
	}

}
