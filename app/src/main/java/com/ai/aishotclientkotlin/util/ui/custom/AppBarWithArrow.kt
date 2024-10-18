package com.ai.aishotclientkotlin.util.ui.custom




import androidx.compose.foundation.background
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.ui.graphics.Color
import com.ai.aishotclientkotlin.ui.theme.Purple200

@ExperimentalMaterial3Api
@Composable
fun AppBarWithArrow(
    title: String?,
    showMenu : Boolean = true,
    pressOnBack: () -> Unit,
    menuClick: () -> Unit = {}
) {

    TopAppBar(
        title = {
            title?.let { Text(it, maxLines = 1, overflow = TextOverflow.Ellipsis) }
        },
        modifier = Modifier.height(48.dp).background(Color.Red),

        navigationIcon = {

            IconButton(onClick = {
                pressOnBack()
            }
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Localized description",
                    modifier = Modifier.padding(1.dp).height(30.dp).background(Color.Red)
                )
            }
        },
        actions = {
            if(showMenu) {
                IconButton(onClick = { menuClick() }) {
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