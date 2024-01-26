package org.example.basicMarket.factory.entity;

import org.example.basicMarket.entity.post.Image;

public class ImageFactory {

    public static Image createImage() {
        return new Image("origin_filename.jpg");
    }

    public static Image createImageWithOriginName(String originName) {
        return new Image(originName);
    }
}
