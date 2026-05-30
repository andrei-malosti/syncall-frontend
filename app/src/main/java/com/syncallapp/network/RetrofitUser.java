package com.syncallapp.network;

import android.content.Context;
import android.content.SharedPreferences;
import com.syncallapp.BuildConfig;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitUser {

    private static Retrofit retrofit = null;

    public static Retrofit getUser(Context context) {
        if (retrofit == null) {

            OkHttpClient client = new OkHttpClient.Builder().addInterceptor(chain -> {
                Request originalRequest = chain.request();

                SharedPreferences prefs = context.getSharedPreferences("SyncallPrefs", Context.MODE_PRIVATE);
                String token = prefs.getString("TOKEN_JWT", null);

                if (token != null) {
                    Request requestWithToken = originalRequest.newBuilder()
                            .header("Authorization", "Bearer " + token)
                            .build();
                    return chain.proceed(requestWithToken);
                }
                return chain.proceed(originalRequest);
            }).build();

            retrofit = new Retrofit.Builder()
                    .baseUrl(BuildConfig.API_BASE_URL)
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }

}
