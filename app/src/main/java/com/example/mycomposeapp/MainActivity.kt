package com.example.mycomposeapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.mycomposeapp.components.SearchBottomSheet
import com.example.mycomposeapp.ui.theme.MyComposeAppTheme
import com.example.mycomposeapp.utils.PhoneNumberFormatter

class MainActivity : ComponentActivity() {
    private val viewModel: SearchViewModel by viewModels()
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyComposeAppTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    SearchScreen(
                        viewModel = viewModel,
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    viewModel: SearchViewModel,
    modifier: Modifier = Modifier
) {
    var showBottomSheet by remember { mutableStateOf(false) }
    var searchText by remember { mutableStateOf("") }
    var selectedText by remember { mutableStateOf("") }
    
    Column(
        modifier = modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Phone number input field
        PhoneNumberInput()

        // Main text field that triggers bottom sheet
        OutlinedTextField(
            value = selectedText,
            onValueChange = { },
            modifier = Modifier
                .fillMaxWidth()
                .clickable { showBottomSheet = true },
            enabled = false,
            label = { Text("Click to search") }
        )
        
        // Using the generic SearchBottomSheet component
        if (showBottomSheet) {
            SearchBottomSheet<String>(
                searchText = searchText,
                onSearchTextChange = { newText -> 
                    searchText = newText
                    // Check for exact match and auto-select
                    viewModel.findExactMatch(newText)?.let { match ->
                        selectedText = match
                        showBottomSheet = false
                        searchText = ""
                    }
                },
                suggestions = viewModel.getSuggestions(searchText),
                createSuggestionView = { suggestion ->
                    Text(
                        text = suggestion,
                        modifier = Modifier.fillMaxWidth()
                    )
                },
                onSuggestionSelected = { suggestion ->
                    selectedText = suggestion
                    showBottomSheet = false
                    searchText = ""
                },
                onDismiss = {
                    showBottomSheet = false
                    searchText = ""
                },
                showSearchBox = true
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PhoneNumberInput(modifier: Modifier = Modifier) {
    var phoneNumberState by remember { 
        mutableStateOf(
            TextFieldValue(
                text = "",
                selection = TextRange(0)
            )
        ) 
    }
    
    OutlinedTextField(
        value = phoneNumberState,
        onValueChange = { input ->
            // Only update if the deformatted input is not longer than 10 digits
            val deformatted = PhoneNumberFormatter.deformat(input.text)
            if (deformatted.length <= 10) {
                val formatted = PhoneNumberFormatter.format(deformatted)
                phoneNumberState = TextFieldValue(
                    text = formatted,
                    selection = TextRange(formatted.length)
                )
            }
        },
        modifier = modifier.fillMaxWidth(),
        label = { Text("Phone Number") },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
        supportingText = {
            if (phoneNumberState.text.isNotEmpty() && !PhoneNumberFormatter.isValid(phoneNumberState.text)) {
                Text("Please enter a valid 10-digit phone number")
            }
        },
        isError = phoneNumberState.text.isNotEmpty() && !PhoneNumberFormatter.isValid(phoneNumberState.text)
    )
}

@Preview(showBackground = true)
@Composable
fun SearchScreenPreview() {
    MyComposeAppTheme {
        SearchScreen(viewModel = SearchViewModel())
    }
}
