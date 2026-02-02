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
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.LocalCafe
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.platform.LocalUriHandler
import com.hisabmate.ui.theme.*
import com.hisabmate.viewmodel.SummaryViewModel

@Composable
fun SummaryScreen(
    viewModel: SummaryViewModel,
    onBack: () -> Unit = {},
    onUpdateTheme: () -> Unit = {}
) {
    val mealRate by viewModel.mealRate.collectAsState()
    val teaRate by viewModel.teaRate.collectAsState()
    val uriHandler = LocalUriHandler.current
    val monthlyGoal by viewModel.monthlyGoal.collectAsState()
    val rentAmount by viewModel.rentAmount.collectAsState()
    
    val totalMeals by viewModel.totalMeals.collectAsState()
    val totalTeas by viewModel.totalTeas.collectAsState()
    
    val mealCost by viewModel.mealCost.collectAsState()
    val teaCost by viewModel.teaCost.collectAsState()
    val finalAmount by viewModel.finalAmount.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
        ) {
            // Header
            SummaryHeader(onBack = onBack, onSave = { viewModel.saveSummary() })

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                // Status Card
                val monthName = java.time.Month.of(selectedMonth).name.lowercase().replaceFirstChar { it.uppercase() }
                StatusCard(monthName = monthName)

                // Theme Settings
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        text = "App Settings",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp).fillMaxWidth().clickable { onUpdateTheme() },
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Dark Mode", fontWeight = FontWeight.Medium)
                            Switch(checked = MaterialTheme.colorScheme.surface == Color(0xFF1E293B) || MaterialTheme.colorScheme.surface == Color.Black, onCheckedChange = { onUpdateTheme() })
                        }
                    }
                }

                // Set Rates
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        text = "Set Rates",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        RateInput(
                            label = "Meals",
                            value = mealRate.toInt().toString(),
                            onValueChange = { viewModel.updateMealRate(it.toDoubleOrNull() ?: 0.0) },
                            modifier = Modifier.weight(1f)
                        )
                        RateInput(
                            label = "Teas",
                            value = teaRate.toInt().toString(),
                            onValueChange = { viewModel.updateTeaRate(it.toDoubleOrNull() ?: 0.0) },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                // App Configuration (Goal & Rent)
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        text = "Contribution Goals",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        RateInput(
                            label = "Goal",
                            value = monthlyGoal.toInt().toString(),
                            onValueChange = { viewModel.updateMonthlyGoal(it.toDoubleOrNull() ?: 0.0) },
                            modifier = Modifier.weight(1f)
                        )
                        RateInput(
                            label = "Rent",
                            value = rentAmount.toInt().toString(),
                            onValueChange = { viewModel.updateRentAmount(it.toDoubleOrNull() ?: 0.0) },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                // Breakdown Card
                BreakdownCard(
                    totalMeals = totalMeals,
                    mealRate = mealRate,
                    mealCost = mealCost,
                    totalTeas = totalTeas,
                    teaRate = teaRate,
                    teaCost = teaCost
                )

                // Socials Section
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        text = "Help & Feedback",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Column(modifier = Modifier.padding(vertical = 8.dp)) {
                            SocialItem(
                                icon = Icons.Default.CheckCircle,
                                label = "Report Bugs & Feedback",
                                value = "sandeshgehani18@gmail.com",
                                onClick = { uriHandler.openUri("mailto:sandeshgehani18@gmail.com") }
                            )
                            Divider(modifier = Modifier.padding(horizontal = 16.dp), color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                            SocialItem(
                                icon = Icons.Default.Restaurant,
                                label = "Instagram",
                                value = "sandeshgehanii",
                                onClick = { uriHandler.openUri("https://www.instagram.com/sandeshgehanii/") }
                            )
                            Divider(modifier = Modifier.padding(horizontal = 16.dp), color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                            SocialItem(
                                icon = Icons.Default.Share,
                                label = "LinkedIn",
                                value = "Sandesh Gehani",
                                onClick = { uriHandler.openUri("https://www.linkedin.com/in/sandeshgehani/") }
                            )
                            Divider(modifier = Modifier.padding(horizontal = 16.dp), color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                            SocialItem(
                                icon = Icons.Default.Share, 
                                label = "GitHub",
                                value = "SandeshGehani",
                                onClick = { uriHandler.openUri("https://github.com/SandeshGehani") }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(160.dp))
            }
        }
        
        // Footer (Fixed at bottom)
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.background)
                .padding(16.dp)
                .navigationBarsPadding(),
             verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Divider(color = MaterialTheme.colorScheme.surfaceVariant)
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                Text(
                    text = "PKR ${finalAmount.toInt()}",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            Button(
                onClick = { /* Share Logic */ },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Orange500)
            ) {
                Icon(Icons.Default.Share, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Finalize & Share", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun SummaryHeader(onBack: () -> Unit, onSave: () -> Unit) {
    val currentDate = java.time.LocalDate.now()
    val monthName = currentDate.month.name.lowercase().replaceFirstChar { it.uppercase() }
    
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        IconButton(
            onClick = onBack,
            modifier = Modifier
                .size(40.dp)
                .background(Color.Transparent, CircleShape)
        ) {
            Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = MaterialTheme.colorScheme.onBackground)
        }
        
        Text(
            text = "$monthName Summary",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )
        
        TextButton(onClick = onSave) {
            Text("Save", fontWeight = FontWeight.Bold, color = Blue500)
        }
    }
}

@Composable
fun StatusCard(monthName: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Box {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .background(Orange500.copy(alpha = 0.1f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Orange500)
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text("Monthly Record", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                    Text("Reviewing data for $monthName.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
            // Decorative Blur Blob (Simplified as Circle)
            Box(
                 modifier = Modifier
                     .align(Alignment.TopEnd)
                     .offset(x = 10.dp, y = (-10).dp)
                     .size(60.dp)
                     .background(Orange500.copy(alpha = 0.1f), CircleShape)
            )
        }
    }
}

@Composable
fun RateInput(label: String, value: String, onValueChange: (String) -> Unit, modifier: Modifier = Modifier) {
    Column(modifier = modifier) {
        Text(label, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Medium, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.padding(bottom = 8.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(MaterialTheme.colorScheme.surface)
                .border(1.dp, MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(12.dp)),
            contentAlignment = Alignment.CenterStart
        ) {
            BasicTextField(
                value = value,
                onValueChange = onValueChange,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                textStyle = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface 
                ),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )
            Text("PKR", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.align(Alignment.CenterEnd).padding(end = 16.dp))
        }
    }
}

@Composable
fun BreakdownCard(
    totalMeals: Double, mealRate: Double, mealCost: Double,
    totalTeas: Double, teaRate: Double, teaCost: Double
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Meals Row
            Row(
                modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(modifier = Modifier.size(40.dp).background(SoftOrangeLight, RoundedCornerShape(8.dp)), contentAlignment = Alignment.Center) {
                        Icon(Icons.Default.Restaurant, contentDescription = null, tint = Orange500, modifier = Modifier.size(20.dp))
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text("Total Meals", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text("${totalMeals.toString().removeSuffix(".0")} count × PKR ${mealRate.toInt()}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f))
                    }
                }
                Text("PKR ${mealCost.toInt()}", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
            }
            
            Divider(color = MaterialTheme.colorScheme.surfaceVariant, modifier = Modifier.padding(bottom = 16.dp))
            
            // Tea Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(modifier = Modifier.size(40.dp).background(SoftIndigoLight, RoundedCornerShape(8.dp)), contentAlignment = Alignment.Center) {
                        Icon(Icons.Default.LocalCafe, contentDescription = null, tint = Purple500, modifier = Modifier.size(20.dp))
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text("Total Tea", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium, color = MaterialTheme.colorScheme.onBackground)
                        Text("${totalTeas.toString().removeSuffix(".0")} count × PKR ${teaRate.toInt()}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f))
                    }
                }
                Text("PKR ${teaCost.toInt()}", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
            }
        }
    }
}

@Composable
fun SocialItem(
    icon: ImageVector,
    label: String,
    value: String, // Defining 'value' here fixes the error
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.width(16.dp))
        Column {
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = value,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

