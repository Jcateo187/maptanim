package com.maptanim.app.features.auth.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun TermsCheckbox(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    onOpenTerms: (() -> Unit)? = null,
    onOpenPrivacy: (() -> Unit)? = null
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = CheckboxDefaults.colors(
                checkedColor = Color(0xFF2E7D32),
                uncheckedColor = Color(0xFF6B7280)
            )
        )

        val annotatedString = buildAnnotatedString {
            withStyle(style = SpanStyle(color = Color(0xFF111813))) {
                append("I agree to the ")
            }

            pushStringAnnotation(tag = "TERMS", annotation = "terms")
            withStyle(
                style = SpanStyle(
                    color = Color(0xFF2E7D32),
                    fontWeight = FontWeight.Bold,
                    textDecoration = TextDecoration.Underline
                )
            ) {
                append("Terms & Conditions")
            }
            pop()

            withStyle(style = SpanStyle(color = Color(0xFF111813))) {
                append(" and ")
            }

            pushStringAnnotation(tag = "PRIVACY", annotation = "privacy")
            withStyle(
                style = SpanStyle(
                    color = Color(0xFF2E7D32),
                    fontWeight = FontWeight.Bold,
                    textDecoration = TextDecoration.Underline
                )
            ) {
                append("Privacy Policy")
            }
            pop()
        }

        androidx.compose.foundation.text.ClickableText(
            text = annotatedString,
            onClick = { offset ->
                annotatedString.getStringAnnotations(tag = "TERMS", start = offset, end = offset)
                    .firstOrNull()?.let {
                        if (onOpenTerms != null) onOpenTerms() else onCheckedChange(!checked)
                    }

                annotatedString.getStringAnnotations(tag = "PRIVACY", start = offset, end = offset)
                    .firstOrNull()?.let {
                        if (onOpenPrivacy != null) onOpenPrivacy() else onCheckedChange(!checked)
                    }

                if (annotatedString.getStringAnnotations(start = offset, end = offset).isEmpty()) {
                    onCheckedChange(!checked)
                }
            },
            style = androidx.compose.ui.text.TextStyle(
                fontSize = 13.sp,
                color = Color(0xFF111813)
            ),
            modifier = Modifier.padding(start = 4.dp)
        )
    }
}