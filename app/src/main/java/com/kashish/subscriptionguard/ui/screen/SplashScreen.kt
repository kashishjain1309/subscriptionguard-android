package com.kashish.subscriptionguard.ui.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
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
import com.kashish.subscriptionguard.R
import com.kashish.subscriptionguard.data.local.TokenManager
import com.kashish.subscriptionguard.ui.theme.*
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    onNavigateToLogin: () -> Unit,
    onNavigateToHome: () -> Unit
) {
    val context = LocalContext.current
    val tokenManager = remember { TokenManager(context.applicationContext) }

    LaunchedEffect(Unit) {
        delay(2500)

        val accessToken = tokenManager.getAccessToken()
        if (accessToken != null) {
            onNavigateToHome()
        }
        else {
            onNavigateToLogin()
        }
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = LightBlueBackground
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.weight(1f))

            Image(
                painter = painterResource(id = R.drawable.splash_illustration),
                contentDescription = "SubscriptionGuard illustration",
                modifier = Modifier.size(220.dp),
                contentScale = ContentScale.Fit
            )

            Spacer(modifier = Modifier.height(32.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_logo),
                    contentDescription = "SubscriptionGuard logo",
                    modifier = Modifier.size(36.dp)
                )

                Spacer(modifier = Modifier.width(10.dp))

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
            }

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = "Never get surprised by a\nrenewal again.",
                textAlign = TextAlign.Center,
                color = TaglineGray,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                lineHeight = 20.sp
            )

            Spacer(modifier = Modifier.weight(1f))
        }
    }
}