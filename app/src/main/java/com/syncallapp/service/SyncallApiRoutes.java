package com.syncallapp.service;

import android.os.Message;

import com.syncallapp.TicketDetailsActivity;
import com.syncallapp.dto.AuthResponse;
import com.syncallapp.dto.ChatResponse;
import com.syncallapp.dto.LoginRequest;
import com.syncallapp.dto.MessageRequest;
import com.syncallapp.dto.MessageResponse;
import com.syncallapp.dto.PageResponse;
import com.syncallapp.dto.RegisterRequest;
import com.syncallapp.dto.ReportResponse;
import com.syncallapp.dto.TicketRegister;
import com.syncallapp.dto.TicketResponse;
import com.syncallapp.dto.UserRegisterRequest;
import com.syncallapp.dto.UserResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.PATCH;
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

    @POST("/tickets")
    Call<TicketResponse> createTicket(@Body TicketRegister ticketRegister);

    @GET("/tickets/client")
    Call<TicketResponse> clientTicket();

    @PATCH("/tickets/{ticketId}/assign")
    Call<TicketResponse> assignTicket(@Path("ticketId") Long ticketId);

    @GET("/tickets/attendant")
    Call<PageResponse<TicketResponse>> getAttendantTickets(@Query("page") int page, @Query("size") int size);

    @GET("/chats/{ticketId}")
    Call<ChatResponse> getTicketChat(@Path("ticketId") Long ticketId);

    @GET("/messages/chat/{chatId}")
    Call<PageResponse<MessageResponse>> getTicketMessages(@Path("chatId") Long chatId, @Query("page") int page, @Query("size") int size);

    @POST("/messages/chat/{chatId}")
    Call<MessageResponse> createTicketMessages(@Path("chatId") Long chatId, @Body MessageRequest messageRegister);

    @PATCH("/tickets/conclude")
    Call<TicketResponse> concludeTicket();

    @GET("/reports")
    Call<PageResponse<ReportResponse>> getAllReports();

}