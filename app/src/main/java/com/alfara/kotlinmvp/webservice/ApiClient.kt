package com.alfara.kotlinmvp.webservice

import com.alfara.kotlinmvp.models.Post
import com.alfara.kotlinmvp.models.User
import com.alfara.kotlinmvp.utils.Constants
import com.alfara.kotlinmvp.utils.WrappedListResponse
import com.alfara.kotlinmvp.utils.WrappedResponse
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*
import java.util.concurrent.TimeUnit

class ApiClient{
    companion object{
        private var retrofit : Retrofit ?= null
        private val opt = OkHttpClient.Builder().apply {
            connectTimeout(30, TimeUnit.SECONDS)
            writeTimeout(30, TimeUnit.SECONDS)
            readTimeout(30, TimeUnit.SECONDS)
        }.build()

        private fun getClient() : Retrofit {
            return if (retrofit == null){
                retrofit = Retrofit.Builder().apply {
                    client(opt)
                    baseUrl(Constants.END_POINT)
                    addConverterFactory(GsonConverterFactory.create())
                }.build()
                retrofit!!
            }else{
                retrofit!!
            }
        }

        fun instance() = getClient().create(ApiService::class.java)
    }
}

interface ApiService{

    @FormUrlEncoded
    @POST("login")
    fun login(
        @Field("email") email: String,
        @Field("password") password : String
    ) : Call<WrappedResponse<User>>

    @FormUrlEncoded
    @POST("register")
    fun register(
        @Field("name") name : String,
        @Field("email") email : String,
        @Field("password") password : String
    ) : Call<WrappedResponse<User>>

    @GET("post")
    fun getAllPost(
        @Header("Authorization") token : String
    ) : Call<WrappedListResponse<Post>>

    @FormUrlEncoded
    @POST("post")
    fun create (
        @Header("Authorization") token : String,
        @Field("title") title : String,
        @Field("content") content : String
    ) : Call<WrappedResponse<Post>>

    @FormUrlEncoded
    @PUT("post/{id}")
    fun update (
        @Header("Authorization") token : String,
        @Path("id") id : String,
        @Field("title") title :String,
        @Field("content") content : String
    ) : Call<WrappedResponse<Post>>

    @DELETE("post/{id}")
    fun delete(
        @Header("Authorization") token : String,
        @Path("id") id : String
    ) : Call<WrappedResponse<Post>>

}