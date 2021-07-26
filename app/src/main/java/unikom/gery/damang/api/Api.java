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
    Call<CheckUser> register(
            @Field("email") String email,
            @Field("nama") String nama,
            @Field("tanggalLahir") String tanggalLahir,
            @Field("gender") String gender,
            @Field("weight") Float weight,
            @Field("height") Float height,
            @Field("photo") String photo
    );

    @POST("user/insertHeartRateNormal.php")
    @FormUrlEncoded
    Call<CheckUser> insertHeartRateNormal(
            @Field("email") String email,
            @Field("date_time") String dateTime,
            @Field("heart_rate") int heartRate,
            @Field("mode") String mode,
            @Field("status") String status
    );

    @POST("user/insertSportData.php")
    @FormUrlEncoded
    Call<CheckUser> insertSportData(
            @Field("id") String id,
            @Field("start_time") String startTime,
            @Field("tns_target") int tnsTarget,
            @Field("type") String type
    );

    @POST("user/insertHeartRateSport.php")
    @FormUrlEncoded
    Call<CheckUser> insertHeartRateSport(
            @Field("email") String email,
            @Field("id_sport") String idSport,
            @Field("date_time") String dateTime,
            @Field("heart_rate") int heartRate,
            @Field("mode") String mode,
            @Field("status") String status
    );

    @POST("user/updateSportData.php")
    @FormUrlEncoded
    Call<CheckUser> updateSportData(
            @Field("id") String id,
            @Field("end_time") String endTime,
            @Field("duration") int duration,
            @Field("tns_status") String tnsStatus,
            @Field("average_heart_rate") int average,
            @Field("calories_burned") int caloriesBurned
    );

    @POST("user/deleteSportData.php")
    @FormUrlEncoded
    Call<CheckUser> deleteSportData(
            @Field("id") String id
    );
}
