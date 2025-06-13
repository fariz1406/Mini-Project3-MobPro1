package com.fariznst0075.miniproject3.ui.screen

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fariznst0075.miniproject3.model.Film
import com.fariznst0075.miniproject3.network.ApiStatus
import com.fariznst0075.miniproject3.network.ImgbbApi
import com.fariznst0075.miniproject3.network.FilmApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.ByteArrayOutputStream

class MainViewModel : ViewModel() {

    var data = mutableStateOf(emptyList<Film>())
        private set

    var status = MutableStateFlow(ApiStatus.LOADING)
        private set

    var errorMessage = mutableStateOf<String?>(null)
        private set


    fun retrieveData(userId: String?){
        viewModelScope.launch(Dispatchers.IO) {
            status.value = ApiStatus.LOADING
            try {
                val allResep = FilmApi.service.getFilmAll()
                Log.d("MainViewModel", "allResep: $allResep")

                val dummyData = allResep.filter { it.userId.isBlank() }
                Log.d("MainViewModel", "dummyData: $dummyData")

                val finalList = if (userId.isNullOrBlank()) {
                    dummyData
                } else {
                    val userData = try {
                        FilmApi.service.getFilm(userId)
                    } catch (e: Exception) {
                        Log.d("MainViewModel", "getResep failed: ${e.message}")
                        emptyList()
                    }

                    if (userData.isEmpty()) {
                        dummyData
                    } else {
                        dummyData + userData
                    }
                }

                Log.d("MainViewModel", "finalList: $finalList")
                data.value = finalList
                status.value = ApiStatus.SUCCES
            }catch (e: Exception) {
                Log.d("MainViewModel", "Failure: ${e.message}")
                status.value = ApiStatus.FAILED
            }
        }
    }

    @SuppressLint("SuspiciousIndentation")
    fun deleteData(userId: String, resepId: String ){
        viewModelScope.launch(Dispatchers.IO){
            try {
                FilmApi.service.deleteFilm(resepId)
                retrieveData(userId)
            } catch (e: Exception){
                Log.d("MainViewModel", "Failure: ${e.message}")
                errorMessage.value = "Error: ${e.message}"
            }
        }
    }

    private fun saveData( title: String, description: String, userId: String, imageUrl: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                FilmApi.service.postFilm(title, description, userId, imageUrl
                )
                retrieveData(userId)
            } catch (e: Exception) {
                Log.d("MainViewModel", "Failure: ${e.message}")
                errorMessage.value = "Error: ${e.message}"
            }
        }
    }


    fun clearMessage() {
        errorMessage.value = null
    }


    private suspend fun uploadImageToImgBBViaRetrofit(bitmap: Bitmap): String? {
        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 90, byteArrayOutputStream)
        val imageBytes = byteArrayOutputStream.toByteArray()

        val requestBody = imageBytes.toRequestBody("image/jpeg".toMediaTypeOrNull())
        val multipart = MultipartBody.Part.createFormData("image", "upload.jpg", requestBody)

        return try {
            val response = ImgbbApi.services.uploadImage(image = multipart)
            if (response.success) {
                response.data.url
            } else {
                Log.e("UploadError", "Upload gagal: ${response.status}")
                null
            }
        } catch (e: Exception) {
            Log.e("UploadError", "Exception: ${e.message}")
            null
        }
    }

    fun uploadAndSave(
        title: String,
        description: String,
        userId: String,
        bitmap: Bitmap,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            val imageUrl = uploadImageToImgBBViaRetrofit(bitmap)
            if (imageUrl != null) {
                saveData(title, description, userId, imageUrl)
                onSuccess()
            } else {
                onError("Gagal upload gambar ke ImgBB")
            }
        }
    }

    fun updateData(
        id: String,
        title: String,
        description: String,
        userId: String,
        imageUrl: String
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                FilmApi.service.updateFilm(id, title, description, userId, imageUrl)
                retrieveData(userId)
            } catch (e: Exception) {
                Log.d("MainViewModel", "Update gagal: ${e.message}")
                errorMessage.value = "Gagal update: ${e.message}"
            }
        }
    }




}