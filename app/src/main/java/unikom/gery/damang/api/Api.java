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
}
