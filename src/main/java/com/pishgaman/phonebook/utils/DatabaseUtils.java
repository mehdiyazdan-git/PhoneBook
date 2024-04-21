package com.pishgaman.phonebook.utils;

public class DatabaseUtils {

    /**
     * Extracts the database name from a JDBC URL.
     * Assumes the URL ends with the database name after the last '/'.
     * @param jdbcUrl the JDBC URL
     * @return the extracted database name
     */
    public static String extractDatabaseName(String jdbcUrl) {
        int lastIndex = jdbcUrl.lastIndexOf("/");
        if (lastIndex != -1) {
            return jdbcUrl.substring(lastIndex + 1);
        }
        throw new IllegalArgumentException("Invalid JDBC URL format");
    }
}

