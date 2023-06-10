package me.srrapero720.watermedia.network;

import me.srrapero720.watermedia.api.network.models.KickChannel;
import me.srrapero720.watermedia.api.network.models.KickVideo;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Path;

public interface KickApi {
    me.srrapero720.watermedia.api.network.KickApi NET = new Retrofit.Builder()
            .baseUrl("https://kick.com/api/v1/")
            .addConverterFactory(GsonConverterFactory.create())
            .client(new OkHttpClient.Builder()/*.addInterceptor(new BasicAuthInterceptor(API_USER_NAME, API_PASSWORD))*/.build())
            .build().create(me.srrapero720.watermedia.api.network.KickApi.class);

    @Headers({"accept: application/json",})
    @GET("channels/{ch}")
    Call<KickChannel> getChannelInfo(@Path("ch") String ch);

    @Headers({"accept: application/json",})
    @GET("video/{vid}")
    Call<KickVideo> getVideoInfo(@Path("vid") String vid);
}