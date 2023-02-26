package com.me.newsapp.presentation.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.sp
import com.me.newsapp.ui.theme.TextDisabledLight
import com.me.newsapp.ui.theme.TextLight

@Composable
fun DropdownSearch(items: List<String>, selectedItem: String, onSelectedItemChange: (String) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    val dropdownModifier = Modifier
        .wrapContentSize()

    Box(modifier = dropdownModifier) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = selectedItem, style = TextStyle(
                    fontSize = 14.sp,
                    color = TextLight
                ),
                modifier = Modifier.clickable { expanded = true }
            )
            IconButton(onClick = { expanded = true }) {
                Icon(
                    imageVector = Icons.Default.ArrowDropDown,
                    contentDescription = "Icon Drop Down",
                    tint = TextDisabledLight
                )
            }
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
        ) {
            items.forEach { item ->
                DropdownMenuItem(
                    onClick = {
                        onSelectedItemChange(item)
                        expanded = false
                    }
                ) {
                    Text(text = item)
                }
            }
        }
    }
}