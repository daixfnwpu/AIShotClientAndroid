package com.ai.aishotclientkotlin.data.repository

import androidx.annotation.WorkerThread
import com.ai.aishotclientkotlin.data.dao.ReviewDao
import com.ai.aishotclientkotlin.data.dao.entity.Review
import com.ai.aishotclientkotlin.data.remote.ReviewService
import com.skydoves.sandwich.onError
import com.skydoves.sandwich.onException
import com.skydoves.sandwich.suspendOnSuccess
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import javax.inject.Inject

class ReviewRepository @Inject constructor(
    private val reviewDao: ReviewDao, // Room DAO
    private val reviewService: ReviewService // Retrofit API 服务
) {

    @WorkerThread
    fun loadReviews(success: () -> Unit, error: () -> Unit): Flow<List<Review>> = flow {
        var reviews = reviewDao.getAllReviews()

        if (reviews.isEmpty()) {
            val response = reviewService.fetchReviews()
            response.suspendOnSuccess {
                reviews = data
                reviewDao.insertReviews(data)
                emit(reviews)
                success()
            }.onError {
                error()
            }.onException {
                error()
            }
        } else {
            emit(reviews)
        }
    }.flowOn(Dispatchers.IO)

    // 创建新评论
    @WorkerThread
    fun createReview(review: Review, success: () -> Unit, error: () -> Unit): Flow<Result<Review>> = flow {
       try {
           val response = reviewService.createReview(review)
           response.suspendOnSuccess {
               reviewDao.insertReviews(listOf(data)) // 将新评论插入数据库

               success()
               emit(Result.success(data))
           }
       }catch (e:Exception){
           error()
           emit(Result.failure<Review>(e))
       }
    }.flowOn(Dispatchers.IO)

    // 获取特定评论
    @WorkerThread
    fun loadReview(id: Long, success: () -> Unit, error: () -> Unit): Flow<Review> = flow {
        val review = reviewDao.getReviewById(id) // 从数据库获取
        if (review == null) {
            val response = reviewService.fetchReview(id)
            response.suspendOnSuccess {
                emit(data)
                success()
            }.onError {
                error()
            }.onException {
                error()
            }
        } else {
            emit(review)
        }
    }.flowOn(Dispatchers.IO)

    // 更新特定评论
    @WorkerThread
    fun updateReview(id: Long, review: Review, success: () -> Unit, error: () -> Unit): Flow<Result<Review>> = flow {
        try {
            val response = reviewService.updateReview(id, review)
            response.suspendOnSuccess {
                reviewDao.updateReview(data) // Update the review in the database
                emit(Result.success(data)) // Emit the updated review wrapped in Result.success
                success() // Call success callback
            }
        } catch (e: Exception) {
            emit(Result.failure<Review>(e)) // Emit the exception wrapped in Result.failure
            error() // Call error callback
        }
    }.flowOn(Dispatchers.IO)

    // 删除特定评论
    @WorkerThread
    fun deleteReview(id: Long): Flow<Result<Unit>> = flow {
        try {
            // 调用服务删除评论
            reviewService.deleteReview(id)
            // 从数据库删除评论
            reviewDao.deleteReviewById(id)
            // 返回成功结果
            emit(Result.success(Unit))
        } catch (e: Exception) {
            // 捕获异常并返回失败结果
            emit(Result.failure(e))
        }
    }.flowOn(Dispatchers.IO)
}
