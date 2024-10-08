package com.ai.aishotclientkotlin.ui.screens.home.model

import android.content.Context
import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ai.aishotclientkotlin.data.dao.entity.Review
import com.ai.aishotclientkotlin.data.repository.ReviewRepository
import com.ai.aishotclientkotlin.util.SpManager
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class ReviewViewModel @Inject constructor(
   // private val savedStateHandle: SavedStateHandle,
    private val reviewRepository: ReviewRepository,
    @ApplicationContext val context: Context
) : ViewModel() {
    var movieId: Long? = null
    val userID: Long? = SpManager(context).getSharedPreference(SpManager.Sp.USERID, "0L")
        ?.toLong()
    val userName: String =
        SpManager(context).getSharedPreference(SpManager.Sp.USERNAME, "User").toString()

    fun loadReviews() {
        viewModelScope.launch {
            if (movieId != null) {
                reviewRepository.loadReviews(
                    movieId!!,
                    success = { /* 处理成功逻辑 */ },
                    error = { /* 处理错误逻辑 */ }
                ).collect { reviews ->
                    // 收集评论数据
                }
            }
        }
    }
    fun sendReview(content: String,url: String ="",success: () -> Unit,error: () -> Unit) {
        viewModelScope.launch {
            val review: Review =
                Review(author = userName, content = content, url = url, userId = userID, movieId = movieId)
            reviewRepository.createReview(review, success = {
                //     reviewListFlow.tryEmit(id)
                success()
                Log.e("HTTP", "send the review : ${review}")
            }, error = {
                error()
            }).collect { createdReview ->
                // 处理创建的评论
                success()
                Log.e("HTTP","crate View success")
            }
        }

    }

    fun createReview(review: Review) {
        viewModelScope.launch {
            reviewRepository.createReview(
                review,
                success = { /* 处理成功逻辑 */ },
                error = { /* 处理错误逻辑 */ }
            ).collect { createdReview ->
                // 处理创建的评论
                Log.e("HTTP","crate View success")
            }
        }
    }

    fun loadReview(id: Long) {
        viewModelScope.launch {
            reviewRepository.loadReview(
                id,
                success = { /* 处理成功逻辑 */ },
                error = { /* 处理错误逻辑 */ }
            ).collect { review ->
                // 处理评论数据
            }
        }
    }

    fun updateReview(id: Long, review: Review) {
        viewModelScope.launch {
            reviewRepository.updateReview(
                id,
                review,
                success = { /* 处理成功逻辑 */ },
                error = { /* 处理错误逻辑 */ }
            ).collect { result ->
                result.onSuccess { updatedReview ->
                    // Handle the successfully updated review
                }.onFailure { throwable ->
                    // Handle the error (exception)
                    Log.e("UpdateReviewError", throwable.message ?: "Unknown error")
                }
            }
        }
    }

    fun deleteReview(id: Long,success:()-> Unit,error:()->Unit) {
        viewModelScope.launch {
            reviewRepository.deleteReview(id).collect { result ->
                result.onSuccess {
                    // 操作成功
                    success()
                }.onFailure {
                    // 操作失败，处理异常
                    error()
                }
            }
        }
    }
}
