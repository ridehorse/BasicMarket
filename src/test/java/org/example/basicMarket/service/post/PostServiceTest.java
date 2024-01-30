package org.example.basicMarket.service.post;

import org.example.basicMarket.dto.post.PostCreateRequest;
import org.example.basicMarket.dto.post.PostDto;
import org.example.basicMarket.dto.post.PostListDto;
import org.example.basicMarket.dto.post.PostUpdateRequest;
import org.example.basicMarket.entity.post.Image;
import org.example.basicMarket.entity.post.Post;
import org.example.basicMarket.exception.CategoryNotFoundException;
import org.example.basicMarket.exception.MemberNotFoundException;
import org.example.basicMarket.exception.PostNotFoundException;
import org.example.basicMarket.exception.UnsupportedImageFormatException;
import org.example.basicMarket.repository.member.CategoryRepository;
import org.example.basicMarket.repository.member.MemberRepository;
import org.example.basicMarket.repository.post.PostRepository;
import org.example.basicMarket.service.file.FileService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;

import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

import static java.util.stream.Collectors.toList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.example.basicMarket.factory.dto.PostCreateRequestFactory.createPostCreateRequest;
import static org.example.basicMarket.factory.dto.PostCreateRequestFactory.createPostCreateRequestWithImages;
import static org.example.basicMarket.factory.dto.PostReadConditionFactory.createPostReadCondition;
import static org.example.basicMarket.factory.dto.PostUpdatedRequestFactory.createPostUpdateRequest;
import static org.example.basicMarket.factory.entity.CategoryFactory.createCategory;
import static org.example.basicMarket.factory.entity.ImageFactory.createImage;
import static org.example.basicMarket.factory.entity.ImageFactory.createImageWithIdAndOriginName;
import static org.example.basicMarket.factory.entity.MemberFactory.createMember;
import static org.example.basicMarket.factory.entity.PostFactory.createPostWithImages;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class PostServiceTest {

    //@InjectMocks: 이 어노테이션은 테스트 대상이 되는 클래스의 인스턴스를 생성하고, 해당 클래스의 필드에 @Mock 어노테이션이 붙은 mock 객체들을 주입합니다. 즉, 테스트할 클래스의 인스턴스를 자동으로 생성하고, 해당 인스턴스 내에서 @Mock 어노테이션이 붙은 필드에 mock 객체를 주입하는 역할을 합니다.
    //
    //@Mock: 이 어노테이션은 Mockito에게 해당 필드를 mock 객체로 만들라고 지시합니다. 이 mock 객체는 @InjectMocks 어노테이션이 붙은 클래스의 인스턴스에 주입되어 사용됩니다.
    @InjectMocks
    PostService postService;
    @Mock
    PostRepository postRepository;
    @Mock
    MemberRepository memberRepository;
    @Mock
    CategoryRepository categoryRepository;
    @Mock
    FileService fileService;

    @Test
    void createTest(){

        //given
        PostCreateRequest req = createPostCreateRequest();
        given(memberRepository.findById(anyLong())).willReturn(Optional.of(createMember()));
        given(categoryRepository.findById(anyLong())).willReturn(Optional.of(createCategory()));
        given(postRepository.save(any())).willReturn(createPostWithImages(
                IntStream.range(0, req.getImages().size()).mapToObj(i -> createImage()).collect(toList()))
        );
        // when
        postService.create(req);

        // then
        verify(postRepository).save(any());
        verify(fileService, times(req.getImages().size())).upload(any(), anyString());
    }

    @Test
    void createExceptionByMemberNotFoundTest() {
        // given
        given(memberRepository.findById(anyLong())).willReturn(Optional.ofNullable(null));

        // when, then
        assertThatThrownBy(() -> postService.create(createPostCreateRequest())).isInstanceOf(MemberNotFoundException.class);
    }

    @Test
    void createExceptionByCategoryNotFoundTest() {
        // given
        given(memberRepository.findById(anyLong())).willReturn(Optional.of(createMember()));
        given(categoryRepository.findById(anyLong())).willReturn(Optional.ofNullable(null));

        // when, then
        assertThatThrownBy(() -> postService.create(createPostCreateRequest())).isInstanceOf(CategoryNotFoundException.class);
    }

    @Test
    void createExceptionByUnsupportedImageFormatExceptionTest() {
        // given
        PostCreateRequest req = createPostCreateRequestWithImages(
                List.of(new MockMultipartFile("test", "test.txt", MediaType.TEXT_PLAIN_VALUE, "test".getBytes()))
        );
        given(memberRepository.findById(anyLong())).willReturn(Optional.of(createMember()));
        given(categoryRepository.findById(anyLong())).willReturn(Optional.of(createCategory()));

        // when, then
        assertThatThrownBy(() -> postService.create(req)).isInstanceOf(UnsupportedImageFormatException.class);
    }

    @Test
    void readTest() {
        // given
        Post post = createPostWithImages(List.of(createImage(), createImage()));
        given(postRepository.findById(anyLong())).willReturn(Optional.of(post));

        // when
        PostDto postDto = postService.read(1L);

        // then
        assertThat(postDto.getTitle()).isEqualTo(post.getTitle());
        assertThat(postDto.getImages().size()).isEqualTo(post.getImages().size());
    }

    @Test
    void readExceptionByPostNotFoundTest() {
        // given
        given(postRepository.findById(anyLong())).willReturn(Optional.ofNullable(null));

        // when, then
        assertThatThrownBy(() -> postService.read(1L)).isInstanceOf(PostNotFoundException.class);
    }

    @Test
    void deleteTest() {
        // given
        Post post = createPostWithImages(List.of(createImage(), createImage()));
        given(postRepository.findById(anyLong())).willReturn(Optional.of(post));

        // when
        postService.delete(1L);

        // then
        verify(fileService, times(post.getImages().size())).delete(anyString());
        verify(postRepository).delete(any());
    }

    @Test
    void deleteExceptionByNotFoundPostTest() {
        // given
        given(postRepository.findById(anyLong())).willReturn(Optional.ofNullable(null));

        // when, then
        assertThatThrownBy(() -> postService.delete(1L)).isInstanceOf(PostNotFoundException.class);
    }

    @Test
    void updateTest() {
        // given
        Image a = createImageWithIdAndOriginName(1L, "a.png");
        Image b = createImageWithIdAndOriginName(2L, "b.png");
        Post post = createPostWithImages(List.of(a, b));
        given(postRepository.findById(anyLong())).willReturn(Optional.of(post));
        MockMultipartFile cFile = new MockMultipartFile("c", "c.png", MediaType.IMAGE_PNG_VALUE, "c".getBytes());
        PostUpdateRequest postUpdateRequest = createPostUpdateRequest("title", "content", 1000L, List.of(cFile), List.of(a.getId()));

        // when
        postService.update(1L, postUpdateRequest);

        // then
        List<Image> images = post.getImages();
        List<String> originNames = images.stream().map(i -> i.getOriginName()).collect(toList());
        assertThat(originNames.size()).isEqualTo(2);
        assertThat(originNames).contains(b.getOriginName(), cFile.getOriginalFilename());

        verify(fileService, times(1)).upload(any(), anyString());
        verify(fileService, times(1)).delete(anyString());
    }

    @Test
    void updateExceptionByPostNotFoundTest() {
        // given
        given(postRepository.findById(anyLong())).willReturn(Optional.ofNullable(null));

        // when, then
        assertThatThrownBy(() -> postService.update(1L, createPostUpdateRequest("title", "content", 1234L, List.of(), List.of())))
                .isInstanceOf(PostNotFoundException.class);
    }

    @Test
    void readAllTest() {
        // given
        given(postRepository.findAllByCondition(any())).willReturn(Page.empty());

        // when
        PostListDto postListDto = postService.readAll(createPostReadCondition(1, 1));

        // then
        assertThat(postListDto.getPostList().size()).isZero();
    }
}