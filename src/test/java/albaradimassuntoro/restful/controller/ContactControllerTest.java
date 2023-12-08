package albaradimassuntoro.restful.controller;

import albaradimassuntoro.restful.entity.Contact;
import albaradimassuntoro.restful.entity.User;
import albaradimassuntoro.restful.model.ContactResponse;
import albaradimassuntoro.restful.model.CreateContactRequest;
import albaradimassuntoro.restful.model.WebResponse;
import albaradimassuntoro.restful.repository.ContactRepository;
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

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.MockMvcBuilder.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;

@SpringBootTest
@AutoConfigureMockMvc
class ContactControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private ContactRepository contactRepository;

  @Autowired
  private ObjectMapper objectMapper;


  @BeforeEach
  void setUp() {
    contactRepository.deleteAll();
    userRepository.deleteAll();

    User user = new User();
    user.setUsername("test");
    user.setPassword(BCrypt.hashpw("test", BCrypt.gensalt()));
    user.setName("Test");
    user.setToken("test");
    user.setTokenExpiredAt(System.currentTimeMillis() + 100000000000L);
    userRepository.save(user);
  }

  @Test
  void createContactBadRequest() throws Exception {
    CreateContactRequest request = new CreateContactRequest();
    request.setFirstName("");
    request.setEmail("salah");

    mockMvc.perform(
        post("/api/contacts")
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request))
            .header("X-API-TOKEN", "test")
    ).andExpectAll(
        status().isBadRequest()
    ).andDo(result -> {
      WebResponse<String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
      });
      assertNotNull(response.getErrors());
    });
  }

  @Test
  void createContactSuccess() throws Exception {
    CreateContactRequest request = new CreateContactRequest();
    request.setFirstName("Albar");
    request.setLastName("Suntoro");
    request.setEmail("albar@gmail.com");
    request.setPhone("0128238233");

    mockMvc.perform(
        post("/api/contacts")
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request))
            .header("X-API-TOKEN", "test")
    ).andExpectAll(
        status().isOk()
    ).andDo(result -> {
      WebResponse<ContactResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
      });
      assertNull(response.getErrors());
      assertEquals("Albar", response.getData().getFirstName());
      assertEquals("Suntoro", response.getData().getLastName());
      assertEquals("albar@gmail.com", response.getData().getEmail());
      assertEquals("0128238233", response.getData().getPhone());

      assertTrue(contactRepository.existsById(response.getData().getId()));
    });
  }


  @Test
  void getContactNotFound() throws Exception {
    mockMvc.perform(
        get("/api/contacts/13932423")
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON)
            .header("X-API-TOKEN", "test")
    ).andExpectAll(
        status().isNotFound()
    ).andDo(result -> {
      WebResponse<String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
      });
      assertNotNull(response.getErrors());
    });
  }

  @Test
  void getContactSuccess() throws Exception {
    User user = userRepository.findById("test").orElseThrow();

    Contact contact = new Contact();
    contact.setId(UUID.randomUUID().toString());
    contact.setUser(user);
    contact.setFirstName("Albar");
    contact.setLastName("Suntoro");
    contact.setEmail("test@gmail.com");
    contact.setPhone("123");
    contactRepository.save(contact);

    mockMvc.perform(
        get("/api/contacts/" + contact.getId())
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON)
            .header("X-API-TOKEN", "test")
    ).andExpectAll(
        status().isOk()
    ).andDo(result -> {
      WebResponse<ContactResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
      });
      assertNull(response.getErrors());
      assertEquals(contact.getId(), response.getData().getId());
      assertEquals(contact.getFirstName(), response.getData().getFirstName());
      assertEquals(contact.getLastName(), response.getData().getLastName());
      assertEquals(contact.getPhone(), response.getData().getPhone());
      assertEquals(contact.getEmail(), response.getData().getEmail());

    });
  }

}