package com.kunvarpreet.skirk.presentation.picker

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.kunvarpreet.skirk.widget.core.WidgetRegistry

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WidgetPickerScreen(
    panelId: String,
    slotIndex: Int,
    widgetRegistry: WidgetRegistry,
    onNavigateBack: () -> Unit,
    onSelectWidget: (widgetTypeId: String) -> Unit,
    modifier: Modifier = Modifier
) {
    val definitions = widgetRegistry.getAllDefinitions()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Widget Picker") },
                navigationIcon = {
                    Button(onClick = onNavigateBack) {
                        Text("‹ Back")
                    }
                }
            )
        },
        modifier = modifier
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Text(
                    text = "Add to Slot #${slotIndex + 1} ($panelId)",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Available Registered Widgets (${definitions.size})",
                    style = MaterialTheme.typography.titleMedium
                )
            }

            items(definitions) { def ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onSelectWidget(def.id) }
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = def.displayName,
                                style = MaterialTheme.typography.titleMedium
                            )
                            Text(
                                text = def.category.displayName,
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = def.description,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Designs: " + def.availableDesigns.joinToString { it.displayName },
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.secondary
                        )
                    }
                }
            }
        }
    }
}
