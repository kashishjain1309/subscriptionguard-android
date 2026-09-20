package com.kashish.subscriptionguard.ui.component

import androidx.annotation.DrawableRes
import com.kashish.subscriptionguard.R

object IconResolver {

    private val knownIcons = mapOf(
        "netflix" to (R.drawable.ic_netflix to listOf("netflix")),
        "spotify" to (R.drawable.ic_spotify to listOf("spotify")),
        "youtube" to (R.drawable.ic_youtube to listOf("youtube", "yt premium")),
        "amazon_prime" to (R.drawable.ic_amazon_prime_video to listOf("amazon prime", "prime video", "amazon video")),
        "disney" to (R.drawable.ic_disney_plus to listOf("disney", "disney+", "disney plus", "disney hotstar", "hotstar", "jiohotstar")),
    )

    @DrawableRes
    fun resolve(subscriptionName: String): Int? {
        val normalized = subscriptionName.lowercase().trim()
        return knownIcons.values.firstOrNull { (_, aliases) ->
            aliases.any { alias -> normalized.contains(alias) }
        }?.first
    }
}