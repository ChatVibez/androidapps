package com.moodlog.ai.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.moodlog.ai.data.local.MoodEntry
import com.moodlog.ai.data.repository.MoodRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class HomeUiState(
    val moodScore: Int = 5,
    val selectedEmoji: String = "😐",
    val journal: String = "",
    val isSaving: Boolean = false,
    val justSaved: Boolean = false
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: MoodRepository
) : ViewModel() {

    private val _state = MutableStateFlow(HomeUiState())
    val state: StateFlow<HomeUiState> = _state.asStateFlow()

    fun onScoreChange(score: Int) = _state.update { it.copy(moodScore = score) }
    fun onEmojiSelect(emoji: String) = _state.update { it.copy(selectedEmoji = emoji) }
    fun onJournalChange(text: String) = _state.update { it.copy(journal = text) }

    fun save() {
        val current = _state.value
        if (current.isSaving) return
        _state.update { it.copy(isSaving = true) }
        viewModelScope.launch {
            repository.save(
                MoodEntry(
                    moodScore = current.moodScore,
                    emoji = current.selectedEmoji,
                    journal = current.journal
                )
            )
            _state.update {
                HomeUiState(justSaved = true)
            }
        }
    }

    fun consumeSavedEvent() = _state.update { it.copy(justSaved = false) }
}
