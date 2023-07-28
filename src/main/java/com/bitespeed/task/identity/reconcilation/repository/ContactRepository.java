package com.bitespeed.task.identity.reconcilation.repository;

import com.bitespeed.task.identity.reconcilation.entities.Contact;
import java.util.List;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


public interface ContactRepository extends JpaRepository <Contact,Integer> {

    @Query("SELECT c.id FROM Contact c WHERE c.linkedId = :primaryContactId")
    List<Integer> findSecondaryContactIds(@Param("primaryContactId") Integer primaryContactId);
    List<Contact> findByEmail(String email, Sort sort);
    List<Contact> findByPhoneNumber(String phoneNumber,Sort sort);

//    It retrieves the smallest (minimum) id value from the Contact table based on the provided email or phoneNumber.
    @Query("SELECT MIN(c.id) FROM Contact c WHERE c.email = :email OR c.phoneNumber = :phoneNumber")
    Integer findSmallestIdByEmailOrPhoneNumber(@Param("email") String email, @Param("phoneNumber") String phoneNumber);
}
