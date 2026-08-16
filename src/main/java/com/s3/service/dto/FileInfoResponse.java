package com.s3.service.dto;

public class FileInfoResponse {

    private String fileName;
    private String objectKey;
    private long size;
    private String lastModified;

    public FileInfoResponse() {
    }

    public FileInfoResponse(String fileName, String objectKey,
                            long size, String lastModified) {
        this.fileName = fileName;
        this.objectKey = objectKey;
        this.size = size;
        this.lastModified = lastModified;
    }

    public String getFileName() {
        return fileName;
    }

    public String getObjectKey() {
        return objectKey;
    }

    public long getSize() {
        return size;
    }

    public String getLastModified() {
        return lastModified;
    }
}
