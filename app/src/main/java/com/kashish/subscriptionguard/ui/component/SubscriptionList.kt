package com.kashish.subscriptionguard.ui.component

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.kashish.subscriptionguard.data.model.Subscription

@Composable
fun SubscriptionList(subscriptions: List<Subscription>, onSubscriptionClick: (String) -> Unit) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(subscriptions) { subscription ->
            SubscriptionCard(subscription = subscription, onClick = { onSubscriptionClick(subscription.id) })
        }
    }
}