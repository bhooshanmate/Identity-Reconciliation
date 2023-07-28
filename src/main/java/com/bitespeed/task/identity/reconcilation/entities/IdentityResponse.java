package com.bitespeed.task.identity.reconcilation.entities;

import java.util.List;

public class IdentityResponse {
    private Integer primaryContactId;
    private List<String> emails;
    private List<String> phoneNumbers;
    private List<Integer> secondaryContactIds;

    public Integer getPrimaryContactId() {
        return primaryContactId;
    }

    public void setPrimaryContactId(Integer primaryContactId) {
        this.primaryContactId = primaryContactId;
    }

    public List<String> getEmails() {
        return emails;
    }

    public void setEmails(List<String> emails) {
        this.emails = emails;
    }

    public List<String> getPhoneNumbers() {
        return phoneNumbers;
    }

    public void setPhoneNumbers(List<String> phoneNumbers) {
        this.phoneNumbers = phoneNumbers;
    }

    public List<Integer> getSecondaryContactIds() {
        return secondaryContactIds;
    }

    public void setSecondaryContactIds(List<Integer> secondaryContactIds) {
        this.secondaryContactIds = secondaryContactIds;
    }
    public static class Builder {
        private Integer primaryContactId;
        private List<String> emails;
        private List<String> phoneNumbers;
        private List<Integer> secondaryContactIds;

        public Builder setPrimaryContactId(Integer primaryContactId) {
            this.primaryContactId = primaryContactId;
            return this;
        }

        public Builder setEmails(List<String> emails) {
            this.emails = emails;
            return this;
        }

        public Builder setPhoneNumbers(List<String> phoneNumbers) {
            this.phoneNumbers = phoneNumbers;
            return this;
        }

        public Builder setSecondaryContactIds(List<Integer> secondaryContactIds) {
            this.secondaryContactIds = secondaryContactIds;
            return this;
        }

        public IdentityResponse build() {
            IdentityResponse identityResponse = new IdentityResponse();
            identityResponse.primaryContactId = primaryContactId;
            identityResponse.emails = emails;
            identityResponse.phoneNumbers = phoneNumbers;
            identityResponse.secondaryContactIds = secondaryContactIds;
            return identityResponse;
        }
    }

}
