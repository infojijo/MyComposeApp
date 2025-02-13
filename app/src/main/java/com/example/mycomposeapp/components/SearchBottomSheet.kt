package com.example.mycomposeapp.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun <T> SearchBottomSheet(
    searchText: String,
    onSearchTextChange: (String) -> Unit,
    suggestions: List<T>,
    createSuggestionView: @Composable (T) -> Unit,
    onSuggestionSelected: (T) -> Unit,
    onDismiss: () -> Unit,
    showSearchBox: Boolean = true,
    modifier: Modifier = Modifier
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        modifier = modifier.fillMaxHeight(0.8f)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Search text field (optional)
            if (showSearchBox) {
                TextField(
                    value = searchText,
                    onValueChange = onSearchTextChange,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    placeholder = { Text("Search...") }
                )
            }
            
            // Suggestions list with custom view
            LazyColumn {
                items(suggestions) { suggestion ->
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onSuggestionSelected(suggestion) }
                            .padding(vertical = 12.dp)
                    ) {
                        createSuggestionView(suggestion)
                    }
                }
            }
        }
    }
}
