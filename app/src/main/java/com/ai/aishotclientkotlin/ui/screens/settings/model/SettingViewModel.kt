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
class SettingViewModel  @Inject constructor(private val uploadRepository: UploadRepository, @ApplicationContext val context: Context) : ViewModel() {
    private val _uploadAvatarState = mutableStateOf(true)
    val         uploadAvatarState: State<Boolean> = _uploadAvatarState
    private var         job: Job? = null
    private val   userID : Int? = SpManager(context).getSharedPreference(SpManager.Sp.USERID,"0")
        ?.toInt()
    private val _avatarUrl = mutableStateOf("")
    val avatarUrl: State<String> = _avatarUrl

    fun uploadAvatar(uri: Uri) {
        job?.cancel()
        job = viewModelScope.launch(Dispatchers.IO) {
            uploadRepository.uploadAvatar(uri, success = {
                _uploadAvatarState.value = true
            }, error = {
                _uploadAvatarState.value = false
            })
        }
    }

    val avatarUrlFlow: MutableStateFlow<Int> = MutableStateFlow(1)
    private val avatarUrlNewFlow = avatarUrlFlow.flatMapLatest {
        if (userID != null) {
            uploadRepository.fetchUserAvatar(
                userId = userID,
                success = { Log.e("uploadRepository","uploadRepository.fetchUserAvatar  success")  },
                error = { Log.e("uploadRepository","uploadRepository.fetchUserAvatar error")}
            )
        }else
            flowOf(null)
    }.shareIn(viewModelScope, SharingStarted.WhileSubscribed(), replay = 1)

    init {
        viewModelScope.launch(Dispatchers.IO) {
            avatarUrlNewFlow.collectLatest {
                if (it != null) {
                    _avatarUrl.value=it
                }
            }
        }
    }


}
