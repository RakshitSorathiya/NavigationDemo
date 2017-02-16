package demo.android.navigationdemo.api.service;

import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by ln-149 on 16/2/17.
 */

public interface MyService {

    @GET("maps/api/geocode/json?")
    Call<JsonObject> getResult(@Query("address") String address);


}
