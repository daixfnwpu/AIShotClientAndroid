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
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.shareIn
import javax.inject.Inject

data class UserProfile(
    val avatarUrl: String = "",
    val name: String = "",
    val nickname: String = "",
    val email: String = "",
    val currentPassword: String = "",
    val newPassword: String = "",
    val confirmPassword: String = "",
    val phoneNumber: String = ""
)
@HiltViewModel
class UserProfileViewModel @Inject constructor(private val uploadRepository: UploadRepository,
                                               @ApplicationContext val context: Context
) : ViewModel() {

    // UI State for the UserProfileScreen
    private val _userProfileState = MutableStateFlow(UserProfile())
    val userProfileState: StateFlow<UserProfile> = _userProfileState

    var avatarUrl: String
        get() = _userProfileState.value.avatarUrl
        set(value) {
            _userProfileState.value = _userProfileState.value.copy(avatarUrl = value)
        }

    var name: String
        get() = _userProfileState.value.name
        set(value) {
            _userProfileState.value = _userProfileState.value.copy(name = value)
        }

    var nickname: String
        get() = _userProfileState.value.nickname
        set(value) {
            _userProfileState.value = _userProfileState.value.copy(nickname = value)
        }

    var phoneNumber: String
        get() = _userProfileState.value.phoneNumber
        set(value) {
            _userProfileState.value = _userProfileState.value.copy(phoneNumber = value)
        }
    var email: String
        get() = _userProfileState.value.email
        set(value) {
            _userProfileState.value = _userProfileState.value.copy(email = value)
        }

    var currentPassword: String
        get() = _userProfileState.value.currentPassword
        set(value) {
            _userProfileState.value = _userProfileState.value.copy(currentPassword = value)
        }

    var newPassword: String
        get() = _userProfileState.value.newPassword
        set(value) {
            _userProfileState.value = _userProfileState.value.copy(newPassword = value)
        }

    var confirmPassword: String
        get() = _userProfileState.value.confirmPassword
        set(value) {
            _userProfileState.value = _userProfileState.value.copy(confirmPassword = value)
        }




    private val _uploadAvatarState = mutableStateOf(true)
    val uploadAvatarState: State<Boolean> = _uploadAvatarState
    private var job: Job? = null
    private val userID: Int? = SpManager(context).getSharedPreference(SpManager.Sp.USERID, "0")
        ?.toInt()
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
    private fun uploadAvatar(uri: Uri) {
        job?.cancel()
        Log.e("uploadAvatar","!!!!")
        job = viewModelScope.launch(Dispatchers.IO) {
            Log.e("uploadAvatar","${uri}")

            uploadRepository.uploadAvatar(context,uri, success = {
                _uploadAvatarState.value = true
            }, error = {
                _uploadAvatarState.value = false
            }).collect { success ->
                if (success) {
                    Log.d("Upload", "Files uploaded")
                }
            }
        }
    }

    fun onImageSelected(uri: Uri?) {
     //   Log.e("onImageSelected","!!!!")
        uri?.let {
      //      Log.e("onImageSelected",".....")
            uploadAvatar(it)
        //    Log.e("onImageSelected",".....")
        }
     //   Log.e("onImageSelected","?????")
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
                    avatarUrl = it
                }
            }
        }
    }



    // Loading profile from repository or network (stubbed here)
    fun loadUserProfile() {
        viewModelScope.launch {
            // Simulate loading user data from repository
            val loadedProfile = UserProfile(
                avatarUrl = "https://example.com/avatar.jpg",
                name = "John Doe",
                nickname = "Johnny",
                phoneNumber = "13800000000",
                email = "john.doe@example.com"
            )
            _userProfileState.value = loadedProfile
        }
    }

    // Save user profile (for example, to a server or database)
    fun saveUserProfile(onSaveSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            val profile = _userProfileState.value

            // Validate password change
            if (profile.newPassword != profile.confirmPassword) {
                onError("密码不匹配！")
                return@launch
            }

            // Perform save logic (e.g., network call or database save)
            // Stubbed as success here
            try {
                // Simulate saving user profile
                onSaveSuccess()
            } catch (e: Exception) {
                onError("保存失败，请重试。")
            }
        }
    }
}
