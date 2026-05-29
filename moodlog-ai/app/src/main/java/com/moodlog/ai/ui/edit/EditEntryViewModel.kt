package com.moodlog.ai.ui.edit

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.moodlog.ai.data.local.MoodEntry
import com.moodlog.ai.data.repository.MoodRepository
import com.moodlog.ai.ui.navigation.NavArgs
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface EditUiState {
    data object Loading : EditUiState
    data object NotFound : EditUiState
    data class Editing(
        val entry: MoodEntry,
        val moodScore: Int,
        val selectedEmoji: String,
        val journal: String,
        val isWorking: Boolean = false
    ) : EditUiState
}

sealed interface EditEvent {
    data object Saved : EditEvent
    data object Deleted : EditEvent
}

@HiltViewModel
class EditEntryViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val repository: MoodRepository
) : ViewModel() {

    private val entryId: Long = savedStateHandle[NavArgs.ENTRY_ID] ?: 0L

    private val _state = MutableStateFlow<EditUiState>(EditUiState.Loading)
    val state: StateFlow<EditUiState> = _state.asStateFlow()

    private val _events = MutableSharedFlow<EditEvent>()
    val events: SharedFlow<EditEvent> = _events.asSharedFlow()

    init {
        viewModelScope.launch {
            val entry = repository.getById(entryId)
            _state.value = if (entry == null) {
                EditUiState.NotFound
            } else {
                EditUiState.Editing(
                    entry = entry,
                    moodScore = entry.moodScore,
                    selectedEmoji = entry.emoji,
                    journal = entry.journal
                )
            }
        }
    }

    fun onScoreChange(score: Int) = updateEditing { it.copy(moodScore = score) }
    fun onEmojiChange(emoji: String) = updateEditing { it.copy(selectedEmoji = emoji) }
    fun onJournalChange(text: String) = updateEditing { it.copy(journal = text) }

    fun save() {
        val current = _state.value as? EditUiState.Editing ?: return
        if (current.isWorking) return
        updateEditing { it.copy(isWorking = true) }
        viewModelScope.launch {
            repository.update(
                current.entry.copy(
                    moodScore = current.moodScore,
                    emoji = current.selectedEmoji,
                    journal = current.journal
                )
            )
            _events.emit(EditEvent.Saved)
        }
    }

    fun delete() {
        val current = _state.value as? EditUiState.Editing ?: return
        if (current.isWorking) return
        updateEditing { it.copy(isWorking = true) }
        viewModelScope.launch {
            repository.delete(current.entry.id)
            _events.emit(EditEvent.Deleted)
        }
    }

    private inline fun updateEditing(transform: (EditUiState.Editing) -> EditUiState.Editing) {
        _state.update { current ->
            if (current is EditUiState.Editing) transform(current) else current
        }
    }
}
