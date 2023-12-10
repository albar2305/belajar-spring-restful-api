package albaradimassuntoro.restful.service;

import albaradimassuntoro.restful.entity.Contact;
import albaradimassuntoro.restful.entity.User;
import albaradimassuntoro.restful.model.ContactResponse;
import albaradimassuntoro.restful.model.CreateContactRequest;
import albaradimassuntoro.restful.model.UpdateContactRequest;
import albaradimassuntoro.restful.repository.ContactRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

@Service
public class ContactService {

  @Autowired
  private ValidationService validationService;

  @Autowired
  private ContactRepository contactRepository;

  @Transactional
  public ContactResponse create(User user, CreateContactRequest request) {
    validationService.validate(request);

    Contact contact = new Contact();
    contact.setId(UUID.randomUUID().toString());
    contact.setFirstName(request.getFirstName());
    contact.setLastName(request.getLastName());
    contact.setEmail(request.getEmail());
    contact.setPhone(request.getPhone());
    contact.setUser(user);

    contactRepository.save(contact);

    return toContactResponse(contact);
  }

  private ContactResponse toContactResponse(Contact contact) {
    return ContactResponse.builder().
        id(contact.getId())
        .firstName(contact.getFirstName())
        .lastName(contact.getLastName())
        .email(contact.getEmail())
        .phone(contact.getPhone())
        .build();
  }

  public ContactResponse get(User user, String id) {
    Contact contact = contactRepository.findFirstByUserAndId(user, id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Content not Found"));
    return toContactResponse(contact);
  }

  @Transactional
  public ContactResponse update(User user, UpdateContactRequest request) {
    validationService.validate(request);
    Contact contact = contactRepository.findFirstByUserAndId(user, request.getId())
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Content not Found"));
    contact.setFirstName(request.getFirstName());
    contact.setLastName(request.getLastName());
    contact.setPhone(request.getPhone());
    contact.setEmail(request.getEmail());
    contactRepository.save(contact);

    return toContactResponse(contact);
  }

  @Transactional
  public void delete(User user, String contactId) {
    Contact contact = contactRepository.findFirstByUserAndId(user, contactId)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Content not Found"));

    contactRepository.delete(contact);
  }
}
