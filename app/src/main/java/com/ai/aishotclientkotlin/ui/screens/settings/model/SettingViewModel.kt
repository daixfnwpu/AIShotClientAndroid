package com.ai.aishotclientkotlin.ui.screens.settings.model

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ai.aishotclientkotlin.data.repository.UploadRepository
import com.ai.aishotclientkotlin.util.SpManager
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingViewModel @Inject constructor(
    private val uploadRepository: UploadRepository,
    @ApplicationContext val context: Context
) : ViewModel() {
    private val _uploadAvatarState = mutableStateOf(true)
    val uploadAvatarState: State<Boolean> = _uploadAvatarState
    private var job: Job? = null
    private val userID: Int? = SpManager(context).getSharedPreference(SpManager.Sp.USERID, "0")
        ?.toInt()
    private val _avatarUrl = mutableStateOf("")
    val avatarUrl = _avatarUrl
    private val _avatarUpdateUri = mutableStateOf(Uri.EMPTY)
    val avatarUpdateUri = _avatarUpdateUri
  /*  val avtarUpdateFlow = MutableStateFlow(false)

    @OptIn(ExperimentalCoroutinesApi::class)
    private val avtarUPdateNewFlow  = avtarUpdateFlow.flatMapLatest {
        uploadRepository.uploadAvatar(avatarUrl.value, success = {
            _uploadAvatarState.value = true
        }, error = {
            _uploadAvatarState.value = false
        })
    }*/
    fun uploadAvatar(uri: Uri) {
    //  job?.cancel()
        Log.e("uploadAvatar","!!!!")
      job = viewModelScope.launch(Dispatchers.IO) {
          Log.e("uploadAvatar","inininin")

          uploadRepository.uploadAvatar(uri, success = {
              _uploadAvatarState.value = true
          }, error = {
              _uploadAvatarState.value = false
          })
      }
    }

    fun onImageSelected(uri: Uri?) {
        Log.e("onImageSelected","!!!!")
        uri?.let {
            Log.e("onImageSelected",".....")
            uploadAvatar(it)
            Log.e("onImageSelected",".....")
        }
        Log.e("onImageSelected","?????")
    }

    val avatarUrlFlow: MutableStateFlow<Int> = MutableStateFlow(1)
    private val avatarUrlNewFlow = avatarUrlFlow.flatMapLatest {
        if (userID != null) {
           val f =  uploadRepository.fetchUserAvatar(
                userId = userID,
                success = {
                    Log.e(
                        "uploadRepository",
                        "uploadRepository.fetchUserAvatar  success"
                    )

                },
                error = { Log.e("uploadRepository", "uploadRepository.fetchUserAvatar error") }
            )
            f
        } else
            flowOf(null)
    }.shareIn(viewModelScope, SharingStarted.WhileSubscribed(), replay = 1)

    init {
        viewModelScope.launch(Dispatchers.IO) {
            avatarUrlNewFlow.collectLatest {
                if (it != null) {
                    _avatarUrl.value = it
                }
            }
        }
    }


}
