
package com.example.cat2mobileapplication.ui.validation

import java.text.ParsePosition
import java.text.SimpleDateFormat
import java.util.Locale

object InputValidator {

    fun isValidName(name: String): Boolean {
        return name.trim().length >= 2
    }

    fun isValidEmail(email: String): Boolean {
        return email.trim().matches(
            Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")
        )
    }

    fun isValidPhone(phone: String): Boolean {
        val cleaned = phone.trim()
        val digits = cleaned.filter { it.isDigit() }

        return cleaned.matches(Regex("^\\+?[0-9\\s()-]+$")) &&
                digits.length in 7..15
    }

    fun isValidPositiveAmount(amount: Double): Boolean {
        return amount.isFinite() && amount > 0
    }

    fun isValidNonNegativeNumber(value: Int): Boolean {
        return value >= 0
    }

    private fun parseDate(date: String): Long? {
        val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        formatter.isLenient = false

        val position = ParsePosition(0)
        val parsedDate = formatter.parse(date.trim(), position)

        return if (
            parsedDate != null &&
            position.index == date.trim().length
        ) {
            parsedDate.time
        } else {
            null
        }
    }

    fun isValidDateRange(start: String, end: String): Boolean {
        val startDate = parseDate(start) ?: return false
        val endDate = parseDate(end) ?: return false

        return endDate >= startDate
    }
}
