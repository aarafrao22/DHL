package com.aaraf.dhl

import android.annotation.SuppressLint
import android.app.Activity
import android.app.ProgressDialog
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aaraf.dhl.ui.theme.DHLTheme
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine

class LoginActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            DHLTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    LoginScreen(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}


@Composable
fun LoginScreen(modifier: Modifier) {

    val username = rememberSaveable { mutableStateOf("") }
    val password = rememberSaveable { mutableStateOf("") }
    val userNameError = rememberSaveable { mutableStateOf<String?>(null) }
    val passwordError = rememberSaveable { mutableStateOf<String?>(null) }
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF0F0F0)), contentAlignment = Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,

            modifier = Modifier
                .padding(30.dp)
                .fillMaxWidth()
                .imePadding()
                .verticalScroll(rememberScrollState(), reverseScrolling = true)
                .wrapContentHeight()
        ) {
            Image(
                painter = painterResource(id = R.drawable.img),
                contentDescription = "Splash Background",
                modifier = Modifier
                    .size(180.dp)
            )

            Spacer(modifier = Modifier.height(8.dp)) // Space between image and text


            Text(
                text = "Enter Credentials\nto Continue",
                fontSize = 16.sp,
                color = colorResource(id = R.color.theme_blue),
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(40.dp))

            loginField(
                value = username.value,
                onValueChange = { username.value = it },
                placeholder = "Enter Email",
                errorMessage = userNameError.value
            )

            loginField(
                value = password.value,
                onValueChange = { password.value = it },
                placeholder = "Enter Password",
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                errorMessage = passwordError.value
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                shape = RoundedCornerShape(32.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = colorResource(id = R.color.theme_blue), contentColor = White
                ),
                onClick = {
                    if (validateInputs(
                            username.value, password.value, userNameError, passwordError
                        )
                    ) coroutineScope.launch {

                        var fcm_token = getSavedToken(context)
//                        if (fcm_token.equals("")) fcm_token = FCM_TOKEN

//                        ServiceBuilder.buildService(RetrofitInterface::class.java)
//                            .updateFCM(getSavedAppId(context), fcm_token)
//                            .enqueue(object : Callback<UpdateFCMResponse> {
//                                override fun onResponse(
//                                    call: Call<UpdateFCMResponse>,
//                                    response: Response<UpdateFCMResponse>
//                                ) {
//                                    if (response.body()!!.Success) {
//                                        Log.d(TAG, "onResponse: SUCCESS")
//                                    }
//                                }
//
//                                override fun onFailure(
//                                    call: Call<UpdateFCMResponse>, t: Throwable
//                                ) {
//                                    Log.d(TAG, "onFailure: ${t.message}")
//                                }
//                            })


//                        callApi(username.value, password.value, context, fcm_token!!)

                        context.startActivity(Intent(context, MainActivity2::class.java))
                        (context as? Activity)?.finish() // Finish the LoginActivity
                    }
                },
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .height(height = 50.dp)
                    .fillMaxWidth(),
            ) {
                Text(text = "Login")
            }

            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}


private fun getSavedToken(context: Context): String? {
    val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("MySharedPref", MODE_PRIVATE)
    val token: String = sharedPreferences.getString("RefreshedToken", "").toString()

    if (token != "") {
        Log.d(TAG, "getSavedToken: Token retrieved: $token")
    } else {
        Log.d(TAG, "getSavedToken: No token found")
    }
    return token
}


@SuppressLint("ComposableNaming")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun loginField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    errorMessage: String?,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default
) {
    Column {
        TextField(
            value = value,
            visualTransformation = visualTransformation,
            colors = TextFieldDefaults.textFieldColors(
                disabledTextColor = Color.Transparent,
                containerColor = White,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                disabledIndicatorColor = Color.Transparent
            ),
            textStyle = TextStyle(
                color = Color.DarkGray,
                fontSize = 18.sp,
            ),
            shape = RoundedCornerShape(32.dp),
            keyboardOptions = keyboardOptions,
            singleLine = true,
            onValueChange = onValueChange,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            placeholder = {
                Text(text = placeholder, color = Color.Gray)
            },
            isError = errorMessage != null
        )
        if (errorMessage != null) {
            Text(
                text = errorMessage,
                color = Color.Red,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(start = 16.dp)
            )
        }
    }
}

