package com.moodlog.ai.ui.insights

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.moodlog.ai.R
import java.util.concurrent.TimeUnit

@Composable
fun InsightsScreen(
    contentPadding: PaddingValues,
    viewModel: InsightsViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(contentPadding)
            .padding(horizontal = 24.dp, vertical = 16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = stringResource(R.string.insights_title),
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.SemiBold
        )
        Text(
            text = stringResource(R.string.insights_subtitle),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            )
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                when (val s = state) {
                    is InsightsUiState.Idle -> Text(stringResource(R.string.insights_empty))
                    is InsightsUiState.Loading -> {
                        CircularProgressIndicator(modifier = Modifier.size(28.dp))
                        Spacer(Modifier.height(12.dp))
                        Text(stringResource(R.string.insights_loading))
                    }
                    is InsightsUiState.Success -> {
                        Text(s.text)
                        Spacer(Modifier.height(12.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = generatedLabel(s.generatedAt, s.fromCache),
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            IconButton(onClick = { viewModel.generate(forceRefresh = true) }) {
                                Icon(
                                    imageVector = Icons.Filled.Refresh,
                                    contentDescription = stringResource(R.string.insights_refresh)
                                )
                            }
                        }
                    }
                    is InsightsUiState.Error -> Text(
                        text = stringResource(R.string.insights_error, s.message),
                        color = MaterialTheme.colorScheme.error
                    )
                    is InsightsUiState.MissingKey -> Text(stringResource(R.string.insights_no_key))
                }
            }
        }

        Button(
            onClick = { viewModel.generate(forceRefresh = false) },
            enabled = state !is InsightsUiState.Loading,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(stringResource(R.string.insights_cta))
        }

        Text(
            text = stringResource(R.string.insights_disclaimer),
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun generatedLabel(generatedAt: Long, fromCache: Boolean): String {
    val now = System.currentTimeMillis()
    val diff = (now - generatedAt).coerceAtLeast(0)
    val mins = TimeUnit.MILLISECONDS.toMinutes(diff)
    val hours = TimeUnit.MILLISECONDS.toHours(diff)
    val ageText = when {
        mins < 1 -> stringResource(R.string.insights_just_now)
        hours < 1 -> stringResource(R.string.insights_minutes_ago, mins)
        else -> stringResource(R.string.insights_hours_ago, hours)
    }
    return if (fromCache) {
        stringResource(R.string.insights_from_cache, ageText)
    } else {
        stringResource(R.string.insights_just_generated, ageText)
    }
}
