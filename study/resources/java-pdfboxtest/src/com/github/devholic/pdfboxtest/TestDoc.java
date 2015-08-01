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
		renderDocx();
		pdfboxSample();
		iTextSample();
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
		mappings.put("class", "웹");
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
		contentStream.showText("밯빪반발땊툼바품맔부삿붏웈찴뿞붛첦붖부쌵쎨뱌분쌵쳔얌부않모버윲옆엄엺퓴옆헐였촎몄썭엸억");
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
		document.add(new Paragraph("Helloㅎㅎ World!~~ 한글 테스트", font));
		document.close();
	}
}
