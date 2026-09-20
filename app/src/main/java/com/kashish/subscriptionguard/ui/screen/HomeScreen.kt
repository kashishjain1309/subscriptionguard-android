package com.kashish.subscriptionguard.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import com.kashish.subscriptionguard.ui.component.ReconciliationDialog
import com.kashish.subscriptionguard.ui.component.SubscriptionList
import com.kashish.subscriptionguard.ui.theme.*
import com.kashish.subscriptionguard.util.estimateMonthlyCost
import com.kashish.subscriptionguard.viewmodel.HomeUiState
import com.kashish.subscriptionguard.viewmodel.HomeViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onAddSubscription: () -> Unit,
    onSubscriptionClick: (String) -> Unit,
    viewModel: HomeViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val pendingReconciliations by viewModel.pendingReconciliations.collectAsState()
    val lifecycleOwner = LocalLifecycleOwner.current

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                viewModel.loadSubscriptions()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Dashboard", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = LightBlueBackground)
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddSubscription,
                containerColor = IndigoBlue
            ) {
                Icon(Icons.Filled.Add, contentDescription = "Add Subscription", tint = CardWhite)
            }
        },
        containerColor = LightBlueBackground
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (val state = uiState) {
                is HomeUiState.Loading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                is HomeUiState.Success -> {
                    Column {
                        val totalMonthly = state.subscriptions.sumOf { estimateMonthlyCost(it) }

                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = IndigoBlue)
                        ) {
                            Column(modifier = Modifier.padding(20.dp)) {
                                Text(
                                    text = "Total Monthly Spend",
                                    color = CardWhite.copy(alpha = 0.8f),
                                    fontSize = 14.sp
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = "₹${"%.0f".format(totalMonthly)}",
                                    color = CardWhite,
                                    fontSize = 28.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Across ${state.subscriptions.size} active subscriptions",
                                    color = CardWhite.copy(alpha = 0.8f),
                                    fontSize = 13.sp
                                )
                            }
                        }

                        Text(
                            text = "Your Subscriptions",
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 16.sp,
                            color = TextDark,
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Box(modifier = Modifier.weight(1f)) {
                            SubscriptionList(
                                subscriptions = state.subscriptions,
                                onSubscriptionClick = onSubscriptionClick
                            )
                        }
                    }
                }
                is HomeUiState.Error -> {
                    Text(
                        text = "Error: ${state.message}",
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
            }
        }
    }

    pendingReconciliations.firstOrNull()?.let { subscription ->
        ReconciliationDialog(
            subscription = subscription,
            onRenew = { viewModel.resolveReconciliation(subscription.id, "RENEW") },
            onCancel = { viewModel.resolveReconciliation(subscription.id, "CANCEL") },
            onDismiss = { }
        )
    }
}