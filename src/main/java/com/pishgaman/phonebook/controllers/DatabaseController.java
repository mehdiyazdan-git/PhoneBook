package com.pishgaman.phonebook.controllers;

import com.pishgaman.phonebook.dtos.BackupDto;
import com.pishgaman.phonebook.services.AppSettingsService;
import com.pishgaman.phonebook.services.DatabaseService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin
@RestController
@RequestMapping(path = "/api/v1/database")
@RequiredArgsConstructor
public class DatabaseController {

    private final DatabaseService databaseService;
    private final AppSettingsService appSettingsService;

    @GetMapping("/database-size")
    public String getDatabaseSize() {
        return databaseService.getDatabaseSize();
    }

    @GetMapping("/backup/{dbName}")
    public String backupDatabase(@PathVariable String dbName) {
        String backupDirectory = "/var/backups/myapp"; // Typically for Linux/Unix systems
        try {
            databaseService.performBackup(dbName, backupDirectory);
            return "Backup successful!";
        } catch (Exception e) {
            e.printStackTrace();
            return "Backup failed: " + e.getMessage();
        }
    }

    @GetMapping("/backup-windows/{dbName}")
    public String backupDatabaseInWindows(@PathVariable String dbName) {
        String backupDirectory = "C:\\Backups\\MyApp"; // Windows-specific directory path
        try {
            databaseService.performBackupInWindowsOs(dbName, backupDirectory);
            return "Backup successful!";
        } catch (Exception e) {
            e.printStackTrace();
            return "Backup failed: " + e.getMessage();
        }
    }

    @GetMapping("/settings/backup")
    public ResponseEntity<BackupDto> getBackupSettings() {
        try {
            BackupDto backupSettings = appSettingsService.getBackupSettings();
            return ResponseEntity.ok(backupSettings);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body(null);
        }
    }

    @PutMapping("/settings/backup")
    public ResponseEntity<String> updateBackupSettings(@RequestBody BackupDto backupDto) {
        try {
            appSettingsService.updateBackupSettings(backupDto);
            return ResponseEntity.ok("Backup settings updated successfully!");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body("Failed to update backup settings: " + e.getMessage());
        }
    }
}
