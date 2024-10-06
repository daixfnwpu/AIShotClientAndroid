package com.ai.aishotclientkotlin.data.dao

import androidx.room.*
import com.ai.aishotclientkotlin.domain.model.bi.bean.Review

@Dao
interface ReviewDao {
    @Query("SELECT * FROM review")
    fun getAllReviews(): List<Review>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertReviews(reviews: List<Review>)

    @Query("SELECT * FROM review WHERE id = :id LIMIT 1")
    fun getReviewById(id: Long): Review?

    @Update
    fun updateReview(review: Review)

    @Query("DELETE FROM review WHERE id = :id")
    fun deleteReviewById(id: Long)
}
