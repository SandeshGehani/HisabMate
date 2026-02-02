package com.hisabmate.ui.screens
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.LocalCafe
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hisabmate.ui.theme.*
import com.hisabmate.viewmodel.AddRecordViewModel

@Composable
fun AddEditRecordScreen(
    viewModel: AddRecordViewModel,
    initialDate: Long = System.currentTimeMillis(),
    onBack: () -> Unit = {},
    onNavigateToCalendar: () -> Unit = {}
) {
    var selectedDate by remember { mutableLongStateOf(initialDate) }
    // Local State
    var mealCount by remember { mutableDoubleStateOf(0.0) }
    var teaCount by remember { mutableDoubleStateOf(0.0) }
    var contributionAmount by remember { mutableStateOf("") }
    
    var isCustomMeal by remember { mutableStateOf(false) }
    var isCustomTea by remember { mutableStateOf(false) }
    var isCustomMoney by remember { mutableStateOf(true) } 

    // Load existing
    LaunchedEffect(selectedDate) {
        viewModel.loadRecordForDate(selectedDate)
    }
    
    val existingRecord by viewModel.existingRecord.collectAsState()
    
    LaunchedEffect(existingRecord) {
        existingRecord?.let {
            mealCount = it.mealsCount
            teaCount = it.teasCount
            contributionAmount = if (it.moneyAmount > 0) it.moneyAmount.toInt().toString() else ""
            
            // Check if amounts fit in standard pill selections
            isCustomMeal = it.mealsCount !in listOf(0.0, 0.5, 1.0, 2.0, 3.0, 4.0, 5.0)
            isCustomTea = it.teasCount !in listOf(0.0, 0.5, 1.0, 2.0, 3.0, 4.0, 5.0)
            isCustomMoney = it.moneyAmount.toInt() !in listOf(1000, 1500, 2000, 2500, 3000)
        } ?: run {
            // Reset if no record found for this date
            mealCount = 0.0
            teaCount = 0.0
            contributionAmount = ""
            isCustomMeal = false
            isCustomTea = false
            isCustomMoney = true
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 100.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // Sticky Header (Simulated)
            Header(onClose = onBack, onReset = {
                mealCount = 0.0
                teaCount = 0.0
                contributionAmount = ""
                isCustomMeal = false
                isCustomTea = false
                isCustomMoney = true
            })

            // Date Strip
            DateStrip(
                selectedDate = selectedDate, 
                onDateSelected = { selectedDate = it },
                onCalendarClick = onNavigateToCalendar
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Meals Section
                InputSection(
                    title = "Meals",
                    subtitle = "Lunch & Dinner",
                    icon = Icons.Default.Restaurant,
                    iconBg = Color(0xFFFFEDD5), // orange-100
                    iconTint = Color(0xFFEA580C), // orange-600
                ) {
                    CountSelector(
                        selectedCount = if (isCustomMeal) -1.0 else mealCount,
                        onCountSelected = { count ->
                            mealCount = if (mealCount == count) 0.0 else count
                            isCustomMeal = false
                        },
                        onCustomClick = { isCustomMeal = true }
                    )
                    if (isCustomMeal) {
                        CustomInput(
                            value = if (mealCount > 5) mealCount.toString() else (if(mealCount > 0) mealCount.toString() else ""),
                            onValueChange = { mealCount = it.toDoubleOrNull() ?: 0.0 },
                            label = "Custom Amount",
                            suffix = ""
                        )
                    }
                }

                // Teas Section
                InputSection(
                    title = "Teas",
                    subtitle = "Cups",
                    icon = Icons.Default.LocalCafe,
                    iconBg = Color(0xFFDBEAFE), // blue-100
                    iconTint = Color(0xFF2563EB), // blue-600
                ) {
                    CountSelector(
                        selectedCount = if (isCustomTea) -1.0 else teaCount,
                        onCountSelected = { count ->
                            teaCount = if (teaCount == count) 0.0 else count 
                            isCustomTea = false
                        },
                        onCustomClick = { isCustomTea = true }
                    )
                    if (isCustomTea) {
                        CustomInput(
                            value = if (teaCount > 5) teaCount.toString() else (if(teaCount > 0) teaCount.toString() else ""),
                            onValueChange = { teaCount = it.toDoubleOrNull() ?: 0.0 },
                            label = "Custom Amount",
                            suffix = ""
                        )
                    }
                }

                // Money Section
                InputSection(
                    title = "Contribution",
                    subtitle = "PKR",
                    icon = Icons.Default.Payments,
                    iconBg = Color(0xFFD1FAE5), // emerald-100
                    iconTint = Color(0xFF059669), // emerald-600
                ) {
                    MoneySelector(
                        selectedAmount = if (isCustomMoney) -1 else contributionAmount.toIntOrNull() ?: 0,
                        onAmountSelected = { amount ->
                            val current = contributionAmount.toIntOrNull() ?: 0
                            contributionAmount = if (current == amount) "" else amount.toString()
                            isCustomMoney = false
                        },
                        onCustomClick = { isCustomMoney = true }
                    )
                    if (isCustomMoney) {
                        CustomInput(
                            value = contributionAmount,
                            onValueChange = { contributionAmount = it },
                            label = "Custom Amount",
                            prefix = "Rs."
                        )
                    }
                }
                
                val isFutureDate = selectedDate > System.currentTimeMillis()

                if (isFutureDate) {
                    Card(
                        modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.5f)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Close, contentDescription = null, tint = MaterialTheme.colorScheme.error)
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                "Cannot add/edit records for a future date.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onErrorContainer
                            )
                        }
                    }
                }

                Text(
                    text = "Keep your hisab up to date daily.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
                )
            }
        }
        
        // Bottom Save Button
        val isFutureDate = selectedDate > System.currentTimeMillis()
        SaveButtonContainer(
            modifier = Modifier.align(Alignment.BottomCenter),
            enabled = !isFutureDate,
            onSave = {
                viewModel.saveRecord(
                    date = selectedDate,
                    meals = mealCount,
                    teas = teaCount,
                    money = contributionAmount.toDoubleOrNull() ?: 0.0
                )
                onBack()
            }
        )
    }
}

