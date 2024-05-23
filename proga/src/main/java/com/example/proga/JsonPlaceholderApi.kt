package com.example.proga

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface JsonPlaceholderApi {
    @GET("posts/{id}")
    fun getPost(@Path("id") postId: Int): Call<Post>
}
