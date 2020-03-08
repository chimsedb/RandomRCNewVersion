package com.example.myapplication.retrofit;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface IREadAPI {

    @GET("test/{value}")
    Observable<ModelValue> getValue(@Path("value") String value);
}
