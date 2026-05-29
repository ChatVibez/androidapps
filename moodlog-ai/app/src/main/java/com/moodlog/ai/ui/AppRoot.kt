package com.moodlog.ai.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.moodlog.ai.data.preferences.UserPreferences
import com.moodlog.ai.ui.navigation.AppNavigation
import com.moodlog.ai.ui.onboarding.OnboardingScreen

/**
 * Top-level switcher between the onboarding flow and the main app.
 *
 * Renders a small loading indicator while DataStore emits its first value to avoid
 * flashing onboarding briefly when the user has already completed it.
 */
@Composable
fun AppRoot(prefs: UserPreferences?) {
    when {
        prefs == null -> SplashLoading()
        !prefs.onboardingCompleted -> OnboardingScreen(onComplete = { /* prefs flow re-emits */ })
        else -> AppNavigation()
    }
}

@Composable
private fun SplashLoading() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator()
    }
}
