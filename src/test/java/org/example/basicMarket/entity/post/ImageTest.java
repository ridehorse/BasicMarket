package org.example.basicMarket.entity.post;

import org.example.basicMarket.exception.UnsupportedImageFormatException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.example.basicMarket.factory.entity.ImageFactory.createImage;
import static org.example.basicMarket.factory.entity.ImageFactory.createImageWithOriginName;
import static org.example.basicMarket.factory.entity.PostFactory.createPost;

public class ImageTest {

    @Test
    void createImageTest() {
        // given
        String validExtension = "JPEG";

        // when, then
        createImageWithOriginName("image." + validExtension);
    }

    @Test
    void createImageExceptionByUnsupportedFormatTest() {
        // given
        String invalidExtension = "invalid";

        // when, then
        assertThatThrownBy(() -> createImageWithOriginName("image." + invalidExtension))
                .isInstanceOf(UnsupportedImageFormatException.class);
    }

    @Test
    void createImageExceptionByNoneExtensionTest() {
        // given
        String originName = "image";

        // when, then
        assertThatThrownBy(() -> createImageWithOriginName(originName))
                .isInstanceOf(UnsupportedImageFormatException.class);
    }

    @Test
    void initPostTest() {
        // given
        Image image = createImage();

        // when
        Post post = createPost();
        image.initPost(post);

        // then
        assertThat(image.getPost()).isSameAs(post);
    }

    @Test
    void initPostNotChangedTest() {
        // given
        Image image = createImage();
        image.initPost(createPost());

        // when
        Post post = createPost();
        image.initPost(post);

        // then
        assertThat(image.getPost()).isNotSameAs(post);
    }
}
