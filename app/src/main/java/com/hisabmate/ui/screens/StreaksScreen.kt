package com.hisabmate.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Savings
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hisabmate.ui.theme.*
import com.hisabmate.viewmodel.StreaksViewModel

@Composable
fun StreaksScreen(
    viewModel: StreaksViewModel,
    onBack: () -> Unit = {}
) {
    val streak by viewModel.streak.collectAsState()
    val monthEntries by viewModel.currentMonthEntries.collectAsState()
    val totalEntries by viewModel.totalEntries.collectAsState()
    val consistency by viewModel.consistency.collectAsState()
    val earnedBadges by viewModel.earnedBadges.collectAsState()

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
            StreaksHeader(onBack = onBack)

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                // Hero Card
                HeroStreakCard(
                    currentStreak = streak?.currentStreak ?: 0
                )

                // Recent Activity
                RecentActivitySection()

                // Statistics
                StatisticsSection(
                    bestStreak = streak?.bestStreak ?: 0,
                    totalEntries = totalEntries,
                    monthEntries = monthEntries,
                    consistency = consistency
                )
                
                // Badges
                MonthlyBadgesSection(badges = earnedBadges)
                
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}

@Composable
fun StreaksHeader(onBack: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(
            onClick = onBack,
            modifier = Modifier.size(40.dp).background(MaterialTheme.colorScheme.onBackground.copy(alpha = 0.05f), CircleShape)
        ) {
            Icon(Icons.Default.ArrowBackIosNew, contentDescription = "Back", modifier = Modifier.size(20.dp), tint = MaterialTheme.colorScheme.onBackground)
        }
        
        Text(
            text = "Achievements",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )
        
         IconButton(
            onClick = { /* TODO */ },
            modifier = Modifier.size(40.dp).background(MaterialTheme.colorScheme.onBackground.copy(alpha = 0.05f), CircleShape)
        ) {
            Icon(Icons.Default.MoreHoriz, contentDescription = "More", tint = MaterialTheme.colorScheme.onBackground)
        }
    }
}

