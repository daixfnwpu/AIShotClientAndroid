package com.ai.aishotclientkotlin.ui.screens.entrance.login.screen

import android.content.Intent
import android.provider.Settings
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Warning
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.ai.aishotclientkotlin.R
import com.ai.aishotclientkotlin.data.remote.Api
import com.ai.aishotclientkotlin.ui.nav.util.SCAFFOLD_GRAPH_ROUTE
import com.ai.aishotclientkotlin.ui.nav.util.ScreenList
import com.ai.aishotclientkotlin.ui.screens.entrance.login.util.LoginUtils
import com.ai.aishotclientkotlin.ui.screens.entrance.login.viewmodel.LoginViewModel
import com.ai.aishotclientkotlin.ui.screens.entrance.splash.screen.LoadingAnimation
import com.ai.aishotclientkotlin.ui.theme.AppTheme
import com.ai.aishotclientkotlin.ui.theme.LoginScreenTheme
import com.ai.aishotclientkotlin.ui.theme.RedVisne
import com.ai.aishotclientkotlin.util.Constants
import com.ai.aishotclientkotlin.util.dialogalert.*
import com.skydoves.landscapist.ImageOptions
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import com.skydoves.landscapist.glide.GlideImage


@Composable
fun LoginPage(
    navController: NavController,
    viewModel: LoginViewModel = hiltViewModel(),
    phoneNum: String? = null
) {
    val state = viewModel.state.value
    val context = LocalContext.current

    val intent = remember {
        Intent(Settings.ACTION_WIRELESS_SETTINGS)
    }

    val snackbarHostState = remember { SnackbarHostState() }

    val scope = rememberCoroutineScope()

    val username = remember {
        mutableStateOf("")
    }

    if (phoneNum != null && phoneNum != "Null") {

        LaunchedEffect(key1 = Unit) {

            username.value = phoneNum

        }
    }

    val password = remember {
        mutableStateOf("")
    }

    val keyboardController = LocalSoftwareKeyboardController.current

    var passwordVisibility by remember {
        mutableStateOf(false)
    }

    val isErrorPhoneNumIcon = remember {
        mutableStateOf(false)
    }

    val isErrorPhoneNumMessage = remember {
        mutableStateOf("Null")
    }

    val isErrorPasswordMessage = remember {
        mutableStateOf("Null")
    }

    val infoDialog = remember {
        mutableStateOf(false)
    }

    if (infoDialog.value) {
        CustomDialogAlert(
            type = CustomDialogType.INFO,
            title = stringResource(R.string.bilgi),
            desc = stringResource(R.string.kullanici_bulunamadi),
            processText = stringResource(R.string.uye_ol),
            onProcess = {
                infoDialog.value = false
                scope.launch {
                    delay(150)
                    viewModel.clearViewModel()
                    navController.navigate(ScreenList.SignInScreen.withArgs(username.value))
                }
            },
            onDismiss = {
                viewModel.clearViewModel()
                infoDialog.value = false
            },
        )
    }

    val icon = if (passwordVisibility)
        painterResource(id = R.drawable.ic_visibility)
    else
        painterResource(id = R.drawable.ic_visibility_off)

    when (state.success) {
        0 -> {
        }

        1 -> {
            LaunchedEffect(key1 = Unit) {
                navController.navigate(SCAFFOLD_GRAPH_ROUTE) {
                    popUpTo("Login_Screen") { inclusive = true }
                }
            }
        }

        202 -> {
            infoDialog.value = true
            state.success = -1
        }

        203 -> {
            infoDialog.value = true
            state.success = -1
        }
    }

    LoginScreenTheme {
        Scaffold(
            // scaffoldState = scaffoldState,
            snackbarHost = {
                SnackbarHost(snackbarHostState) {
                    Snackbar(
                        containerColor = Color.Red,
                        contentColor = Color.White,
                        actionColor = Color.White,
                        snackbarData = it
                    )
                }
            },
            content = { paddingValues ->
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                ) {
                    Column {
                        Column(modifier = Modifier
                            .weight(1.3f)
                            .fillMaxWidth()) {
                            GlideImage(
                                modifier = Modifier.fillMaxWidth(),
                                // !!TODO this place maybe wrong ,from Constants.LoginImagePath  ->  {Constants.LoginImagePath}
                                imageModel = { Api.LoginImagePath },
                                imageOptions = ImageOptions(contentScale = ContentScale.FillBounds),
                            )
                        }
                        Column(
                            modifier = Modifier
                                .weight(2.7f)
                                .fillMaxWidth()
                                .offset(y = -30.dp)
                                .background(
                                    color = Color.White, RoundedCornerShape(
                                        topStart = AppTheme.dimens.grid_5,
                                        topEnd = AppTheme.dimens.grid_5
                                    )
                                )
                        ) {
                            Row(
                                horizontalArrangement = Arrangement.Center, modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = AppTheme.dimens.grid_2_5)
                            ) {

                                Text(
                                    text = stringResource(R.string.hos_geldiniz),
                                    color = RedVisne,
                                    fontSize = 29.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            Column(
                                verticalArrangement = Arrangement.SpaceBetween,
                                modifier = Modifier.fillMaxSize()
                            ) {
                                Column(modifier = Modifier) {
                                    OutlinedTextField(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(
                                                start = AppTheme.dimens.grid_3_5,
                                                end = AppTheme.dimens.grid_3_5,
                                                top = AppTheme.dimens.grid_4
                                            ),
                                        value = username.value,
                                        onValueChange = { username.value = it },
                                        label = {
                                            Text(
                                                text = stringResource(R.string.username),
                                                color = Color.Black
                                            )
                                        },
                                        colors = if (!isErrorPhoneNumIcon.value) OutlinedTextFieldDefaults.colors(
                                            focusedTextColor = Color.Black,
                                            cursorColor = RedVisne,
                                            focusedBorderColor = Color.Black,
                                            unfocusedLabelColor = Color.Gray,
                                            unfocusedTextColor = Color.Black,
                                            focusedContainerColor = Color.White,
                                            focusedLeadingIconColor = RedVisne

                                        ) else OutlinedTextFieldDefaults.colors(

                                            focusedTextColor = Color.Black,
                                            cursorColor = RedVisne,
                                            focusedBorderColor = Color.Red,
                                            unfocusedLabelColor = Color.Red,
                                            unfocusedTextColor = Color.Black,
                                            focusedContainerColor = Color.White,
                                            focusedLeadingIconColor = RedVisne
                                        ),

                                        leadingIcon = {

                                            IconButton(onClick = {


                                            }) {

                                                Icon(
                                                    imageVector = Icons.Filled.AccountCircle,
                                                    contentDescription = "E-Mail İcon"
                                                )

                                            }
                                        },

                                        keyboardOptions = KeyboardOptions(

                                            keyboardType = KeyboardType.Phone,
                                            imeAction = ImeAction.Next
                                        ),

                                        trailingIcon = {

                                            if (isErrorPhoneNumIcon.value)
                                                Icon(
                                                    Icons.Filled.Warning,
                                                    contentDescription = "E-Mail Error Icon",
                                                    tint = MaterialTheme.colorScheme.error
                                                )
                                        }
                                    )

                                    if (isErrorPhoneNumIcon.value) {
                                        Text(
                                            text = isErrorPhoneNumMessage.value,
                                            color = MaterialTheme.colorScheme.error,
                                            style = MaterialTheme.typography.titleMedium,
                                            modifier = Modifier.padding(
                                                top = AppTheme.dimens.grid_1,
                                                start = AppTheme.dimens.grid_3_5
                                            )
                                        )
                                    }

                                    OutlinedTextField(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(
                                                start = AppTheme.dimens.grid_3_5,
                                                end = AppTheme.dimens.grid_3_5,
                                                top = AppTheme.dimens.grid_2
                                            ),
                                        value = password.value,
                                        onValueChange = { password.value = it },
                                        label = {
                                            Text(
                                                text = stringResource(R.string.password),
                                                color = Color.Black
                                            )
                                        },

                                        colors = if (isErrorPasswordMessage.value == "Null")

                                            OutlinedTextFieldDefaults.colors(
                                                focusedTextColor = Color.Black,
                                                cursorColor = RedVisne,
                                                focusedBorderColor = Color.Black,
                                                unfocusedLabelColor = Color.Gray,
                                                unfocusedTextColor = Color.Black,
                                                focusedContainerColor = Color.White,
                                                focusedLeadingIconColor = RedVisne

                                            )
                                        else OutlinedTextFieldDefaults.colors(

                                            focusedTextColor = Color.Black,
                                            cursorColor = RedVisne,
                                            focusedBorderColor = Color.Red,
                                            unfocusedLabelColor = Color.Red,
                                            unfocusedTextColor = Color.Black,
                                            focusedContainerColor = Color.White,
                                            focusedLeadingIconColor = RedVisne
                                        ),

                                        leadingIcon = {

                                            IconButton(onClick = {

                                            }) {

                                                Icon(
                                                    imageVector = Icons.Filled.Lock,
                                                    contentDescription = "Password İcon"
                                                )

                                            }
                                        },

                                        trailingIcon = {

                                            IconButton(onClick = {

                                                passwordVisibility = !passwordVisibility

                                            }) {

                                                Icon(
                                                    painter = icon,
                                                    contentDescription = "Password İcon"
                                                )

                                            }

                                        },

                                        visualTransformation = if (passwordVisibility) VisualTransformation.None
                                        else PasswordVisualTransformation(),

                                        singleLine = true,

                                        keyboardOptions = KeyboardOptions(

                                            keyboardType = KeyboardType.Password,
                                            imeAction = ImeAction.Done,
                                        ),

                                        keyboardActions = KeyboardActions(

                                            onDone = {

                                                keyboardController?.hide()

                                            }
                                        )
                                    )

                                    if (isErrorPasswordMessage.value != "Null") {
                                        Text(
                                            text = isErrorPasswordMessage.value,
                                            color = MaterialTheme.colorScheme.error,
                                            style = MaterialTheme.typography.titleMedium,
                                            modifier = Modifier.padding(
                                                top = AppTheme.dimens.grid_1,
                                                start = AppTheme.dimens.grid_3_5
                                            )
                                        )
                                    }

                                    Row(
                                        horizontalArrangement = Arrangement.End, modifier = Modifier
                                            .padding(
                                                top = AppTheme.dimens.grid_1_5,
                                                end = AppTheme.dimens.grid_3_5
                                            )
                                            .fillMaxWidth()
                                    ) {

                                        Text(
                                            text = stringResource(R.string.sifremi_unuttum),
                                            color = Color.Gray
                                        )

                                    }

                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(
                                                start = AppTheme.dimens.grid_3_5 * 2,
                                                end = AppTheme.dimens.grid_3_5 * 2,
                                                top = AppTheme.dimens.grid_1_5 * 2
                                            ),
                                        horizontalArrangement = Arrangement.Center
                                    ) {

                                        Button(
                                            onClick = {

                                                when (LoginUtils().loginFormatValidation(
                                                    username.value,
                                                    password.value
                                                )) {

                                                    1 -> {

                                                        viewModel.getUserLogin(
                                                            Api.LOGIN,
                                                            Constants.TYPETWO,
                                                            username.value,
                                                            password.value
                                                        )

                                                    }

                                                    2 -> {

                                                        isErrorPhoneNumIcon.value = true
                                                        isErrorPhoneNumMessage.value =
                                                            context.getString(R.string.lutfen_phoneNum_girin)

                                                    }

                                                    3 -> {

                                                        isErrorPhoneNumIcon.value = true
                                                        isErrorPhoneNumMessage.value =
                                                            context.getString(R.string.phoneNum_cok_kisa)

                                                    }

                                                    4 -> {

                                                        isErrorPhoneNumIcon.value = true
                                                        isErrorPhoneNumMessage.value =
                                                            context.getString(R.string.uyumsuz_mail_formati)

                                                    }

                                                    5 -> {

                                                        isErrorPhoneNumIcon.value = false
                                                        isErrorPhoneNumMessage.value = "Null"
                                                        isErrorPasswordMessage.value =
                                                            context.getString(R.string.lutfen_sifrenizi_giriniz)

                                                    }
                                                }
                                            },
                                            shape = RoundedCornerShape(AppTheme.dimens.grid_4),
                                            modifier = Modifier
                                                .fillMaxWidth(),

                                            colors = ButtonDefaults.buttonColors(
                                                containerColor = RedVisne,
                                                contentColor = Color.White
                                            )

                                        ) {

                                            Text(

                                                text = stringResource(R.string.login),
                                                fontSize = 18.sp,
                                                modifier = Modifier
                                                    .padding(
                                                        top = AppTheme.dimens.grid_1,
                                                        bottom = AppTheme.dimens.grid_1
                                                    )

                                            )
                                        }
                                    }

                                    Row(
                                        horizontalArrangement = Arrangement.Center,
                                        modifier = Modifier.fillMaxWidth()
                                    ) {

                                        if (state.isLoading) {

                                            LoadingAnimation(speed = 4f)

                                        }
                                    }
                                }

                                Row(
                                    horizontalArrangement = Arrangement.Center,
                                    verticalAlignment = Alignment.Bottom,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                ) {

                                    Text(text = stringResource(R.string.hesabiniz_yokmu))

                                    Spacer(modifier = Modifier.padding(AppTheme.dimens.grid_0_5))

                                    Text(text = stringResource(R.string.uye_ol),
                                        color = RedVisne,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier
                                            .clickable {

                                                scope.launch {

                                                    viewModel.clearViewModel()

                                                    navController.navigate(
                                                        ScreenList.SignInScreen.withArgs(
                                                            "Null"
                                                        )
                                                    )

                                                }
                                            }
                                    )
                                }
                            }
                        }
                    }

                    if (state.internet) {

                        LaunchedEffect(key1 = Unit) {

                            scope.launch {

                                val sb = snackbarHostState.showSnackbar(
                                    context.getString(R.string.no_internet_connection),
                                    actionLabel = context.getString(R.string.ayarlar),
                                    duration = SnackbarDuration.Long
                                )

                                if (sb == SnackbarResult.ActionPerformed) {

                                    context.startActivity(intent)

                                }
                            }
                        }
                    }
                }
            }
        )
    }
}

