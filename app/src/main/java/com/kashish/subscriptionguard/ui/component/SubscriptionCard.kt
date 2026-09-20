package com.kashish.subscriptionguard.ui.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kashish.subscriptionguard.data.model.Subscription
import com.kashish.subscriptionguard.ui.theme.*
import com.kashish.subscriptionguard.util.formatBillingCycle
import com.kashish.subscriptionguard.util.formatCycleLabel
import java.time.LocalDate
import java.time.temporal.ChronoUnit

@Composable
fun SubscriptionCard(
    subscription: Subscription,
    onClick: () -> Unit
) {
    val daysUntilRenewal = try {
        ChronoUnit.DAYS.between(LocalDate.now(), LocalDate.parse(subscription.cycleEndDate))
    } catch (e: Exception) {
        Long.MAX_VALUE
    }
    val isRenewingSoon = daysUntilRenewal in 0..2

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .then(
                if (isRenewingSoon) {
                    Modifier.border(1.5.dp, DangerRed, RoundedCornerShape(16.dp))
                }
                else Modifier
            ),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CardWhite),
        onClick = onClick
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                val iconRes = IconResolver.resolve(subscription.name)

                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(PeriwinkleBlue.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    if (iconRes != null) {
                        Image(
                            painter = painterResource(id = iconRes),
                            contentDescription = subscription.name,
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(RoundedCornerShape(12.dp)),
                            contentScale = ContentScale.Crop
                        )
                    }
                    else {
                        Text(
                            text = subscription.name.take(1).uppercase(),
                            color = IndigoBlue,
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = subscription.name,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 16.sp,
                        color = TextDark
                    )
                    Text(
                        text = subscription.category.lowercase()
                            .replaceFirstChar { it.uppercase() },
                        fontSize = 13.sp,
                        color = TaglineGray
                    )
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "₹${subscription.cost}",
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 16.sp,
                        color = TextDark
                    )
                    Text(
                        text = formatBillingCycle(subscription.intervalCount, subscription.billingInterval),
                        fontSize = 12.sp,
                        color = TaglineGray
                    )
                }
            }

            if (isRenewingSoon) {
                Spacer(modifier = Modifier.height(10.dp))
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(DangerRed.copy(alpha = 0.1f))
                        .padding(horizontal = 10.dp, vertical = 5.dp)
                ) {
                    Text(
                        text = formatCycleLabel(subscription.type, daysUntilRenewal, isSoon = true),
                        color = DangerRed,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
            else {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "${formatCycleLabel(subscription.type, daysUntilRenewal, isSoon = false)} ${subscription.cycleEndDate}",
                    fontSize = 12.sp,
                    color = TaglineGray
                )
            }
        }
    }
}