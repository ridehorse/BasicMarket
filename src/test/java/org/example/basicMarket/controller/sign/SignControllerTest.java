package org.example.basicMarket.controller.sign;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.basicMarket.dto.sign.SignInRequest;
import org.example.basicMarket.dto.sign.SignInResponse;
import org.example.basicMarket.dto.sign.SignUpRequest;
import org.example.basicMarket.service.sign.SignService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class SignControllerTest {

    @InjectMocks SignController signController;
    @Mock
    SignService signService;

    MockMvc mockMvc;

    ObjectMapper objectMapper = new ObjectMapper(); // 객체를 JSON 문자열로 변환

    @BeforeEach
    void beforeEach() {
        mockMvc = MockMvcBuilders.standaloneSetup(signController).build();
    }

    @Test
    void signUpTest() throws Exception {
        // given
        SignUpRequest req = new SignUpRequest("email@email.com", "123456a!", "username", "nickname");

        // when, then
        mockMvc.perform(
                        post("/api/sign-up")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(req))) // 2
                .andExpect(status().isCreated());

        verify(signService).signUp(req);
    }

    @Test
    void signInTest() throws Exception {
        // given
        SignInRequest req = new SignInRequest("email@email.com", "123456a!");
        // signService.signIn() 매서드가 실행된다면 응답으로 new SIgnInResponse("access","refresh") 객체가 return 될 것이다. 라고 약속한거다.
        given(signService.signIn(req)).willReturn(new SignInResponse("access", "refresh"));

        // when, then
        mockMvc.perform(
                        post("/api/sign-in")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                // "/api/sign-in" API는 응답으로 new response(T data) 객체를 반환한다. $ : 응답으로 반환하는 new response(T data) 객체를 나타낸다.
                // $.result : new response(T data) 객체의 result 필드명을 나타낸다.
                // result 필드에는 signService.signIn(req) 매서드가 반환한 값(new SignInResponse("access","refresh"))이 success 객체에 담겨 저장된다.
                // $.result.data : success의 data 필드를 선택한다. data 필드에는 new SignInResponse("access","refresh")가 담겨 있다.
                .andExpect(jsonPath("$.result.data.accessToken").value("access")) // 3
                .andExpect(jsonPath("$.result.data.refreshToken").value("refresh"));

        verify(signService).signIn(req);
    }

    @Test
    void ignoreNullValueInJsonResponseTest() throws Exception { // 4
        // given
        SignUpRequest req = new SignUpRequest("email@email.com", "123456a!", "username", "nickname");

        // when, then
        mockMvc.perform(
                        post("/api/sign-up")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.result").doesNotExist());

    }
}
