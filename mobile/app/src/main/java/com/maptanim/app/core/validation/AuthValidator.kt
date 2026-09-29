package com.maptanim.app.core.validation

/**
 * Validates user authentication inputs including Google/Gmail addresses.
 */
object AuthValidator {

    sealed class ValidationResult {
        object Valid : ValidationResult()
        data class Invalid(val message: String) : ValidationResult()

        val isValid: Boolean get() = this is Valid
    }

    /**
     * Validates that an email is a legitimate Google/Gmail address.
     * Enforces Google email policies:
     * - Must contain '@' and domain must be gmail.com or googlemail.com
     * - Username length must be between 6 and 30 characters
     * - Allowed characters: letters (a-z), numbers (0-9), periods (.)
     * - Username cannot start or end with a period
     * - Username cannot contain consecutive periods (..)
     */
    fun validateGoogleEmail(email: String): ValidationResult {
        val trimmed = email.trim()
        if (trimmed.isBlank()) {
            return ValidationResult.Invalid("Please enter your Google (Gmail) email address.")
        }

        val parts = trimmed.split("@")
        if (parts.size != 2) {
            return ValidationResult.Invalid("Please enter a valid email address.")
        }

        val username = parts[0]
        val domain = parts[1].lowercase()

        // 1. Domain verification: Must be Google mail domain
        if (domain != "gmail.com" && domain != "googlemail.com") {
            return ValidationResult.Invalid(
                "Please enter a valid Google email (@gmail.com) so password reset OTPs can be delivered to your inbox."
            )
        }

        // 2. Length check: Google usernames are 6–30 characters
        if (username.length < 6) {
            return ValidationResult.Invalid("Gmail username must be at least 6 characters.")
        }
        if (username.length > 30) {
            return ValidationResult.Invalid("Gmail username cannot exceed 30 characters.")
        }

        // 3. Dot position checks
        if (username.startsWith(".") || username.endsWith(".")) {
            return ValidationResult.Invalid("Gmail username cannot begin or end with a period.")
        }
        if (username.contains("..")) {
            return ValidationResult.Invalid("Gmail username cannot contain consecutive periods.")
        }

        // 4. Character set check: Google permits alphanumeric and periods
        if (!username.matches(Regex("^[a-zA-Z0-9.]+$"))) {
            return ValidationResult.Invalid("Gmail username can only contain letters (a-z), numbers (0-9), and periods (.).")
        }

        return ValidationResult.Valid
    }
}
