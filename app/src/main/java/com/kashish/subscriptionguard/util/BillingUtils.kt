package com.kashish.subscriptionguard.util

import com.kashish.subscriptionguard.data.model.Subscription

fun estimateMonthlyCost(subscription: Subscription): Double {
    val cost = subscription.cost.toDoubleOrNull() ?: return 0.0
    val intervalCount = subscription.intervalCount

    val dailyRate = when (subscription.billingInterval.uppercase()) {
        "DAYS" -> cost / intervalCount
        "MONTHS" -> cost / (intervalCount * 30.0)
        "YEARS" -> cost / (intervalCount * 365.0)
        else -> 0.0
    }

    return dailyRate * 30.0
}

fun formatBillingCycle(intervalCount: Int, billingInterval: String): String {
    val unit = billingInterval.lowercase()
    return if (intervalCount == 1) {
        "for a $unit".let { it.dropLast(1) }
    }
    else {
        "for $intervalCount $unit"
    }
}