package albaradimassuntoro.restful.repository;

import albaradimassuntoro.restful.entity.Address;
import albaradimassuntoro.restful.entity.Contact;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AddressRepository extends JpaRepository<Address, String> {
  Optional<Address> findFirstByContactAndId(Contact contact, String id);

  List<Address> findAllByContact(Contact contact);
}
