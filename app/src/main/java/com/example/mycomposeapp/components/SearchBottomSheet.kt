package com.example.mycomposeapp.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter

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

                // Add AddressComplete logo at the bottom
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 16.dp, bottom = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = rememberAsyncImagePainter("https://ws1.postescanada-canadapost.ca/images/icons/captureplus/address_complete_logo_en.png"),
                            contentDescription = "Canada Post AddressComplete Logo",
                            modifier = Modifier
                                .size(120.dp)
                        )
                    }
                }
            }
        }
    }
}