@Composable
fun Header(onClose: () -> Unit, onReset: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.background.copy(alpha = 0.95f))
            .padding(16.dp)
            .statusBarsPadding(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        IconButton(
            onClick = onClose,
            modifier = Modifier
                .background(MaterialTheme.colorScheme.onBackground.copy(alpha = 0.05f), CircleShape)
                .size(40.dp)
        ) {
            Icon(Icons.Default.Close, contentDescription = "Close")
        }
        
        Text(
            text = "Add Record",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        
        TextButton(onClick = onReset) {
            Text(
                text = "Reset",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun DateStrip(selectedDate: Long, onDateSelected: (Long) -> Unit, onCalendarClick: () -> Unit) {
    val selectedLocalDate = java.time.Instant.ofEpochMilli(selectedDate).atZone(java.time.ZoneId.systemDefault()).toLocalDate()
    
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Show 5 days centered around selected date
        (-2..2).forEach { offset ->
            val date = selectedLocalDate.plusDays(offset.toLong())
            val dateMillis = date.atStartOfDay(java.time.ZoneId.systemDefault()).toInstant().toEpochMilli()
            val isSelected = offset == 0
            val dayName = if (date == java.time.LocalDate.now()) "Today" else date.dayOfWeek.name.take(3).lowercase().replaceFirstChar { it.uppercase() }
            
            Box(modifier = Modifier.weight(1f).clickable { 
                onDateSelected(dateMillis)
            }) {
                DateItem(
                    day = dayName,
                    date = date.dayOfMonth.toString(),
                    isSelected = isSelected,
                    alpha = if (isSelected) 1f else 0.6f
                )
            }
        }
        
        // Calendar Button
        Box(
            modifier = Modifier
                .size(width = 56.dp, height = 72.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(MaterialTheme.colorScheme.surface)
                .border(1.dp, MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(16.dp))
                .clickable { onCalendarClick() },
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Default.CalendarMonth, contentDescription = "Calendar", tint = Blue500)
        }
    }
}

@Composable
fun DateItem(day: String, date: String, isSelected: Boolean = false, alpha: Float = 1f) {
    val bgColor = if (isSelected) Blue500 else MaterialTheme.colorScheme.surface
    val contentColor = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface
    val shadowElevation = if (isSelected) 4.dp else 0.dp

    Surface(
        modifier = Modifier.size(width = 56.dp, height = 72.dp).graphicsLayer(alpha = alpha),
        shape = RoundedCornerShape(16.dp),
        color = bgColor,
        shadowElevation = shadowElevation
    ) {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
             Text(
                text = day,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                color = if (isSelected) contentColor else MaterialTheme.colorScheme.onSurfaceVariant
             )
             Text(
                 text = date,
                 style = MaterialTheme.typography.titleLarge,
                 fontWeight = FontWeight.Bold,
                 color = contentColor
             )
             if (isSelected) {
                 Spacer(modifier = Modifier.height(2.dp))
                 Box(modifier = Modifier.size(4.dp).background(Color.White, CircleShape))
             }
        }
    }
}

@Composable
fun InputSection(
    title: String,
    subtitle: String,
    icon: ImageVector,
    iconBg: Color,
    iconTint: Color,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                 Row(verticalAlignment = Alignment.CenterVertically) {
                     Box(
                         modifier = Modifier
                             .size(40.dp)
                             .background(iconBg, CircleShape),
                         contentAlignment = Alignment.Center
                     ) {
                         Icon(icon, contentDescription = null, tint = iconTint)
                     }
                     Spacer(modifier = Modifier.width(12.dp))
                     Text(
                         text = title,
                         style = MaterialTheme.typography.titleMedium,
                         fontWeight = FontWeight.Bold
                     )
                 }
                 Surface(
                     color = MaterialTheme.colorScheme.surfaceVariant,
                     shape = RoundedCornerShape(8.dp)
                 ) {
                     Text(
                         text = subtitle,
                         style = MaterialTheme.typography.labelSmall,
                         fontWeight = FontWeight.Medium,
                         modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                         color = MaterialTheme.colorScheme.onSurfaceVariant
                     )
                 }
            }
            content()
        }
    }
}

