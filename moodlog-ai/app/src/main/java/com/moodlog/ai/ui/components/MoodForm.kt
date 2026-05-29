package com.moodlog.ai.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.moodlog.ai.R

val MoodEmojis: List<String> = listOf("😞", "😟", "😐", "🙂", "😄")

/**
 * Stateless mood input form: emoji picker + 1-10 slider + free-form journal.
 *
 * Reused by [com.moodlog.ai.ui.home.HomeScreen] (new entry) and
 * [com.moodlog.ai.ui.edit.EditEntryScreen] (edit existing entry).
 */
@Composable
fun MoodForm(
    moodScore: Int,
    selectedEmoji: String,
    journal: String,
    onScoreChange: (Int) -> Unit,
    onEmojiChange: (String) -> Unit,
    onJournalChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // Emoji picker
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            MoodEmojis.forEach { emoji ->
                EmojiOption(
                    emoji = emoji,
                    selected = selectedEmoji == emoji,
                    onClick = { onEmojiChange(emoji) }
                )
            }
        }

        // Mood score slider
        Column {
            Text(
                text = "Skor mood: $moodScore/10",
                style = MaterialTheme.typography.labelLarge
            )
            Slider(
                value = moodScore.toFloat(),
                onValueChange = { onScoreChange(it.toInt()) },
                valueRange = 1f..10f,
                steps = 8
            )
        }

        // Journal text
        OutlinedTextField(
            value = journal,
            onValueChange = onJournalChange,
            modifier = Modifier
                .fillMaxWidth()
                .height(160.dp),
            placeholder = { Text(stringResource(R.string.journal_hint)) }
        )
    }
}

@Composable
private fun EmojiOption(emoji: String, selected: Boolean, onClick: () -> Unit) {
    val bg =
        if (selected) MaterialTheme.colorScheme.primaryContainer
        else MaterialTheme.colorScheme.surfaceVariant
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .size(56.dp)
            .clip(CircleShape)
            .background(bg)
            .clickable(onClick = onClick)
    ) {
        Text(text = emoji, fontSize = 28.sp)
    }
}
