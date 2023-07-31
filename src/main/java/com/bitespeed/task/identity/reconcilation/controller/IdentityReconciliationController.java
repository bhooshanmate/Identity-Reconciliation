package com.bitespeed.task.identity.reconcilation.controller;

import com.bitespeed.task.identity.reconcilation.components.IdentityWrapper;
import com.bitespeed.task.identity.reconcilation.entities.ConsolidatedContact;
import com.bitespeed.task.identity.reconcilation.entities.Contact;
import com.bitespeed.task.identity.reconcilation.services.ContactService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class IdentityReconciliationController {
    @Autowired
    private ContactService contactService;
    @PostMapping("/identity")
    public ResponseEntity<IdentityWrapper> saveContact(@RequestBody ConsolidatedContact consolidatedContact) {
        IdentityWrapper response = contactService.save(consolidatedContact);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/")
    public String home(){
        return "<h1>Hi there, this is home page, please refer to this readme file (https://github.com/bhooshanmate/Identity-Reconciliation/blob/main/README.md) for endpoints</h1>";
    }

    @GetMapping("/getAll")
    public List<Contact> getAllContacts() {
        return contactService.findAll();
    }

}
