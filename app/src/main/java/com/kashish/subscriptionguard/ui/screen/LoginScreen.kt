package com.kashish.subscriptionguard.ui.screen

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.kashish.subscriptionguard.R
import com.kashish.subscriptionguard.data.auth.GoogleSignInHelper
import com.kashish.subscriptionguard.ui.theme.*
import com.kashish.subscriptionguard.viewmodel.AuthViewModel
import com.kashish.subscriptionguard.viewmodel.LoginUiState
import kotlinx.coroutines.launch

@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit
) {
    val context = LocalContext.current
    val viewModel = remember { AuthViewModel(context) }
    val uiState by viewModel.uiState.collectAsState()
    val coroutineScope = rememberCoroutineScope()
    val notificationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()) { }

    LaunchedEffect(uiState) {
        if (uiState is LoginUiState.Success) {
            onLoginSuccess()
        }
    }

    LaunchedEffect(Unit) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val alreadyGranted = ContextCompat.checkSelfPermission(
                context, Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED

            if (!alreadyGranted) {
                notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = LightBlueBackground
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.weight(1f))

            Image(
                painter = painterResource(id = R.drawable.ic_logo),
                contentDescription = "SubscriptionGuard logo",
                modifier = Modifier.size(100.dp),
                contentScale = ContentScale.Fit
            )

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = buildAnnotatedString {
                    withStyle(style = SpanStyle(color = TextDark, fontWeight = FontWeight.Bold)) {
                        append("Subscription")
                    }
                    withStyle(style = SpanStyle(color = IndigoBlue, fontWeight = FontWeight.Bold)) {
                        append("Guard")
                    }
                },
                fontSize = 24.sp
            )

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = "Never get surprised by a renewal again.",
                textAlign = TextAlign.Center,
                color = TaglineGray,
                fontSize = 16.sp,
                lineHeight = 20.sp
            )

            Spacer(modifier = Modifier.height(40.dp))

            Button(
                onClick = {
                    coroutineScope.launch {
                        GoogleSignInHelper.signIn(context)
                            .onSuccess { idToken ->
                                viewModel.loginWithGoogle(idToken)
                            }
                            .onFailure { error ->
                                viewModel.setError(error.message ?: "Google sign-in was cancelled or failed")
                            }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(0.2.dp, TaglineGray),
                colors = ButtonDefaults.buttonColors(containerColor = CardWhite),
                enabled = uiState !is LoginUiState.Loading
            ) {
                if (uiState is LoginUiState.Loading) {
                    CircularProgressIndicator(modifier = Modifier.size(20.dp))
                }
                else {
                    Image(
                        painter = painterResource(id = R.drawable.ic_google_logo),
                        contentDescription = "Google logo",
                        modifier = Modifier.size(40.dp),
                        contentScale = ContentScale.Fit
                    )
                    Text(text = "Sign in with Google", color = TextDark, fontSize = 16.sp)
                }
            }

            if (uiState is LoginUiState.Error) {
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = (uiState as LoginUiState.Error).message,
                    color = DangerRed,
                    fontSize = 13.sp
                )
            }

            Spacer(modifier = Modifier.weight(1f))
        }
    }
}