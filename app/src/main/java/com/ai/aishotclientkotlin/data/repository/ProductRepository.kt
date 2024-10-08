package com.ai.aishotclientkotlin.data.repository

import android.util.Log
import com.ai.aishotclientkotlin.data.dao.entity.Product
import com.ai.aishotclientkotlin.data.dao.ProductDao
import com.ai.aishotclientkotlin.data.remote.ProductService
import com.skydoves.sandwich.ApiResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class ProductRepository @Inject constructor(
    private val productDao: ProductDao,
    private val apiService: ProductService
): Repository{
    // 从 Room 获取产品列表
    suspend fun getProducts(): List<Product> {
        return withContext(Dispatchers.IO) {
            val localProducts = productDao.getAllProducts()
            if (localProducts.isNotEmpty()) {
                localProducts // 返回本地缓存的产品
            } else {
                fetchProductsFromApi() // 否则从 API 获取
            }
        }
    }

    // 从 API 获取产品列表并缓存到 Room
    private suspend fun fetchProductsFromApi(): List<Product> {
        return withContext(Dispatchers.IO) {
            val products = apiService.fetchProducts() // 从 Django API 获取
            productDao.insertProducts(products) // 缓存到 Room
            products
        }
    }

    suspend fun updateProduct(product: Product){

        return withContext(Dispatchers.IO) {

            when (val response =  apiService.updateProduct(productId = product.id,product)) {
                is ApiResponse.Success -> {
                    true // 成功时返回 true
                    Log.e("updateProduct", "Failed to update product: ${response}")
                    productDao.updateProduct(product)
                }
                is ApiResponse.Failure -> {
                    // 处理失败情况，可以记录日志或抛出异常
                    Log.e("updateProduct", "Failed to update product: ${response}")
                    false // 失败时返回 false
                }
            }

        }
    }
}