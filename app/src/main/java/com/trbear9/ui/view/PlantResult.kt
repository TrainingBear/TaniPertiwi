@file:Suppress("LombokKotlinCompilerPlugin")

package com.trbear9.ui.view

import SoilResultTopBar
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Park
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.mutableStateSetOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.paging.LoadState
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.pseudoankit.coachmark.UnifyCoachmark
import com.trbear9.plants.parameters.blob.Plant
import com.trbear9.ui.LocalNav
import com.trbear9.ui.fragment.NavigateSoilStats
import com.trbear9.ui.fragment.PlantCardDisplayer
import com.trbear9.ui.util.ResultPagingSource
import com.trbear9.ui.util.Screen
import com.trbear9.ui.util.debug
import com.trbear9.ui.util.info
import com.trbear9.ui.viewmodel.completer
import com.trbear9.ui.viewmodel.flatten
import com.trbear9.ui.viewmodel.recommendationResult
import com.trbear9.ui.viewmodel.search
import kotlinx.coroutines.flow.onCompletion

var coachStep = 0

@OptIn(ExperimentalMaterial3Api::class)
@Composable
@Preview()
fun SoilResultScreen() {
    val keyboard = LocalSoftwareKeyboardController.current
    var query by rememberSaveable { mutableStateOf("") }
    var finalQuery by rememberSaveable { mutableStateOf("") }
    var selected by rememberSaveable { mutableStateOf("All") }
    var expanded by rememberSaveable { mutableStateOf(false) }
    var expandedSearch by rememberSaveable { mutableStateOf(false) }
    var expandCat by rememberSaveable { mutableStateOf(false) }
    var hasFocus by rememberSaveable { mutableStateOf(false) }
    var order by rememberSaveable { mutableStateOf("Tertinggi") }
    var scroll = rememberScrollState()
    var completerScroll = rememberScrollState()

    var search = remember { mutableStateSetOf<String>() }
    var focusRequester = remember { FocusRequester() }

    LaunchedEffect(Unit) {
        val result = recommendationResult
        "It is already collected? ${result.collected}".debug(this)
        if (!result.collected) {
            "Collecting".info(this)
            result.flow?.onCompletion {
                result.collected = true
                result.flow = null
                "Collected".info(this)
            }?.collect {
                // TODO
            }
        }
    }
    val completer = rememberSaveable { mutableStateSetOf<String>() }
    LaunchedEffect(query) {
        completer.clear()
        completer(query, completer, 5)
        "Updated ${completer.size} items".debug(this)
    }

    Scaffold(
        topBar = {
            SoilResultTopBar(
                query = query,
                onQueryChange = { query = it; search.clear() },
                onSearch = { finalQuery = query },
                selected = selected,
                onSelectedChange = { selected = it },
                order = order,
                onOrderChange = { order = it },
                hasFocus = hasFocus,
                onFocusChange = { hasFocus = it },
                expandedSearch = expandedSearch,
                onExpandedSearchChange = { expandedSearch = it },
                focusRequester = focusRequester,
                keyboard = keyboard,
                scroll = scroll,
                hasil = recommendationResult.response.total
            )
        },
        bottomBar = {
            var selected by rememberSaveable { mutableIntStateOf(1) }
            NavigationBar {
                NavigationBarItem(
                    selected = selected == 0,
                    onClick = {
                        selected = 0
                        LocalNav.current?.navigate(Screen.home)
                    },
                    icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
                    label = { Text("Home") }
                )
                NavigationBarItem(
                    selected = selected == 1,
                    onClick = {
                        selected = 1
                    },
                    icon = { Icon(Icons.Default.Park, contentDescription = "Hasil") },
                    label = { Text("Rekomendasi Tanaman") }
                )
            }
        })
    { padding ->
        "ExpandedSearch: $expandedSearch".debug("PlantResult")
        UnifyCoachmark {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            ) {
                if (recommendationResult.collecting) {
                    Column(
                        modifier = Modifier.align(Alignment.Center),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        CircularProgressIndicator()
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = if (!recommendationResult.response.predicted) "Menganalisa tanahmu.."
                            else if (!recommendationResult.response.geoParameterLoaded) "Mencari riwayat suhu 6 hari terakhir di daerah mu...\nPastikan koneksimu terhubung ke internet\nUntuk hasil yang lebih akurat!"
                            else "Mencari data ${recommendationResult.response.current}",
                            fontSize = 16.sp,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(20.dp)
                        )
                    }
                    return@Box
                } else if (!recommendationResult.collected) {
                    Column(
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Text(
                            text = "Ooops!",
                            textAlign = TextAlign.Center,
                            fontSize = 40.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Tidak dapat menemukan jenis tanaman dengan tanahmu",
                            textAlign = TextAlign.Center,
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Button(
                            onClick = {
                                LocalNav.current?.navigate(Screen.camera)
                            },
                            modifier = Modifier
                                .padding(top = 20.dp)
                        ) {
                            Text(
                                text = "Scan tanahmu!",
                                fontWeight = FontWeight.Bold,
                                fontSize = 30.sp
                            )
                        }
                    }
                    return@Box
                }
                val pagerFlow =
                    if (finalQuery.isEmpty())
                        remember(selected, order) {
                            Pager(PagingConfig(pageSize = 7)) {
                                ResultPagingSource(
                                    items = recommendationResult.plantByCategory[selected]?.flatten(
                                        order
                                    ) ?: mutableListOf()
                                )
                            }.flow
                        }
                    else
                        remember(finalQuery, order, selected) {
                            Pager(PagingConfig(pageSize = 7)) {
                                ResultPagingSource(items = search(query, order, selected))
                            }.flow
                        }
                val plants: LazyPagingItems<Pair<Int, Plant>> = pagerFlow.collectAsLazyPagingItems()
                if (recommendationResult.plants.isNotEmpty()) {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        items(plants.itemCount) { i ->
                            PlantCardDisplayer(plants[i]!!.first, plants[i]!!.second)
                            "Displaying ${plants[i]!!.second}".info("SoilResultScreen")
                        }

//                        if(plants.itemCount >= 1) item {
//                            PlantCardDisplayer(plants[0]!!.first, Data.plant[plants[0]!!.second])
////                            "Displaying ${Data.plant[plants[i]!!.second]?.commonName}".info("SoilResultScreen")
//                        }

                        plants.apply {
                            when {
                                loadState.refresh is LoadState.Loading -> {
                                    item { Text("Loading...") }
                                }

                                loadState.append is LoadState.Loading -> {
                                    item { Text("Loading more...") }
                                }

                                loadState.append is LoadState.Error -> {
                                    item { Text("Error loading more.") }
                                }
                            }
                        }

                    }
                } else if (plants.itemCount == 0)
                    Text(
                        text = "Tidak dapat menemukan jenis tanaman dengan tanahmu",
                        textAlign = TextAlign.Center,
                        fontSize = 28.sp,
                        modifier = Modifier.align(Alignment.Center)
                    )
            }
            if (recommendationResult.response.completed) NavigateSoilStats(
                Modifier
                    .fillMaxSize()
                    .padding(padding)
            )
        }
        if (expandedSearch) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .background(Color.DarkGray.copy(alpha = 0.8f))
                    .clip(RoundedCornerShape(bottomStart = 10.dp, bottomEnd = 10.dp))
            ) {
                Box(
                    modifier = Modifier
                        .height(150.dp)
                        .fillMaxWidth()
                        .background(Color.White)
                        .padding(top = 10.dp),
                ) {
                    Column(
                        modifier = Modifier.verticalScroll(completerScroll),
                        horizontalAlignment = Alignment.Start
                    ) {
                        completer.forEach {
                            Text(
                                text = it,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Medium,
                                modifier = Modifier
                                    .clickable {
                                        query = it
                                    }
                                    .padding(start = 16.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}