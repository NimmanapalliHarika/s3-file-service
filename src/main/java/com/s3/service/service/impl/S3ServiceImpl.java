package com.s3.service.service.impl;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.s3.service.S3Service;
import com.s3.service.dto.FileDetailsResponse;
import com.s3.service.dto.FileDownloadResponse;
import com.s3.service.dto.FileInfoResponse;
import com.s3.service.dto.FileListResponse;
import com.s3.service.dto.FileUploadResponse;
import com.s3.service.entity.FileMetadata;
import com.s3.service.exception.FileNotFoundException;
import com.s3.service.exception.InvalidFileException;
import com.s3.service.repository.FileMetadataRepository;

import software.amazon.awssdk.awscore.exception.AwsServiceException;
import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.core.exception.SdkClientException;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Request;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Response;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;

@Service
public class S3ServiceImpl implements S3Service{
	private final S3Client s3Client;
	private S3Presigner s3Presigner;
	private final FileMetadataRepository fileMetadataRepository;

	public S3ServiceImpl(
	        S3Client s3Client,
	        S3Presigner s3Presigner,
	        FileMetadataRepository fileMetadataRepository) {

	    this.s3Client = s3Client;
	    this.s3Presigner = s3Presigner;
	    this.fileMetadataRepository = fileMetadataRepository;
	}
    @Value("${aws.s3.bucket-name}")
    private String bucketName;
    @Value("${aws.region}")
    private String region;
    

	@Override
	public FileUploadResponse uploadFile(MultipartFile file) {
		if (file.isEmpty()) {
			throw new InvalidFileException("File cannot be empty.");
		}
		long maxFileSize = 5 * 1024 * 1024;

		if (file.getSize() > maxFileSize) {
		    throw new InvalidFileException(
		            "File size must not exceed 5 MB."
		    );
		}
		String contentType = file.getContentType();

		if (!"image/jpeg".equals(contentType)
		        && !"image/png".equals(contentType)) {

			throw new InvalidFileException("Only JPG and PNG files are allowed.");

		}
		String fileName = UUID.randomUUID() + "-" + file.getOriginalFilename();
		String objectKey = "products/" + fileName;
		String fileUrl = "https://" + bucketName
        + ".s3." + region
        + ".amazonaws.com/" + objectKey;
		PutObjectRequest request = PutObjectRequest.builder()
		        .bucket(bucketName)
		        .key(objectKey)
		        .contentType(file.getContentType())
		        .build();
		try {
			s3Client.putObject(
			        request,
			        RequestBody.fromInputStream(
			                file.getInputStream(),
			                file.getSize()
			        )
			);
			FileMetadata metadata = new FileMetadata();

			metadata.setOriginalFileName(file.getOriginalFilename());
			metadata.setS3ObjectKey(objectKey);
			metadata.setContentType(file.getContentType());
			metadata.setFileSize(file.getSize());
			metadata.setUploadedAt(LocalDateTime.now());

			fileMetadataRepository.save(metadata);
			FileUploadResponse response = new FileUploadResponse();

			response.setSuccess(true);
			response.setMessage("File uploaded successfully");
			response.setFileName(fileName);
			response.setFileUrl(fileUrl);

			return response;
		} catch (AwsServiceException | SdkClientException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	@Override
	public FileDownloadResponse downloadFile(String objectKey) {

	    GetObjectRequest request = GetObjectRequest.builder()
	            .bucket(bucketName)
	            .key(objectKey)
	            .build();

	    ResponseBytes<GetObjectResponse> response =
	            s3Client.getObjectAsBytes(request);

	    GetObjectResponse objectResponse = response.response();

	    String contentType = objectResponse.contentType();

	    return new FileDownloadResponse(
	            response.asByteArray(),
	            contentType,
	            objectKey.substring(objectKey.lastIndexOf("/") + 1)
	    );
	}
	@Override
	public void deleteFile(String objectKey) {

	    DeleteObjectRequest request = DeleteObjectRequest.builder()
	            .bucket(bucketName)
	            .key(objectKey)
	            .build();

	    s3Client.deleteObject(request);
	}
	@Override
	public FileListResponse listFiles(Integer pageSize,
	                                  String continuationToken) {

	    ListObjectsV2Request.Builder requestBuilder =
	            ListObjectsV2Request.builder()
	                    .bucket(bucketName)
	                    .prefix("products/")
	                    .maxKeys(pageSize);

	    if (continuationToken != null && !continuationToken.isBlank()) {
	        requestBuilder.continuationToken(continuationToken);
	    }

	    ListObjectsV2Response response =
	            s3Client.listObjectsV2(requestBuilder.build());

	    List<FileInfoResponse> files = response.contents()
	            .stream()
	            .map(object -> new FileInfoResponse(
	                    object.key().substring(
	                            object.key().lastIndexOf("/") + 1
	                    ),
	                    object.key(),
	                    object.size(),
	                    object.lastModified().toString()
	            ))
	            .toList();

	    return new FileListResponse(
	            files,
	            response.nextContinuationToken(),
	            response.isTruncated()
	    );
	}
	@Override
	public String generatePresignedUrl(String objectKey) {

	    GetObjectRequest getObjectRequest =
	            GetObjectRequest.builder()
	                    .bucket(bucketName)
	                    .key(objectKey)
	                    .build();

	    GetObjectPresignRequest presignRequest =
	            GetObjectPresignRequest.builder()
	                    .signatureDuration(Duration.ofMinutes(10))
	                    .getObjectRequest(getObjectRequest)
	                    .build();

	    PresignedGetObjectRequest presignedRequest =
	            s3Presigner.presignGetObject(presignRequest);

	    return presignedRequest.url().toString();
	}
	@Override
	public List<FileMetadata> getAllFiles() {
	    return fileMetadataRepository.findAll();
	}
	@Override
	public FileDetailsResponse getFileById(Long id) {

	    FileMetadata metadata = fileMetadataRepository.findById(id)
	            .orElseThrow(() -> new FileNotFoundException("File not found with id: " + id));

	    String downloadUrl = generatePresignedUrl(
	            metadata.getS3ObjectKey()
	    );

	    return new FileDetailsResponse(
	            metadata.getId(),
	            metadata.getOriginalFileName(),
	            metadata.getContentType(),
	            metadata.getFileSize(),
	            metadata.getUploadedAt(),
	            downloadUrl
	    );
	}
	@Override
	public void deleteFileById(Long id) {

	    FileMetadata metadata = fileMetadataRepository.findById(id)
	            .orElseThrow(() -> new RuntimeException("File not found"));

	    // Delete actual file from S3
	    deleteFile(metadata.getS3ObjectKey());

	    // Delete metadata from MySQL
	    fileMetadataRepository.deleteById(id);
	}
}
