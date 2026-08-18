package com.trbear9.ui.view

import SoilResultTopBar
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.paging.LoadState
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingSource
import androidx.paging.PagingState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.pseudoankit.coachmark.UnifyCoachmark
import com.trbear9.plants.Data
import com.trbear9.plants.parameters.blob.Plant
import com.trbear9.ui.fragment.PlantCardDisplayer
import com.trbear9.ui.model.Searcher
import com.trbear9.ui.util.error
import com.trbear9.ui.viewmodel.databaseSearchResult
import kotlinx.coroutines.delay
import kotlin.math.min

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
fun SearchLayout(nav: NavController? = null, from: String = "home") {
    val scroll = rememberScrollState()
    var selected by rememberSaveable { mutableStateOf("All") }
    var query by rememberSaveable { mutableStateOf("") }
    var finalQuery by rememberSaveable { mutableStateOf("") }
    val completer = rememberSaveable { mutableListOf<Plant>() }
    val focusRequester = remember { FocusRequester() }

    var focus by rememberSaveable { mutableStateOf(false) }
    val keyboard = LocalSoftwareKeyboardController.current

    fun search() {
        if (query.isEmpty()) return
        databaseSearchResult.reset()
        Searcher.searchByScienceName(9999, query = query, output = databaseSearchResult)
        Searcher.searchByCommonName(9999, query = query, output = databaseSearchResult)
        keyboard?.hide()
        finalQuery = query
        focus = false
    }

    LaunchedEffect(Unit) {
//        runBlocking {
//            delay(900)
//            focusRequester.requestFocus()
//        }
    }

    LaunchedEffect(query) {
        completer.clear()
        focus = true
        if (query.isNotEmpty()) {
            Data.plant.values.filter { it.nama_ilmiah.contains(query) }.forEach { completer.add(it) }
            Data.plant.values.filter { it.nama_umum.contains(query) }.forEach { completer.add(it) }
        }
    }
    val source = CompleterSearchSource(completer)
    val pagerFlow = remember(query) {
        Pager(PagingConfig(pageSize = 20, prefetchDistance = 5)) {
            source
        }.flow
    }
    val items = pagerFlow.collectAsLazyPagingItems()

    var order by rememberSaveable { mutableStateOf("Tertinggi") }
    var expandedSearch by rememberSaveable { mutableStateOf(false) }
    var hasFocus by rememberSaveable { mutableStateOf(false) }
    Scaffold(
        topBar = {
            SoilResultTopBar(
                query = query,
                onQueryChange = { query = it; completer.clear() },
                onSearch = { finalQuery = query; search() },
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
            )
        }
    ) { padding ->
        val pagerFlow = if (finalQuery.isEmpty())
                remember(selected) {
                    Pager(PagingConfig(pageSize = 7)) {
                        CompleterSearchSource(
                            Data.plantByTag[selected]?.toMutableList() ?: mutableListOf()
                        )
                    }.flow
                }
            else
                remember(finalQuery, selected) {
                    Pager(PagingConfig(pageSize = 7)) {
                        CompleterSearchSource(
                            databaseSearchResult.plantByCategory.get(selected)?.toMutableList()
                                ?: mutableListOf()
                        )
                    }.flow
                }
        val plants: LazyPagingItems<Plant> =
            pagerFlow.collectAsLazyPagingItems()

        UnifyCoachmark {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                items(plants.itemCount) { i ->
                    val plant = plants[i]
                    if (plant != null) PlantCardDisplayer(0, plant, true)
                }

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
        }
        if (focus && query.isNotEmpty()) {
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
                    LazyColumn(
                        horizontalAlignment = Alignment.Start
                    ) {
                        items(items.itemCount) { i ->
                            Text(
                                text = items[i]!!.commonName,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Medium,
                                modifier = Modifier
                                    .clickable {
                                        query = items[i]!!.commonName
                                    }
                                    .padding(start = 16.dp)
                                    .fillMaxWidth()
                            )
                        }
                    }
                }
            }
        }
    }
}

class CompleterSearchSource(val completer: MutableList<Plant>) : PagingSource<Int, Plant>() {
    override fun getRefreshKey(state: PagingState<Int, Plant>): Int? {
        return state.anchorPosition?.let { pos ->
            state.closestPageToPosition(pos)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(pos)?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Plant> {
        val page = params.key ?: 0
        val pageSize = 20

        return try {
            delay(500)
            val nextKey = if (page.toDouble() >= (completer.size / pageSize)) null else page + 1
            val item = completer.subList(
                page * pageSize,
                min((page + 1) * pageSize, completer.size)
            ).toList()
            LoadResult.Page(
                data = item,
                prevKey = if (page == 0) null else page - 1,
                nextKey = nextKey
            )
        } catch (e: Exception) {
            "Error when loading page $page: ${e.message}".error("Search")
            e.printStackTrace()
            LoadResult.Error(e)
        }
    }
}