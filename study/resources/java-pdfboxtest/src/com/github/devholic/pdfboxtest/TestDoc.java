package com.github.devholic.pdfboxtest;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType0Font;
import org.docx4j.Docx4J;
import org.docx4j.model.datastorage.migration.VariablePrepare;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.openpackaging.parts.WordprocessingML.MainDocumentPart;


import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Font;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfWriter;

public class TestDoc {
	public static void main(String args[]) throws Exception {
//		renderDocx();
//		pdfboxSample();
//		iTextSample();
		renderDocx_mentoringReport();
	}

	public static void renderDocx() throws Exception {
		WordprocessingMLPackage wordPackage = WordprocessingMLPackage
				.load(new java.io.File("mentoringform.docx"));
		VariablePrepare.prepare(wordPackage);
		MainDocumentPart documentPart = wordPackage.getMainDocumentPart();
		HashMap<String, String> mappings = new HashMap<String, String>();
		mappings.put("TAG0", "");
		mappings.put("TAG1", "");
		mappings.put("TAG2", "O");
		mappings.put("TAG3", "");
		mappings.put("projectName", "SoMa Report");
		mappings.put("class", "안");
		mappings.put("section", "6기");
		documentPart.variableReplace(mappings);
		wordPackage.save(new File("mentoringfilled.docx"));
		OutputStream os = new java.io.FileOutputStream(new File(
				"mentoringfilled.pdf"));
		Docx4J.toPDF(wordPackage, os);
	}

	public static void pdfboxSample() throws IOException {
		PDDocument document = new PDDocument();
		PDPage page = new PDPage(PDRectangle.A4);
		document.addPage(page);
		File file = new File("nanumgothic.ttf");
		PDFont font = PDType0Font.load(document, file);
		PDPageContentStream contentStream = new PDPageContentStream(document,
				page);
		contentStream.beginText();
		contentStream.setFont(font, 12);
		contentStream.newLineAtOffset(50, 760);
		contentStream.showText("안녕하세요");
		contentStream.endText();
		contentStream.close();
		document.save("pdfbox_test.pdf");
		document.close();
	}

	public static void iTextSample() throws DocumentException, IOException {
		Document document = new Document();
		PdfWriter.getInstance(document, new FileOutputStream("itext_test.pdf"));
		BaseFont base = BaseFont.createFont("nanumgothic.ttf",
				BaseFont.IDENTITY_H, BaseFont.NOT_EMBEDDED);
		Font font = new Font(base, 12);
		document.open();
		document.add(new Paragraph("Hello안녕하세요", font));
		document.close();
	}
	
	public static void renderDocx_mentoringReport() throws Exception {
		System.out.println("renderDocx_mentoringReport excuted....");
		WordprocessingMLPackage wordPackage = WordprocessingMLPackage
				.load(new java.io.File("mentoringReport.docx"));
		VariablePrepare.prepare(wordPackage);
		MainDocumentPart documentPart = wordPackage.getMainDocumentPart();
		
		
		HashMap<String, String> mappings = new HashMap<String, String>();
		
		mappings.put("division1", "O");
		mappings.put("division2", "");
		mappings.put("division3", "");
		mappings.put("division4", "");
		
		mappings.put("projectName","SOMAReport");
		mappings.put("term","2015-07-01 ~ 2015.08-28");
		mappings.put("main_mento","김태완");
		mappings.put("sub_mento","고재관");
		mappings.put("section","웹");
		mappings.put("class","6기");
		mappings.put("stage","1단계 1차");
		mappings.put("field","웹");
		
		mappings.put("mentee1","민종현");
		mappings.put("mentee2","강성훈");
		mappings.put("mentee3","이재연");
		mappings.put("mentee4","");
		
		mappings.put("absent_reason1","집안일");
		mappings.put("absent_reason2","");
		mappings.put("absent_reason3","");
		mappings.put("absent_reason4","");
		
		mappings.put("times","3");
		mappings.put("date","2015-08-18");
		mappings.put("location","아람 6-2");
		mappings.put("start_time","19:00-22:00");
		mappings.put("except_time","");
		
		mappings.put("topic","개발 스코프 정의");
		mappings.put("purpose","개발 스코프를 정의한다");
		mappings.put("propel","개발을 하는것");
		mappings.put("solution","구글링을 해보기");
		mappings.put("plan","다음주까지 개발 완성");
		mappings.put("mento_opinion","잘하고 잇군요");
		mappings.put("etc","안녕 이건 기타란이야");
		mappings.put("content","안녕 이건 내용란이야 너는 무슨 내용이니?");
		

		documentPart.variableReplace(mappings);
		wordPackage.save(new File("mentoringReport_filled.docx"));
		
		
		OutputStream os = new java.io.FileOutputStream(new File(
				"mentoringReport_filled.pdf"));
		Docx4J.toPDF(wordPackage, os);
	}
}
