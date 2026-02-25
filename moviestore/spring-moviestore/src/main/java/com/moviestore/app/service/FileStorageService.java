package com.moviestore.app.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Objects;
import java.util.UUID;

@Service
public class FileStorageService {
  private final Path uploadRoot;

  public FileStorageService(@Value("${app.upload-dir:uploads}") String uploadDir) {
    this.uploadRoot = Paths.get(uploadDir).toAbsolutePath().normalize();
    try {
      Files.createDirectories(this.uploadRoot);
    } catch (IOException e) {
      throw new RuntimeException("Unable to initialize upload directory", e);
    }
  }

  public String store(MultipartFile file, String subdirectory) {
    if (file == null || file.isEmpty()) {
      throw new IllegalArgumentException("File is required");
    }

    String contentType = file.getContentType();
    if (contentType == null || !contentType.startsWith("image/")) {
      throw new IllegalArgumentException("Only image uploads are supported");
    }

    String original = Objects.requireNonNullElse(file.getOriginalFilename(), "image");
    String cleanName = original.replaceAll("[^a-zA-Z0-9._-]", "_");
    String filename = UUID.randomUUID() + "-" + cleanName;

    try {
      Path targetDir = uploadRoot.resolve(subdirectory).normalize();
      Files.createDirectories(targetDir);
      Path targetFile = targetDir.resolve(filename);
      Files.copy(file.getInputStream(), targetFile, StandardCopyOption.REPLACE_EXISTING);
      return "/uploads/" + subdirectory + "/" + filename;
    } catch (IOException e) {
      throw new RuntimeException("Failed to store file", e);
    }
  }

  public Path buildPath(String subdirectory, String filename) {
    try {
      Path targetDir = uploadRoot.resolve(subdirectory).normalize();
      Files.createDirectories(targetDir);
      return targetDir.resolve(filename);
    } catch (IOException e) {
      throw new RuntimeException("Failed to prepare file path", e);
    }
  }
}
