package org.example.basicMarket.service.post;

import lombok.RequiredArgsConstructor;
import org.example.basicMarket.dto.post.PostCreateRequest;
import org.example.basicMarket.dto.post.PostCreateResponse;
import org.example.basicMarket.dto.post.PostDto;
import org.example.basicMarket.entity.post.Image;
import org.example.basicMarket.entity.post.Post;
import org.example.basicMarket.exception.PostNotFoundException;
import org.example.basicMarket.repository.member.CategoryRepository;
import org.example.basicMarket.repository.member.MemberRepository;
import org.example.basicMarket.repository.post.PostRepository;
import org.example.basicMarket.service.file.FileService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.IntStream;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final MemberRepository memberRepository;
    private final CategoryRepository categoryRepository;
    private final FileService fileService;

    @Transactional
    public PostCreateResponse create(PostCreateRequest req) {
        Post post = postRepository.save(
                PostCreateRequest.toEntity(
                        req,
                        memberRepository,
                        categoryRepository
                )
        );
        uploadImages(post.getImages(), req.getImages());
        return new PostCreateResponse(post.getId());
    }

    private void uploadImages(List<Image> images, List<MultipartFile> fileImages) {
        //여기서 사용된 IntStream.range(0, images.size())은 0부터 images.size() - 1까지의 정수 범위를 생성합니다.
        // forEach 메서드는 해당 범위의 각 정수에 대해 주어진 동작(람다 표현식)을 수행합니다.
        IntStream.range(0, images.size()).forEach(i -> fileService.upload(fileImages.get(i), images.get(i).getUniqueName()));
    }

    public PostDto read(Long id){
        return PostDto.toDto(postRepository.findById(id).orElseThrow(PostNotFoundException::new));
    }

    @Transactional
    public void delete(Long id) {
        Post post = postRepository.findById(id).orElseThrow(PostNotFoundException::new);
        deleteImages(post.getImages());
        postRepository.delete(post);
    }

    private void deleteImages(List<Image> images) {
        images.stream().forEach(i -> fileService.delete(i.getUniqueName()));
    }
}
