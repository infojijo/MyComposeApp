package com.example.mycomposeapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.mycomposeapp.api.CanadaPostRepository
import com.example.mycomposeapp.components.SearchBottomSheet
import com.example.mycomposeapp.ui.theme.MyComposeAppTheme
import com.example.mycomposeapp.utils.PhoneNumberFormatter
import com.example.mycomposeapp.BuildConfig
import com.example.mycomposeapp.data.Address

class MainActivity : ComponentActivity() {
    private val viewModel: SearchViewModel by viewModels {
        object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return SearchViewModel(
                    repository = CanadaPostRepository(
                        apiKey = BuildConfig.CANADA_POST_API_KEY
                    )
                ) as T
            }
        }
    }
    
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
    Column(
        modifier = modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Phone number input field
        PhoneNumberInput()
        
        // Direct address input field
        AddressTextField(viewModel)
        
        // Address search with bottom sheet
        AddressSearchWithBottomSheet(viewModel)
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddressTextField(viewModel: SearchViewModel) {
    var searchText by remember { mutableStateOf("") }
    var selectedAddress by remember { mutableStateOf<Address?>(null) }
    val suggestions by viewModel.addressSuggestions.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()
    
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        OutlinedTextField(
            value = searchText,
            onValueChange = { query ->
                searchText = query
                selectedAddress = null // Clear selected address when searching
                viewModel.searchAddress(query)
            },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Search Address") },
            trailingIcon = {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        strokeWidth = 2.dp
                    )
                }
            }
        )
        
        // Show selected address in a read-only field
        selectedAddress?.let { selected ->
            OutlinedTextField(
                value = selected.toFullString(),
                onValueChange = { },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Complete Address") },
                enabled = false
            )
        }
        
        // Show suggestions in a dropdown
        if (suggestions.isNotEmpty()) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 200.dp),
                shadowElevation = 4.dp
            ) {
                LazyColumn {
                    items(suggestions) { address ->
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    searchText = ""  // Clear search text
                                    selectedAddress = address  // Set selected address
                                    viewModel.clearAddressSuggestions()
                                }
                                .padding(16.dp)
                        ) {
                            Text(text = address.toShortString())
                            Text(
                                text = address.toFullString(),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddressSearchWithBottomSheet(viewModel: SearchViewModel) {
    var showBottomSheet by remember { mutableStateOf(false) }
    var searchText by remember { mutableStateOf("") }
    
    val selectedAddress by viewModel.selectedAddress.collectAsStateWithLifecycle()
    val suggestions by viewModel.addressSuggestions.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()
    
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Search trigger field
        OutlinedTextField(
            value = "Click to search address",
            onValueChange = { },
            modifier = Modifier
                .fillMaxWidth()
                .clickable { showBottomSheet = true },
            enabled = false,
            label = { Text("Search Address") }
        )
        
        // Show selected address in a read-only field
        selectedAddress?.let { selected ->
            OutlinedTextField(
                value = selected.toFullString(),
                onValueChange = { },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Complete Address") },
                enabled = false
            )
        }
        
        if (showBottomSheet) {
            SearchBottomSheet(
                searchText = searchText,
                onSearchTextChange = { query ->
                    searchText = query
                    viewModel.searchAddress(query)
                },
                suggestions = if (isLoading) emptyList() else suggestions,
                createSuggestionView = { address ->
                    Column(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(text = address.toShortString())
                        Text(
                            text = address.toFullString(),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                onSuggestionSelected = { address ->
                    viewModel.selectAddress(address)
                    showBottomSheet = false
                    searchText = ""
                },
                onDismiss = {
                    showBottomSheet = false
                    searchText = ""
                }
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SearchScreenPreview() {
    MyComposeAppTheme {
        SearchScreen(
            viewModel = SearchViewModel(
                repository = CanadaPostRepository("preview-key")
            )
        )
    }
}
