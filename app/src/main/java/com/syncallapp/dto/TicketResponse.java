package com.syncallapp.dto;

import java.time.LocalDateTime;

public class TicketResponse {

    private Long id;
    private String description;
    private String createdAt;
    private TicketStatus ticketStatus;
    private String attendantName;
    private Long attendantId;
    private String clientName;
    private Long clientId;

    public TicketResponse(Long id, String description, String createdAt, TicketStatus ticketStatus, String attendantName, Long attendantId, String clientName, Long clientId) {
        this.id = id;
        this.description = description;
        this.createdAt = createdAt;
        this.ticketStatus = ticketStatus;
        this.attendantName = attendantName;
        this.attendantId = attendantId;
        this.clientName = clientName;
        this.clientId = clientId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public TicketStatus getTicketStatus() {
        return ticketStatus;
    }

    public void setTicketStatus(TicketStatus ticketStatus) {
        this.ticketStatus = ticketStatus;
    }

    public String getAttendantName() {
        return attendantName;
    }

    public void setAttendantName(String attendantName) {
        this.attendantName = attendantName;
    }

    public Long getAttendantId() {
        return attendantId;
    }

    public void setAttendantId(Long attendantId) {
        this.attendantId = attendantId;
    }

    public String getClientName() {
        return clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    public Long getClientId() {
        return clientId;
    }

    public void setClientId(Long clientId) {
        this.clientId = clientId;
    }
}
