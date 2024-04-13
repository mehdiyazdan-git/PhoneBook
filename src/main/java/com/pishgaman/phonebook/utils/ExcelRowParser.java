package com.pishgaman.phonebook.utils;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.time.LocalDate;

import com.github.eloyzone.jalalicalendar.DateConverter;
import com.github.eloyzone.jalalicalendar.JalaliDate;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;

public class ExcelRowParser {

    public static <T> T parseRowToDto(Row row, Class<T> dtoClass, int rowNum) {
        try {
            Constructor<T> constructor = dtoClass.getDeclaredConstructor();
            T dtoInstance = constructor.newInstance();
            Field[] fields = dtoClass.getDeclaredFields();

            for (int colNum = 0; colNum < fields.length; colNum++) {
                Field field = fields[colNum];
                field.setAccessible(true);
                Object value = getCellValue(row.getCell(colNum), field.getType());
                field.set(dtoInstance, value);
            }
            return dtoInstance;
        } catch (Exception e) {
            throw new RuntimeException("Error creating DTO instance: " + e.getMessage(), e);
        }
    }

    private static Object getCellValue(Cell cell, Class<?> targetType) {
        if (cell == null || cell.getCellType() == CellType.BLANK) {
            return null;
        }

        if (targetType == String.class) {
            return cell.getStringCellValue();
        } else if (targetType == Long.class) {
            return convertCellToLong(cell);
        } else if (targetType == LocalDate.class) {
            return convertCellToDate(cell);
        } else if (targetType == Boolean.class) {
            return convertCellToBoolean(cell);
        }

        throw new IllegalArgumentException("Unsupported target type: " + targetType);
    }

    private static Long convertCellToLong(Cell cell) {
        if (cell.getCellType() == CellType.NUMERIC) {
            return (long) cell.getNumericCellValue();
        } else if (cell.getCellType() == CellType.STRING) {
            String cellValue = cell.getStringCellValue();
            return cellValue.isEmpty() ? null : Long.parseLong(cellValue);
        } else {
            throw new IllegalArgumentException("Cell contains non-numeric data");
        }
    }

    private static LocalDate convertCellToDate(Cell cell) {
        DateConverter dateConverter = new DateConverter();
        String[] parts = cell.getStringCellValue().split("-");
        int jalaliYear = Integer.parseInt(parts[0]);
        int jalaliMonth = Integer.parseInt(parts[1]);
        int jalaliDay = Integer.parseInt(parts[2]);
        JalaliDate jalaliDate = dateConverter.gregorianToJalali(jalaliYear, jalaliMonth, jalaliDay);
        if (jalaliDate != null) {
            return LocalDate.of(jalaliDate.getYear(), jalaliDate.getMonthPersian().getValue(), jalaliDate.getDay());
        }
        return null;
    }

    private static Boolean convertCellToBoolean(Cell cell) {
        if (cell.getCellType() == CellType.BOOLEAN) {
            return cell.getBooleanCellValue();
        } else if (cell.getCellType() == CellType.STRING) {
            String cellValue = cell.getStringCellValue();
            return cellValue.isEmpty() ? null : Boolean.parseBoolean(cellValue);
        } else {
            throw new IllegalArgumentException("Cell contains non-boolean data");
        }
    }
}

