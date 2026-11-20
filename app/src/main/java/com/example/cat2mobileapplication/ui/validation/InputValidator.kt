package com.example.cat2mobileapplication.ui.validation

object InputValidator {
    fun isValidName(name: String): Boolean {
        return name.trim().length >= 2
    }

    fun isValidEmail(email: String): Boolean {
        return email.isNotBlank() && android.util.Patterns.EMAIL_ADDRESS.matcher(email.trim()).matches()
    }

    fun isValidPhone(phone: String): Boolean {
        return phone.trim().length >= 7
    }

    fun isValidDateRange(startDate: String, endDate: String): Boolean {
        if (startDate.isBlank() || endDate.isBlank()) return true
        return endDate >= startDate
    }
}
