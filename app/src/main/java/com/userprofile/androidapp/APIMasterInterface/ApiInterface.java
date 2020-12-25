package com.userprofile.androidapp.APIMasterInterface;


import com.userprofile.androidapp.ResponseModel.ReponseUserListModel;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Path;

/**
 * Created by Annaso Shinde on 24-Dec-2020
 */

public interface ApiInterface {
    @Headers({
            "Authorization: Basic secure@puneit",
            "Content-Encoding: gzip"
    })
    @GET("users")
    Call<List<ReponseUserListModel>> getUsersList();


    @GET("users/{followers_name}")
    @Headers({"Authorization: Basic secure@puneit", "Content-Encoding: gzip"})
    Call<ReponseUserListModel> getPersonData(@Path("followers_name") String str);

    @GET("users/{followers_name}/followers")
    @Headers({"Authorization: Basic secure@puneit", "Content-Encoding: gzip"})
    Call<List<ReponseUserListModel>> getFollowerList(@Path("followers_name") String str);

}