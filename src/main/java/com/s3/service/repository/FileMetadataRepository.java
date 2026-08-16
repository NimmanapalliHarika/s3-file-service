package com.s3.service.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.s3.service.entity.FileMetadata;

public interface FileMetadataRepository extends JpaRepository<FileMetadata, Long>{

}
