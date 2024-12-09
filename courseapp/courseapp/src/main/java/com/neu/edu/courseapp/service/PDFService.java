package com.neu.edu.courseapp.service;

import com.neu.edu.courseapp.modals.Course;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Service;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import org.springframework.http.HttpHeaders;

import java.io.IOException;
import java.util.List;

@Service
public class PDFService {

    public void generateRegisteredCoursesPDF(List<Course> registeredCourses, HttpServletResponse response) throws IOException {
        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename=RegisteredCourses.pdf");

        try {
            Document document = new Document();
            PdfWriter.getInstance(document, response.getOutputStream());
            document.open();

            // Add Title
            Font titleFont = new Font(Font.FontFamily.HELVETICA, 18, Font.BOLD);
            Paragraph title = new Paragraph("Registered Courses", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);
            document.add(new Paragraph(" ")); // Empty line

            // Add Content
            if (registeredCourses != null && !registeredCourses.isEmpty()) {
                PdfPTable table = new PdfPTable(7);
                table.setWidthPercentage(100);
                table.setSpacingBefore(10);

                // Add Table Headers
                addTableHeader(table, "Course Code");
                addTableHeader(table, "Course Number");
                addTableHeader(table, "Course Name");
                addTableHeader(table, "Instructor");
                addTableHeader(table, "Timings");
                addTableHeader(table, "Term");
                addTableHeader(table, "Number of Seats");

                // Add Table Data
                for (Course course : registeredCourses) {
                    addTableCell(table, course.getCourseCode());
                    addTableCell(table, course.getCourseNumber());
                    addTableCell(table, course.getCourseName());
                    addTableCell(table, course.getInstructor());
                    addTableCell(table, course.getTimings());
                    addTableCell(table, course.getTerm());
                    addTableCell(table, String.valueOf(course.getNumberOfSeats()));
                }

                document.add(table);
            } else {
                document.add(new Paragraph("No courses registered yet."));
            }

            document.close();
        } catch (DocumentException e) {
            throw new IOException("Error while creating PDF: " + e.getMessage());
        }
    }

    private void addTableHeader(PdfPTable table, String headerText) {
        Font headerFont = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD);
        PdfPCell header = new PdfPCell(new Phrase(headerText, headerFont));
        header.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(header);
    }

    private void addTableCell(PdfPTable table, String cellText) {
        PdfPCell cell = new PdfPCell(new Phrase(cellText));
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(cell);
    }

}
