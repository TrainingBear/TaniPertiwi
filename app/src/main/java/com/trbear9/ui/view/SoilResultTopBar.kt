import androidx.compose.foundation.ScrollState
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
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.SoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.trbear9.ui.util.Util

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SoilResultTopBar(
    query: String,
    onQueryChange: (String) -> Unit,
    onSearch: () -> Unit,
    selected: String,
    onSelectedChange: (String) -> Unit,
    order: String,
    onOrderChange: (String) -> Unit,
    hasFocus: Boolean,
    onFocusChange: (Boolean) -> Unit,
    expandedSearch: Boolean,
    onExpandedSearchChange: (Boolean) -> Unit,
    focusRequester: FocusRequester,
    keyboard: SoftwareKeyboardController?,
    scroll: ScrollState,
    hasil: Int = 0
) {
    val sortOptions = listOf("Tertinggi", "Terendah")
    val categoryOptions = listOf("All") + Util.getCategory()

    var expandCat by rememberSaveable { mutableStateOf(false) }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFFF5F5F5))
            .statusBarsPadding()
    ) {
        // ── Row 1: Search + Category dropdown ──────────────────────────────
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Search field
            BasicTextField(
                value = query,
                onValueChange = { input ->
                    onQueryChange(input)
                    onExpandedSearchChange(true)
                },
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                keyboardActions = KeyboardActions(
                    onSearch = {
                        onExpandedSearchChange(false)
                        keyboard?.hide()
                        onFocusChange(false)
                        onSearch()
                    }
                ),
                modifier = Modifier
                    .weight(1f)
                    .height(38.dp)
                    .focusRequester(focusRequester)
                    .onFocusChanged { onFocusChange(it.hasFocus) },
                decorationBox = { innerTextField ->
                    Row(
                        modifier = Modifier
                            .fillMaxSize()
                            .border(1.dp, Color.Gray, RoundedCornerShape(10.dp))
                            .padding(horizontal = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = null,
                            tint = Color.Gray,
                            modifier = Modifier.size(18.dp)
                        )
                        Box(modifier = Modifier.weight(1f)) {
                            if (query.isEmpty() && !hasFocus) {
                                Text(
                                    "Masukan nama tanaman",
                                    color = Color.Gray,
                                    fontSize = 14.sp
                                )
                            }
                            innerTextField()
                        }
                        if (query.isNotEmpty()) {
                            IconButton(
                                onClick = { onQueryChange("") },
                                modifier = Modifier.size(20.dp)
                            ) {
                                Icon(
                                    Icons.Default.Close,
                                    contentDescription = "Clear",
                                    tint = Color.Gray,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }
                }
            )

            // Category dropdown chip
            Box {
                Row(
                    modifier = Modifier
                        .height(38.dp)
                        .border(1.dp, Color.LightGray, RoundedCornerShape(10.dp))
                        .clickable { expandCat = !expandCat }
                        .padding(horizontal = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    Text(
                        text = order.take(10) + if (selected.length > 10) ".." else "",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Normal,
                        color = Color.Black
                    )
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowDown,
                        contentDescription = "Tingkat kecocokan",
                        modifier = Modifier.size(20.dp),
                        tint = Color.Black
                    )
                }
                DropdownMenu(
                    expanded = expandCat,
                    onDismissRequest = { expandCat = false },
                    modifier = Modifier.height(300.dp),
                    scrollState = scroll
                ) {
                    sortOptions.forEach { cat ->
                        DropdownMenuItem(
                            text = { Text(cat) },
                            onClick = {
                                expandCat = false
                                onOrderChange(cat)
                                onExpandedSearchChange(false)
                            }
                        )
                    }
                }
            }

        }

        // ── Row 2: Horizontally scrollable sort/filter chips ───────────────
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(start = 12.dp, end = 12.dp, bottom = 10.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "menampilkan $hasil tanaman",
                fontSize = 13.sp,
                color = Color.Black,
                fontWeight = FontWeight.Normal,
            )

            // Divider between sort and category quick-chips
            Spacer(
                modifier = Modifier
                    .height(24.dp)
                    .width(1.dp)
                    .background(Color.LightGray)
            )

            // Category quick-chips
            categoryOptions.forEach { cat ->
                val isSelected = selected == cat
                Text(
                    text = cat,
                    fontSize = 13.sp,
                    color = if (isSelected) Color.White else Color.Black,
                    fontWeight = if (isSelected) FontWeight.Medium else FontWeight.Normal,
                    modifier = Modifier
                        .height(32.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .background(if (isSelected) Color(0xFF1D9E75) else Color.White)
                        .border(
                            1.dp,
                            if (isSelected) Color(0xFF1D9E75) else Color.LightGray,
                            RoundedCornerShape(20.dp)
                        )
                        .clickable {
                            onSelectedChange(cat)
                            onExpandedSearchChange(false)
                        }
                        .padding(horizontal = 12.dp)
                        .wrapContentHeight()
                )
            }
        }

        Divider(color = Color(0xFFE0E0E0), thickness = 0.5.dp)
    }
}

@Composable
fun RoundedCornerShape(x0: Dp) {
    TODO("Not yet implemented")
}
