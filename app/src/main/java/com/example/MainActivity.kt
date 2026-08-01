package com.example

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.example.ui.components.InfoDialog
import com.example.ui.components.TopNavbar
import com.example.ui.screens.HistoryScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.ToolDetailScreen
import com.example.ui.theme.BanglaAiTheme
import com.example.ui.viewmodel.MainViewModel

enum class Screen {
    HOME,
    TOOL_DETAIL,
    HISTORY
}

class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val language by viewModel.language.collectAsState()
            val isDarkTheme by viewModel.isDarkTheme.collectAsState()
            val searchQuery by viewModel.searchQuery.collectAsState()
            val selectedCategory by viewModel.selectedCategoryId.collectAsState()
            val selectedFilterTag by viewModel.selectedFilterTag.collectAsState()
            val filteredTools by viewModel.filteredTools.collectAsState()
            val favoriteIds by viewModel.favoriteIds.collectAsState()
            val recentHistory by viewModel.recentHistory.collectAsState()
            val selectedTool by viewModel.selectedTool.collectAsState()
            val userPrompt by viewModel.userPrompt.collectAsState()
            val generationState by viewModel.generationState.collectAsState()
            val toastMessage by viewModel.toastMessage.collectAsState()

            var currentScreen by remember { mutableStateOf(Screen.HOME) }
            var showInfoDialog by remember { mutableStateOf(false) }
            val snackbarHostState = remember { SnackbarHostState() }

            // Handle Toast
            LaunchedEffect(toastMessage) {
                toastMessage?.let { msg ->
                    Toast.makeText(this@MainActivity, msg, Toast.LENGTH_SHORT).show()
                    viewModel.clearToast()
                }
            }

            // Back button handling
            BackHandler(enabled = currentScreen != Screen.HOME) {
                if (currentScreen == Screen.TOOL_DETAIL || currentScreen == Screen.HISTORY) {
                    viewModel.selectTool(null)
                    currentScreen = Screen.HOME
                }
            }

            BanglaAiTheme(darkTheme = isDarkTheme) {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    topBar = {
                        TopNavbar(
                            language = language,
                            isDarkTheme = isDarkTheme,
                            onLanguageToggle = { viewModel.toggleLanguage() },
                            onThemeToggle = { viewModel.toggleTheme() },
                            onInfoClick = { showInfoDialog = true }
                        )
                    },
                    snackbarHost = { SnackbarHost(snackbarHostState) }
                ) { innerPadding ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                    ) {
                        AnimatedContent(
                            targetState = currentScreen,
                            transitionSpec = { fadeIn() togetherWith fadeOut() },
                            label = "ScreenTransition"
                        ) { targetScreen ->
                            when (targetScreen) {
                                Screen.HOME -> {
                                    HomeScreen(
                                        language = language,
                                        searchQuery = searchQuery,
                                        selectedCategory = selectedCategory,
                                        selectedFilterTag = selectedFilterTag,
                                        categories = viewModel.categories,
                                        tools = filteredTools,
                                        favoriteIds = favoriteIds,
                                        onSearchChange = { viewModel.setSearchQuery(it) },
                                        onCategorySelect = { viewModel.setSelectedCategory(it) },
                                        onFilterTagSelect = { viewModel.setFilterTag(it) },
                                        onToolSelect = { tool ->
                                            viewModel.selectTool(tool)
                                            currentScreen = Screen.TOOL_DETAIL
                                        },
                                        onFavoriteToggle = { viewModel.toggleFavorite(it) },
                                        onOpenHistory = { currentScreen = Screen.HISTORY }
                                    )
                                }

                                Screen.TOOL_DETAIL -> {
                                    selectedTool?.let { tool ->
                                        ToolDetailScreen(
                                            tool = tool,
                                            language = language,
                                            userPrompt = userPrompt,
                                            generationState = generationState,
                                            isFavorite = favoriteIds.contains(tool.id),
                                            onUserPromptChange = { viewModel.setUserPrompt(it) },
                                            onGenerateClick = { viewModel.generateResult() },
                                            onClearClick = { viewModel.clearPromptAndResult() },
                                            onFavoriteToggle = { viewModel.toggleFavorite(tool.id) },
                                            onBackClick = {
                                                viewModel.selectTool(null)
                                                currentScreen = Screen.HOME
                                            },
                                            onShowToast = { viewModel.showToast(it) }
                                        )
                                    }
                                }

                                Screen.HISTORY -> {
                                    HistoryScreen(
                                        language = language,
                                        historyList = recentHistory,
                                        onBackClick = { currentScreen = Screen.HOME },
                                        onDeleteHistoryItem = { viewModel.deleteHistoryItem(it) },
                                        onClearAllHistory = { viewModel.clearAllHistory() },
                                        onShowToast = { viewModel.showToast(it) }
                                    )
                                }
                            }
                        }

                        if (showInfoDialog) {
                            InfoDialog(
                                language = language,
                                onDismiss = { showInfoDialog = false }
                            )
                        }
                    }
                }
            }
        }
    }
}