@Composable
fun CountSelector(
    selectedCount: Double,
    onCountSelected: (Double) -> Unit,
    onCustomClick: () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            listOf(0.0, 0.5, 1.0, 2.0, 3.0, 4.0, 5.0).take(4).forEach { count ->
                 val isSelected = selectedCount == count
                 val bgColor = if (isSelected) Blue500 else MaterialTheme.colorScheme.background
                 val textColor = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                 val fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium

                 Box(
                     modifier = Modifier
                         .weight(1f)
                         .height(44.dp)
                         .clip(RoundedCornerShape(12.dp))
                         .background(bgColor)
                         .clickable { onCountSelected(count) },
                     contentAlignment = Alignment.Center
                 ) {
                     Text(text = count.toString().removeSuffix(".0"), color = textColor, fontWeight = fontWeight, fontSize = 14.sp)
                 }
            }
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            listOf(0.0, 0.5, 1.0, 2.0, 3.0, 4.0, 5.0).drop(4).forEach { count ->
                 val isSelected = selectedCount == count
                 val bgColor = if (isSelected) Blue500 else MaterialTheme.colorScheme.background
                 val textColor = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                 val fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium

                 Box(
                     modifier = Modifier
                         .weight(1f)
                         .height(44.dp)
                         .clip(RoundedCornerShape(12.dp))
                         .background(bgColor)
                         .clickable { onCountSelected(count) },
                     contentAlignment = Alignment.Center
                 ) {
                     Text(text = count.toString().removeSuffix(".0"), color = textColor, fontWeight = fontWeight, fontSize = 14.sp)
                 }
            }
            // Other/Custom button placeholder
            Box(
                 modifier = Modifier
                     .weight(1f)
                     .height(44.dp)
                     .clip(RoundedCornerShape(12.dp))
                     .background(if (selectedCount == -1.0) Blue500 else MaterialTheme.colorScheme.background)
                     .clickable { onCustomClick() },
                 contentAlignment = Alignment.Center
             ) {
                 Text(text = "Custom", color = if (selectedCount == -1.0) Color.White else MaterialTheme.colorScheme.onSurfaceVariant, fontWeight = FontWeight.Bold, fontSize = 14.sp)
             }
        }
    }
    
    Spacer(modifier = Modifier.height(16.dp))
    
    TextButton(
        onClick = onCustomClick,
        modifier = Modifier.fillMaxWidth()
    ) {
        Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(18.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(modifier = Modifier.width(8.dp))
        Text("Enter Custom Amount", color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
fun MoneySelector(
    selectedAmount: Int,
    onAmountSelected: (Int) -> Unit,
    onCustomClick: () -> Unit
) {
     val amounts = listOf(1000, 1500, 2000, 2500, 3000)
    
     Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            amounts.take(3).forEach { amount ->
                 MoneyButton(amount = amount, isSelected = selectedAmount == amount, onClick = { onAmountSelected(amount) }, modifier = Modifier.weight(1f))
            }
        }
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            amounts.drop(3).forEach { amount ->
                 MoneyButton(amount = amount, isSelected = selectedAmount == amount, onClick = { onAmountSelected(amount) }, modifier = Modifier.weight(1f))
            }
            Box(
                 modifier = Modifier
                     .weight(1f)
                     .height(40.dp)
                     .clip(RoundedCornerShape(8.dp))
                     .background(if (selectedAmount == -1) Blue500 else MaterialTheme.colorScheme.background)
                     .clickable { onCustomClick() },
                 contentAlignment = Alignment.Center
             ) {
                 Text(text = "Other", color = if (selectedAmount == -1) Color.White else MaterialTheme.colorScheme.onSurfaceVariant, fontWeight = FontWeight.Bold, fontSize = 14.sp)
             }
        }
     }
}

