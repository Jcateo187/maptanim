package com.maptanim.app.features.auth.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun AppTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    placeholder: String? = null,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    supportingText: @Composable (() -> Unit)? = null,
    isError: Boolean = false,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    singleLine: Boolean = true
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        placeholder = placeholder?.let { { Text(it) } },
        leadingIcon = leadingIcon,
        trailingIcon = trailingIcon,
        supportingText = supportingText,
        isError = isError,
        keyboardOptions = keyboardOptions,
        modifier = modifier.fillMaxWidth(),
        singleLine = singleLine,
        shape = RoundedCornerShape(14.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedTextColor = Color(0xFF111813),
            unfocusedTextColor = Color(0xFF111813),
            focusedContainerColor = Color.White,
            unfocusedContainerColor = Color.White,
            focusedBorderColor = Color(0xFF2E7D32),
            unfocusedBorderColor = Color(0xFFD1D5DB),
            focusedLabelColor = Color(0xFF2E7D32),
            unfocusedLabelColor = Color(0xFF4B5563),
            focusedPlaceholderColor = Color(0xFF6B7280),
            unfocusedPlaceholderColor = Color(0xFF9CA3AF),
            cursorColor = Color(0xFF2E7D32),
            focusedLeadingIconColor = Color(0xFF2E7D32),
            unfocusedLeadingIconColor = Color(0xFF6B7280),
            focusedTrailingIconColor = Color(0xFF2E7D32),
            unfocusedTrailingIconColor = Color(0xFF6B7280),
            errorTextColor = Color(0xFF111813),
            errorBorderColor = Color(0xFFDC2626),
            errorLabelColor = Color(0xFFDC2626),
            errorLeadingIconColor = Color(0xFFDC2626),
            errorTrailingIconColor = Color(0xFFDC2626),
            errorContainerColor = Color.White
        )
    )
}