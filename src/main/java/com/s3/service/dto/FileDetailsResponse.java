package com.s3.service.dto;

import java.time.LocalDateTime;

public class FileDetailsResponse {

    private Long id;
    private String fileName;
    private String contentType;
    private long fileSize;
    private LocalDateTime uploadedAt;
    private String downloadUrl;

    public FileDetailsResponse() {
    }

    public FileDetailsResponse(Long id,
                               String fileName,
                               String contentType,
                               long fileSize,
                               LocalDateTime uploadedAt,
                               String downloadUrl) {
        this.id = id;
        this.fileName = fileName;
        this.contentType = contentType;
        this.fileSize = fileSize;
        this.uploadedAt = uploadedAt;
        this.downloadUrl = downloadUrl;
    }

    public Long getId() {
        return id;
    }

    public String getFileName() {
        return fileName;
    }

    public String getContentType() {
        return contentType;
    }

    public long getFileSize() {
        return fileSize;
    }

    public LocalDateTime getUploadedAt() {
        return uploadedAt;
    }

    public String getDownloadUrl() {
        return downloadUrl;
    }
}
