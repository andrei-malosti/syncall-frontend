package com.syncallapp.service;

import com.syncallapp.TicketDetailsActivity;
import com.syncallapp.dto.AuthResponse;
import com.syncallapp.dto.LoginRequest;
import com.syncallapp.dto.PageResponse;
import com.syncallapp.dto.RegisterRequest;
import com.syncallapp.dto.TicketResponse;
import com.syncallapp.dto.UserRegisterRequest;
import com.syncallapp.dto.UserResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface SyncallApiRoutes {

    @POST("/auth/login")
    Call<AuthResponse> login(@Body LoginRequest request);

    @GET("/tickets")
    Call<PageResponse<TicketResponse>> getTickets(@Query("page") int page, @Query("size") int size);

    @GET("/tickets/{ticketId}")
    Call<TicketResponse> getTicket(@Path("ticketId") Long ticketId);

    @POST("/auth/register")
    Call<AuthResponse> register(@Body RegisterRequest registerRequest);

    @POST("/clients")
    Call<UserResponse> clientRegister(@Body UserRegisterRequest registerRequest);

    @POST("/attendants")
    Call<UserResponse> attendantRegister(@Body UserRegisterRequest registerRequest);

}