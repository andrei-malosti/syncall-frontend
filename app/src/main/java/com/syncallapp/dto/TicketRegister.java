package com.syncallapp.dto;

public class TicketRegister {

    private String description;

    public TicketRegister(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
