package com.pishgaman.phonebook.services;

import com.pishgaman.phonebook.utils.DatabaseUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
public class DatabaseService {
    private final JdbcTemplate jdbcTemplate;

    @Value("${spring.datasource.username}")
    private String dbUsername;

    @Value("${spring.datasource.password}")
    private String dbPassword;

    @Value("${spring.datasource.url}")
    private String jdbcUrl;

    /**
     * Retrieves the size of the database in a human-readable format.
     * @return the size of the database as a string
     */
    public String getDatabaseSize() {
        String dbName = DatabaseUtils.extractDatabaseName(jdbcUrl);
        String sql = "SELECT pg_size_pretty(pg_database_size(?))";
        return jdbcTemplate.queryForObject(sql, String.class, dbName);
    }

    public void performBackup(String dbName, String backupDirectory) throws IOException, InterruptedException {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss");
        LocalDateTime now = LocalDateTime.now();
        String fileName = String.format("%s_%s.sql", dbName, formatter.format(now));
        String backupPath = String.format("%s/%s", backupDirectory, fileName);

        String command = String.format("pg_dump -U %s -d %s -f %s", dbUsername, dbName, backupPath);
        ProcessBuilder processBuilder = new ProcessBuilder(command.split(" "));
        processBuilder.environment().put("PGPASSWORD", dbPassword);

        processBuilder.directory(new java.io.File("/home/user"));
        processBuilder.redirectErrorStream(true);

        Process process = processBuilder.start();
        int exitCode = process.waitFor();
        if (exitCode != 0) {
            throw new RuntimeException("Backup failed with exit code " + exitCode);
        }
    }
}
