package com.ai.aishotclientkotlin.ui.screens.home.model

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import com.ai.aishotclientkotlin.data.repository.UploadRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject


@HiltViewModel
class UploadViewModel @Inject constructor(
    private val uploadRepository: UploadRepository
) : ViewModel() {
    fun uploadFiles(context: Context,desc: String?, imageUris: List<Uri>, videoUri: Uri?, success: () -> Unit, error: () -> Unit): Flow<Boolean> {
      return  uploadRepository.uploadFiles(context,desc,imageUris,videoUri,success,error)
    }

}