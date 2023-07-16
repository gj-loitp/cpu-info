package com.kgurgul.roy93group.ui.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.toggleable
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.Preview
import com.kgurgul.roy93group.ui.theme.CpuInfoTheme
import com.kgurgul.roy93group.ui.theme.spacingMedium

@Composable
fun CpuSwitchBox(
    text: String,
    isChecked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
) {
    Row(
        Modifier
            .fillMaxWidth()
            .toggleable(
                value = isChecked,
                onValueChange = onCheckedChange,
                role = Role.Switch
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
        )
        Switch(
            checked = isChecked,
            onCheckedChange = null,
            modifier = Modifier.padding(start = spacingMedium),
        )
    }
}

@Preview
@Composable
fun CpuSwitchBoxPreview() {
    CpuInfoTheme {
        CpuCheckbox(
            text = "Test",
            isChecked = true,
            onCheckedChange = {}
        )
    }
}