package com.neu.edu.courseapp.service;

import com.neu.edu.courseapp.modals.Course;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Service
public class ExcelService {

    @Autowired
    private SessionFactory sessionFactory;

    public void saveCoursesFromExcel(MultipartFile file) throws Exception {
        List<Course> courses = parseExcelFile(file.getInputStream());
        saveCoursesToDatabase(courses); // Save to database
    }

    private List<Course> parseExcelFile(InputStream is) throws Exception {
        List<Course> courses = new ArrayList<>();

        Workbook workbook = new XSSFWorkbook(is);
        Sheet sheet = workbook.getSheetAt(0);
        Iterator<Row> rows = sheet.iterator();

        // Skip header row
        if (rows.hasNext()) {
            rows.next();
        }

        while (rows.hasNext()) {
            Row row = rows.next();
            Course course = new Course();

            course.setSerialNumber(Integer.parseInt(getCellValue(row.getCell(0))));
            course.setCourseName(getCellValue(row.getCell(1)));
            course.setInstructor(getCellValue(row.getCell(2)));
            course.setTimings(getCellValue(row.getCell(3)));
            course.setHours(Integer.parseInt(getCellValue(row.getCell(4))));
            course.setTerm(getCellValue(row.getCell(5)));
            course.setSection(Integer.parseInt(getCellValue(row.getCell(6))));
            course.setCrn(Integer.parseInt(getCellValue(row.getCell(7))));
            course.setCampus(getCellValue(row.getCell(8)));
            course.setProgram(getCellValue(row.getCell(9)));
            course.setCourseNumber(getCellValue(row.getCell(10)));
            course.setCourseCode(getCellValue(row.getCell(11)));
            course.setNumberOfSeats(Integer.parseInt(getCellValue(row.getCell(12))));

            courses.add(course);
        }

        workbook.close();
        return courses;
    }

    private String getCellValue(Cell cell) {
        if (cell == null) {
            return "";
        }
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue();
            case NUMERIC:
                // Check if the numeric cell is a date
                if (DateUtil.isCellDateFormatted(cell)) {
                    return cell.getDateCellValue().toString();
                }
                // Otherwise, return the numeric value as a string
                return String.valueOf((int) cell.getNumericCellValue());
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            case BLANK:
                return "";
            default:
                return cell.toString();
        }
    }

    private void saveCoursesToDatabase(List<Course> courses) {
        Session session = sessionFactory.openSession();
        Transaction transaction = session.beginTransaction();

        try {
            for (Course course : courses) {
                session.save(course);
            }
            transaction.commit();
        } catch (Exception e) {
            transaction.rollback();
            throw e;
        } finally {
            session.close();
        }
    }
}
