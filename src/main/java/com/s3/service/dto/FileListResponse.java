package com.s3.service.dto;

import java.util.List;

public class FileListResponse {

    private List<FileInfoResponse> files;
    private String nextToken;
    private boolean hasMore;

    public FileListResponse() {
    }

    public FileListResponse(List<FileInfoResponse> files,
                            String nextToken,
                            boolean hasMore) {
        this.files = files;
        this.nextToken = nextToken;
        this.hasMore = hasMore;
    }

    public List<FileInfoResponse> getFiles() {
        return files;
    }

    public String getNextToken() {
        return nextToken;
    }

    public boolean isHasMore() {
        return hasMore;
    }
}
