package com.fariznst0075.miniproject3.network

import retrofit2.http.GET

interface FilmApiService {
    @GET("films")
    suspend fun getFilms(): String
}