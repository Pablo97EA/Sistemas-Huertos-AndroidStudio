package com.moviles.agrocity.ui.screens

import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.Color


@Composable
fun TabSelector(
    selectedIndex: Int,
    onTabSelected: (Int) -> Unit,
    tabTitles: List<String>,
    tabIcons: List<ImageVector>
) {
    ScrollableTabRow(
        selectedTabIndex = selectedIndex,
        edgePadding = 12.dp,
        containerColor = Color(0xFFB9FBC0),
        contentColor = Color(0xFF1B4332),
        indicator = { tabPositions ->
            TabRowDefaults.Indicator(
                modifier = Modifier.tabIndicatorOffset(tabPositions[selectedIndex]),
                color = Color(0xFF2D6A4F)
            )
        }

    ) {
        tabTitles.forEachIndexed { index, title ->
            Tab(
                selected = selectedIndex == index,
                onClick = { onTabSelected(index) },
                text = { Text(text = title) },
                icon = {
                    Icon(
                        imageVector = tabIcons[index],
                        contentDescription = title
                    )
                }
            )
        }
    }
}
