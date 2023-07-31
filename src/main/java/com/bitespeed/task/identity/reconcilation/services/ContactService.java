package com.bitespeed.task.identity.reconcilation.services;

import com.bitespeed.task.identity.reconcilation.components.IdentityWrapper;
import com.bitespeed.task.identity.reconcilation.entities.ConsolidatedContact;
import com.bitespeed.task.identity.reconcilation.entities.Contact;
import com.bitespeed.task.identity.reconcilation.entities.IdentityResponse;
import com.bitespeed.task.identity.reconcilation.repository.ContactRepository;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;




@Service
public class ContactService {
    @Autowired
    private IdentityWrapper identityWrapper;
    @Autowired
    private ContactRepository contactRepository;


    public Integer findContactIdByConsolidatedContact(ConsolidatedContact consolidatedContact) {
        String email = consolidatedContact.getEmail();
        String phoneNumber = consolidatedContact.getPhoneNumber();
        // Search for the contact by email or phone number
        Integer contactId = contactRepository.findSmallestIdByEmailOrPhoneNumber(email, phoneNumber);
        return contactId;
    }

    public IdentityWrapper save(ConsolidatedContact consolidatedContact) {
        Sort sortById = Sort.by(Sort.Direction.ASC, "id");

        List<Contact> emails = contactRepository.findByEmail(consolidatedContact.getEmail(),sortById);
        List<Contact> phoneNumbers = contactRepository.findByPhoneNumber(consolidatedContact.getPhoneNumber(),sortById);

        Contact newContact;
        List<Contact> primaryAndSecondaryContact = determinePrimaryAndSecondaryContact(emails, phoneNumbers);

        // handles if the email or phone number is new or both are new    
        if(emails.isEmpty() || phoneNumbers.isEmpty()){
            newContact = createOrUpdateContact(consolidatedContact);
        }
        else {
            Contact primaryContact = primaryAndSecondaryContact.get(0);
            Contact secondaryContact = primaryAndSecondaryContact.get(1);
            if (primaryContact == null) {
                    // If no primary contact found (should not happen, but handle the case)
                newContact = createOrUpdateContact(consolidatedContact);
            }
            else {
                if(!primaryContact.getEmail().equals(consolidatedContact.getEmail()) || !primaryContact.getPhoneNumber().equals(consolidatedContact.getPhoneNumber())){
                    newContact = createOrUpdateSecondaryContact(consolidatedContact,secondaryContact,primaryContact);
                }
                else {
                    newContact = updatePrimaryContact(primaryContact,consolidatedContact);
                }
            }
        }
        newContact = contactRepository.save(newContact);

            // prepare and return the IdentityWrapper with the IdentityResponse;
        IdentityResponse identityResponse = prepareIdentityReponse(newContact,primaryAndSecondaryContact.get(0),primaryAndSecondaryContact.get(1));
        identityWrapper.setContact(identityResponse);
        return identityWrapper;
        }

    private IdentityResponse prepareIdentityReponse(Contact contact,Contact primaryContact, Contact secondaryContact) {
        IdentityResponse.Builder responseBuilder = new IdentityResponse.Builder();
        responseBuilder.setPrimaryContactId(contact.getLinkedId());
        List<Contact> contactsForResponse = new ArrayList<>();
        List<String> emails = new ArrayList<>();
        List<String> phoneNumbers = new ArrayList<>();
        
        if(contact.getLinkedId() != null) {
            contactsForResponse = contactRepository.getContactsForReponse(contact.getLinkedId());
            for(Contact c : contactsForResponse){
                emails.add(c.getEmail());
                phoneNumbers.add(c.getPhoneNumber());
            }
        }
            responseBuilder.setEmails(emails.stream().distinct().collect(Collectors.toList()));
            responseBuilder.setPhoneNumbers(phoneNumbers.stream().distinct().collect(Collectors.toList()));
            
            // for the response fields
         List<Integer> secondaryContactIds = contactRepository.findSecondaryContactIds(contact.getLinkedId());
         System.out.println(secondaryContactIds);
            responseBuilder.setSecondaryContactIds(secondaryContactIds);
        return responseBuilder.build();
    }

    private Contact updatePrimaryContact(Contact primaryContact, ConsolidatedContact consolidatedContact) {
        primaryContact.setEmail(consolidatedContact.getEmail());
        primaryContact.setPhoneNumber(consolidatedContact.getPhoneNumber());
        primaryContact.setUpdatedAt(LocalDateTime.now());
        return primaryContact;
    }

    // changes the state of db and does not create 
    private Contact createOrUpdateSecondaryContact(ConsolidatedContact consolidatedContact, Contact secondaryContact,Contact primaryContact) {
        secondaryContact.setLinkPrecedence("secondary");
            secondaryContact.setLinkedId(primaryContact.getId());
            System.out.println("db state changed!");
            return secondaryContact;
    }

    private List<Contact> determinePrimaryAndSecondaryContact(List<Contact> emails, List<Contact> phoneNumbers) {
        Contact primaryContact = null;
        Contact secondaryContact = null;
        if(!emails.isEmpty()) {
            primaryContact = emails.get(0);
        }
        if(!phoneNumbers.isEmpty()) {
            secondaryContact = phoneNumbers.get(0);
        }
        if(!phoneNumbers.isEmpty()) {
            Contact phoneNumberPrimary = phoneNumbers.get(0);
            if (primaryContact == null || phoneNumberPrimary.getId() < primaryContact.getId()) {
                secondaryContact = primaryContact;
                primaryContact = phoneNumberPrimary;
            }
        }
        List<Contact> result = new ArrayList<>();
        result.add(primaryContact);
        result.add(secondaryContact);
        return result;
    }

    // creates new entry in db if one of the arrtibutes of incoming req matches one of the attributes of contact present in db
    public Contact createOrUpdateContact(ConsolidatedContact consolidatedContact) {

            Integer existingContactId = findContactIdByConsolidatedContact(consolidatedContact);
            Contact newContact;

            if(existingContactId != null) {
            // Contact already exists, create a new one with linkedId and linkPrecedence set
            newContact = new Contact.Builder()
                    .phoneNumber(consolidatedContact.getPhoneNumber())
                    .email(consolidatedContact.getEmail())
                    .linkedId(existingContactId)
                    .linkPrecedence("secondary")
                    .createdAt(LocalDateTime.now())
                    .updatedAt(LocalDateTime.now())
                    .build();
            }
            else {
                newContact = new Contact.Builder()
                        .phoneNumber(consolidatedContact.getPhoneNumber())
                        .email(consolidatedContact.getEmail())
                        .linkPrecedence("primary")
                        .createdAt(LocalDateTime.now())
                        .updatedAt(LocalDateTime.now())
                        .build();
            }
            return newContact;
    }

    public List<Contact> findAll() {
        return contactRepository.findAll();
    }
}
