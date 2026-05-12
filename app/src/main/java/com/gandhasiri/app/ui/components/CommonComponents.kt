package com.gandhasiri.app.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gandhasiri.app.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SandalTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier.fillMaxWidth(),
    keyboardType: KeyboardType = KeyboardType.Text,
    singleLine: Boolean = true
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label, color = SandalwoodBrown) },
        modifier = modifier,
        singleLine = singleLine,
        maxLines = if (singleLine) 1 else 4,
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        textStyle = TextStyle(color = SandalwoodDark, fontSize = 16.sp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedTextColor = SandalwoodDark,
            unfocusedTextColor = SandalwoodDark,
            focusedContainerColor = Color.White,
            unfocusedContainerColor = Color.White.copy(alpha = 0.9f),
            focusedBorderColor = SandalwoodBrown,
            unfocusedBorderColor = SandalwoodMedium,
            focusedLabelColor = SandalwoodBrown,
            unfocusedLabelColor = SandalwoodMedium,
            cursorColor = SandalwoodBrown
        ),
        shape = RoundedCornerShape(10.dp)
    )
}
