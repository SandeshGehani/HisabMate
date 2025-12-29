package com.hisabmate.ui.screens

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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hisabmate.ui.theme.*
import com.hisabmate.viewmodel.AddRecordViewModel

@Composable
fun AddEditRecordScreen(
    viewModel: AddRecordViewModel,
    selectedDate: Long = System.currentTimeMillis(),
    onBack: () -> Unit = {}
) {
    // Local State
    var mealCount by remember { mutableIntStateOf(0) }
    var teaCount by remember { mutableIntStateOf(0) }
    var contributionAmount by remember { mutableStateOf("") }
    
    var isCustomMeal by remember { mutableStateOf(false) }
    var isCustomTea by remember { mutableStateOf(false) }
    var isCustomMoney by remember { mutableStateOf(true) } 

    // Load existing (Future: when editing specific date, pass date)
    // For now, always editing "Today" or creating new
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
            isCustomMeal = it.mealsCount > 5
            isCustomTea = it.teasCount > 5
            isCustomMoney = it.moneyAmount.toInt() !in listOf(1000, 1500, 2000, 2500, 3000)
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
                mealCount = 0
                teaCount = 0
                contributionAmount = ""
            })

            // Date Strip
            DateStrip()

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
                        selectedCount = if (isCustomMeal) -1 else mealCount,
                        onCountSelected = { 
                            mealCount = it
                            isCustomMeal = false
                        },
                        onCustomClick = { isCustomMeal = true }
                    )
                    if (isCustomMeal) {
                        CustomInput(
                            value = if (mealCount > 5) mealCount.toString() else (if(mealCount > 0) mealCount.toString() else ""),
                            onValueChange = { mealCount = it.toIntOrNull() ?: 0 },
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
                        selectedCount = if (isCustomTea) -1 else teaCount,
                        onCountSelected = { 
                            teaCount = it 
                            isCustomTea = false
                        },
                        onCustomClick = { isCustomTea = true }
                    )
                     if (isCustomTea) {
                        CustomInput(
                            value = if (teaCount > 5) teaCount.toString() else (if(teaCount > 0) teaCount.toString() else ""),
                            onValueChange = { teaCount = it.toIntOrNull() ?: 0 },
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
                        onAmountSelected = { 
                            contributionAmount = it.toString()
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
                
                Text(
                    text = "Records are automatically synced with your flatmates.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
                )
            }
        }
        
        // Bottom Save Button
        SaveButtonContainer(
            modifier = Modifier.align(Alignment.BottomCenter),
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
fun DateStrip() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        DateItem(day = "Mon", date = "21")
        DateItem(day = "Tue", date = "22")
        DateItem(day = "Today", date = "23", isSelected = true)
        DateItem(day = "Thu", date = "24", alpha = 0.6f)
        DateItem(day = "Fri", date = "25", alpha = 0.6f)
        
        // Calendar Button
        Box(
            modifier = Modifier
                .size(width = 56.dp, height = 72.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(MaterialTheme.colorScheme.surface)
                .border(1.dp, MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(16.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Default.CalendarMonth, contentDescription = null, tint = Blue500)
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
    selectedCount: Int,
    onCountSelected: (Int) -> Unit,
    onCustomClick: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        (0..5).forEach { count ->
             val isSelected = selectedCount == count
             val bgColor = if (isSelected) Blue500 else MaterialTheme.colorScheme.background
             val textColor = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
             val fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium

             Box(
                 modifier = Modifier
                     .weight(1f)
                     .height(48.dp)
                     .clip(RoundedCornerShape(12.dp))
                     .background(bgColor)
                     .clickable { onCountSelected(count) }
                     .then(if (isSelected) Modifier else Modifier.border(1.dp, Color.Transparent, RoundedCornerShape(12.dp))), // Placeholder for border logic
                 contentAlignment = Alignment.Center
             ) {
                 Text(text = count.toString(), color = textColor, fontWeight = fontWeight, fontSize = 16.sp)
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
        // Grid layout manually for 3 items per row
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            amounts.take(3).forEach { amount ->
                 MoneyButton(amount = amount, isSelected = selectedAmount == amount, onClick = { onAmountSelected(amount) }, modifier = Modifier.weight(1f))
            }
        }
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            amounts.drop(3).forEach { amount ->
                 MoneyButton(amount = amount, isSelected = selectedAmount == amount, onClick = { onAmountSelected(amount) }, modifier = Modifier.weight(1f))
            }
            // Other Button
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
    ) {
         Button(
            onClick = onSave,
            modifier = Modifier.fillMaxWidth().height(56.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Blue500,
                contentColor = Color.White
            ),
            elevation = ButtonDefaults.buttonElevation(defaultElevation = 8.dp)
        ) {
            Icon(Icons.Default.Save, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = "Save Record", fontSize = 18.sp, fontWeight = FontWeight.Bold)
        }
    }
}
import androidx.compose.ui.graphics.graphicsLayer
