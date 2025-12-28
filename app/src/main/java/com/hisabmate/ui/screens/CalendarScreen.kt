package com.hisabmate.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.LocalCafe
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hisabmate.data.local.entities.DailyRecord
import com.hisabmate.ui.theme.*
import com.hisabmate.viewmodel.CalendarViewModel
import com.hisabmate.utils.DateUtils
import java.time.LocalDate

@Composable
fun CalendarScreen(
    viewModel: CalendarViewModel,
    onNavigateToEntry: (Long) -> Unit = {}
) {
    val records by viewModel.monthlyRecords.collectAsState(initial = emptyList())
    
    // Map records by date for easy lookup by day
    val recordsByDay = records.associateBy { record ->
        java.time.Instant.ofEpochMilli(record.date).atZone(java.time.ZoneId.systemDefault()).dayOfMonth
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            CalendarHeader()
            
            DaysOfWeekHeader()

            CalendarGrid(records = recordsByDay, onDateClick = onNavigateToEntry)
        }

        // Bottom Sheet / Floating Summary (Simplified for now - using real total counts from list)
        val totalMeals = records.sumOf { it.mealsCount }
        val totalTeas = records.sumOf { it.teasCount }
        val totalSpent = records.sumOf { it.moneyAmount }

        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 72.dp) // Space for nav bar
                .padding(horizontal = 16.dp)
        ) {
            MonthlySummaryCard(
                meals = totalMeals,
                teas = totalTeas,
                spent = totalSpent,
                onAddRecord = onNavigateToEntry
            )
        }

        // Bottom Nav Bar
            }
            Text(
                text = "CURRENT MONTH",
                style = MaterialTheme.typography.labelSmall,
                color = Blue500,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )
        }

        IconButton(onClick = {}, modifier = Modifier.size(40.dp)) {
            Icon(Icons.Default.ChevronRight, contentDescription = "Next", tint = MaterialTheme.colorScheme.onSurface)
        }
    }
}

