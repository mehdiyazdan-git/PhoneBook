package com.pishgaman.phonebook.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class LocalFileStorageService implements FileStorageService {

    @Value("${file.storage.location}")
    private String storageLocation;

    @Override
    public String storeFile(MultipartFile file) {
        try {
            if (file.isEmpty()) {
                throw new IllegalStateException("Cannot store empty file");
            }

            // Create the directory if it does not exist
            Path storagePath = Paths.get(storageLocation);
            if (!Files.exists(storagePath)) {
                Files.createDirectories(storagePath);
            }

            // Generate a unique filename to avoid overwriting existing files
            String originalFilename = file.getOriginalFilename();
            String fileExtension = originalFilename != null ? originalFilename.substring(originalFilename.lastIndexOf(".")) : "";
            String storedFilename = System.currentTimeMillis() + fileExtension;

            // Save the file
            Path targetLocation = storagePath.resolve(storedFilename);
            Files.copy(file.getInputStream(), targetLocation);

            // Return the relative path to the stored file
            return storageLocation + "/" + storedFilename;

        } catch (IOException e) {
            throw new RuntimeException("Failed to store file", e);
        }
    }
}
