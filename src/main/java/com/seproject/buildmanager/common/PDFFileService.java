package com.seproject.buildmanager.common;

import java.util.stream.Stream;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

@Component
@Scope("prototype") // 毎回インスタンスを生成し、他クラスと共有しない
public class PDFFileService extends FileService {

  public PDFFileService(@Value("${file.server.address}") String path) {
    super(path);
    // TODO 自動生成されたコンストラクター・スタブ
  }

  public void initializePDF(String fileName) {
    this.setFileName(fileName);
  }

  public void writerPDF() {
    this.importOutputStream();
    Document document = new Document();
    try {
      PdfWriter.getInstance(document, this.output);
      document.open();
      PdfPTable table = new PdfPTable(3);
      addTableHeader(table);
      addRows(table);
      addRows(table);

      document.add(table);
    } catch (DocumentException e) {
      // TODO 自動生成された catch ブロック
      e.printStackTrace();
    } finally {
      document.close();
    }

  }

  private void addTableHeader(PdfPTable table) {
    Stream.of("column header 1", "column header 2", "column header 3").forEach(columnTitle -> {
      PdfPCell header = new PdfPCell();
      header.setBackgroundColor(BaseColor.LIGHT_GRAY);
      header.setBorderWidth(2);
      header.setPhrase(new Phrase(columnTitle));
      table.addCell(header);
    });
  }

  private void addRows(PdfPTable table) {
    table.addCell("row 1, col 1");
    table.addCell("row 1, col 2");
    table.addCell("row 1, col 3");
  }
}
