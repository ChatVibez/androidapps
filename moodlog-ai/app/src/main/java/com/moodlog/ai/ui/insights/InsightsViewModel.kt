package com.moodlog.ai.ui.insights

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.moodlog.ai.BuildConfig
import com.moodlog.ai.data.repository.InsightRepository
import com.moodlog.ai.data.repository.InsightResult
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
    data class Success(
        val text: String,
        val generatedAt: Long,
        val fromCache: Boolean
    ) : InsightsUiState
    data class Error(val message: String) : InsightsUiState
    data object MissingKey : InsightsUiState
}

@HiltViewModel
class InsightsViewModel @Inject constructor(
    private val repository: InsightRepository
) : ViewModel() {

    private val _state = MutableStateFlow<InsightsUiState>(InsightsUiState.Idle)
    val state: StateFlow<InsightsUiState> = _state.asStateFlow()

    /**
     * Generates the weekly insight. By default uses cache when available;
     * pass [forceRefresh] = true to bypass cache (e.g. user pressed refresh).
     */
    fun generate(forceRefresh: Boolean = false) {
        if (BuildConfig.GEMINI_API_KEY.isBlank()) {
            _state.update { InsightsUiState.MissingKey }
            return
        }
        _state.update { InsightsUiState.Loading }
        viewModelScope.launch {
            val result = repository.getInsight(forceRefresh = forceRefresh)
            _state.update {
                when (result) {
                    is InsightResult.Success -> InsightsUiState.Success(
                        text = result.content,
                        generatedAt = result.generatedAt,
                        fromCache = result.fromCache
                    )
                    is InsightResult.Failure -> InsightsUiState.Error(result.message)
                }
            }
        }
    }
}