fun validateInputs(
    email: String,
    password: String,
    emailError: MutableState<String?>,
    passwordError: MutableState<String?>
): Boolean {
    val emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+".toRegex()
    emailError.value = null
    passwordError.value = null

//    when {
//        email.trim().isEmpty() || password.trim().isEmpty() -> {
//            if (email.trim().isEmpty()) emailError.value = "Enter Email"
//            if (password.trim().isEmpty()) passwordError.value = "Enter Password"
//            Log.d("TAG", "Enter Credentials First")
//            return false
//        }
//
//        !email.matches(emailPattern) -> {
//            emailError.value = "Invalid Name Address"
//            Log.d("TAG", "Invalid Email Address")
//            return false
//        }
//
//        else -> {
//            Log.d("TAG", "validated Inputs xD xD")
//            return true
//        }
//    }

    return true
}

suspend fun callApi(email: String, password: String, context: Context, fcm_token: String): Boolean {
    return suspendCancellableCoroutine { continuation ->
//
//        // Create and show the loading dialog
//        val progressDialog = ProgressDialog(context)
//        progressDialog.setMessage("Loading...")
//        progressDialog.setCancelable(false) // Optional: Prevent the user from canceling the dialog
//        progressDialog.show()

//        try {
//            ServiceBuilder.buildService(RetrofitInterface::class.java)
//                .login(email, password, "1", fcm_token).enqueue(object : Callback<LoginResponse> {
//                    override fun onResponse(
//                        call: Call<LoginResponse>, response: Response<LoginResponse>
//                    ) {
//                        val responseBody = response.body()
//
//                        progressDialog.dismiss()
//
//                        if (response.isSuccessful && responseBody!!.Success) {
//                            val message = responseBody.message
//                            val success = responseBody.Success
//                            val driverName = responseBody.driver_name
//                            val appId = responseBody.app_id
//
//                            Log.d(TAG, "onResponse: $message")
//
//                            saveLoginId(appId, driverName)
//
//                            val destination = Intent(context, HomeActivity::class.java)
//                            destination.putExtra("driver_name", driverName)
//                            startActivity(context, destination, null)
//
//                            (context as? Activity)?.finish()
//                            continuation.resume(true)
//
//                        } else {
//                            continuation.resume(false)
//                            Toast.makeText(context, responseBody!!.message, Toast.LENGTH_SHORT)
//                                .show()
//                            Log.d(TAG, "onResponse: ${response.body()?.message}")
//                        }
//
//                    }
//
//                    private fun saveLoginId(appId: Int, driver_name: String) {
//                        val sharedPreferences =
//                            context.getSharedPreferences("MySharedPref", MODE_PRIVATE)
//                        val myEdit = sharedPreferences.edit()
//
//                        myEdit.putString("appId", appId.toString())
//                        myEdit.putString("driver_name", driver_name)
//                        Log.d(TAG, "saveUpdatedToken: appId $appId")
//                        Log.d(TAG, "saveUpdatedToken: driver_name $appId")
//                        myEdit.apply()
//                    }
//
//                    override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
//                        Log.d(TAG, "onFailure: ${t.message}")
//                        Toast.makeText(context, t.message, Toast.LENGTH_SHORT).show()
//                        continuation.resumeWithException(t)
//                        progressDialog.dismiss()
//
//                    }
//                })
//        } catch (e: Exception) {
//            Toast.makeText(context, e.message, Toast.LENGTH_SHORT).show()
//        }

    }
}