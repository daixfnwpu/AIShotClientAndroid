package com.ai.aishotclientkotlin.ui.screens.shop.screen

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CardElevation
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil3.compose.AsyncImage
import com.ai.aishotclientkotlin.R
import com.ai.aishotclientkotlin.data.dao.entity.Product
import com.ai.aishotclientkotlin.ui.screens.shop.model.ProductViewModel

@Composable
fun ShopScreen(viewModel: ProductViewModel = hiltViewModel(),
    navController: NavController,
    modifier: Modifier = Modifier
) {
  //  TaobaoWebView("https://shop125506066.taobao.com/")
    ProductScreen(viewModel)
}



@Composable
fun ProductScreen(viewModel: ProductViewModel) {

    // 观察产品列表的状态
    val products by viewModel.products.collectAsState(initial = emptyList())

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp)
    ) {
        itemsIndexed(products) { index, product ->
            ProductItem(product = product, onFavoriteToggle = { updatedProduct ->
                // 切换收藏状态
               // val index = products.indexOf(product)
                viewModel.toggleFavorite(product)
            })
        }
    }
}


@Composable
fun ProductItem(product: Product, onFavoriteToggle: (Product) -> Unit) {
    Card(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth()
            .clickable { /* 点击事件，例如打开商品详情 */ },
        elevation = CardDefaults.elevatedCardElevation(), // 增加阴影
        shape = RoundedCornerShape(12.dp) // 圆角边框
    ) {
        Column {
            Row(
                modifier = Modifier
                    .height(200.dp) // 设置 Box 的大小以匹配图片
                    .fillMaxWidth(), // 填满宽度
               // contentAlignment = Alignment.Center // 设置内容居中
            ) {
                AsyncImage(
                    model = product.imageUrl,
                    contentDescription = product.title,
                    modifier = Modifier
                        .fillMaxSize() // 填满 Box
                        .clip(RoundedCornerShape(12.dp)), // 图片圆角
                    contentScale = ContentScale.Fit,
                    placeholder = painterResource(id = R.drawable.placeholder_image),
                    error = painterResource(id = R.drawable.poster) // 错误图片
                )
            }
            Text(
                text = product.title,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                modifier = Modifier.padding(8.dp),
                color = Color.Black
            )
            Text(
                text = product.description,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                color = Color.Gray
            )
            Row(
                modifier = Modifier.padding(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (product.isOnSale) {
                        "Sale: $${product.salePrice ?: product.price} (was $${product.price})"
                    } else {
                        "Price: $${product.price}"
                    },
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = if (product.isOnSale) Color.Red else Color.Black
                )
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    text = "${product.rating} ★", // 显示评分
                    color = Color.Gray,
                    fontSize = 14.sp
                )
            }
            Text(
                text = "Sales: ${product.salesCount}", // 显示成交量
                modifier = Modifier.padding(8.dp),
                color = Color.Gray,
                fontSize = 14.sp
            )
            IconButton(onClick = { onFavoriteToggle(product) }) {
                Icon(
                    imageVector = if (product.isFavorited) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                    contentDescription = "Toggle Favorite",
                    tint = if (product.isFavorited) Color.Red else Color.Gray
                )
            }
        }
    }
}


@Composable
fun TaobaoWebView(url: String) {
    val context = LocalContext.current
    AndroidView(factory = { context ->
        WebView(context).apply {
            settings.javaScriptEnabled = true
            webViewClient = object : WebViewClient() {
                override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
                    val url = request?.url.toString()
                    if (url.startsWith("http") || url.startsWith("https")) {
                        view?.loadUrl(url)
                        return true
                    } else {
                        // 将所有非 HTTP/HTTPS 的 URL 打开到外部浏览器
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                        context.startActivity(intent)
                        return true
                    }
                }
            }
            loadUrl(url)
        }
    }, modifier = Modifier.fillMaxSize())
}

fun isTaobaoInstalled(context: Context): Boolean {
    return try {
        context.packageManager.getPackageInfo("com.taobao.taobao", 0)
        true
    } catch (e: PackageManager.NameNotFoundException) {
        false
    }
}

@Composable
fun OpenTaobaoButton(url: String) {
    val context = LocalContext.current

    Button(onClick = {
        val taobaoUrl = "tbopen://m.taobao.com/... " // 替换为你的淘宝链接
        if (isTaobaoInstalled(context)) {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(taobaoUrl))
            context.startActivity(intent)
        } else {
            val httpUrl = "https://m.taobao.com/..." // 替换为相应的 HTTP 链接
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(httpUrl))
            context.startActivity(intent)
        }
    }) {
        Text("Open Taobao")
    }
}
