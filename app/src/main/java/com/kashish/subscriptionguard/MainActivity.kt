package com.kashish.subscriptionguard

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.kashish.subscriptionguard.data.network.RetrofitClient
import com.kashish.subscriptionguard.ui.navigation.NavGraph
import com.kashish.subscriptionguard.ui.theme.SubscriptionGuardTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        RetrofitClient.init(applicationContext)
        enableEdgeToEdge()
        setContent {
            SubscriptionGuardTheme {
                NavGraph()
            }
        }
    }
}