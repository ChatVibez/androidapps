package com.moodlog.ai.ui.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.moodlog.ai.data.local.MoodEntry
import com.moodlog.ai.data.repository.MoodRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

enum class HistoryRange(val days: Int) { LAST_7(7), LAST_30(30) }

data class HistoryUiState(
    val range: HistoryRange = HistoryRange.LAST_7,
    val entries: List<MoodEntry> = emptyList(),
    val averageScore: Double? = null
)

@HiltViewModel
class HistoryViewModel @Inject constructor(
    repository: MoodRepository
) : ViewModel() {

    private val rangeFlow = MutableStateFlow(HistoryRange.LAST_7)

    val state: StateFlow<HistoryUiState> = combine(
        repository.observeAll(),
        rangeFlow
    ) { all, range ->
        val cutoff = System.currentTimeMillis() - range.days * 24L * 60L * 60L * 1000L
        val filtered = all.filter { it.createdAt >= cutoff }
        val avg = filtered.takeIf { it.isNotEmpty() }
            ?.map { it.moodScore }
            ?.average()
        HistoryUiState(range = range, entries = filtered, averageScore = avg)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = HistoryUiState()
    )

    fun setRange(range: HistoryRange) {
        rangeFlow.value = range
    }
}
