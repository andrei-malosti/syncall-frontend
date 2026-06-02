package com.syncallapp.dto;

import java.time.LocalDateTime;

public class ReportResponse {


    private Long id;
    private String ticketDescription;
    private String ticketCreatedAt;
    private String ticketConcludedAt;

    public ReportResponse(Long id, String ticketDescription, String ticketCreatedAt, String ticketConcludedAt) {
        this.id = id;
        this.ticketDescription = ticketDescription;
        this.ticketCreatedAt = ticketCreatedAt;
        this.ticketConcludedAt = ticketConcludedAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTicketDescription() {
        return ticketDescription;
    }

    public void setTicketDescription(String ticketDescription) {
        this.ticketDescription = ticketDescription;
    }

    public String getTicketCreatedAt() {
        return ticketCreatedAt;
    }

    public void setTicketCreatedAt(String ticketCreatedAt) {
        this.ticketCreatedAt = ticketCreatedAt;
    }

    public String getTicketConcludedAt() {
        return ticketConcludedAt;
    }

    public void setTicketConcludedAt(String ticketConcludedAt) {
        this.ticketConcludedAt = ticketConcludedAt;
    }
}
