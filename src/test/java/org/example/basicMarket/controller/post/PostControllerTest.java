package org.example.basicMarket.controller.post;

import org.example.basicMarket.dto.post.PostCreateRequest;
import org.example.basicMarket.service.post.PostService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.example.basicMarket.factory.dto.PostCreateRequestFactory.createPostCreateRequestWithImages;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class PostControllerTest {

    @InjectMocks
    PostController postController;
    @Mock
    PostService postService;
    MockMvc mockMvc;

    @BeforeEach
    void beforeEach() {
        mockMvc = MockMvcBuilders.standaloneSetup(postController).build();
    }

    @Test
    void createTest() throws Exception{
        // given
        // 캡쳐될 대상을 forClass(PostCreateRequest.class); PostCreateRequest로 정한다.
        // 나중에 /api/posts로 요청을 할때, controller러 매서드가 PostCreateRequest객체를 전달할때 postCreateRequestArgumentCaptor.capture() 로 전달될때 객체를 캡쳐(저장)한다.
        // 이렇게 저장된 객체는  postCreateRequestArgumentCaptor.getValue(); 코드로 가장 최근데 캡쳐(저장된) 객체인 PostCreateRequest를 가져올수 있따.
        ArgumentCaptor<PostCreateRequest> postCreateRequestArgumentCaptor = ArgumentCaptor.forClass(PostCreateRequest.class);

        List<MultipartFile> imageFiles = List.of(
                new MockMultipartFile("test1", "test1.PNG", MediaType.IMAGE_PNG_VALUE, "test1".getBytes()),
                new MockMultipartFile("test2", "test2.PNG", MediaType.IMAGE_PNG_VALUE, "test2".getBytes())
        );
        PostCreateRequest req = createPostCreateRequestWithImages(imageFiles);

        // when, then
        mockMvc.perform(
                        multipart("/api/posts")
                                .file("images", imageFiles.get(0).getBytes()) // 2
                                .file("images", imageFiles.get(1).getBytes())
                                .param("title", req.getTitle())
                                .param("content", req.getContent())
                                .param("price", String.valueOf(req.getPrice()))
                                .param("categoryId", String.valueOf(req.getCategoryId()))
                                .with(requestPostProcessor -> { // 3
                                    requestPostProcessor.setMethod("POST"); // POST METHOD로 설정
                                    return requestPostProcessor; // requestPostProcessor를 리턴함으로서 뒤에 메서드체이닝을 계속 할수 있다.(.contentType())
                                })
                                .contentType(MediaType.MULTIPART_FORM_DATA)) // 4
                .andExpect(status().isCreated());

        verify(postService).create(postCreateRequestArgumentCaptor.capture()); // verify(postService).create() : create() 매서드가 호출되는지 확인, postCreateRequestArgumentCaptor.capture() : 약속한 PostCreateRequest객체를 저장

        PostCreateRequest capturedRequest = postCreateRequestArgumentCaptor.getValue(); // 6
        assertThat(capturedRequest.getImages().size()).isEqualTo(2);
    }

    @Test
    void readTest() throws Exception {
        // given
        Long id = 1L;

        // when, then
        mockMvc.perform(
                        get("/api/posts/{id}", id))
                .andExpect(status().isOk());
        verify(postService).read(id);
    }

    @Test
    void deleteTest() throws Exception {
        // given
        Long id = 1L;

        // when, then
        mockMvc.perform(
                        delete("/api/posts/{id}", id))
                .andExpect(status().isOk());
        verify(postService).delete(id);
    }

}
