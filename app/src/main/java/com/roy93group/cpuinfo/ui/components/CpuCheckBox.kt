package com.roy93group.cpuinfo.ui.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.toggleable
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.Preview
import com.roy93group.cpuinfo.ui.theme.CpuInfoTheme
import com.roy93group.cpuinfo.ui.theme.spacingMedium

@Composable
fun CpuCheckbox(
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
                role = Role.Checkbox
            )
            .padding(spacingMedium),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(
            checked = isChecked,
            onCheckedChange = null
        )
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(start = spacingMedium)
        )
    }
}

@Preview
@Composable
fun CpuCheckboxPreview() {
    CpuInfoTheme {
        CpuCheckbox(
            text = "Test",
            isChecked = true,
            onCheckedChange = {}
        )
    }
}