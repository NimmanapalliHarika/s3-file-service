package com.s3.service;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.s3.service.dto.FileDetailsResponse;
import com.s3.service.dto.FileDownloadResponse;
import com.s3.service.dto.FileInfoResponse;
import com.s3.service.dto.FileListResponse;
import com.s3.service.dto.FileUploadResponse;
import com.s3.service.entity.FileMetadata;



public interface S3Service {
	FileUploadResponse uploadFile(MultipartFile file);
	FileDownloadResponse downloadFile(String objectKey);
	void deleteFile(String objectKey);
	FileListResponse listFiles(Integer pageSize, String continuationToken);
	String generatePresignedUrl(String objectKey);
	List<FileMetadata> getAllFiles();
	FileDetailsResponse getFileById(Long id);
	void deleteFileById(Long id);

}
