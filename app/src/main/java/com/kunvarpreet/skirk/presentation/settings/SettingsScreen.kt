package com.kunvarpreet.skirk.presentation.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.kunvarpreet.skirk.domain.repository.ThemeMode
import com.kunvarpreet.skirk.domain.repository.UserSettingsRepository
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    userSettingsRepository: UserSettingsRepository,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scope = rememberCoroutineScope()
    val settings by userSettingsRepository.observeSettings().collectAsState(initial = null)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings") },
                navigationIcon = {
                    Button(onClick = onNavigateBack) {
                        Text("‹ Back")
                    }
                }
            )
        },
        modifier = modifier
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Appearance & StandBy Settings",
                style = MaterialTheme.typography.titleMedium
            )

            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("Dark Theme", style = MaterialTheme.typography.bodyLarge)
                            Text("Force dark theme for StandBy", style = MaterialTheme.typography.bodySmall)
                        }
                        Switch(
                            checked = settings?.themeMode == ThemeMode.DARK,
                            onCheckedChange = { checked ->
                                scope.launch {
                                    userSettingsRepository.updateThemeMode(
                                        if (checked) ThemeMode.DARK else ThemeMode.LIGHT
                                    )
                                }
                            }
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("Dynamic Colors", style = MaterialTheme.typography.bodyLarge)
                            Text("Use system Material You palette", style = MaterialTheme.typography.bodySmall)
                        }
                        Switch(
                            checked = settings?.dynamicColor ?: true,
                            onCheckedChange = { checked ->
                                scope.launch {
                                    userSettingsRepository.updateDynamicColor(checked)
                                }
                            }
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("Keep Screen On While Charging", style = MaterialTheme.typography.bodyLarge)
                            Text("Prevent standby screen sleep", style = MaterialTheme.typography.bodySmall)
                        }
                        Switch(
                            checked = settings?.keepScreenOnWhenCharging ?: true,
                            onCheckedChange = { checked ->
                                scope.launch {
                                    userSettingsRepository.updateKeepScreenOn(checked)
                                }
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            Text(
                text = "Skirk Phase 0 • Architecture Foundation",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
        }
    }
}
