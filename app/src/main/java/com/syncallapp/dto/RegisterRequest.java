package com.syncallapp.dto;

public class RegisterRequest {

    private String name;
    private String email;
    private String password;

    private CompanyRequestDTO company;

    public RegisterRequest(String name, String email, String password, CompanyRequestDTO company) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.company = company;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public CompanyRequestDTO getCompany() {
        return company;
    }

    public void setCompany(CompanyRequestDTO company) {
        this.company = company;
    }
}
