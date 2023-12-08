package albaradimassuntoro.restful.controller;

import albaradimassuntoro.restful.entity.User;
import albaradimassuntoro.restful.model.ContactResponse;
import albaradimassuntoro.restful.model.CreateContactRequest;
import albaradimassuntoro.restful.model.WebResponse;
import albaradimassuntoro.restful.service.ContactService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@RestController
public class ContactController {

  @Autowired
  private ContactService contactService;

  @PostMapping(path = "/api/contacts",
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE)
  private WebResponse<ContactResponse> create(User user, @RequestBody CreateContactRequest request) {
    ContactResponse contactResponse = contactService.create(user, request);
    return WebResponse.<ContactResponse>builder().data(contactResponse).build();
  }

  @GetMapping(
      path = "/api/contacts/{contactId}"
      , produces = MediaType.APPLICATION_JSON_VALUE)
  private WebResponse<ContactResponse> get(User user, @PathVariable("contactId") String id) {
    ContactResponse contactResponse = contactService.get(user, id);
    return WebResponse.<ContactResponse>builder().data(contactResponse).build();
  }
}
