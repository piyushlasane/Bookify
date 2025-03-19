package com.project.makeagain;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface APIResponse {
    @GET("volumes")
    Call<BookResponse> getBooks(@Query("q") String query, @Query("key") String apiKey);
}
