package com.tripplannerai.controller.image;

import com.tripplannerai.service.image.ImageService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.net.http.HttpResponse;

@RestController
@RequiredArgsConstructor
public class ImageController {
    private final ImageService imageService;
    @GetMapping("/image/{id}")
    public Resource fetchImage(@PathVariable Long id, HttpServletResponse response) throws IOException {
        return imageService.fetchImage(id,response);
    }
}
