package org.example.basicMarket.entity.post;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.basicMarket.exception.UnsupportedImageFormatException;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.util.Arrays;
import java.util.UUID;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Image {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String uniqueName;

    @Column(nullable = false)
    private String originName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Post post; // 1

    private final static String supportedExtension[] = {"jpg", "jpeg", "gif", "bmp", "png"}; // 2

    public Image(String originName) {
        this.uniqueName = generateUniqueName(extractExtension(originName)); // 3
        this.originName = originName;
    }

    //Post의 연관 관계에 대한 정보가 없다면 이를 등록해줍니다.
    //이미지는 작성된 게시글에 소속되어야하므로, 다른 게시글로 연관 관계가 뒤바뀌면 안됩니다.
    //이를 위해 this.post가 null일 때만 초기화되도록 하였습니다.
    //post는 nullable=false이고, initPost 메소드는 Image가 처음 Post에 등록될 때 호출되므로, 연관 관계 정보가 없어지거나 뒤바뀌는 상황을 제한할 수 있을 것입니다.
    public void initPost(Post post) { // 4
        if(this.post == null) {
            this.post = post;
        }
    }

    private String generateUniqueName(String extension) { // 5
        return UUID.randomUUID().toString() + "." + extension;
    }

    private String extractExtension(String originName) { // 6
        try {
            String ext = originName.substring(originName.lastIndexOf(".") + 1);
            if(isSupportedFormat(ext)) return ext;
        } catch (StringIndexOutOfBoundsException e) { }
        throw new UnsupportedImageFormatException();
    }

    private boolean isSupportedFormat(String ext) { // 7
        return Arrays.stream(supportedExtension).anyMatch(e -> e.equalsIgnoreCase(ext));
    }

}
