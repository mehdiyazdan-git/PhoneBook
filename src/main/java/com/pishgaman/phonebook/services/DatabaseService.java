package com.pishgaman.phonebook.services;

import com.pishgaman.phonebook.utils.DatabaseUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.io.File;
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

    private void validateBackupDirectory(String backupDirectory) throws IOException {
        File dir = new File(backupDirectory);
        if (!dir.exists() || !dir.isDirectory()) {
            throw new IOException("Backup directory does not exist: " + backupDirectory);
        }
        if (!dir.canWrite()) {
            throw new IOException("Backup directory is not writable: " + backupDirectory);
        }
    }

    public String getDatabaseSize() {
        String dbName = DatabaseUtils.extractDatabaseName(jdbcUrl);
        String sql = "SELECT pg_size_pretty(pg_database_size(?))";
        return jdbcTemplate.queryForObject(sql, String.class, dbName);
    }

    public void performBackup(String dbName, String backupDirectory) throws IOException, InterruptedException {
        validateBackupDirectory(backupDirectory);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss");
        LocalDateTime now = LocalDateTime.now();
        String fileName = String.format("%s_%s.sql", dbName, formatter.format(now));
        String backupPath = String.format("%s/%s", backupDirectory, fileName);

        String command = String.format("pg_dump -U %s -d %s -f %s", dbUsername, dbName, backupPath);
        ProcessBuilder processBuilder = new ProcessBuilder(command.split(" "));
        processBuilder.environment().put("PGPASSWORD", dbPassword);

        processBuilder.directory(new File("/home/user"));
        processBuilder.redirectErrorStream(true);

        Process process = processBuilder.start();
        int exitCode = process.waitFor();
        if (exitCode != 0) {
            throw new RuntimeException("Backup failed with exit code " + exitCode);
        }
    }

    public void performBackupInWindowsOs(String dbName, String backupDirectory) throws IOException, InterruptedException {
        validateBackupDirectory(backupDirectory);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss");
        LocalDateTime now = LocalDateTime.now();
        String fileName = String.format("%s_%s.sql", dbName, formatter.format(now));
        String backupPath = String.format("%s\\%s", backupDirectory, fileName);

        String command = String.format("pg_dump -U %s -d %s -f \"%s\"", dbUsername, dbName, backupPath);
        ProcessBuilder processBuilder = new ProcessBuilder("cmd", "/c", command);
        processBuilder.environment().put("PGPASSWORD", dbPassword);

        processBuilder.directory(new File("C:\\Users\\user"));
        processBuilder.redirectErrorStream(true);

        Process process = processBuilder.start();
        int exitCode = process.waitFor();
        if (exitCode != 0) {
            throw new RuntimeException("Backup failed with exit code " + exitCode);
        }
    }
}
