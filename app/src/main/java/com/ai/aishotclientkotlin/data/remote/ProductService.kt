package com.ai.aishotclientkotlin.data.remote

import com.ai.aishotclientkotlin.data.dao.entity.Product
import com.skydoves.sandwich.ApiResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PUT
import retrofit2.http.Path

interface ProductService {

    @GET("api/products/")
    suspend fun fetchProducts(): List<Product>

    @PUT("api/products/{id}/")
    suspend fun updateProduct(@Path("id") productId: Long, @Body product: Product): ApiResponse<Unit>
}