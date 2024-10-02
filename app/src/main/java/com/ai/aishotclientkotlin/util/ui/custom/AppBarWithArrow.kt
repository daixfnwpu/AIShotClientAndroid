package com.ai.aishotclientkotlin.util.ui.custom




import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ai.aishotclientkotlin.ui.theme.Purple200

@ExperimentalMaterial3Api
@Composable
fun AppBarWithArrow(
    title: String?,
    showHeart : Boolean = true,
    pressOnBack: () -> Unit
) {

    TopAppBar(
        title = {
            title?.let { Text(it, maxLines = 1, overflow = TextOverflow.Ellipsis) }
        },
        modifier = Modifier.height(48.dp),

        navigationIcon = {

            IconButton(onClick = {
                pressOnBack()
            }
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Localized description",
                    modifier = Modifier.padding(1.dp).height(30.dp)
                )
            }
        },
        actions = {
            if(showHeart) {
                IconButton(onClick = { /* doSomething() */ }) {
                    Icon(
                        imageVector = Icons.Filled.Favorite,
                        contentDescription = "Localized description"
                    )
                }
            }
        }
    )

}


@ExperimentalMaterial3Api
@Composable
fun AppBarWithMenu(
    title: String?
) {
    TopAppBar(
        title = { title },
        //     elevation = 6.dp,
        //     backgroundColor = Purple200,
        modifier = Modifier.height(58.dp).background(Purple200),
        actions = {
            IconButton(onClick = {
            }) {
                Icon(
                    imageVector = Icons.Filled.Menu,
                    contentDescription = "Localized description"
                )
            }
        }
    )
}