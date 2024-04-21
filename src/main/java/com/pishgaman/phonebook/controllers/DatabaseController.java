package com.pishgaman.phonebook.controllers;

import com.pishgaman.phonebook.services.DatabaseService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

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
    @GetMapping("/backup/{dbName}")
    public String backupDatabase(@PathVariable String dbName) {
        String backupDirectory = "/var/backups/myapp";
        try {
            databaseService.performBackup(dbName, backupDirectory);
            return "Backup successful!";
        } catch (Exception e) {
            e.printStackTrace();
            return "Backup failed: " + e.getMessage();
        }
    }
}
