package org.example.basicMarket.controller.message;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.basicMarket.advice.ExceptionAdvice;
import org.example.basicMarket.dto.message.MessageCreateRequest;
import org.example.basicMarket.dto.sign.SignInResponse;
import org.example.basicMarket.entity.member.Member;
import org.example.basicMarket.exception.MemberNotFoundException;
import org.example.basicMarket.exception.MessageNotFoundException;
import org.example.basicMarket.init.TestInitDB;
import org.example.basicMarket.repository.member.MemberRepository;
import org.example.basicMarket.repository.message.MessageRepository;
import org.example.basicMarket.service.message.MessageService;
import org.example.basicMarket.service.sign.SignService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import static org.example.basicMarket.factory.dto.SignInRequestFactory.createSignInRequest;
import static org.example.basicMarket.factory.entity.MessageCreateRequestFactory.createMessageCreateRequest;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doThrow;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

//@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles(value = "test")
@Transactional
public class MessageControllerIntegrationTest {
    @Autowired
    WebApplicationContext context;
    @Autowired MockMvc mockMvc;

    @Autowired
    TestInitDB initDB;
    @Autowired
    SignService signService;
    @Autowired
    MemberRepository memberRepository;
    @Autowired
    MessageRepository messageRepository;
    ObjectMapper objectMapper = new ObjectMapper();

    Member admin, sender, receiver;

