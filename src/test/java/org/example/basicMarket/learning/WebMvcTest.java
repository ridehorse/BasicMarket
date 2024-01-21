package org.example.basicMarket.learning;

import org.example.basicMarket.dto.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.stereotype.Controller;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.annotation.GetMapping;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class WebMvcTest {

    @InjectMocks
    TestController testController;

    MockMvc mockMvc;

    @Controller
    public static class TestController{
        @GetMapping("/test/ignore-null-value")
        public Response ignoreNullValueTest(){
            return Response.success();
        }
    }

    @BeforeEach
    void beforeEach(){
        // TestController가 작동되게 한다.
        mockMvc = MockMvcBuilders.standaloneSetup(testController).build();
    }

    @Test
    void ignoreNullValueJsonResponseTest() throws Exception{

        mockMvc.perform( // API에 요청 보내는 method (get)
                get("/test/ignore-null-value"))
                .andExpect(status().isOk()) // 응답상태코드 200
                .andExpect(jsonPath("$.result").doesNotExist()); // result 필드 없음을 확인

    }

}
