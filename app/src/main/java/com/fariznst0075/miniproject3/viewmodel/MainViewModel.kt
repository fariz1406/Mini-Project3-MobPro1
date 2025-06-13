package com.fariznst0075.miniproject3.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fariznst0075.miniproject3.network.FilmApi
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {
    init {
        retrieveData()
    }

    private fun retrieveData() {
        viewModelScope.launch {
            try {
                val response = FilmApi.retrofitService.getFilms()
                Log.d("TandaTonton", "Success: $response")
            } catch (e: Exception) {
                Log.e("TandaTonton", "Failure: ${e.message}")
            }
        }
    }
}