    @BeforeEach
    void beforeEach() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context).apply(springSecurity()).build();
        initDB.initDB();
        admin = memberRepository.findByEmail(initDB.getAdminEmail()).orElseThrow(MemberNotFoundException::new);
        sender = memberRepository.findByEmail(initDB.getMember1Email()).orElseThrow(MemberNotFoundException::new);
        receiver = memberRepository.findByEmail(initDB.getMember2Email()).orElseThrow(MemberNotFoundException::new);
    }

    @Test
    void readAllBySenderTest() throws Exception{
        // given
        Integer size = 2;
        SignInResponse signInRes = signService.signIn(createSignInRequest(sender.getEmail(), initDB.getPassword()));

        // when, then
        mockMvc.perform(
                        get("/api/messages/sender")
                                .param("size", String.valueOf(size))
                                .header("Authorization", signInRes.getAccessToken()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.data.numberOfElements").value(2));
    }

    @Test
    void readAllBySenderUnauthorizedByNoneTokenTest() throws Exception {
        // given
        Integer size = 2;

        // when, then
        mockMvc.perform(
                        get("/api/messages/sender")
                                .param("size", String.valueOf(size)))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/exception/entry-point"));
    }

    @Test
    void readAllByReceiverTest() throws Exception {
        // given
        Integer size = 2;
        SignInResponse signInRes = signService.signIn(createSignInRequest(receiver.getEmail(), initDB.getPassword()));

        // when, then
        mockMvc.perform(
                        get("/api/messages/receiver")
                                .param("size", String.valueOf(size))
                                .header("Authorization", signInRes.getAccessToken()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.data.numberOfElements").value(2));
    }

    @Test
    void readAllByReceiverUnauthorizedByNoneTokenTest() throws Exception {
        // given
        Integer size = 2;

        // when, then
        mockMvc.perform(
                        get("/api/messages/receiver")
                                .param("size", String.valueOf(size)))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/exception/entry-point"));
    }

    @Test
    void readByResourceOwnerTest() throws Exception {
        // given
        Long id = messageRepository.findAll().get(0).getId();
        SignInResponse signInRes = signService.signIn(createSignInRequest(sender.getEmail(), initDB.getPassword()));

        // when, then
        mockMvc.perform(
                        get("/api/messages/{id}", id)
                                .header("Authorization", signInRes.getAccessToken()))
                .andExpect(status().isOk());
    }

    @Test
    void readByAdminTest() throws Exception {
        // given
        Long id = messageRepository.findAll().get(0).getId();
        SignInResponse adminSignInRes = signService.signIn(createSignInRequest(admin.getEmail(), initDB.getPassword()));

        // when, then
        mockMvc.perform(
                        get("/api/messages/{id}", id)
                                .header("Authorization", adminSignInRes.getAccessToken()))
                .andExpect(status().isOk());
    }

    @Test
    void readUnauthorizedByNoneTokenTest() throws Exception {
        // given
        Long id = messageRepository.findAll().get(0).getId();
        // when, then
        mockMvc.perform(
                        get("/api/messages/{id}", id))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/exception/entry-point"));
    }

    @Test
    void readAccessDeniedByNotResourceOwnerTest() throws Exception {
        // given
        Long id = messageRepository.findAll().get(0).getId();
        SignInResponse notResourceOwnerSignInRes = signService.signIn(createSignInRequest(receiver.getEmail(), initDB.getPassword()));

        // when, then
        mockMvc.perform(
                        get("/api/messages/{id}", id)
                                .header("Authorization", notResourceOwnerSignInRes.getAccessToken()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/exception/access-denied"));
    }

    @Test
    void createTest() throws Exception {
        // given
        MessageCreateRequest req = createMessageCreateRequest("content", null, receiver.getId());
        SignInResponse signInRes = signService.signIn(createSignInRequest(sender.getEmail(), initDB.getPassword()));

        // when, then
        mockMvc.perform(
                        post("/api/messages")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(req))
                                .header("Authorization", signInRes.getAccessToken()))
                .andExpect(status().isCreated());
    }

    @Test
    void createUnauthorizedByNoneTokenTest() throws Exception {
        // given
        MessageCreateRequest req = createMessageCreateRequest("content", null, receiver.getId());

        // when, then
        mockMvc.perform(
                        post("/api/messages")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/exception/entry-point"));
    }

    @Test
    void deleteBySenderByResourceOwnerTest() throws Exception {
        // given
        Long id = messageRepository.findAll().get(0).getId();
        SignInResponse signInRes = signService.signIn(createSignInRequest(sender.getEmail(), initDB.getPassword()));

        // when, then
        mockMvc.perform(
                        delete("/api/messages/sender/{id}", id)
                                .header("Authorization", signInRes.getAccessToken()))
                .andExpect(status().isOk());
    }

    @Test
    void deleteBySenderByAdminTest() throws Exception {
        // given
        Long id = messageRepository.findAll().get(0).getId();
        SignInResponse adminSignInRes = signService.signIn(createSignInRequest(admin.getEmail(), initDB.getPassword()));

        // when, then
        mockMvc.perform(
                        delete("/api/messages/sender/{id}", id)
                                .header("Authorization", adminSignInRes.getAccessToken()))
                .andExpect(status().isOk());
    }

    @Test
    void deleteBySenderUnauthorizedByNoneTokenTest() throws Exception {
        // given
        Long id = messageRepository.findAll().get(0).getId();

        // when, then
        mockMvc.perform(
                        delete("/api/messages/sender/{id}", id))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/exception/entry-point"));
    }

    @Test
    void deleteBySenderAccessDeniedByNotResourceOwnerTest() throws Exception {
        // given
        Long id = messageRepository.findAll().get(0).getId();
        SignInResponse notResourceOwnerSignInRes = signService.signIn(createSignInRequest(receiver.getEmail(), initDB.getPassword()));

        // when, then
        mockMvc.perform(
                        delete("/api/messages/sender/{id}", id)
                                .header("Authorization", notResourceOwnerSignInRes.getAccessToken()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/exception/access-denied"));
    }

    @Test
    void deleteByReceiverByResourceOwnerTest() throws Exception {
        // given
        Long id = messageRepository.findAll().get(0).getId();
        SignInResponse signInRes = signService.signIn(createSignInRequest(receiver.getEmail(), initDB.getPassword()));

        // when, then
        mockMvc.perform(
                        delete("/api/messages/receiver/{id}", id)
                                .header("Authorization", signInRes.getAccessToken()))
                .andExpect(status().isOk());
    }

    @Test
    void deleteByReceiverByAdminTest() throws Exception {
        // given
        Long id = messageRepository.findAll().get(0).getId();
        SignInResponse adminSignInRes = signService.signIn(createSignInRequest(admin.getEmail(), initDB.getPassword()));

        // when, then
        mockMvc.perform(
                        delete("/api/messages/receiver/{id}", id)
                                .header("Authorization", adminSignInRes.getAccessToken()))
                .andExpect(status().isOk());
    }

    @Test
    void deleteByReceiverUnauthorizedByNoneTokenTest() throws Exception {
        // given
        Long id = messageRepository.findAll().get(0).getId();

        // when, then
        mockMvc.perform(
                        delete("/api/messages/receiver/{id}", id))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/exception/entry-point"));
    }

    @Test
    void deleteByReceiverAccessDeniedByNotResourceOwnerTest() throws Exception {
        // given
        Long id = messageRepository.findAll().get(0).getId();
        SignInResponse notResourceOwnerSignInRes = signService.signIn(createSignInRequest(sender.getEmail(), initDB.getPassword()));

        // when, then
        mockMvc.perform(
                        delete("/api/messages/receiver/{id}", id)
                                .header("Authorization", notResourceOwnerSignInRes.getAccessToken()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/exception/access-denied"));
    }

}
