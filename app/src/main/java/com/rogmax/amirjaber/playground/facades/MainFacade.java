package com.rogmax.amirjaber.playground.facades;

import com.rogmax.amirjaber.playground.models.Value;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;

/**
 * Created by Amir Jaber on 1/30/2018.
 */

public interface MainFacade {
    @GET("view.php")
    Call<Value> read();

    @FormUrlEncoded
    @POST("insert.php")
    Call<Value> list(@Field("name") String name,
                     @Field("address") String address);

    @FormUrlEncoded
    @POST("search.php")
    Call<Value> search(@Field("search") String search);

    @FormUrlEncoded
    @POST("update.php")
    Call<Value> update(@Field("id") Long id,
                       @Field("name") String name,
                       @Field("address") String address);

    @FormUrlEncoded
    @POST("delete.php")
    Call<Value> delete(@Field("id") Long id);
}
