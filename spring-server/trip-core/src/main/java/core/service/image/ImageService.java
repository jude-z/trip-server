package core.service.image;

import core.common.Status;
import core.common.exception.CommonException;
import core.domain.entity.image.Image;
import core.infra.jpa.image.ImageRepository;
import core.service.s3.S3DownloadService;
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
        Image image = imageRepository.findById(id).orElseThrow(()-> new CommonException(Status.NOT_FOUND_IMAGE));
        String contentType = image.getContentType();
        response.setContentType(contentType);
        return s3DownloadService.getFileByteArrayFromS3(image.getUrl());
    }
}
