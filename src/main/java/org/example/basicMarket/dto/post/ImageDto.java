package org.example.basicMarket.dto.post;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.example.basicMarket.entity.post.Image;

@Data
@AllArgsConstructor
public class ImageDto {

    private Long id;
    private String originName;
    private String uniqueName;
    public static ImageDto toDto(Image image) {
        return new ImageDto(image.getId(), image.getOriginName(), image.getUniqueName());
    }
}