@Composable
fun DaysOfWeekHeader() {
    Row(modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)) {
        val days = listOf("S", "M", "T", "W", "T", "F", "S")
        days.forEach { day ->
            Text(
                text = day,
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun CalendarGrid(records: Map<Int, DailyRecord>, onDateClick: () -> Unit) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(7),
        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Empty cells for offset (assuming month starts on Tuesday for demo)
        items(2) {
            Box(modifier = Modifier.aspectRatio(0.8f))
        }

        // Days 1-31
        items(31) { index ->
            val day = index + 1
            val record = records[day]
            val date = java.time.LocalDate.now().withDayOfMonth(day)
            val dateMillis = date.atStartOfDay(java.time.ZoneId.systemDefault()).toInstant().toEpochMilli()
            DayCell(day = day, record = record, onClick = { onDateClick(dateMillis) })
        }
        
        // Spacer for bottom content
        items(7) { 
             Spacer(modifier = Modifier.height(180.dp))
        }
    }
}

@Composable
fun DayCell(day: Int, record: DailyRecord?, onClick: () -> Unit) {
    
    val date = java.time.LocalDate.now()
    val isToday = day == date.dayOfMonth // Simple check for current month demo
    
    // Warning logic: Example if user missed logging
    // For now, no implicit warning.
    val isWarning = false 
    val hasData = record != null
    
    val bgColor = when {
        isToday -> Blue500
        isWarning -> Red50 
        hasData -> Blue500.copy(alpha = 0.1f)
        else -> MaterialTheme.colorScheme.surface
    }
    
    val borderColor = when {
        isToday -> Blue500
        isWarning -> Red500.copy(alpha = 0.3f)
        hasData -> Blue500.copy(alpha = 0.3f)
        else -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
    }

    val textColor = if (isToday) Color.White else if (isWarning) Red500 else MaterialTheme.colorScheme.onSurface

    Box(
        modifier = Modifier
            .aspectRatio(0.8f)
            .clip(RoundedCornerShape(12.dp))
            .background(bgColor)
            .border(1.dp, borderColor, RoundedCornerShape(12.dp))
            .clickable { onClick() }
            .padding(6.dp)
    ) {
        Text(
            text = day.toString(),
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            color = textColor,
            modifier = Modifier.align(Alignment.TopStart)
        )
        
        Column(
            modifier = Modifier.align(Alignment.BottomStart).fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            if (isToday) {
                 // Nothing specific for today's content layout in this placeholder
            } else if (hasData) {
                if (record!!.mealsCount > 0) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                        Icon(Icons.Default.Restaurant, contentDescription = null, tint = Orange500, modifier = Modifier.size(10.dp))
                        Text(record.mealsCount.toString(), fontSize = 8.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
                if (record.teasCount > 0) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                        Icon(Icons.Default.LocalCafe, contentDescription = null, tint = Purple500, modifier = Modifier.size(10.dp))
                        Text(record.teasCount.toString(), fontSize = 8.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
                if (record.moneyAmount > 0) {
                     Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                         Icon(Icons.Default.AttachMoney, contentDescription = null, tint = Blue500, modifier = Modifier.size(10.dp))
                         Text(record.moneyAmount.toInt().toString(), fontSize = 8.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            } 
        }
    }
}

@Composable
fun MonthlySummaryCard(meals: Int, teas: Int, spent: Double, onAddRecord: () -> Unit) {
    Card(
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                 Text(
                    text = "MONTHLY SUMMARY",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                 )
                 Text(
                     text = "View Details",
                     style = MaterialTheme.typography.labelSmall,
                     fontWeight = FontWeight.Bold,
                     color = Blue500
                 )
            }
            
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                SummaryItem(count = meals.toString(), label = "Meals", icon = Icons.Default.Restaurant, iconTint = Orange500, modifier = Modifier.weight(1f))
                SummaryItem(count = teas.toString(), label = "Teas", icon = Icons.Default.LocalCafe, iconTint = Purple500, modifier = Modifier.weight(1f))
                SummaryItem(count = "Rs." + spent.toInt().toString(), label = "Spent", icon = Icons.Default.AttachMoney, iconTint = Blue500, modifier = Modifier.weight(1f))
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Button(
                onClick = onAddRecord,
                modifier = Modifier.fillMaxWidth().height(48.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Blue500)
            ) {
                Icon(Icons.Default.AddCircle, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Add Record for Today", fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun SummaryItem(count: String, label: String, icon: ImageVector, iconTint: Color, modifier: Modifier) {
    Column(
        modifier = modifier
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
            .border(1.dp, MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
            .padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = count, fontWeight = FontWeight.Bold, fontSize = 20.sp, color = MaterialTheme.colorScheme.onSurface)
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(top = 4.dp)) {
            Icon(icon, contentDescription = null, tint = iconTint, modifier = Modifier.size(12.dp))
            Spacer(modifier = Modifier.width(2.dp))
            Text(text = label, style = MaterialTheme.typography.labelSmall, fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
fun BottomNavBar(modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shadowElevation = 16.dp,
        color = MaterialTheme.colorScheme.surface
    ) {
        Row(
            modifier = Modifier.padding(vertical = 12.dp).navigationBarsPadding(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
             NavItem(icon = Icons.Default.CalendarToday, label = "Calendar", isSelected = true)
             NavItem(icon = Icons.Default.BarChart, label = "Stats", isSelected = false)
             NavItem(icon = Icons.Default.Settings, label = "Settings", isSelected = false)
        }
    }
}

@Composable
fun NavItem(icon: ImageVector, label: String, isSelected: Boolean) {
    val color = if (isSelected) Blue500 else MaterialTheme.colorScheme.onSurfaceVariant
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(icon, contentDescription = label, tint = color)
        Text(text = label, style = MaterialTheme.typography.labelSmall, fontSize = 10.sp, color = color, fontWeight = FontWeight.Bold)
    }
}
