package com.pishgaman.phonebook.controllers;

import com.pishgaman.phonebook.services.DatabaseService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@CrossOrigin
@RestController
@RequestMapping(path = "/api/v1/database")
@RequiredArgsConstructor
public class DatabaseController {

    private final DatabaseService databaseService;

    @GetMapping("/database-size")
    public String getDatabaseSize() {
        return databaseService.getDatabaseSize();
    }

    @GetMapping("/backup")
    public ResponseEntity<?> backupDatabase() {
        try {
            databaseService.performBackup();
            return ResponseEntity.status(HttpStatus.OK).body("فایل پشتیبان با موفقیت ایجاد شد.");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("خطا در ایجاد فایل پشتیبان: " + e.getMessage());
        }
    }

    @GetMapping("/backup-files")
    public ResponseEntity<?> getBackupFiles() {
        try {
            return ResponseEntity.ok(databaseService.getBackupFilesList());
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("خطا در بازیابی فهرست فایل‌های پشتیبان: " + e.getMessage());
        }
    }

    @DeleteMapping("/backup-files")
    public ResponseEntity<String> deleteBackup(@RequestParam String filePath) {
        try {
            filePath = java.net.URLDecoder.decode(filePath, StandardCharsets.UTF_8);
            databaseService.removeBackup(filePath);
            return ResponseEntity.ok("فایل پشتیبان حذف شد.");
        } catch (Exception e) {
            System.err.println("Error deleting backup file: " + e.getMessage());

            // Return an error response with a more informative message
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("خطا در حذف فایل پشتیبان: " + e.getMessage());
        }
    }

}
