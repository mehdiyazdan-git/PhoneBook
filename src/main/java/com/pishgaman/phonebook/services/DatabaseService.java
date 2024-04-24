package com.pishgaman.phonebook.services;

import com.github.eloyzone.jalalicalendar.DateConverter;
import com.github.eloyzone.jalalicalendar.JalaliDate;
import com.pishgaman.phonebook.dtos.BackupFileDetails;
import com.pishgaman.phonebook.entities.AppSettings;
import com.pishgaman.phonebook.repositories.AppSettingsRepository;
import com.pishgaman.phonebook.utils.DatabaseUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DatabaseService {
    private final JdbcTemplate jdbcTemplate;
    private final AppSettingsRepository appSettingsRepository;


    @Value("${spring.datasource.username}")
    private String dbUsername;

    @Value("${spring.datasource.password}")
    private String dbPassword;

    @Value("${spring.datasource.url}")
    private String jdbcUrl;


    private void validateBackupDirectory(String backupDirectory) throws IOException {
        File dir = new File(backupDirectory);
        if (!dir.exists() || !dir.isDirectory()) {
            throw new IOException("محل ذخیره سازی فایل پشتیبان معتبر نمی باشد.: " + backupDirectory);
        }
        if (!dir.canWrite()) {
            throw new IOException("پوشه انتخاب شده قابل ویرایش نیست: " + backupDirectory);
        }
    }
    public String getDatabaseVersion() {
        String sql = "SELECT version()";
        return jdbcTemplate.queryForObject(sql, String.class);
    }




    public String getDatabaseSize() {
        String dbName = DatabaseUtils.extractDatabaseName(jdbcUrl);
        String sql = "SELECT pg_size_pretty(pg_database_size(?))";
        return jdbcTemplate.queryForObject(sql, String.class, dbName);
    }
    public void performBackup() throws IOException, InterruptedException {
        String osName = System.getProperty("os.name").toLowerCase();

        if (osName.contains("win")) {
            performBackupInWindowsOs();
        } else {
            performBackupInUnixOs();
        }
    }

    protected void performBackupInUnixOs() throws IOException, InterruptedException {

        String backupDirectory = getBackupPathFromAppSettings();
        String dbName = DatabaseUtils.extractDatabaseName(jdbcUrl);


        if (backupDirectory == null || backupDirectory.isEmpty()) {
            backupDirectory = "/var/backups/MyApp";
        }

        validateBackupDirectory(backupDirectory);
        createBackupDirectory(backupDirectory);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss");
        LocalDateTime now = LocalDateTime.now();
        String fileName = String.format("%s_%s.sql", dbName, formatter.format(now));
        String backupPath = String.format("%s/%s", backupDirectory, fileName);

        String command = String.format("pg_dump -U %s -d %s -f '%s'", dbUsername, dbName, backupPath);
        ProcessBuilder processBuilder = new ProcessBuilder("bash", "-c", command);
        processBuilder.environment().put("PGPASSWORD", dbPassword);

        processBuilder.directory(new File("/home/user")); // Adjust the working directory if necessary
        processBuilder.redirectErrorStream(true);

        Process process = processBuilder.start();
        int exitCode = process.waitFor();
        if (exitCode != 0) {
            throw new RuntimeException("عملیات پشتیبان گرفتن با خطا مواجه شد. خطا کد: " + exitCode);
        }
    }


    protected void performBackupInWindowsOs() throws IOException, InterruptedException {

        String backupDirectory = getBackupPathFromAppSettings();

        // If backup directory is not set in the application settings, use the default directory
        if (backupDirectory == null || backupDirectory.isEmpty()){
            backupDirectory = "C:\\Backups\\MyApp"; // Windows-specific directory path
        }

        validateBackupDirectory(backupDirectory);

        createBackupDirectory(backupDirectory);

        // Extract the database name from the JDBC URL
        String dbName = DatabaseUtils.extractDatabaseName(jdbcUrl);

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
            throw new RuntimeException("عملیات پشتیبان گرفتن با خطا مواجه شد. خطا کد: " + exitCode);
        }
    }

    public List<BackupFileDetails> getBackupFilesList() throws IOException {
        String backupDirectory = getBackupPathFromAppSettings();
        if (backupDirectory == null || backupDirectory.isEmpty()) {
            backupDirectory = "C:\\Backups\\MyApp";
        }
        File directory = new File(backupDirectory);
        File[] files = directory.listFiles();
        List<BackupFileDetails> backupFiles = new ArrayList<>();

        if (files != null) {
            for (File file : files) {
                if (!file.isDirectory()) {
                    BasicFileAttributes attrs = Files.readAttributes(file.toPath(), BasicFileAttributes.class);
                    org.joda.time.LocalDateTime localDateTime = new org.joda.time.LocalDateTime(attrs.creationTime().toMillis());

                    int hour = localDateTime.getHourOfDay();
                    int minute = localDateTime.getMinuteOfHour();
                    int second = localDateTime.getSecondOfMinute();

                    DateConverter dateConverter = new DateConverter();
                    JalaliDate jalaliDate = dateConverter.gregorianToJalali(
                            localDateTime.getYear(),
                            localDateTime.getMonthOfYear(),
                            localDateTime.getDayOfMonth()
                    );
                    int jalaliYear = jalaliDate.getYear();
                    int jalaliMonth = jalaliDate.getMonthPersian().getValue();
                    int jalaliDay = jalaliDate.getDay();


                    // concat the date and time parts into a single string
                    String creationDate = String.format("%d/%02d/%02d %02d:%02d:%02d", jalaliYear, jalaliMonth, jalaliDay, hour, minute, second);

                    String fileSize = String.format("%,d bytes", attrs.size());
                    String fileType = getFileExtension(file);

                    backupFiles.add(new BackupFileDetails(
                            file.getName(),
                            file.getAbsolutePath(),
                            fileSize,
                            fileType,
                            creationDate
                    ));
                }
            }
        }

        return backupFiles;
    }

    public void removeBackup(String filePath) {
        Path path = Paths.get(filePath);
        try {
            Assert.notNull(filePath, "مسیر فایل نباید خالی باشد"); // File path must not be null
            if (!Files.exists(path)) {
                throw new IOException("فایل وجود ندارد: " + filePath); // File does not exist
            }
            Files.delete(path);
        } catch (IOException e) {
            System.err.println("خطا در حذف فایل: " + e.getMessage());
            throw new RuntimeException("خطا در حذف فایل", e); // Error deleting file
        }
    }


    private String getFileExtension(File file) {
        String name = file.getName();
        int lastIndexOf = name.lastIndexOf(".");
        if (lastIndexOf == -1) {
            return ""; // empty extension
        }
        return name.substring(lastIndexOf + 1);
    }

    private String getBackupPathFromAppSettings() {
        Optional<AppSettings> appSettingsOptional = appSettingsRepository.findById(1L);

        if (appSettingsOptional.isEmpty()) {
            throw new RuntimeException("تنظمات دیتابیس یافت نشد");
        }

        AppSettings appSettings = appSettingsOptional.get();

        return appSettings.getBackupPath();
    }
    private void createBackupDirectory(String backupDirectory) throws IOException {
        File dir = new File(backupDirectory);
        if (!dir.exists()) {
            if (!dir.mkdirs()) {
                throw new IOException("امکان ایجاد پوشه پشتیبان وجود ندارد: " + backupDirectory);
            }
            System.out.println("پوشه پشتیبان ایجاد شد: " + backupDirectory);
        } else if (!dir.isDirectory()) {
            throw new IOException("پوشه پشتیبان یک پوشه نیست: " + backupDirectory);
        }
    }
}

