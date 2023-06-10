package me.srrapero720.watermedia.network;

import me.srrapero720.watermedia.api.network.models.StardeosVideo;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface StardeosApi {
    StardeosApi NET = new Retrofit.Builder()
            .baseUrl("https://stardeos.com/api/v2/")
            .addConverterFactory(GsonConverterFactory.create())
            .client(new OkHttpClient.Builder()/*.addInterceptor(new BasicAuthInterceptor(API_USER_NAME, API_PASSWORD))*/.build())
            .build().create(StardeosApi.class);

    @GET("videos/{vid}")
    Call<StardeosVideo> getVideoInfo(@Path("vid") String vid);
}
