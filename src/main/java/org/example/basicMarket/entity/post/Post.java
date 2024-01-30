package org.example.basicMarket.entity.post;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.basicMarket.dto.post.PostUpdateRequest;
import org.example.basicMarket.entity.category.Category;
import org.example.basicMarket.entity.common.EntityDate;
import org.example.basicMarket.entity.member.Member;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.springframework.web.multipart.MultipartFile;


import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static java.util.stream.Collectors.toList;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Post extends EntityDate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    @Lob
    private String content;

    @Column(nullable = false)
    private Long price;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Member member; // 1

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Category category; // 2

    @OneToMany(mappedBy = "post", cascade = CascadeType.PERSIST, orphanRemoval = true)
    private List<Image> images; // 3

    public Post(String title, String content, Long price, Member member, Category category, List<Image> images) {
        this.title = title;
        this.content = content;
        this.price = price;
        this.member = member;
        this.category = category;
        this.images = new ArrayList<>();
        addImages(images); // 4
    }

    private void addImages(List<Image> added) { // 5
        added.stream().forEach(i -> {
            images.add(i);
            i.initPost(this);
        });
    }

    // 게시물 수정 요청해 의해 이미지가 새롭게 업데이트 될 경우에 객체로 따로 구분했다.
    @Getter
    @AllArgsConstructor
    public static class ImageUpdatedResult { // 4
        private List<MultipartFile> addedImageFiles;
        private List<Image> addedImages;
        private List<Image> deletedImages;
    }

    // PostUpdateRequest <title,content,price,List<MultipartFile>addedImage,List<Long>deletedImage>
    public ImageUpdatedResult update(PostUpdateRequest req) { // 1
        this.title = req.getTitle();
        this.content = req.getContent();
        this.price = req.getPrice();
        // PostUpdateRequest에서 보낸 이미지 정보에 따라 바로 Post객체에 업데이트될 이미지와 제거될 이미지가 적용 되는것아니라,
        // ImageUpdatedResult라는 객체에 이미지의 수정요청 정보를 저장한뒤에 이 객체에서 정보를 꺼내 업데이트와 삭제를 진행한다.
        ImageUpdatedResult result = findImageUpdatedResult(req.getAddedImages(), req.getDeletedImages());
        addImages(result.getAddedImages());
        deleteImages(result.getDeletedImages());
        return result;
    }

    private void deleteImages(List<Image> deleted){
        // this.images는 Post객체의 images 필드에 담긴 List<image>객체를 나타낸다. 일치하는 image 객체를 List에서 제거한다.
        // Post객체의 필드에서 제거된 image 객체는 Post와 연결성이 끊어지므로 고아가 된다.
        // orphanRemoval=true 조건에 의해 고아가된 image 객체는 제거된다.
        deleted.stream().forEach(di -> this.images.remove(di));
    }

    private ImageUpdatedResult findImageUpdatedResult(List<MultipartFile> addedImageFiles, List<Long> deletedImageIds) {
        List<Image> addedImages = convertImageFilesToImages(addedImageFiles);
        List<Image> deletedImages = convertImageIdsToImages(deletedImageIds);
        return new ImageUpdatedResult(addedImageFiles, addedImages, deletedImages);
    }

    private List<Image> convertImageIdsToImages(List<Long> imageIds) {
        return imageIds.stream()
                .map(id -> convertImageIdToImage(id))
                .filter(i -> i.isPresent())
                .map(i -> i.get())
                .collect(toList());
    }

    private Optional<Image> convertImageIdToImage(Long id) {
        return this.images.stream().filter(i -> i.getId().equals(id)).findAny();
    }

    private List<Image> convertImageFilesToImages(List<MultipartFile> imageFiles) {
        return imageFiles.stream().map(imageFile -> new Image(imageFile.getOriginalFilename())).collect(toList());
    }

}
