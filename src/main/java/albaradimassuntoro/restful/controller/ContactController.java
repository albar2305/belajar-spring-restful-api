package albaradimassuntoro.restful.controller;

import albaradimassuntoro.restful.entity.User;
import albaradimassuntoro.restful.model.ContactResponse;
import albaradimassuntoro.restful.model.CreateContactRequest;
import albaradimassuntoro.restful.model.UpdateContactRequest;
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
  private WebResponse<ContactResponse> get(User user, @PathVariable("contactId") String contactId) {
    ContactResponse contactResponse = contactService.get(user, contactId);
    return WebResponse.<ContactResponse>builder().data(contactResponse).build();
  }

  @PutMapping(path = "/api/contacts/{contactId}",
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE)
  private WebResponse<ContactResponse> update(User user,
                                              @RequestBody UpdateContactRequest request,
                                              @PathVariable("contactId") String contactId) {
    request.setId(contactId);
    ContactResponse contactResponse = contactService.update(user, request);
    return WebResponse.<ContactResponse>builder().data(contactResponse).build();
  }

  @DeleteMapping(
      path = "/api/contacts/{contactId}"
      , produces = MediaType.APPLICATION_JSON_VALUE)
  private WebResponse<String> delete(User user, @PathVariable("contactId") String contactId) {
    contactService.delete(user, contactId);
    return WebResponse.<String>builder().data("OK").build();
  }
}
