package com.syncallapp.network;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitEmoji {

    private static Retrofit retrofit;
    private static final String BASE_URL = "https://emojihub.yurace.pro/";

    public static Retrofit getEmojiClient() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }
}