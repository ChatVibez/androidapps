package com.moodlog.ai.ui.insights

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.moodlog.ai.BuildConfig
import com.moodlog.ai.data.ai.GeminiService
import com.moodlog.ai.data.repository.MoodRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface InsightsUiState {
    data object Idle : InsightsUiState
    data object Loading : InsightsUiState
    data class Success(val text: String) : InsightsUiState
    data class Error(val message: String) : InsightsUiState
    data object MissingKey : InsightsUiState
}

@HiltViewModel
class InsightsViewModel @Inject constructor(
    private val repository: MoodRepository,
    private val gemini: GeminiService
) : ViewModel() {

    private val _state = MutableStateFlow<InsightsUiState>(InsightsUiState.Idle)
    val state: StateFlow<InsightsUiState> = _state.asStateFlow()

    fun generate() {
        if (BuildConfig.GEMINI_API_KEY.isBlank()) {
            _state.update { InsightsUiState.MissingKey }
            return
        }
        _state.update { InsightsUiState.Loading }
        viewModelScope.launch {
            val sevenDaysAgo = System.currentTimeMillis() - 7L * 24 * 60 * 60 * 1000
            val entries = repository.entriesSince(sevenDaysAgo)
            val result = gemini.weeklyInsight(entries)
            _state.update {
                result.fold(
                    onSuccess = { InsightsUiState.Success(it) },
                    onFailure = { InsightsUiState.Error(it.message ?: "Unknown error") }
                )
            }
        }
    }
}
