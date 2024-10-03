package com.ai.aishotclientkotlin.data.repository

import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.provider.OpenableColumns
import android.util.Log
import androidx.annotation.WorkerThread
import com.ai.aishotclientkotlin.data.remote.UploadService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.suspendCancellableCoroutine
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Response
import timber.log.Timber
import java.io.File
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class UploadRepository @Inject constructor(
    private val uploadService: UploadService

) : Repository {

    init {
        Timber.e("Injection UploadRepository")
    }

    @WorkerThread
    fun uploadFiles(context: Context, description : String?, imageUris: List<Uri>, videoUri: Uri?, success: () -> Unit, error: () -> Unit) = flow {

        val imageFiles = imageUris?.mapNotNull { getFileFromUri(context, it) } ?: emptyList()
        val videoFile = videoUri?.let { getFileFromUri(context, it) }
       // val description_ = description?.let {  }
        val imageParts = if (imageFiles.isNotEmpty()) {
            imageFiles.map { prepareFilePart("images[]", it) }
        } else {
            null
        }

        val videoPart = videoFile?.let { prepareFilePart("video", it) }
        val descriptionPart = description?.toRequestBody("text/plain".toMediaTypeOrNull())
        if (imageParts == null && videoPart == null && descriptionPart == null) {
            Log.e("Upload", "No files and description to upload")
            emit(false)  // 没有要上传的文件时直接返回
            return@flow
        }

        try {
            // 发送挂起的 Retrofit 请求
            val response = uploadService.uploadFiles(imageParts, videoPart, description = descriptionPart).awaitResponse()

            if (response.isSuccessful) {
                Log.e("Upload", "Success")
                emit(true)
            } else {
                Log.e("Upload", "Failed: ${response.message()}")
                error()
            }
        } catch (e: Exception) {
            Log.e("Upload", "Error: ${e.message}")
            error()
        }
    }.onCompletion { success() }.flowOn(Dispatchers.IO)


    private fun prepareFilePart(partName: String, file: File): MultipartBody.Part {
        val requestBody = file.asRequestBody("multipart/form-data".toMediaTypeOrNull())
        return MultipartBody.Part.createFormData(partName, file.name, requestBody)
    }

}
// 扩展 Retrofit 回调为 suspend 函数
suspend fun <T> Call<T>.awaitResponse(): Response<T> {
    return suspendCancellableCoroutine { continuation ->
        enqueue(object : retrofit2.Callback<T> {
            override fun onResponse(call: Call<T>, response: Response<T>) {
                continuation.resume(response)
            }

            override fun onFailure(call: Call<T>, t: Throwable) {
                continuation.resumeWithException(t)
            }
        })

        continuation.invokeOnCancellation {
            try {
                cancel()
            } catch (ex: Exception) {
                // Ignore cancel exception
            }
        }
    }
}

fun getFileFromUri(context: Context, uri: Uri): File? {
    val cursor: Cursor? = context.contentResolver.query(uri, null, null, null, null)
    cursor?.use {
        val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
        cursor.moveToFirst()
        val fileName = cursor.getString(nameIndex)
        val file = File(context.cacheDir, fileName)
        context.contentResolver.openInputStream(uri)?.use { inputStream ->
            file.outputStream().use { outputStream ->
                inputStream.copyTo(outputStream)
            }
        }
        return file
    }
    return null
}