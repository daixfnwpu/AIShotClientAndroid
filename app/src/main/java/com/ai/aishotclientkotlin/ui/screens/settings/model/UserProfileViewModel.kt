package com.ai.aishotclientkotlin.ui.screens.settings.model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
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
class UserProfileViewModel @Inject constructor() : ViewModel() {

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
