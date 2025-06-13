package com.fariznst0075.miniproject3.network

import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory

object FilmApi {
    private const val BASE_URL = "https://684bd625ed2578be881ca7f1.mockapi.io/"

    val retrofitService: FilmApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(ScalarsConverterFactory.create())
            .build()
            .create(FilmApiService::class.java)
    }
}