package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.local.RecentEntity
import com.example.data.model.AiTool
import com.example.data.model.Language
import com.example.data.model.ToolCategory
import com.example.data.repository.ToolRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

sealed interface GenerationState {
    object Idle : GenerationState
    object Loading : GenerationState
    data class Success(val text: String) : GenerationState
    data class Error(val message: String) : GenerationState
}

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: ToolRepository

    init {
        val database = AppDatabase.getDatabase(application)
        repository = ToolRepository(database.toolDao())
    }

    private val _language = MutableStateFlow(Language.BN)
    val language: StateFlow<Language> = _language.asStateFlow()

    private val _isDarkTheme = MutableStateFlow(true)
    val isDarkTheme: StateFlow<Boolean> = _isDarkTheme.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedCategoryId = MutableStateFlow<String?>(null)
    val selectedCategoryId: StateFlow<String?> = _selectedCategoryId.asStateFlow()

    private val _selectedFilterTag = MutableStateFlow("all") // all, popular, trending, newest, favorites, history
    val selectedFilterTag: StateFlow<String> = _selectedFilterTag.asStateFlow()

    val favoriteIds: StateFlow<List<String>> = repository.favoriteToolIds.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val recentHistory: StateFlow<List<RecentEntity>> = repository.recentHistory.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    private val _selectedTool = MutableStateFlow<AiTool?>(null)
    val selectedTool: StateFlow<AiTool?> = _selectedTool.asStateFlow()

    private val _userPrompt = MutableStateFlow("")
    val userPrompt: StateFlow<String> = _userPrompt.asStateFlow()

    private val _generationState = MutableStateFlow<GenerationState>(GenerationState.Idle)
    val generationState: StateFlow<GenerationState> = _generationState.asStateFlow()

    private val _toastMessage = MutableStateFlow<String?>(null)
    val toastMessage: StateFlow<String?> = _toastMessage.asStateFlow()

    val categories: List<ToolCategory> = repository.getCategories()
    val allTools: List<AiTool> = repository.getAllTools()

    // Filtered tools flow
    val filteredTools: StateFlow<List<AiTool>> = combine(
        _searchQuery,
        _selectedCategoryId,
        _selectedFilterTag,
        favoriteIds
    ) { query, catId, filterTag, favs ->
        var list = allTools

        // 1. Category Filter
        if (catId != null) {
            list = list.filter { it.categoryId == catId }
        }

        // 2. Filter Tag
        when (filterTag) {
            "popular" -> list = list.filter { it.tags.contains("popular") }
            "trending" -> list = list.filter { it.tags.contains("trending") }
            "newest" -> list = list.filter { it.tags.contains("newest") }
            "favorites" -> list = list.filter { favs.contains(it.id) }
        }

        // 3. Search Query
        if (query.isNotBlank()) {
            val q = query.trim().lowercase()
            list = list.filter { tool ->
                tool.titleEn.lowercase().contains(q) ||
                tool.titleBn.contains(q) ||
                tool.descriptionEn.lowercase().contains(q) ||
                tool.descriptionBn.contains(q) ||
                tool.id.lowercase().contains(q)
            }
        }

        list
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = allTools
    )

    fun toggleLanguage() {
        _language.value = if (_language.value == Language.BN) Language.EN else Language.BN
    }

    fun setLanguage(lang: Language) {
        _language.value = lang
    }

    fun toggleTheme() {
        _isDarkTheme.value = !_isDarkTheme.value
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setSelectedCategory(catId: String?) {
        _selectedCategoryId.value = catId
    }

    fun setFilterTag(tag: String) {
        _selectedFilterTag.value = tag
    }

    fun selectTool(tool: AiTool?) {
        _selectedTool.value = tool
        _userPrompt.value = ""
        _generationState.value = GenerationState.Idle
    }

    fun setUserPrompt(prompt: String) {
        _userPrompt.value = prompt
    }

    fun toggleFavorite(toolId: String) {
        viewModelScope.launch {
            repository.toggleFavorite(toolId)
            val isFav = repository.isFavorite(toolId)
            val msg = if (_language.value == Language.BN) {
                if (isFav) "ফেভারিটে যোগ করা হয়েছে" else "ফেভারিট থেকে সরানো হয়েছে"
            } else {
                if (isFav) "Added to favorites" else "Removed from favorites"
            }
            showToast(msg)
        }
    }

    fun generateResult() {
        val tool = _selectedTool.value ?: return
        val prompt = _userPrompt.value.trim()
        if (prompt.isEmpty()) {
            val msg = if (_language.value == Language.BN) "দয়া করে আপনার প্রশ্ন বা প্রম্পট লিখুন!" else "Please enter your prompt!"
            showToast(msg)
            return
        }

        _generationState.value = GenerationState.Loading

        viewModelScope.launch {
            val isBangla = _language.value == Language.BN
            val result = repository.generateAiResult(tool, prompt, isBangla)

            result.fold(
                onSuccess = { responseText ->
                    _generationState.value = GenerationState.Success(responseText)
                    val toolTitle = if (isBangla) tool.titleBn else tool.titleEn
                    repository.saveRecent(
                        toolId = tool.id,
                        toolTitle = toolTitle,
                        prompt = prompt,
                        result = responseText,
                        lang = _language.value.code
                    )
                },
                onFailure = { error ->
                    _generationState.value = GenerationState.Error(error.localizedMessage ?: "Generation failed")
                }
            )
        }
    }

    fun clearPromptAndResult() {
        _userPrompt.value = ""
        _generationState.value = GenerationState.Idle
    }

    fun deleteHistoryItem(id: Long) {
        viewModelScope.launch {
            repository.deleteRecent(id)
            showToast(if (_language.value == Language.BN) "ইতিহাস মুছে ফেলা হয়েছে" else "History item deleted")
        }
    }

    fun clearAllHistory() {
        viewModelScope.launch {
            repository.clearHistory()
            showToast(if (_language.value == Language.BN) "সমস্ত ইতিহাস মুছে ফেলা হয়েছে" else "All history cleared")
        }
    }

    fun showToast(message: String) {
        _toastMessage.value = message
    }

    fun clearToast() {
        _toastMessage.value = null
    }
}
