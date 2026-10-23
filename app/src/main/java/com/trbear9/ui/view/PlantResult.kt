package com.trbear9.ui.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Park
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.pseudoankit.coachmark.UnifyCoachmark
import com.trbear9.plants.parameters.blob.Plant
import com.trbear9.ui.LocalNav
import com.trbear9.ui.fragment.NavigateSoilStats
import com.trbear9.ui.fragment.PlantCardDisplayer
import com.trbear9.ui.util.Screen
import com.trbear9.ui.util.info
import com.trbear9.ui.viewmodel.SearchTopBar
import com.trbear9.ui.viewmodel.SearchViewModel
import com.trbear9.ui.viewmodel.recommendationResult
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onCompletion

var coachStep = 0

@Composable
@Preview
fun SoilResultScreen(vm: SearchViewModel = SearchViewModel(recommendationResult.plants.toList())) {
    val nav = LocalNav.current
    val keyboard = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current

    val query by vm.query.collectAsState()
    val category by vm.category.collectAsState()
    val order by vm.order.collectAsState()
    val count by vm.resultCount.collectAsState()
    val suggestions by vm.suggestions.collectAsState()
    val plants = vm.results.collectAsLazyPagingItems()

    var searchFocused by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        val result = recommendationResult
        if (result.collected) {
            return@LaunchedEffect
        }
        "Collecting".info(this)
        result.flow
            ?.onCompletion { cause ->
                // only mark as collected on a real completion, not on cancellation/error
                if (cause == null) {
                    result.flow = null
                    result.collected = true
                    recommendationResult.loaded = true
                    "Collected".info(this)
                    vm.refresh()
                }
            }
            ?.catch { "Collect failed: ${it.message}".info(this) }
            ?.collect {
            }
    }

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
        },
        bottomBar = { SoilResultBottomBar(onHome = { nav?.navigate(Screen.home) }) },
    ) { padding ->
        UnifyCoachmark {
            Box(
                Modifier
                    .fillMaxSize()
                    .padding(padding)
            ) {
                when {
                    recommendationResult.collecting -> LoadingState(
                        message = loadingMessage(),
                        modifier = Modifier.align(Alignment.Center),
                    )

                    !recommendationResult.loaded && !recommendationResult.collecting -> NoSoilMatch(
                        onScan = { nav?.navigate(Screen.camera) })
                    else -> ResultList(plants)
                }

                if (searchFocused && suggestions.isNotEmpty()) {
                    SuggestionList(
                        suggestions = suggestions,
                        onPick = { text ->
                            vm.onQueryChange(text.commonName)
                            submit()
                        },
                        modifier = Modifier.align(Alignment.TopCenter),
                    )
                }

                if (recommendationResult.response.completed) {
                    NavigateSoilStats(Modifier.fillMaxSize())
                }
            }
        }
    }
}

@Composable
private fun loadingMessage(): String {
    val response = recommendationResult.response
    return when {
        !response.predicted -> "Menganalisa tanahmu.."
        !response.geoParameterLoaded ->
            "Mencari riwayat suhu 6 hari terakhir di daerah mu...\nPastikan koneksimu terhubung ke internet\nUntuk hasil yang lebih akurat!"

        else -> "Mencari data ${response.current}"
    }
}

@Composable
private fun LoadingState(message: String, modifier: Modifier = Modifier) {
    Column(modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        CircularProgressIndicator()
        Spacer(Modifier.height(16.dp))
        Text(
            text = message,
            fontSize = 16.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(20.dp),
        )
    }
}

@Composable
private fun NoSoilMatch(onScan: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text("Ooops!", fontSize = 40.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
        Text(
            "Tidak dapat menemukan jenis tanaman dengan tanahmu",
            fontSize = 28.sp,
            fontWeight = FontWeight.Medium,
            textAlign = TextAlign.Center,
        )
        Button(onClick = onScan, modifier = Modifier.padding(top = 20.dp)) {
            Text("Scan tanahmu!", fontWeight = FontWeight.Bold, fontSize = 30.sp)
        }
    }
}

@Composable
private fun ResultList(plants: LazyPagingItems<Plant>) {
    val refresh = plants.loadState.refresh
    val isEmpty = plants.itemCount == 0 && refresh is LoadState.NotLoading

    if (isEmpty) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(
                "Tidak dapat menemukan jenis tanaman dengan tanahmu",
                fontSize = 28.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(20.dp),
            )
        }
        return
    }
    UnifyCoachmark {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            items(plants.itemCount) { i ->
                plants[i]?.let { plant -> PlantCardDisplayer(plant.score, plant) }
            }
            when {
                refresh is LoadState.Loading -> item { Text("Memuat…", Modifier.padding(16.dp)) }
                plants.loadState.append is LoadState.Loading ->
                    item { Text("Memuat lebih banyak…", Modifier.padding(16.dp)) }

                plants.loadState.append is LoadState.Error -> item {
                    Button(onClick = { plants.retry() }, modifier = Modifier.padding(16.dp)) {
                        Text("Gagal memuat. Coba lagi")
                    }
                }
            }
        }
    }
}

@Composable
private fun SoilResultBottomBar(onHome: () -> Unit) {
    NavigationBar {
        NavigationBarItem(
            selected = false,
            onClick = onHome,
            icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
            label = { Text("Home") },
        )
        NavigationBarItem(
            selected = true,
            onClick = {},
            icon = { Icon(Icons.Default.Park, contentDescription = "Hasil") },
            label = { Text("Rekomendasi Tanaman") },
        )
    }
}