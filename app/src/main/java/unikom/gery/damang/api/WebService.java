package unikom.gery.damang.api;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;
import unikom.gery.damang.response.Backup;
import unikom.gery.damang.response.CheckUser;
import unikom.gery.damang.response.News;
import unikom.gery.damang.response.PlaceResponse;

public interface WebService {
    @POST("user/checkUser.php")
    @FormUrlEncoded
    Call<CheckUser> checkUser(@Field("email") String email);

    @POST("user/userRegister.php")
    @FormUrlEncoded
    Call<CheckUser> registerUser(
            @Field("email") String email,
            @Field("nama") String nama,
            @Field("tanggalLahir") String tanggalLahir,
            @Field("gender") String gender,
            @Field("weight") Float weight,
            @Field("height") Float height,
            @Field("photo") String photo
    );

    @Multipart
    @POST("user/backupFile.php")
    Call<Backup> backupCloud(
            @Part MultipartBody.Part file,
            @Part("file") RequestBody name,
            @Query("email") String email,
            @Query("date") String date
    );

    @FormUrlEncoded
    @POST("user/checkBackup.php")
    Call<Backup> restoreCloud(
            @Field("email") String email
    );

    @GET("top-headlines")
    Call<News> getArticleNewsData(
            @Query("country") String country,
            @Query("category") String category,
            @Query("apiKey") String apiKey
    );

    @GET("v2/places")
    Call<PlaceResponse> getNearbyPlace(
            @Query(value = "categories", encoded = true) String categories,
            @Query(value = "filter", encoded = true) String filter,
            @Query(value = "bias", encoded = true) String bias,
            @Query(value = "limit", encoded = true) int limit,
            @Query(value = "apiKey", encoded = true) String key
    );

    @POST("user/updateData.php")
    @FormUrlEncoded
    Call<CheckUser> updateData(
            @Field("email") String email,
            @Field("nama") String nama,
            @Field("tanggalLahir") String tanggalLahir,
            @Field("gender") String gender,
            @Field("weight") Float weight,
            @Field("height") Float height
    );
}
