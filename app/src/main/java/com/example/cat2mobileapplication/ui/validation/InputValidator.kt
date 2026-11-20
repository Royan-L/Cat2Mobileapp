package com.example.cat2mobileapplication.ui.validation

import android.util.Patterns

object InputValidator {

    // Checks if name has at least 2 characters
    fun isValidName(name: String): Boolean {
        return name.trim().length >= 2
    }

    // Checks if the email format is correct
    fun isValidEmail(email: String): Boolean {
        return email.isNotEmpty() && Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    // Checks if the phone number is valid (at least 10 digits)
    fun isValidPhone(phone: String): Boolean {
        return phone.trim().length >= 10 && phone.all { it.isDigit() || it == '+' || it == '-' }
    }

    // Checks if amount is valid (can be converted to a number)
    fun isValidAmount(amount: String): Boolean {
        val amountDouble = amount.toDoubleOrNull()
        return amountDouble != null && amountDouble > 0
    }

    // Checks if lease dates are valid (this is a basic check)
    fun isValidDateRange(startDate: String, endDate: String): Boolean {
        return startDate.isNotEmpty() && endDate.isNotEmpty()
    }
}