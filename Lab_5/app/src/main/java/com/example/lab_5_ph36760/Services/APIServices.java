package com.example.lab_5_ph36760.Services;

import com.example.lab_5_ph36760.Model.Distributor;
import com.example.lab_5_ph36760.Model.Fruit;
import com.example.lab_5_ph36760.Model.Response_Model;
import com.example.lab_5_ph36760.Model.User;

import java.util.ArrayList;
import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.PartMap;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface APIServices {
    //Sử dụng máy ảo android studio thì localhost thay thành id 10.0.0.2
    //Đối với máy thật ta sử dụng ip của máy
    //Base_URL là url của api

    public static String BASE_URL = "http://10.0.2.2:3000/api/";

    //Annotations @GET cho method GET và url phương gọi

    @GET("get-list-distributor")
    Call<Response_Model<ArrayList<Distributor>>> getListDistributos();
    //Call giá trị trả về của api

    @GET("search-distributor")
    Call<Response_Model<ArrayList<Distributor>>> searchDistributor(@Query("key") String key);

    @POST("add-distributor")
    Call<Response_Model<Distributor>> addDistributor(@Body Distributor distributor);

    //Param url sẽ bỏ vào {}
    @DELETE("delete-distributor-by-id/{id}")
    Call<Response_Model<Distributor>> deleteDistributorById(@Path("id") String id);

    @PUT("update-distributor/{id}")
    Call<Response_Model<Distributor>> updateDistributorById(@Path("id") String id, @Body Distributor distributor);

    //đăng nhập
    @Multipart
    @POST("register-send-email")
    Call<Response_Model<User>> register (@Part("username")RequestBody username,
                                         @Part("password") RequestBody password,
                                         @Part("email") RequestBody email,
                                         @Part("name") RequestBody name,
                                         @Part MultipartBody.Part avatar);

    @POST("login")
    Call<Response_Model<User>> login (@Body User user);

    @GET("get-fruit")
    Call<Response_Model<ArrayList<Fruit>>> getListFruit(@Header("Authorization") String token);

    @GET("get-fruit-no-token")
    Call<Response_Model<ArrayList<Fruit>>> getListFruitNoToken();

    @Multipart
    @POST("add-fruit-with-file-image")
    Call<Response_Model<Fruit>> addFruit(@PartMap Map<String, RequestBody> requestBodyMap,
                                         @Part MultipartBody.Part image);

    @PUT("update-user/{id}")
    Call<Response_Model<Fruit>> updateFruit(@Path("id") String id, @Body Fruit fruit);

    @DELETE("delete-fruit-by-id/{id}")
    Call<Response_Model<Fruit>> deleteFruit(@Path("id") String id);
}
