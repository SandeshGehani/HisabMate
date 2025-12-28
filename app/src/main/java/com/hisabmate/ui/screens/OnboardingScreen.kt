package com.hisabmate.ui.screens

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.LocalCafe
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.ShowChart
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hisabmate.ui.theme.SoftBlue
import com.hisabmate.ui.theme.SoftOrange
import kotlinx.coroutines.launch

data class OnboardingPage(
    val title: String,
    val description: String,
    val mainIcon: ImageVector,
    val subIcons: List<Pair<ImageVector, Color>> = emptyList()
)

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun OnboardingScreen(
    onFinishOnboarding: () -> Unit
) {
    val pages = listOf(
        OnboardingPage(
            title = "Track daily meals, teas, and money",
            description = "Effortlessly log your daily consumption and shared expenses in just a few taps. Keep your flat accounting transparent and simple.",
            mainIcon = Icons.Filled.Restaurant,
            subIcons = listOf(
                Icons.Filled.LocalCafe to Color(0xFF34A853),
                Icons.Filled.Payments to Color(0xFFFBBC04)
            )
        ),
        OnboardingPage(
            title = "Never Forget Entries",
            description = "Stay consistent with daily reminders and build your streak. Gamify your habits to ensure your records are always up to date.",
            mainIcon = Icons.Filled.LocalFireDepartment, // Fire for streak
            subIcons = listOf(
                 Icons.Filled.NotificationsActive to SoftBlue,
                 Icons.Filled.CalendarMonth to SoftOrange
            )

        ),
        OnboardingPage(
            title = "Clear monthly hisab without disputes.",
            description = "Simplify your end-of-month calculations. HisabMate tracks all contributions to provide a clear, fair, and dispute-free financial overview for everyone.",
            mainIcon = Icons.Filled.ShowChart,
            subIcons = emptyList()
        )
    )

    val pagerState = rememberPagerState(pageCount = { pages.size })
    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Top Bar with Skip
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 48.dp, end = 16.dp),
            contentAlignment = Alignment.CenterEnd
        ) {
            TextButton(onClick = onFinishOnboarding) {
                Text(
                    text = "Skip",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = FontWeight.Medium
                )
            }
        }

        // Pager Content
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.weight(1f)
        ) { pageIndex ->
            OnboardingPageContent(page = pages[pageIndex])
        }

        // Bottom Section
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
             // Page Indicators
            Row(
                modifier = Modifier.padding(bottom = 32.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                repeat(pages.size) { iteration ->
                    val color = if (pagerState.currentPage == iteration) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant
                    val width = if (pagerState.currentPage == iteration) 32.dp else 8.dp
                    Box(
                        modifier = Modifier
                            .padding(2.dp)
                            .clip(CircleShape)
                            .background(color)
                            .size(width = width, height = 8.dp)
                    )
                }
            }

            // Buttons
            if (pagerState.currentPage < pages.size - 1) {
                Button(
                    onClick = {
                        scope.launch {
                            pagerState.animateScrollToPage(pagerState.currentPage + 1)
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = CircleShape,
                ) {
                    Text(text = "Next", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(imageVector = Icons.Default.ArrowForward, contentDescription = null)
                }
            } else {
                 Button(
                    onClick = onFinishOnboarding,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = CircleShape
                ) {
                    Text(text = "Get Started", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun OnboardingPageContent(page: OnboardingPage) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        
        // Icon Visualization (Simplified version of HTML design)
        Box(
            modifier = Modifier
                .size(240.dp) // Container size
                .padding(bottom = 32.dp),
            contentAlignment = Alignment.Center
        ) {
             // Background blobs could go here
             Surface(
                 shape = CircleShape,
                 color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f),
                 modifier = Modifier.size(180.dp)
             ) {}

            // Main Icon
            Icon(
                imageVector = page.mainIcon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(80.dp)
            )
            
            // Sub Icons (Floating)
            if (page.subIcons.isNotEmpty()) {
                // Top Right
                 Surface(
                     shape = CircleShape,
                     shadowElevation = 4.dp,
                     modifier = Modifier.align(Alignment.TopEnd).offset(x = (-20).dp, y = 20.dp)
                 ) {
                     Icon(
                         imageVector = page.subIcons[0].first,
                         contentDescription = null,
                         tint = page.subIcons[0].second,
                         modifier = Modifier.padding(12.dp).size(24.dp)
                     )
                 }

                 if (page.subIcons.size > 1) {
                     // Bottom Left
                     Surface(
                         shape = CircleShape,
                         shadowElevation = 4.dp,
                         modifier = Modifier.align(Alignment.BottomStart).offset(x = 20.dp, y = (-40).dp)
                     ) {
                          Icon(
                             imageVector = page.subIcons[1].first,
                             contentDescription = null,
                             tint = page.subIcons[1].second,
                             modifier = Modifier.padding(12.dp).size(24.dp)
                         )
                     }
                 }
            }
        }

        Text(
            text = page.title,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = page.description,
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
