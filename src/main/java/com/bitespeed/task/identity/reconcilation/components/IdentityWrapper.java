package com.bitespeed.task.identity.reconcilation.components;

import com.bitespeed.task.identity.reconcilation.entities.IdentityResponse;
import org.springframework.stereotype.Component;

@Component
public class IdentityWrapper {
    private IdentityResponse contact;

    public IdentityResponse getContact() {
        return contact;
    }

    public void setContact(IdentityResponse contact) {
        this.contact = contact;
    }

}
