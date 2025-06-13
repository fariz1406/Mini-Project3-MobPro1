package com.fariznst0075.miniproject3.network

import com.fariznst0075.miniproject3.model.Film
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.DELETE
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface FilmApiService {
    @GET("/films")
    suspend fun getFilm(@Query("userId") userId: String): List<Film>

    @GET("/films")
    suspend fun getFilmAll(): List<Film>

    @DELETE("/films/{id}")
    suspend fun deleteFilm(
        @Path("id") id: String
    )

    @FormUrlEncoded
    @PUT("/films/{id}")
    suspend fun updateFilm(
        @Path("id") id: String,
        @Field("title") title: String,
        @Field("description") description: String,
        @Field("userId") userId: String,
        @Field("imageUrl") imageUrl: String
    ): Film


    @FormUrlEncoded
    @POST("/films")
    suspend fun postFilm(
        @Field("title") title: String,
        @Field("description") description: String,
        @Field("userId") userId: String,
        @Field("imageUrl") imageUrl: String
    ): Film

}

object FilmApi {
    private const val BASE_URL = "https://684bd625ed2578be881ca7f1.mockapi.io"

    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val service: FilmApiService = retrofit.create(FilmApiService::class.java)
}


enum class ApiStatus { LOADING, SUCCES, FAILED }