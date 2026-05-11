package com.kunal.admission.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class FileStorageService {

    @Value("${app.upload.dir}")
    private String uploadDir;

    /**
     * Saves a file to disk and returns the relative path.
     * E.g., "photo/uuid_filename.jpg"
     */
    public String save(MultipartFile file, String subfolder) {
        try {
            // Create subdirectory if it doesn't exist
            Path dir = Paths.get(uploadDir, subfolder);
            Files.createDirectories(dir);

            // Generate a unique filename to avoid conflicts
            String uniqueName = UUID.randomUUID() + "_" + file.getOriginalFilename();
            Path filePath = dir.resolve(uniqueName);

            // Write bytes to disk
            Files.write(filePath, file.getBytes());

            // Return relative path stored in DB
            return subfolder + "/" + uniqueName;

        } catch (IOException e) {
            throw new RuntimeException("Failed to store file: " + e.getMessage());
        }
    }
}