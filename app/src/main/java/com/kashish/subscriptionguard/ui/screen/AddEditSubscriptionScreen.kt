package com.kashish.subscriptionguard.ui.screen

import com.kashish.subscriptionguard.ui.component.DropdownField
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.kashish.subscriptionguard.data.model.CreateSubscriptionRequest
import com.kashish.subscriptionguard.data.model.UpdateSubscriptionRequest
import com.kashish.subscriptionguard.ui.component.DatePickerField
import com.kashish.subscriptionguard.ui.theme.*
import com.kashish.subscriptionguard.viewmodel.AddEditSubscriptionViewModel
import com.kashish.subscriptionguard.viewmodel.AddEditSubscriptionViewModelFactory
import com.kashish.subscriptionguard.viewmodel.SaveState
import java.time.LocalDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditSubscriptionScreen(
    subscriptionId: String?,
    onSaved: () -> Unit,
    onBack: () -> Unit
) {
    val viewModel: AddEditSubscriptionViewModel = viewModel(
        factory = AddEditSubscriptionViewModelFactory(subscriptionId)
    )
    val existing by viewModel.existingSubscription.collectAsState()
    val saveState by viewModel.saveState.collectAsState()

    var name by remember { mutableStateOf("") }
    var cost by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("ENTERTAINMENT") }
    var type by remember { mutableStateOf("AUTO_RENEWING") }
    var intervalCount by remember { mutableStateOf("1") }
    var billingInterval by remember { mutableStateOf("MONTHS") }
    var cycleStartDate by remember { mutableStateOf(LocalDate.now().toString()) }

    val isFormValid = !viewModel.isEditMode && name.isNotBlank() && cost.isNotBlank()
            && intervalCount.isNotBlank() && cycleStartDate.isNotBlank()

    LaunchedEffect(existing) {
        existing?.let {
            name = it.name
            cost = it.cost
            category = it.category
            intervalCount = it.intervalCount.toString()
            billingInterval = it.billingInterval
            cycleStartDate = it.cycleEndDate
        }
    }

    LaunchedEffect(saveState) {
        if (saveState is SaveState.Success) {
            onSaved()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (viewModel.isEditMode) "Edit Subscription" else "Add Subscription") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        containerColor = LightBlueBackground
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Name") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = cost,
                onValueChange = { input -> if (input.matches(Regex("^\\d*\\.?\\d*$"))) cost = input },
                label = { Text("Cost (per billing cycle)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row {
                OutlinedTextField(
                    value = intervalCount,
                    onValueChange = { input -> if (input.all { it.isDigit() }) intervalCount = input },
                    label = { Text("for") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.width(12.dp))
                DropdownField(
                    label = "Interval",
                    selected = billingInterval,
                    options = listOf("DAYS", "MONTHS", "YEARS"),
                    onSelected = { billingInterval = it },
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            DatePickerField(
                label = "Start Date",
                selectedDate = cycleStartDate,
                onDateSelected = { cycleStartDate = it }
            )

            Spacer(modifier = Modifier.height(16.dp))

            if (!viewModel.isEditMode) {
                DropdownField(
                    label = "Subscription Type",
                    selected = type,
                    options = listOf("AUTO_RENEWING", "FIXED_TERM", "MANUAL"),
                    onSelected = { type = it }
                )
                Spacer(modifier = Modifier.height(16.dp))
            }

            DropdownField(
                label = "Category",
                selected = category,
                options = listOf("ENTERTAINMENT", "UTILITIES", "PRODUCTIVITY", "OTHER"),
                onSelected = { category = it }
            )

            Spacer(modifier = Modifier.height(24.dp))

            if (saveState is SaveState.Error) {
                Text(
                    text = (saveState as SaveState.Error).message,
                    color = DangerRed,
                    fontSize = 13.sp
                )
                Spacer(modifier = Modifier.height(12.dp))
            }

            Button(
                onClick = {
                    if (viewModel.isEditMode) {
                        viewModel.updateSubscription(
                            UpdateSubscriptionRequest(
                                name = name,
                                cost = cost,
                                category = category,
                                intervalCount = intervalCount.toIntOrNull(),
                                billingInterval = billingInterval,
                                cycleStartDate = cycleStartDate
                            )
                        )
                    }
                    else {
                        viewModel.createSubscription(
                            CreateSubscriptionRequest(
                                name = name,
                                cost = cost,
                                category = category,
                                type = type,
                                intervalCount = intervalCount.toIntOrNull() ?: 1,
                                billingInterval = billingInterval,
                                cycleStartDate = cycleStartDate
                            )
                        )
                    }
                },
                modifier = Modifier.fillMaxWidth().height(52.dp),
                shape = RoundedCornerShape(12.dp),
                enabled = saveState !is SaveState.Saving && (viewModel.isEditMode || isFormValid)
            ) {
                if (saveState is SaveState.Saving) {
                    CircularProgressIndicator(modifier = Modifier.size(20.dp))
                }
                else {
                    Text(if (viewModel.isEditMode) "Save Changes" else "Add Subscription")
                }
            }
        }
    }
}

