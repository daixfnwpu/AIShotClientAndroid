package com.ai.aishotclientkotlin.ui.screens.home.model

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ai.aishotclientkotlin.data.dao.entity.Review
import com.ai.aishotclientkotlin.data.repository.ReviewRepository
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

class ReviewViewModel @Inject constructor(
    private val reviewRepository: ReviewRepository
) : ViewModel() {

    fun loadReviews() {
        viewModelScope.launch {
            reviewRepository.loadReviews(
                success = { /* 处理成功逻辑 */ },
                error = { /* 处理错误逻辑 */ }
            ).collect { reviews ->
                // 收集评论数据
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
