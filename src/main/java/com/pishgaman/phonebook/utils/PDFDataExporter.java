package com.pishgaman.phonebook.utils;


import com.itextpdf.io.font.FontProgram;
import com.itextpdf.io.font.FontProgramFactory;
import com.itextpdf.io.font.PdfEncodings;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.SolidBorder;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.element.Text;
import com.itextpdf.layout.properties.BaseDirection;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import com.itextpdf.layout.properties.VerticalAlignment;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.List;

public class PDFDataExporter {
    public static final String REGULAR =
            "src/main/resources/fonts/IRANSansMonoSpacedNum_Black.ttf";
    public static final String BOLD =
            "src/main/resources/fonts/IRANSansMonoSpacedNum_Bold.ttf";

    public static <T> byte[] exportData(List<T> data, Class<T> dtoClass) throws IOException, IllegalAccessException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        PdfWriter writer = new PdfWriter(byteArrayOutputStream);
        PdfDocument pdfDoc = new PdfDocument(writer);
        Document document = new Document(pdfDoc);

        FontProgram fontProgram = FontProgramFactory.createFont(REGULAR);
        // Embedded and use Unicode
        PdfFont font = PdfFontFactory.createFont(
                fontProgram,
                PdfEncodings.IDENTITY_H,
                PdfFontFactory.EmbeddingStrategy.PREFER_EMBEDDED);

        // Create a table with a number of columns equal to the DTO fields count
        Field[] fields = dtoClass.getDeclaredFields();
        Table table = new Table(UnitValue.createPercentArray(fields.length)).useAllAvailableWidth();
        table.setBaseDirection(BaseDirection.RIGHT_TO_LEFT);

        // Setting RTL direction for the document
        document.setTextAlignment(TextAlignment.RIGHT);
        document.setBaseDirection(BaseDirection.RIGHT_TO_LEFT);

        for (Field field : fields) {
            Paragraph p = new Paragraph(field.getName())
                    .setFont(font)
                    .setTextAlignment(TextAlignment.RIGHT)
                    .setFontScript(Character.UnicodeScript.ARABIC);
            p.setBaseDirection(BaseDirection.RIGHT_TO_LEFT);
            Cell headerCell = new Cell()
                    .add(p)
                    .setVerticalAlignment(VerticalAlignment.MIDDLE)
                    .setBorder(new SolidBorder(1));
            table.addHeaderCell(headerCell);
        }

        // Populate data rows with the custom font and RTL direction
        for (T item : data) {
            for (Field field : fields) {
                field.setAccessible(true);
                Object value = field.get(item);
                Paragraph p = new Paragraph(value == null ? "" : value.toString());
                p.setFont(font).setTextAlignment(TextAlignment.RIGHT);
                Cell cell = new Cell()
                        .add(p)
                        .setVerticalAlignment(VerticalAlignment.MIDDLE)
                        .setBorder(new SolidBorder(1));
                table.addCell(cell);
            }
        }

        document.add(table);
        document.close();

        return byteArrayOutputStream.toByteArray();
    }
}
