package com.example.mycomposeapp

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class SearchViewModel : ViewModel() {
    private val _suggestions = MutableStateFlow(
        listOf(
            "Apple", "Banana", "Cherry", "Date",
            "Elderberry", "Fig", "Grape", "Honeydew",
            "Ice Apple", "Jackfruit", "Kiwi", "Lemon"
        )
    )
    val suggestions: StateFlow<List<String>> = _suggestions.asStateFlow()

    fun getSuggestions(query: String): List<String> {
        return _suggestions.value.filter {
            it.contains(query, ignoreCase = true)
        }
    }
}
