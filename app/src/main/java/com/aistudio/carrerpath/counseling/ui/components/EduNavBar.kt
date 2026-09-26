package com.aistudio.carrerpath.counseling.ui.components

import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aistudio.carrerpath.counseling.ui.viewmodel.AppNavTab
import com.aistudio.carrerpath.counseling.ui.theme.EduBluePrimary

data class NavItem(
    val tab: AppNavTab,
    val title: String,
    val titleKn: String,
    val icon: ImageVector
)

@Composable
fun EduNavBar(
    currentTab: AppNavTab,
    isAdmin: Boolean,
    currentLanguage: String,
    onTabSelected: (AppNavTab) -> Unit
) {
    val isKn = currentLanguage == "kn"

    val items = if (isAdmin) {
        listOf(
            NavItem(AppNavTab.COURSES, "Courses", "ಕೋರ್ಸ್‌ಗಳು", Icons.Default.MenuBook),
            NavItem(AppNavTab.COLLEGES, "Colleges", "ಕಾಲೇಜುಗಳು", Icons.Default.School),
            NavItem(AppNavTab.CAREERS, "Careers", "ಉದ್ಯೋಗಗಳು", Icons.Default.Work),
            NavItem(AppNavTab.ADMIN, "Admin Panel", "ಅಡ್ಮಿನ್", Icons.Default.AdminPanelSettings),
            NavItem(AppNavTab.DASHBOARD, "Dashboard", "ಡ್ಯಾಶ್‌ಬೋರ್ಡ್", Icons.Default.Dashboard)
        )
    } else {
        listOf(
            NavItem(AppNavTab.HOME, "Home", "ಮುಖಪುಟ", Icons.Default.Home),
            NavItem(AppNavTab.CAREERS, "Careers", "ಉದ್ಯೋಗಗಳು", Icons.Default.Work),
            NavItem(AppNavTab.COLLEGES, "Colleges", "ಕಾಲೇಜುಗಳು", Icons.Default.School),
            NavItem(AppNavTab.COURSES, "Courses", "ಕೋರ್ಸ್‌ಗಳು", Icons.Default.MenuBook),
            NavItem(AppNavTab.DASHBOARD, "Dashboard", "ಡ್ಯಾಶ್‌ಬೋರ್ಡ್", Icons.Default.Dashboard)
        )
    }

    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = 8.dp
    ) {
        items.forEach { item ->
            val isSelected = currentTab == item.tab
            val labelText = if (isKn) item.titleKn else item.title

            NavigationBarItem(
                selected = isSelected,
                onClick = { onTabSelected(item.tab) },
                icon = {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = labelText,
                        modifier = Modifier.size(22.dp)
                    )
                },
                label = {
                    Text(
                        text = labelText,
                        fontSize = 11.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = EduBluePrimary,
                    selectedTextColor = EduBluePrimary,
                    indicatorColor = MaterialTheme.colorScheme.primaryContainer,
                    unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                    unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
                )
            )
        }
    }
}
