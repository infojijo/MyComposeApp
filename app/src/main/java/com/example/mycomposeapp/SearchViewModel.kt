package com.example.mycomposeapp

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mycomposeapp.api.CanadaPostRepository
import com.example.mycomposeapp.data.Address
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SearchViewModel(
    private val repository: CanadaPostRepository
) : ViewModel() {
    private val _addressSuggestions = MutableStateFlow<List<Address>>(emptyList())
    val addressSuggestions: StateFlow<List<Address>> = _addressSuggestions.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _selectedAddress = MutableStateFlow<Address?>(null)
    val selectedAddress: StateFlow<Address?> = _selectedAddress.asStateFlow()

    fun searchAddress(query: String) {
        if (query.length < 3) {
            _addressSuggestions.update { emptyList() }
            return
        }

        viewModelScope.launch {
            _isLoading.update { true }
            try {
                _addressSuggestions.update { repository.searchAddress(query) }
            } finally {
                _isLoading.update { false }
            }
        }
    }

    fun selectAddress(address: Address) {
        _selectedAddress.update { address }
        _addressSuggestions.update { emptyList() }
    }

    fun clearSelectedAddress() {
        _selectedAddress.update { null }
    }

    fun clearAddressSuggestions() {
        _addressSuggestions.update { emptyList() }
    }
}
