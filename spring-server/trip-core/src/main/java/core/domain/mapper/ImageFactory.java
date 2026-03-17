package core.domain.mapper;

import core.domain.entity.image.Image;

public class ImageFactory {

    public static Image of(String url){
        return Image.builder()
                .url(url)
                .build();
    }
}
