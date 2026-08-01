package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.NewReleases
import androidx.compose.material.icons.filled.Whatshot
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AiTool
import com.example.data.model.Language
import com.example.data.model.ToolCategory
import com.example.ui.components.HeroSection
import com.example.ui.components.ToolCard
import com.example.ui.components.getMaterialIconByName
import com.example.ui.theme.PrimaryCyan
import com.example.ui.theme.PrimaryIndigo

@Composable
fun HomeScreen(
    language: Language,
    searchQuery: String,
    selectedCategory: String?,
    selectedFilterTag: String,
    categories: List<ToolCategory>,
    tools: List<AiTool>,
    favoriteIds: List<String>,
    onSearchChange: (String) -> Unit,
    onCategorySelect: (String?) -> Unit,
    onFilterTagSelect: (String) -> Unit,
    onToolSelect: (AiTool) -> Unit,
    onFavoriteToggle: (String) -> Unit,
    onOpenHistory: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isBangla = language == Language.BN

    Column(
        modifier = modifier
            .fillMaxSize()
            .testTag("home_screen")
    ) {
        // Hero & Live Search
        HeroSection(
            language = language,
            searchQuery = searchQuery,
            onSearchQueryChange = onSearchChange,
            totalToolsCount = 100
        )

        // LazyGrid with categories and tools
        LazyVerticalGrid(
            columns = GridCells.Adaptive(minSize = 280.dp),
            contentPadding = PaddingValues(bottom = 24.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
        ) {
            // Section 1: Quick Filter Tags Bar
            item(span = { GridItemSpan(maxLineSpan) }) {
                Column(modifier = Modifier.padding(vertical = 8.dp)) {
                    Text(
                        text = if (isBangla) "ফিল্টার সমূহ" else "Quick Filters",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        item {
                            FilterChipTag(
                                label = if (isBangla) "সব টুলস (১০০)" else "All Tools (100)",
                                selected = selectedFilterTag == "all" && selectedCategory == null,
                                onClick = {
                                    onCategorySelect(null)
                                    onFilterTagSelect("all")
                                },
                                icon = Icons.Default.Category,
                                testTag = "filter_all"
                            )
                        }
                        item {
                            FilterChipTag(
                                label = if (isBangla) "জনপ্রিয়" else "Popular",
                                selected = selectedFilterTag == "popular",
                                onClick = { onFilterTagSelect("popular") },
                                icon = Icons.Default.AutoAwesome,
                                testTag = "filter_popular"
                            )
                        }
                        item {
                            FilterChipTag(
                                label = if (isBangla) "ট্রেন্ডিং" else "Trending",
                                selected = selectedFilterTag == "trending",
                                onClick = { onFilterTagSelect("trending") },
                                icon = Icons.Default.Whatshot,
                                testTag = "filter_trending"
                            )
                        }
                        item {
                            FilterChipTag(
                                label = if (isBangla) "নতুন" else "Newest",
                                selected = selectedFilterTag == "newest",
                                onClick = { onFilterTagSelect("newest") },
                                icon = Icons.Default.NewReleases,
                                testTag = "filter_newest"
                            )
                        }
                        item {
                            FilterChipTag(
                                label = if (isBangla) "ফেভারিট (${favoriteIds.size})" else "Favorites (${favoriteIds.size})",
                                selected = selectedFilterTag == "favorites",
                                onClick = { onFilterTagSelect("favorites") },
                                icon = Icons.Default.Favorite,
                                testTag = "filter_favorites"
                            )
                        }
                        item {
                            FilterChipTag(
                                label = if (isBangla) "ইতিহাস" else "History",
                                selected = false,
                                onClick = onOpenHistory,
                                icon = Icons.Default.History,
                                testTag = "filter_history"
                            )
                        }
                    }
                }
            }

            // Section 2: Horizontal Category Selector
            item(span = { GridItemSpan(maxLineSpan) }) {
                Column(modifier = Modifier.padding(bottom = 8.dp)) {
                    Text(
                        text = if (isBangla) "ক্যাটাগরি সমূহ (১০টি)" else "Categories (10)",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(categories) { cat ->
                            val isSelected = selectedCategory == cat.id
                            val catName = if (isBangla) cat.nameBn else cat.nameEn

                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = if (isSelected) PrimaryIndigo else MaterialTheme.colorScheme.surface,
                                tonalElevation = if (isSelected) 4.dp else 1.dp,
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .clickable {
                                        if (isSelected) onCategorySelect(null) else onCategorySelect(cat.id)
                                    }
                                    .testTag("category_chip_${cat.id}")
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = getMaterialIconByName(cat.iconName),
                                        contentDescription = null,
                                        tint = if (isSelected) Color.White else PrimaryIndigo,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = catName,
                                        style = MaterialTheme.typography.labelMedium.copy(
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                                        ),
                                        color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Section 3: Tool List Title Header
            item(span = { GridItemSpan(maxLineSpan) }) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp, bottom = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (isBangla) "এআই টুলস (${tools.size})" else "AI Tools (${tools.size})",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }

            // Empty Search / Filter State
            if (tools.isEmpty()) {
                item(span = { GridItemSpan(maxLineSpan) }) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (isBangla) "কোনো এআই টুল পাওয়া যায়নি।" else "No AI tools found.",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            } else {
                // Section 4: 100 Tool Cards Grid Items
                items(tools, key = { it.id }) { tool ->
                    ToolCard(
                        tool = tool,
                        language = language,
                        isFavorite = favoriteIds.contains(tool.id),
                        onFavoriteToggle = { onFavoriteToggle(tool.id) },
                        onClick = { onToolSelect(tool) }
                    )
                }
            }
        }
    }
}

@Composable
private fun FilterChipTag(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    testTag: String
) {
    FilterChip(
        selected = selected,
        onClick = onClick,
        label = { Text(text = label, fontWeight = FontWeight.SemiBold, fontSize = 12.sp) },
        leadingIcon = {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(14.dp)
            )
        },
        colors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = PrimaryIndigo,
            selectedLabelColor = Color.White,
            selectedLeadingIconColor = Color.White
        ),
        shape = CircleShape,
        modifier = Modifier.testTag(testTag)
    )
}
