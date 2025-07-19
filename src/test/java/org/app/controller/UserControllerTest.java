package org.app.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.app.model.FrontHost;
import org.app.model.LanguageRequest;
import org.app.model.Login;
import org.app.model.NameChangeRequest;
import org.app.model.common.DefaultAnswer;
import org.app.model.entity.User;
import org.app.model.requests.NewPasswordRequest;
import org.app.services.UserService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void getUser_shouldReturnUserData() throws Exception {
        String userId = "123";
        User mockUser = new User();
        Mockito.when(userService.getUser(userId)).thenReturn(mockUser);
        mockMvc.perform(get("/user/{id}", userId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void newUser_shouldCreateUser() throws Exception {
        User mockUser = new User();
        Mockito.when(userService.newUser(any())).thenReturn(mockUser);
        mockMvc.perform(post("/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void updateFullUser_shouldUpdateUser() throws Exception {
        User mockUser = new User();
        Mockito.when(userService.updateUser(any())).thenReturn(mockUser);
        mockMvc.perform(put("/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void newLogin_shouldReturnUser() throws Exception {
        Login mockLogin = new Login("email", "password", "userId", java.time.LocalDateTime.now(), java.time.LocalDateTime.now(), "language");
        Mockito.when(userService.newLogin(any())).thenReturn(mockLogin);
        mockMvc.perform(post("/user/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void updateName_shouldUpdateName() throws Exception {
        Mockito.when(userService.userExistByEmailAndLanguage(anyString(), anyString(), anyString())).thenReturn(false);
        mockMvc.perform(patch("/user")
                        .param("name", "name")
                        .param("email", "email")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new NameChangeRequest("name", "email", "lang"))))
                .andExpect(status().isOk());
    }

    @Test
    void activateUser_shouldActivate() throws Exception {
        Mockito.when(userService.activateUserById(anyString(), anyString(), anyString(), anyString())).thenReturn(new DefaultAnswer("activated"));
        mockMvc.perform(patch("/user/activate/1/code/email")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new LanguageRequest("lang"))))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void recoverPassword_shouldRecover() throws Exception {
        Mockito.when(userService.recoverPassword(anyString(), anyString())).thenReturn(new DefaultAnswer("recovered"));
        mockMvc.perform(patch("/user/recover/1/password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new FrontHost("host"))))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));
    }

    @Test
    void requestDelete_shouldRequestDelete() throws Exception {
        Mockito.when(userService.requestDelete(anyString(), anyString(), anyString())).thenReturn(new DefaultAnswer("requested"));
        mockMvc.perform(patch("/user/request/1/email/delete")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new LanguageRequest("lang"))))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void requestDoDelete_shouldDelete() throws Exception {
        Mockito.when(userService.doRequestDelete(anyString(), anyString())).thenReturn(new DefaultAnswer("deleted"));
        mockMvc.perform(delete("/user/delete/1/token"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void setPassword_shouldSetPassword() throws Exception {
        Mockito.when(userService.setPassword(anyString(), anyString(), anyString(), anyString(), anyString())).thenReturn(new DefaultAnswer("set"));
        NewPasswordRequest req = new NewPasswordRequest("id", "pass", "token", "email", "lang");
        mockMvc.perform(patch("/user/request/setPassword")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void recoverPasswordByEmail_shouldRecover() throws Exception {
        Mockito.when(userService.recoverPasswordByEmail(anyString(), anyString(), any())).thenReturn(new DefaultAnswer("recovered"));
        mockMvc.perform(patch("/user/recover/email/lang/password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new FrontHost("host"))))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void resendConfirmationAccEmail_shouldResend() throws Exception {
        Mockito.doNothing().when(userService).resendEmail(anyString(), anyString());
        mockMvc.perform(post("/user/resendConfirmationAccEmail/email/lang"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }
}