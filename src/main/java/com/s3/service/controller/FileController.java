package com.s3.service.controller;

import java.util.List;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.s3.service.S3Service;
import com.s3.service.dto.FileDetailsResponse;
import com.s3.service.dto.FileDownloadResponse;
import com.s3.service.dto.FileInfoResponse;
import com.s3.service.dto.FileListResponse;
import com.s3.service.dto.FileUploadResponse;
import com.s3.service.entity.FileMetadata;

@RestController
@RequestMapping("/files")
public class FileController {
	private final S3Service s3Service;

    public FileController(S3Service s3Service) {
        this.s3Service = s3Service;
    }
    
    @PostMapping("/upload")
    public ResponseEntity<FileUploadResponse> uploadFile(
            @RequestParam("file") MultipartFile file) {
    	FileUploadResponse response = s3Service.uploadFile(file);

        return ResponseEntity.ok(response);
    }
    @GetMapping("/download")
    public ResponseEntity<byte[]> downloadFile(
            @RequestParam("key") String objectKey) {

        FileDownloadResponse response =
                s3Service.downloadFile(objectKey);

        return ResponseEntity.ok()
                .contentType(
                        MediaType.parseMediaType(response.getContentType())
                )
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + response.getFileName() + "\""
                )
                .body(response.getData());
    }
    @DeleteMapping
    public ResponseEntity<String> deleteFile(
            @RequestParam("key") String objectKey) {

        s3Service.deleteFile(objectKey);

        return ResponseEntity.ok("File deleted successfully");
    }
    @GetMapping("/s3")
    public ResponseEntity<FileListResponse> listFiles(
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String continuationToken) {

        FileListResponse response =
                s3Service.listFiles(pageSize, continuationToken);

        return ResponseEntity.ok(response);
    }
    @GetMapping("/presigned-url")
    public ResponseEntity<String> generatePresignedUrl(
            @RequestParam("key") String objectKey) {

        String url = s3Service.generatePresignedUrl(objectKey);

        return ResponseEntity.ok(url);
    }
    @GetMapping
    public ResponseEntity<List<FileMetadata>> getAllFiles() {
        return ResponseEntity.ok(s3Service.getAllFiles());
    }
    @GetMapping("/{id}")
    public ResponseEntity<FileDetailsResponse> getFileById(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                s3Service.getFileById(id)
        );
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteFileById(
            @PathVariable Long id) {

        s3Service.deleteFileById(id);

        return ResponseEntity.ok("File deleted successfully");
    }
}
