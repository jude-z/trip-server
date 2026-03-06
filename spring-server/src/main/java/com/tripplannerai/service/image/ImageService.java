package com.tripplannerai.service.image;

import com.tripplannerai.entity.image.Image;
import com.tripplannerai.common.exception.image.NotFoundImageException;
import com.tripplannerai.repository.image.ImageRepository;
import com.tripplannerai.common.s3.S3DownloadService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
@Transactional
@RequiredArgsConstructor
public class ImageService {
    private final ImageRepository imageRepository;
    private final S3DownloadService s3DownloadService;

    public Resource fetchImage(Long id, HttpServletResponse response) throws IOException {
        Image image = imageRepository.findById(id).orElseThrow(()-> new NotFoundImageException("not image found"));
        String contentType = image.getContentType();
        response.setContentType(contentType);
        return s3DownloadService.getFileByteArrayFromS3(image.getUrl());
    }
}
