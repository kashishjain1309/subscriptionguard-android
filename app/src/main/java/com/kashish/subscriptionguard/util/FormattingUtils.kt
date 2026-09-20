package com.kashish.subscriptionguard.util

fun formatEnumLabel(value: String): String {
    return value.split("_").joinToString(" ") { it.lowercase().replaceFirstChar { c -> c.uppercase() } }
}

fun formatCycleLabel(type: String, daysUntil: Long, isSoon: Boolean): String {
    val verb = when (type) {
        "AUTO_RENEWING" -> "Renews"
        "FIXED_TERM" -> "Expires"
        "MANUAL" -> "Payment due"
        else -> "Due"
    }

    return when {
        !isSoon -> verb
        daysUntil <= 0 -> "$verb today"
        else -> "$verb in $daysUntil day${if (daysUntil > 1) "s" else ""}"
    }
}