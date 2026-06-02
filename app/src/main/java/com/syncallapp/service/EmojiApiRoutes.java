package com.syncallapp.service;

import com.syncallapp.dto.EmojiResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface EmojiApiRoutes {

    @GET("api/random")
    Call<EmojiResponse> getRandomEmoji();
}
