package com.kashish.subscriptionguard.ui.component

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.kashish.subscriptionguard.data.model.Subscription
import com.kashish.subscriptionguard.ui.theme.IndigoBlue

@Composable
fun ReconciliationDialog(
    subscription: Subscription,
    onRenew: () -> Unit,
    onCancel: () -> Unit,
    onDismiss: () -> Unit
) {
    val isManual = subscription.type == "MANUAL"

    val question = if (isManual) {
        "Did you renew this subscription?"
    }
    else {
        "Have you cancelled this subscription?"
    }

    val positiveLabel = if (isManual) "Yes, I renewed it" else "Yes, I cancelled it"
    val negativeLabel = if (isManual) "No, I cancelled it" else "No, keep it active"

    val onPositive = if (isManual) onRenew else onCancel
    val onNegative = if (isManual) onCancel else onRenew

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("Billing cycle ended", fontWeight = FontWeight.Bold)
        },
        text = {
            Column {
                Text("₹${subscription.cost} was due for ${subscription.name}.")
                Spacer(modifier = Modifier.height(8.dp))
                Text(question)
            }
        },
        confirmButton = {
            TextButton(onClick = onPositive) {
                Text(positiveLabel)
            }
        },
        dismissButton = {
            TextButton(onClick = onNegative) {
                Text(negativeLabel)
            }
        }
    )
}