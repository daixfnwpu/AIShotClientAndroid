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
    pressOnBack: () -> Unit
) {
//    TopAppBar(
//        title = { "" },
//        //     elevation = 6.dp,
//        //     backgroundColor = Purple200,
//        modifier = Modifier.height(58.dp).background(Purple200)
//    ) {
//        Row {
//            Spacer(modifier = Modifier.width(10.dp))
//
//            Image(
//                imageVector = Icons.Filled.ArrowBack,
//                colorFilter = ColorFilter.tint(Color.White),
//                contentDescription = null,
//                modifier = Modifier
//                    .align(Alignment.CenterVertically)
//                    .clickable {
//                        pressOnBack()
//                    }
//            )
//
//            Spacer(modifier = Modifier.width(12.dp))
//
//            Text(
//                modifier = Modifier
//                    .padding(8.dp)
//                    .align(Alignment.CenterVertically),
//                text = title ?: "",
//                color = Color.White,
//                fontSize = 18.sp,
//                fontWeight = FontWeight.Bold
//            )
//        }
//    }
//}

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
            IconButton(onClick = { /* doSomething() */ }) {
                Icon(
                    imageVector = Icons.Filled.Favorite,
                    contentDescription = "Localized description"
                )
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