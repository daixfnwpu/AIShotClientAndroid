package com.ai.aishotclientkotlin.ui.screens.shop.model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ai.aishotclientkotlin.data.dao.entity.Product
import com.ai.aishotclientkotlin.data.repository.ProductRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProductViewModel @Inject constructor(private val repository: ProductRepository) : ViewModel() {
    private val _products = MutableStateFlow<List<Product>>(emptyList())
    val products: StateFlow<List<Product>> = _products.asStateFlow()

    init {
        // 从仓库加载产品数据
        loadProducts()
    }

    private fun loadProducts() {
        // 加载产品数据并更新_state
        viewModelScope.launch {
            _products.value = repository.getProducts() // 从仓库获取产品
        }
    }

    fun toggleFavorite(product: Product) {
        // 切换收藏状态
        val updatedProduct = product.copy(isFavorited = !product.isFavorited)
        // 更新产品状态并保存
        viewModelScope.launch {
            repository.updateProduct(updatedProduct) // 处理更新逻辑
            _products.value = _products.value.map {
                if (it.id == product.id) updatedProduct else it
            }
        }
    }
}
