package unikom.gery.damang.api;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;
import unikom.gery.damang.response.CheckUser;

public interface Api {
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
}
