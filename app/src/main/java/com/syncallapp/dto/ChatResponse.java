package com.syncallapp.dto;

public class ChatResponse {


    private Long id;
    private Long ticketId;
    private Long companyId;

    public ChatResponse(Long id, Long ticketId, Long companyId) {
        this.id = id;
        this.ticketId = ticketId;
        this.companyId = companyId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getTicketId() {
        return ticketId;
    }

    public void setTicketId(Long ticketId) {
        this.ticketId = ticketId;
    }

    public Long getCompanyId() {
        return companyId;
    }

    public void setCompanyId(Long companyId) {
        this.companyId = companyId;
    }
}