@Composable
fun HeroStreakCard(currentStreak: Int) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Box(modifier = Modifier.padding(24.dp).fillMaxWidth()) {
            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                Box(contentAlignment = Alignment.BottomEnd) {
                    Box(
                        modifier = Modifier
                            .size(96.dp)
                            .border(4.dp, Blue500.copy(alpha = 0.2f), CircleShape)
                            .background(MaterialTheme.colorScheme.background, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.LocalFireDepartment, 
                            contentDescription = null, 
                            tint = Blue500,
                            modifier = Modifier.size(48.dp)
                        )
                    }
                    Surface(
                        color = Blue500,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.offset(x = 4.dp, y = 4.dp).border(2.dp, MaterialTheme.colorScheme.surface, RoundedCornerShape(12.dp))
                    ) {
                        Text(
                            text = "Hot", 
                            style = MaterialTheme.typography.labelSmall, 
                            fontWeight = FontWeight.Bold, 
                            color = Color.White,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Text(
                    text = "$currentStreak Days",
                    style = MaterialTheme.typography.displayMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                
                Text(
                    text = "Current Streak",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = Blue500,
                    modifier = Modifier.padding(top = 4.dp)
                )
                
                Spacer(modifier = Modifier.height(12.dp))
                
                Text(
                    text = "You're on fire! Log an expense today to reach ${currentStreak + 1} days.",
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                
                Spacer(modifier = Modifier.height(24.dp))
                
                Button(
                    onClick = { /* TODO: Nav to Add */ },
                    modifier = Modifier.fillMaxWidth(0.8f).height(48.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Blue500),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Log Today's Entry", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun RecentActivitySection() {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Recent Activity", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleSmall)
            Text("Last 7 Days", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        
        Card(
             modifier = Modifier.fillMaxWidth(),
             shape = RoundedCornerShape(16.dp),
             colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
             border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
        ) {
            Row(
                modifier = Modifier.padding(16.dp).fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Mock Week Data
                val days = listOf("M", "T", "W", "T", "F", "S", "S")
                val status = listOf(true, true, true, false, true, true, false) // 4th is false (T), 7th is Today (S)
                
                days.forEachIndexed { index, day ->
                     DayStatusItem(day = day, checked = status[index], isToday = index == 6)
                }
            }
        }
    }
}

@Composable
fun DayStatusItem(day: String, checked: Boolean, isToday: Boolean) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(day, style = MaterialTheme.typography.labelSmall, color = if(isToday) Blue500 else MaterialTheme.colorScheme.onSurfaceVariant, fontWeight = FontWeight.Bold)
        
        if (isToday) {
             Box(
                modifier = Modifier
                    .size(32.dp)
                    .border(2.dp, MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(8.dp)),
                contentAlignment = Alignment.Center
            ) {
                 Text("Today", fontSize = 8.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        } else if (checked) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .background(Blue500.copy(alpha = 0.2f), RoundedCornerShape(8.dp)),
                contentAlignment = Alignment.Center
            ) {
                 Icon(Icons.Default.Check, contentDescription = null, tint = Blue500, modifier = Modifier.size(16.dp))
            }
        } else {
             Box(
                modifier = Modifier
                    .size(32.dp)
                    .background(MaterialTheme.colorScheme.background, RoundedCornerShape(8.dp)),
                contentAlignment = Alignment.Center
            ) {
                 Box(modifier = Modifier.size(6.dp).background(MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha=0.3f), CircleShape))
            }
        }
    }
}

@Composable
fun StatisticsSection(bestStreak: Int, totalEntries: Int, monthEntries: Int, consistency: Int) {
    Column {
        Text("Statistics", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(vertical = 12.dp, horizontal = 4.dp))
        
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                StatCard(
                    label = "Best Streak", 
                    value = "$bestStreak Days", 
                    icon = Icons.Default.EmojiEvents, 
                    iconBg = Color(0xFFFFEDD5), 
                    iconTint = Color(0xFFEA580C), 
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    label = "Total Entries", 
                    value = totalEntries.toString(), 
                    icon = Icons.Default.ReceiptLong, 
                    iconBg = Color(0xFFDBEAFE), 
                    iconTint = Color(0xFF2563EB), 
                    modifier = Modifier.weight(1f)
                )
            }
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                val currentMonth = java.time.LocalDate.now().month.name.lowercase().replaceFirstChar { it.uppercase() }.substring(0, 3)
                StatCard(
                    label = "Entries ($currentMonth)", 
                    value = monthEntries.toString(), 
                    icon = Icons.Default.CalendarMonth, 
                    iconBg = Color(0xFFF3E8FF), 
                    iconTint = Color(0xFF9333EA), 
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    label = "Consistency", 
                    value = "$consistency%", 
                    icon = Icons.Default.TrendingUp, 
                    iconBg = Color(0xFFDCFCE7), 
                    iconTint = Color(0xFF16A34A), 
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
fun StatCard(label: String, value: String, icon: ImageVector, iconBg: Color, iconTint: Color, modifier: Modifier) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                 Box(modifier = Modifier.size(24.dp).background(iconBg, RoundedCornerShape(6.dp)), contentAlignment = Alignment.Center) {
                     Icon(icon, contentDescription = null, tint = iconTint, modifier = Modifier.size(16.dp))
                 }
                Text(label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 1)
            }
            Text(value, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
        }
    }
}

@Composable
fun MonthlyBadgesSection(badges: List<com.hisabmate.data.local.entities.MonthlySummary>) {
     Column {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Monthly Badges", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
            Text("View All", style = MaterialTheme.typography.labelSmall, color = Blue500, fontWeight = FontWeight.Bold)
        }
        
        Row(
            modifier = Modifier.horizontalScroll(rememberScrollState()).padding(horizontal = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            if (badges.isEmpty()) {
                // Show placeholder locked badges
                BadgeCard(title = "Complete Month", subtitle = "Locked", icon = Icons.Default.Lock, color = Color.Gray, isLocked = true)
                BadgeCard(title = "Perfect Streak", subtitle = "Locked", icon = Icons.Default.Lock, color = Color.Gray, isLocked = true)
            } else {
                badges.forEach { summary ->
                    val monthName = java.time.Month.of(summary.month).name.lowercase().replaceFirstChar { it.uppercase() }
                    BadgeCard(
                        title = "Complete Month", 
                        subtitle = "$monthName ${summary.year}", 
                        icon = Icons.Default.Verified, 
                        color = Orange500
                    )
                }
            }
        }
     }
}

@Composable
fun BadgeCard(title: String, subtitle: String, icon: ImageVector, color: Color, isLocked: Boolean = false) {
    Card(
        modifier = Modifier.width(140.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = androidx.compose.foundation.BorderStroke(1.dp, if(isLocked) MaterialTheme.colorScheme.surfaceVariant else color.copy(alpha = 0.3f))
    ) {
        Column(
            modifier = Modifier.padding(16.dp).fillMaxWidth(), 
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .background(if(isLocked) MaterialTheme.colorScheme.surfaceVariant else color.copy(alpha = 0.1f), CircleShape)
                    .border(1.dp, if(isLocked) Color.Transparent else color.copy(alpha = 0.2f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = if(isLocked) Color.Gray else color, modifier = Modifier.size(32.dp))
            }
            
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(title, style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold, color = if(isLocked) Color.Gray else MaterialTheme.colorScheme.onSurface, textAlign = androidx.compose.ui.text.style.TextAlign.Center)
                Text(subtitle, style = MaterialTheme.typography.labelSmall, fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}
