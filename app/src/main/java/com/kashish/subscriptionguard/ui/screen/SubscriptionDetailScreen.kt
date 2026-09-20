package com.kashish.subscriptionguard.ui.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.kashish.subscriptionguard.ui.component.IconResolver
import com.kashish.subscriptionguard.ui.theme.*
import com.kashish.subscriptionguard.util.formatBillingCycle
import com.kashish.subscriptionguard.util.formatEnumLabel
import com.kashish.subscriptionguard.viewmodel.SubscriptionDetailUiState
import com.kashish.subscriptionguard.viewmodel.SubscriptionDetailViewModel
import com.kashish.subscriptionguard.viewmodel.SubscriptionDetailViewModelFactory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SubscriptionDetailScreen(
    subscriptionId: String,
    onBack: () -> Unit,
    onEdit: (String) -> Unit,
    onCancelled: () -> Unit
) {
    val viewModel: SubscriptionDetailViewModel = viewModel(
        factory = SubscriptionDetailViewModelFactory(subscriptionId)
    )
    val uiState by viewModel.uiState.collectAsState()
    val cancelSuccess by viewModel.cancelSuccess.collectAsState()
    var showCancelConfirm by remember { mutableStateOf(false) }

    LaunchedEffect(cancelSuccess) {
        if (cancelSuccess) onCancelled()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Subscription Details") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        containerColor = LightBlueBackground
    ) { innerPadding ->
        Box(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
            when (val state = uiState) {
                is SubscriptionDetailUiState.Loading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                is SubscriptionDetailUiState.Error -> {
                    Text(text = "Error: ${state.message}", modifier = Modifier.align(Alignment.Center))
                }
                is SubscriptionDetailUiState.Success -> {
                    val sub = state.subscription
                    Column(modifier = Modifier.padding(20.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            val iconRes = IconResolver.resolve(sub.name)

                            Box(
                                modifier = Modifier
                                    .size(60.dp)
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(PeriwinkleBlue.copy(alpha = 0.15f)),
                                contentAlignment = Alignment.Center
                            ) {
                                if (iconRes != null) {
                                    Image(
                                        painter = painterResource(id = iconRes),
                                        contentDescription = sub.name,
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .clip(RoundedCornerShape(16.dp)),
                                        contentScale = ContentScale.Crop
                                    )
                                }
                                else {
                                    Text(
                                        text = sub.name.take(1).uppercase(),
                                        color = IndigoBlue,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 20.sp
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.width(16.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = sub.name,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 22.sp,
                                    color = TextDark
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = formatEnumLabel(sub.category),
                                    fontSize = 13.sp,
                                    color = TaglineGray
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(32.dp))

                        DetailRow("Cost", "₹${sub.cost}")
                        DetailRow("Billing Interval", formatBillingCycle(sub.intervalCount, sub.billingInterval))
                        DetailRow("Subscription Type", formatEnumLabel(sub.type))
                        DetailRow("Next Billing Date", sub.cycleEndDate)
                        DetailRow("Status", formatEnumLabel(sub.status))

                        Spacer(modifier = Modifier.height(32.dp))

                        Button(
                            onClick = { onEdit(sub.id) },
                            modifier = Modifier.fillMaxWidth().height(50.dp),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("Edit")
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        OutlinedButton(
                            onClick = { showCancelConfirm = true },
                            modifier = Modifier.fillMaxWidth().height(50.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = DangerRed)
                        ) {
                            Text("Cancel Subscription")
                        }
                    }
                }
            }

            if (showCancelConfirm) {
                AlertDialog(
                    onDismissRequest = { showCancelConfirm = false },
                    title = { Text("Cancel Subscription?") },
                    text = { Text("This will mark the subscription as inactive.") },
                    confirmButton = {
                        TextButton(onClick = {
                            showCancelConfirm = false
                            viewModel.cancelSubscription()
                        }) { Text("Yes, Cancel") }
                    },
                    dismissButton = {
                        TextButton(onClick = { showCancelConfirm = false }) { Text("No") }
                    }
                )
            }
        }
    }
}

@Composable
fun DetailRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, color = TaglineGray)
        Text(value, fontWeight = FontWeight.Medium, color = TextDark)
    }
}