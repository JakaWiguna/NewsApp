package com.me.newsapp.presentation.component

import androidx.compose.foundation.layout.*
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.me.newsapp.ui.theme.GreypleLight
import com.me.newsapp.ui.theme.TextDisabledLight
import com.me.newsapp.ui.theme.TextLight

@Composable
fun SearchBar(
    value: String,
    onValueChange: (String) -> Unit,
    onModeChange: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val listItems = listOf("Articles", "Sources")

    var selectedValue by remember {
        mutableStateOf(listItems[0])
    }

    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { }) {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Icon Search",
                    tint = TextDisabledLight
                )
            }
            InputEditText(
                modifier = Modifier.weight(1f),
                placeHolderString = "Search...",
                value = value,
                singleLine = true,
                onValueChange = { onValueChange(it) },
                hintTextStyle = TextStyle(
                    fontSize = 14.sp,
                    color = GreypleLight
                ),
                contentTextStyle = TextStyle(
                    fontSize = 14.sp,
                    color = TextLight
                )
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(text = "|", color = GreypleLight)
            Spacer(modifier = Modifier.width(16.dp))
            DropdownSearch(items = listItems, selectedItem = selectedValue, onSelectedItemChange = {
                onValueChange("")
                selectedValue = it
                onModeChange(selectedValue)
            })
        }
    }

}