@Composable
fun MoneyButton(amount: Int, isSelected: Boolean, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Box(
         modifier = modifier
             .height(40.dp)
             .clip(RoundedCornerShape(8.dp))
             .background(if (isSelected) Blue500 else MaterialTheme.colorScheme.background)
             .clickable { onClick() },
         contentAlignment = Alignment.Center
     ) {
         Text(text = amount.toString(), color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant, fontWeight = FontWeight.Medium, fontSize = 14.sp)
     }
}

@Composable
fun CustomInput(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    prefix: String = "",
    suffix: String = ""
) {
    Column(modifier = Modifier.padding(top = 8.dp)) {
        Text(
            text = label, 
            style = MaterialTheme.typography.labelSmall, 
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(start = 4.dp, bottom = 4.dp)
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.background, RoundedCornerShape(12.dp))
                .border(1.dp, MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.2f), RoundedCornerShape(12.dp))
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (prefix.isNotEmpty()) {
                Text(text = prefix, color = MaterialTheme.colorScheme.onSurfaceVariant, fontWeight = FontWeight.Medium)
                Spacer(modifier = Modifier.width(8.dp))
            }
            
            BasicTextField(
                value = value,
                onValueChange = onValueChange,
                textStyle = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                ),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.weight(1f)
            )

             if (suffix.isNotEmpty()) {
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = suffix, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            
            if (value.isNotEmpty()) {
                IconButton(onClick = { onValueChange("") }, modifier = Modifier.size(24.dp)) {
                    Icon(Icons.Default.Close, contentDescription = "Clear", tint = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }
    }
}

@Composable
fun SaveButtonContainer(
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    onSave: () -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color.Transparent,
                        MaterialTheme.colorScheme.background.copy(alpha = 0.8f),
                        MaterialTheme.colorScheme.background
                    )
                )
            )
            .padding(16.dp)
            .navigationBarsPadding()
            .then(if (!enabled) Modifier.graphicsLayer(alpha = 0.5f) else Modifier)
    ) {
         Button(
            onClick = onSave,
            enabled = enabled,
            modifier = Modifier.fillMaxWidth().height(56.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Blue500,
                contentColor = Color.White,
                disabledContainerColor = Color.Gray,
                disabledContentColor = Color.White.copy(alpha = 0.6f)
            ),
            elevation = ButtonDefaults.buttonElevation(defaultElevation = 8.dp)
        ) {
            Icon(Icons.Default.Save, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = "Save Record", fontSize = 18.sp, fontWeight = FontWeight.Bold)
        }
    }
}
