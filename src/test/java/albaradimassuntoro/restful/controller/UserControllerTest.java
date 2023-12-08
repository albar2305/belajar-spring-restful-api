package albaradimassuntoro.restful.controller;

import albaradimassuntoro.restful.entity.User;
import albaradimassuntoro.restful.model.RegisterUserRequest;
import albaradimassuntoro.restful.model.UpdateUserRequest;
import albaradimassuntoro.restful.model.UserResponse;
import albaradimassuntoro.restful.model.WebResponse;
import albaradimassuntoro.restful.repository.UserRepository;
import albaradimassuntoro.restful.security.BCrypt;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.MockMvcBuilder.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;

@SpringBootTest
@AutoConfigureMockMvc
class UserControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @Autowired
  private UserRepository userRepository;

  @BeforeEach
  void setUp() {
    userRepository.deleteAll();
  }

  @Test
  void testRegisterSuccess() throws Exception {
    RegisterUserRequest request = new RegisterUserRequest();
    request.setUsername("test");
    request.setPassword("secret");
    request.setName("Test");

    mockMvc.perform(
        post("/api/users")
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request))
    ).andExpectAll(
        status().isOk()
    ).andDo(result -> {
      WebResponse<String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
      });
      assertEquals("OK", response.getData());
    });
  }

  @Test
  void testRegisterBadRequest() throws Exception {
    RegisterUserRequest request = new RegisterUserRequest();
    request.setUsername("");
    request.setPassword("");
    request.setName("");

    mockMvc.perform(
        post("/api/users")
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request))
    ).andExpectAll(
        status().isBadRequest()
    ).andDo(result -> {
      WebResponse<String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
      });
      assertNotNull(response.getErrors());
    });
  }

  @Test
  void testRegisterDuplicate() throws Exception {
    User user = new User();
    user.setUsername("test");
    user.setPassword(BCrypt.hashpw("secret", BCrypt.gensalt()));
    user.setName("Test");
    userRepository.save(user);

    RegisterUserRequest request = new RegisterUserRequest();
    request.setUsername("test");
    request.setPassword("secret");
    request.setName("Test");

    mockMvc.perform(
        post("/api/users")
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request))
    ).andExpectAll(
        status().isBadRequest()
    ).andDo(result -> {
      WebResponse<String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
      });
      assertNotNull(response.getErrors());
    });
  }

  @Test
  void getUserUnauthorized() throws Exception {
    mockMvc.perform(
        get("/api/users/current")
            .accept(MediaType.APPLICATION_JSON)
            .header("X-API-TOKEN", "notfound")
    ).andExpectAll(
        status().isUnauthorized()
    ).andDo(result -> {
      WebResponse<String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
      });
      assertNotNull(response.getErrors());
    });
  }

  @Test
  void getUserUnauthorizedTokenNotSend() throws Exception {
    mockMvc.perform(
        get("/api/users/current")
            .accept(MediaType.APPLICATION_JSON)
    ).andExpectAll(
        status().isUnauthorized()
    ).andDo(result -> {
      WebResponse<String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
      });
      assertNotNull(response.getErrors());
    });
  }

  @Test
  void getUserSuccess() throws Exception {
    User user = new User();
    user.setUsername("test");
    user.setName("Test");
    user.setPassword("test");
    user.setToken("test");
    user.setTokenExpiredAt(System.currentTimeMillis() + 10000000000000000L);
    userRepository.save(user);

    mockMvc.perform(
        get("/api/users/current")
            .accept(MediaType.APPLICATION_JSON)
            .header("X-API-TOKEN", "test")
    ).andExpectAll(
        status().isOk()
    ).andDo(result -> {
      WebResponse<UserResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
      });
      assertNull(response.getErrors());
      assertEquals("test", response.getData().getUsername());
      assertEquals("Test", response.getData().getName());
    });
  }

  @Test
  void getUserTokenExpired() throws Exception {
    User user = new User();
    user.setUsername("test");
    user.setName("Test");
    user.setPassword("test");
    user.setToken("test");
    user.setTokenExpiredAt(System.currentTimeMillis() - 10000000000000000L);
    userRepository.save(user);

    mockMvc.perform(
        get("/api/users/current")
            .accept(MediaType.APPLICATION_JSON)
            .header("X-API-TOKEN", "test")
    ).andExpectAll(
        status().isUnauthorized()
    ).andDo(result -> {
      WebResponse<String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
      });
      assertNotNull(response.getErrors());
    });
  }

  @Test
  void updateUserUnauthorized() throws Exception {
    UpdateUserRequest request = new UpdateUserRequest();
    mockMvc.perform(
        patch("/api/users/current")
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request))
    ).andExpectAll(
        status().isUnauthorized()
    ).andDo(result -> {
      WebResponse<String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
      });
      assertNotNull(response.getErrors());
    });
  }

  @Test
  void updateUserSuccess() throws Exception {
    User user = new User();
    user.setUsername("test");
    user.setName("Test");
    user.setPassword("test");
    user.setToken("test");
    user.setTokenExpiredAt(System.currentTimeMillis() + 10000000000000000L);
    userRepository.save(user);

    UpdateUserRequest request = new UpdateUserRequest();
    request.setName("Albar");
    request.setPassword("albar12345");
    mockMvc.perform(
        patch("/api/users/current")
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request))
            .header("X-API-TOKEN", "test")
    ).andExpectAll(
        status().isOk()
    ).andDo(result -> {
      WebResponse<UserResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
      });
      assertNull(response.getErrors());
      assertEquals("Albar", response.getData().getName());
      assertEquals("test", response.getData().getUsername());

      User userDb = userRepository.findById("test").orElse(null);
      assertNotNull(userDb);
      assertTrue(BCrypt.checkpw("albar12345", userDb.getPassword()));
    });
  }
}