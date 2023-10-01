package me.srrapero720.watermedia.api.network.imgurv3;

import me.srrapero720.watermedia.api.network.imgurv3.models.ImgurAlbumTagData;
import me.srrapero720.watermedia.api.network.imgurv3.models.ImgurData;
import me.srrapero720.watermedia.api.network.imgurv3.models.images.ImgurImage;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface ImgurAPIv3 {
    ImgurAPIv3 NET = new Retrofit.Builder()
            .baseUrl("https://api.imgur.com/3/")
            .addConverterFactory(GsonConverterFactory.create())
            .client(new OkHttpClient.Builder().build())
            .build().create(ImgurAPIv3.class);

    /**
     * Can get images from
     * /a/
     * /gallery/ <br><br>
     * https://api.imgur.com/3/album/E0Y1VRh?client_id=685cdf74b1229b9
     *
     * @param id id of the picture album or gallery
     * @return response
     */
    @GET("album/{id}?client_id=685cdf74b1229b9")
    Call<ImgurData<ImgurAlbumTagData>> getImageFromAlbum(@Path("id") String id);

    /**
     * Can get images from
     * /t/ <br><br>
     * https://api.imgur.com/3/gallery/t/rick_roll/kGy6J8J?client_id=546c25a59c58ad7
     * @param id id of the picture album or gallery
     * @return response
     */
    @GET("gallery/t/{tag}/{id}?client_id=685cdf74b1229b9")
    Call<ImgurData<ImgurAlbumTagData>> getImageFromTagGallery(@Path("tag") String tagId, @Path("id") String id);

    /**
     * Can get normal images <br><br>
     * https://api.imgur.com/3/image/LkpGYcE?client_id=685cdf74b1229b9
     * @param id of the image
     * @return image info
     */
    @GET("image/{id}?client_id=685cdf74b1229b9")
    Call<ImgurData<ImgurImage>> getImage(@Path("id") String id);
}