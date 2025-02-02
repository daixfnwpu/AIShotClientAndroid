package com.ai.aishotclientkotlin.ui.screens.entrance.signin.screen

import android.util.Log
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
import androidx.compose.material3.Scaffold
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
import com.ai.aishotclientkotlin.ui.nav.util.ScreenList
import com.ai.aishotclientkotlin.ui.screens.entrance.signin.util.SignInUtils
import com.ai.aishotclientkotlin.ui.screens.entrance.signin.viewmodel.SignInViewModel
import com.ai.aishotclientkotlin.ui.screens.entrance.splash.screen.LoadingAnimation
import com.ai.aishotclientkotlin.ui.theme.LoginScreenTheme
import com.ai.aishotclientkotlin.ui.theme.RedVisne
import com.ai.aishotclientkotlin.util.Constants
import com.ai.aishotclientkotlin.util.dialogalert.CustomDialogAlert
import com.ai.aishotclientkotlin.util.dialogalert.CustomDialogType
import com.skydoves.landscapist.ImageOptions
import com.skydoves.landscapist.glide.GlideImage
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


@Composable
fun SignInPage(
    navController: NavController,
    phoneNum: String,
    viewModel: SignInViewModel = hiltViewModel()
) {

    LoginScreenTheme {
        val snackbarHostState = remember { SnackbarHostState() }

        val scope = rememberCoroutineScope()

        val keyboardController = LocalSoftwareKeyboardController.current

        val state = viewModel.state.value

        val context = LocalContext.current

        val username = remember {
            mutableStateOf("")
        }

        if (phoneNum != "Null") {

            LaunchedEffect(key1 = Unit) {

                username.value = phoneNum

            }
        }

        val checkboxDurum = remember {
            mutableStateOf(true)
        }

        val passwordOne = remember {
            mutableStateOf("")
        }

        val passwordTwo = remember {
            mutableStateOf("")
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

        var passwordVisibility by remember {
            mutableStateOf(false)
        }

        var passwordVisibilityTwo by remember {
            mutableStateOf(false)
        }

        val icon = if (passwordVisibility)
            painterResource(id = R.drawable.ic_visibility)
        else
            painterResource(id = R.drawable.ic_visibility_off)

        val iconTwo = if (passwordVisibilityTwo)
            painterResource(id = R.drawable.ic_visibility_two)
        else
            painterResource(id = R.drawable.ic_visibility_off_two)

        val succesDialog = remember {
            mutableStateOf(false)
        }

        val infoDialog = remember {
            mutableStateOf(false)
        }

        if (succesDialog.value) {

            CustomDialogAlert(
                type = CustomDialogType.SUCCESS,
                title = stringResource(R.string.tebrikler),
                desc = stringResource(R.string.basarili_kayit),
                processText = stringResource(R.string.giris_yap),

                onProcess = {

                    succesDialog.value = false

                    scope.launch {
                        delay(150)

                        viewModel.clearSignInViewModel()

                        navController.navigate(ScreenList.LoginScreen.withArgs(username.value)) {

                            popUpTo("SignIn_Screen") { inclusive = true }
                            popUpTo("Login_Screen") { inclusive = true }

                        }

                    }

                },

                onDismiss = {

                    viewModel.clearSignInViewModel()

                    succesDialog.value = false

                },


                )

        }

        if (infoDialog.value) {

            CustomDialogAlert(
                type = CustomDialogType.INFO,
                title = stringResource(id = R.string.bilgi),
                desc = stringResource(R.string.zaten_kayitli_mail),
                processText = stringResource(R.string.giris_yap),

                onProcess = {

                    infoDialog.value = false

                    scope.launch {
                        viewModel.clearSignInViewModel()

                        navController.navigate(ScreenList.LoginScreen.withArgs(username.value)) {

                            popUpTo("SignIn_Screen") { inclusive = true }
                            popUpTo("Login_Screen") { inclusive = true }

                        }

                    }


                },

                onDismiss = {

                    viewModel.clearSignInViewModel()

                    infoDialog.value = false

                },
            )

        }


        when (state.success) {

            0 -> {


            }

            1 -> {

                succesDialog.value = true
                state.success = -1

            }

            210 -> {

                infoDialog.value = true
                state.success = -1

            }
        }


        Scaffold(
            // !!TODO  scaffoldState can not be set to other ,may be other property.
          //  scaffoldState = scaffoldState,

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

            content = { padding ->

                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.fillMaxSize()
                ) {

                    Column() {

                        Column(modifier = Modifier.weight(1.3f)) {

                            GlideImage(

                                modifier = Modifier
                                    .fillMaxWidth(),
                                //!!TODO ,other place is the same.
                                imageModel ={ Api.LoginImagePath},

                                imageOptions = ImageOptions(contentScale = ContentScale.FillBounds),
                            )

                        }

                        Column(
                            modifier = Modifier
                                .weight(2.7f)
                                .fillMaxWidth()
                                .offset(y = -30.dp)
                                .background(
                                    color = Color.White,
                                    RoundedCornerShape(topStart = 30.dp, topEnd = 30.dp)
                                )

                        ) {

                            Row(
                                horizontalArrangement = Arrangement.Center, modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 15.dp)
                            ) {

                                Text(
                                    text = stringResource(R.string.uye_ol),
                                    color = RedVisne,
                                    fontSize = 28.sp,
                                    fontWeight = FontWeight.Bold
                                )

                            }

                            Column(
                                verticalArrangement = Arrangement.SpaceBetween,
                                modifier = Modifier
                                    .fillMaxSize()
                            ) {

                                Column() {

                                    OutlinedTextField(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(start = 20.dp, end = 20.dp, top = 15.dp),
                                        value = username.value,
                                        onValueChange = { username.value = it },
                                        label = {
                                            Text(
                                                text = stringResource(R.string.phoneNum),
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

                                        )  else OutlinedTextFieldDefaults.colors(

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

                                        trailingIcon = {

                                            if (isErrorPhoneNumIcon.value)
                                                Icon(
                                                    Icons.Filled.Warning,
                                                    contentDescription = "E-Mail Error Icon",
                                                    tint = MaterialTheme.colorScheme.error
                                                )
                                        },

                                        keyboardOptions = KeyboardOptions(

                                            keyboardType = KeyboardType.Phone,
                                            imeAction = ImeAction.Next

                                        )

                                    )

                                    if (isErrorPhoneNumIcon.value) {
                                        Text(
                                            text = isErrorPhoneNumMessage.value,
                                            color = MaterialTheme.colorScheme.error,
                                            style = MaterialTheme.typography.titleMedium,
                                            modifier = Modifier.padding(top = 5.dp, start = 20.dp)
                                        )
                                    }

                                    OutlinedTextField(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(start = 20.dp, end = 20.dp, top = 5.dp),
                                        value = passwordOne.value,
                                        onValueChange = { passwordOne.value = it },
                                        label = {
                                            Text(
                                                text = stringResource(R.string.sifre),
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

                                            ) else

                                            OutlinedTextFieldDefaults.colors(

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
                                            imeAction = ImeAction.Next

                                        )

                                    )

                                    OutlinedTextField(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(start = 20.dp, end = 20.dp, top = 5.dp),
                                        value = passwordTwo.value,
                                        onValueChange = { passwordTwo.value = it },
                                        label = {
                                            Text(
                                                text = stringResource(R.string.sifreyi_dogrulayiniz),
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

                                            )else

                                            OutlinedTextFieldDefaults.colors(

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

                                                passwordVisibilityTwo = !passwordVisibilityTwo

                                            }) {
                                                Icon(
                                                    painter = iconTwo,
                                                    contentDescription = "Password İcon"
                                                )
                                            }

                                        },

                                        visualTransformation = if (passwordVisibilityTwo) VisualTransformation.None
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
                                            modifier = Modifier.padding(top = 5.dp, start = 20.dp)
                                        )
                                    }

                                    Row(
                                        modifier = Modifier.padding(start = 20.dp, top = 10.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {

                                        Checkbox(
                                            checked = checkboxDurum.value,
                                            onCheckedChange = {
                                                checkboxDurum.value = it
                                            },

                                            colors = CheckboxDefaults.colors(

                                                checkedColor = RedVisne

                                            )
                                        )

                                        Text(
                                            text = stringResource(R.string.sozlesmeyi_onayladiniz),
                                            fontSize = 14.sp,
                                            modifier = Modifier
                                                .padding(start = 5.dp)
                                        )

                                    }


                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(start = 50.dp, end = 50.dp, top = 30.dp),
                                        horizontalArrangement = Arrangement.Center
                                    ) {

                                        Button(
                                            onClick = {

                                                scope.launch {

                                                    when (SignInUtils().registerFormatValidation(
                                                        username.value,
                                                        passwordOne.value,
                                                        passwordTwo.value

                                                    )) {

                                                        1 -> {

                                                            isErrorPhoneNumIcon.value = false
                                                            isErrorPasswordMessage.value = "Null"

                                                            val postUsername = username.value.trim()
                                                            val postPassword =
                                                                passwordTwo.value.trim()

                                                            viewModel.getRegisterUser(
                                                                Api.REGISTER,
                                                                Constants.TYPETWO,
                                                                postUsername,
                                                                postPassword
                                                            )

                                                        }

                                                        2 -> {

                                                            isErrorPhoneNumIcon.value = true
                                                            isErrorPhoneNumMessage.value =
                                                                context.getString(R.string.lutfen_phoneNum_girin)
                                                            isErrorPasswordMessage.value = "Null"

                                                        }

                                                        3 -> {

                                                            isErrorPhoneNumIcon.value = true
                                                            isErrorPhoneNumMessage.value =
                                                                context.getString(R.string.phoneNum_cok_kisa)
                                                            isErrorPasswordMessage.value = "Null"

                                                        }

                                                        4 -> {

                                                            isErrorPhoneNumIcon.value = true
                                                            isErrorPhoneNumMessage.value =
                                                                context.getString(R.string.uyumsuz_mail_formati)
                                                            isErrorPasswordMessage.value = "Null"

                                                        }

                                                        5 -> {

                                                            isErrorPhoneNumIcon.value = false
                                                            isErrorPasswordMessage.value =
                                                                context.getString(R.string.lutfen_sifrenizi_giriniz)

                                                        }

                                                        6 -> {

                                                            isErrorPhoneNumIcon.value = false
                                                            isErrorPasswordMessage.value =
                                                                context.getString(R.string.sifreler_uyusmuyor)

                                                        }

                                                        7 -> {

                                                            isErrorPhoneNumIcon.value = false
                                                            isErrorPasswordMessage.value =
                                                                context.getString(R.string.sifre_cok_kisa)

                                                        }

                                                    }

                                                }

                                            },
                                            shape = RoundedCornerShape(25.dp),
                                            modifier = Modifier
                                                .fillMaxWidth(),

                                            colors = ButtonDefaults.buttonColors(
                                                containerColor = RedVisne,
                                                contentColor = Color.White
                                            )

                                        ) {

                                            Text(

                                                text = stringResource(id = R.string.uye_ol),
                                                fontSize = 18.sp,
                                                modifier = Modifier
                                                    .padding(top = 5.dp, bottom = 5.dp)

                                            )

                                        }
                                    }

                                    if (state.isLoading) {

                                        Row(
                                            horizontalArrangement = Arrangement.Center,
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(top = 3.dp)
                                        ) {

                                            LoadingAnimation(speed = 3.75f)

                                        }

                                    }
                                }

                                Row(
                                    horizontalArrangement = Arrangement.Center,
                                    verticalAlignment = Alignment.Bottom,
                                    modifier = Modifier.fillMaxWidth()
                                ) {

                                    Text(text = stringResource(R.string.zaten_hesabiniz_varmi))
                                    Spacer(modifier = Modifier.padding(3.dp))
                                    Text(
                                        text = stringResource(id = R.string.giris_yap),
                                        color = RedVisne,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.clickable {

                                            navController.navigate(ScreenList.LoginScreen.withArgs("Null")) {

                                                popUpTo("SignIn_Screen") { inclusive = true }
                                                popUpTo("Login_Screen") { inclusive = true }

                                            }

                                        })
                                }
                            }
                        }
                    }
                }
            }
        )
    }
    Box(modifier = Modifier.fillMaxSize()) {


    }

    Log.e("SignInPage", "SignInPage")

}