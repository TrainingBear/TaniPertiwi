package com.trbear9.ui.view

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.pseudoankit.coachmark.UnifyCoachmark
import com.trbear9.plants.parameters.blob.Plant
import com.trbear9.ui.fragment.PlantCardDisplayer
import com.trbear9.ui.util.Util
import com.trbear9.ui.viewmodel.ALL_CATEGORY
import com.trbear9.ui.viewmodel.SearchViewModel
import androidx.compose.runtime.collectAsState
import com.trbear9.plants.metadata_enumerates.Category
import com.trbear9.ui.model.plantRepository
import com.trbear9.ui.viewmodel.SearchTopBar

@Composable
fun SearchLayout(
    vm: SearchViewModel = SearchViewModel(plantRepository.plant.values.toList()),
) {
    val query by vm.query.collectAsState()
    val category by vm.category.collectAsState()
    val order by vm.order.collectAsState()
    val count by vm.resultCount.collectAsState()
    val suggestions by vm.suggestions.collectAsState()
    val plants = vm.results.collectAsLazyPagingItems()

    val keyboard = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current
    var searchFocused by remember { mutableStateOf(false) }

    fun submit() {
        vm.submit()
        keyboard?.hide()
        focusManager.clearFocus()
    }

    Scaffold(
        topBar = {
            SearchTopBar(
                query = query,
                onQueryChange = vm::onQueryChange,
                onSubmit = ::submit,
                onFocusChange = { searchFocused = it },
                category = category,
                onCategoryChange = vm::onCategoryChange,
                order = order,
                onOrderChange = vm::onOrderChange,
                resultCount = count,
            )
        }
    ) { padding ->
        Box(Modifier.fillMaxSize().padding(padding)) {
            PlantResultList(plants)

            if (searchFocused && suggestions.isNotEmpty()) {
                SuggestionList(
                    suggestions = suggestions,
                    onPick = { plant ->
                        vm.onQueryChange(plant.commonName)
                        submit()
                    },
                    modifier = Modifier.align(Alignment.TopCenter),
                )
            }
        }
    }
}

@Composable
fun PlantResultList(plants: LazyPagingItems<Plant>) {
    val refreshing = plants.loadState.refresh is LoadState.Loading
    val isEmpty = plants.itemCount == 0 && !refreshing &&
        plants.loadState.refresh is LoadState.NotLoading

    UnifyCoachmark {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            items(plants.itemCount) { i ->
                plants[i]?.let { PlantCardDisplayer(0, it, false) }
            }

            when {
                refreshing -> item { StatusText("Memuat…") }
                isEmpty -> item { StatusText("Tidak ada tanaman ditemukan") }
                plants.loadState.append is LoadState.Loading -> item { StatusText("Memuat lebih banyak…") }
                plants.loadState.append is LoadState.Error -> item {
                    StatusText("Gagal memuat. Ketuk untuk coba lagi", onClick = { plants.retry() })
                }
            }
        }
    }
}

@Composable
private fun StatusText(text: String, onClick: (() -> Unit)? = null) {
    Text(
        text = text,
        modifier = Modifier
            .padding(16.dp)
            .then(if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier),
    )
}

@Composable
fun SuggestionList(
    suggestions: List<Plant>,
    onPick: (Plant) -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shadowElevation = 6.dp,
        shape = RoundedCornerShape(bottomStart = 10.dp, bottomEnd = 10.dp),
    ) {
        LazyColumn(Modifier.heightIn(max = 240.dp)) {
            items(suggestions, key = { it.nama_ilmiah }) { plant ->
                Text(
                    text = plant.commonName,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onPick(plant) }
                        .padding(horizontal = 16.dp, vertical = 10.dp),
                )
            }
        }
    }
}
