package com.example.mycomposeapp.utils

object PhoneNumberFormatter {
    fun format(input: String): String {
        // Remove all non-digit characters
        val digits = input.filter { it.isDigit() }
        
        // Build the formatted string based on the number of digits
        val formatted = StringBuilder()
        
        // Add opening parenthesis for first group
        if (digits.isNotEmpty()) {
            formatted.append("(")
        }
        
        // Add first three digits
        digits.take(3).forEach { formatted.append(it) }
        
        // Add closing parenthesis and hyphen after first three digits
        if (digits.length > 3) {
            formatted.append(")-")
        }
        
        // Add next three digits (position 4-6)
        if (digits.length > 3) {
            formatted.append(digits.substring(3, minOf(digits.length, 6)))
        }
        
        // Add hyphen and remaining digits (position 7-10)
        if (digits.length > 6) {
            formatted.append("-")
            formatted.append(digits.substring(6, minOf(digits.length, 10)))
        }
        
        return formatted.toString()
    }

    fun deformat(input: String): String {
        return input.filter { it.isDigit() }
    }

    fun isValid(input: String): Boolean {
        return input.filter { it.isDigit() }.length == 10
    }
}
