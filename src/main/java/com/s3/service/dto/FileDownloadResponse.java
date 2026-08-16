package com.s3.service.dto;

public class FileDownloadResponse {

    private byte[] data;
    private String contentType;
    private String fileName;

    public FileDownloadResponse() {
    }

    public FileDownloadResponse(byte[] data, String contentType, String fileName) {
        this.data = data;
        this.contentType = contentType;
        this.fileName = fileName;
    }

    public byte[] getData() {
        return data;
    }

    public String getContentType() {
        return contentType;
    }

    public String getFileName() {
        return fileName;
    }
}
