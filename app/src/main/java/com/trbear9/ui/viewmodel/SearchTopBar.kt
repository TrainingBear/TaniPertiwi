package com.trbear9.ui.viewmodel

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.trbear9.plants.metadata_enumerates.Category

private val Green = Color(0xFF1D9E75)

@Composable
fun SearchTopBar(
    query: String,
    onQueryChange: (String) -> Unit,
    onSubmit: () -> Unit,
    onFocusChange: (Boolean) -> Unit,
    category: Category,
    onCategoryChange: (Category) -> Unit,
    order: SortOrder,
    onOrderChange: (SortOrder) -> Unit,
    resultCount: Int,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier
            .fillMaxWidth()
            .background(Color(0xFFF5F5F5))
            .statusBarsPadding()
    ) {
        // Row 1: search field + sort dropdown
        Row(
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            SearchField(
                query = query,
                onQueryChange = onQueryChange,
                onSubmit = onSubmit,
                onFocusChange = onFocusChange,
                modifier = Modifier.weight(1f),
            )
            SortDropdown(order = order, onOrderChange = onOrderChange)
        }

        // Row 2: result count + category chips
        Row(
            Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(start = 12.dp, end = 12.dp, bottom = 10.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text("menampilkan $resultCount tanaman", fontSize = 13.sp)
            Spacer(Modifier
                .height(24.dp)
                .width(1.dp)
                .background(Color.LightGray))
            Category.entries.forEach { cat ->
                CategoryChip(cat.toString(), selected = cat == category) { onCategoryChange(cat) }
            }
        }

        Divider(color = Color(0xFFE0E0E0), thickness = 0.5.dp)
    }
}

@Composable
private fun SearchField(
    query: String,
    onQueryChange: (String) -> Unit,
    onSubmit: () -> Unit,
    onFocusChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    BasicTextField(
        value = query,
        onValueChange = onQueryChange,
        singleLine = true,
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
        keyboardActions = KeyboardActions(onSearch = { onSubmit() }),
        modifier = modifier
            .height(38.dp)
            .onFocusChanged { onFocusChange(it.hasFocus) },
        decorationBox = { inner ->
            Row(
                Modifier
                    .fillMaxSize()
                    .border(1.dp, Color.Gray, RoundedCornerShape(10.dp))
                    .padding(horizontal = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                Icon(Icons.Default.Search, null, Modifier.size(18.dp), tint = Color.Gray)
                Box(Modifier.weight(1f)) {
                    if (query.isEmpty()) {
                        Text("Masukan nama tanaman", color = Color.Gray, fontSize = 14.sp)
                    }
                    inner()
                }
                if (query.isNotEmpty()) {
                    IconButton(onClick = {
                        onQueryChange("")
                        onSubmit()
                    }, modifier = Modifier.size(20.dp)) {
                        Icon(Icons.Default.Close, "Clear", Modifier.size(16.dp), tint = Color.Gray)
                    }
                }
            }
        },
    )
}

@Composable
private fun SortDropdown(order: SortOrder, onOrderChange: (SortOrder) -> Unit) {
    var expanded by rememberSaveable { mutableStateOf(false) }
    Box {
        Row(
            Modifier
                .height(38.dp)
                .border(1.dp, Color.LightGray, RoundedCornerShape(10.dp))
                .clickable { expanded = true }
                .padding(horizontal = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(2.dp),
        ) {
            Text(order.label, fontSize = 14.sp, color = Color.Black)
            Icon(Icons.Default.KeyboardArrowDown, "Tingkat kecocokan", Modifier.size(20.dp))
        }
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            SortOrder.entries.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option.label) },
                    onClick = { expanded = false; onOrderChange(option) },
                )
            }
        }
    }
}

@Composable
private fun CategoryChip(label: String, selected: Boolean, onClick: () -> Unit) {
    val shape = RoundedCornerShape(20.dp)
    Text(
        text = label,
        fontSize = 13.sp,
        color = if (selected) Color.White else Color.Black,
        fontWeight = if (selected) FontWeight.Medium else FontWeight.Normal,
        modifier = Modifier
            .height(32.dp)
            .clip(shape)
            .background(if (selected) Green else Color.White)
            .border(1.dp, if (selected) Green else Color.LightGray, shape)
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp)
            .wrapContentHeight(),
    )
}
