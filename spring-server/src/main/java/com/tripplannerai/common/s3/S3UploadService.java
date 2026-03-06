package com.tripplannerai.common.s3;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.ObjectMetadata;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class S3UploadService {

    private final AmazonS3Client s3Client;

    @Value("${s3.bucket}")
    private String bucket;

    public String upload(MultipartFile file) throws IOException {

        if(file.isEmpty()){
            return null;
        }
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentType(file.getContentType());
        metadata.setContentLength(file.getSize());
        String fileName = changeFileName();
        s3Client.putObject(bucket, fileName, file.getInputStream(), metadata);
        return fileName;
    }
    private String changeFileName() {
        return UUID.randomUUID().toString();
    }

    public String uploadReceiptReviewImage(MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            return null;
        }
        String originalFilename = file.getOriginalFilename();
        String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        String fileName = changeFileName() + extension;

        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentType(determineContentType(extension));
        metadata.setContentLength(file.getSize());

        s3Client.putObject(bucket, fileName, file.getInputStream(), metadata);
        return s3Client.getUrl(bucket,fileName).toString();
    }

    private String determineContentType(String extension) {
        switch (extension.toLowerCase()) {
            case ".jpg":
            case ".jpeg":
                return "image/jpeg";
            case ".png":
                return "image/png";
            case ".gif":
                return "image/gif";
            default:
                return "application/octet-stream";
        }
    }

}
