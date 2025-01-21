package io.reactivestax.EMSRestApi.repository;

import io.reactivestax.EMSRestApi.domain.Contact;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ContactRepository extends JpaRepository<Contact, Long> {
}
