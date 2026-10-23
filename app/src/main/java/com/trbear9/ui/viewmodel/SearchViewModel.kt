package com.trbear9.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.PagingSource
import androidx.paging.PagingState
import androidx.paging.cachedIn
import com.trbear9.plants.metadata_enumerates.Category
import com.trbear9.plants.parameters.blob.Plant
import com.trbear9.ui.model.Searcher
import com.trbear9.ui.model.plantRepository
import com.trbear9.ui.util.info
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlin.math.max
import kotlin.math.min
import kotlin.time.Duration.Companion.milliseconds

enum class SortOrder(val label: String) {
    HIGHEST("Tertinggi"),
    LOWEST("Terendah"),
}

const val ALL_CATEGORY = "All"

@OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
class SearchViewModel : ViewModel() {

    // ── Inputs (single source of truth) ────────────────────────────────────
    private val _query = MutableStateFlow("")            // what is typed
    private val _submitted = MutableStateFlow("")        // what was actually searched
    private val _category = MutableStateFlow(Category.ALL)
    private val _order = MutableStateFlow(SortOrder.HIGHEST)

    val query: StateFlow<String> = _query
    val category: StateFlow<Category> = _category
    val order: StateFlow<SortOrder> = _order

    fun onQueryChange(value: String) {
        _query.value = value
    }

    fun onCategoryChange(value: Category) {
        _category.value = value
    }

    fun onOrderChange(value: SortOrder) {
        _order.value = value
    }

    fun submit() {
        _submitted.value = _query.value.trim()
    }

    // ── Autocomplete (cheap, in-memory, no paging needed) ──────────────────
    val suggestions: StateFlow<List<Plant>> = _query
        .debounce(150.milliseconds)
        .map { suggest(it.trim()) }
        .flowOn(Dispatchers.Default)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    // ── Results ────────────────────────────────────────────────────────────
    // 1) the (expensive) search only re-runs when the submitted query changes
    private val searchResult: Flow<List<Plant>?> = _submitted
        .map { q ->
            if (q.isEmpty()) null else Searcher.search(plantRepository.plant.values, q)
        }
        .flowOn(Dispatchers.Default)

    // 2) category + order are cheap and applied on top of it
    private val filtered: StateFlow<List<Plant>> =
        combine(searchResult, _category, _order) { result, category, order ->
            val base = (result ?: plantRepository.plant.values.toList())
            val filt = if (category == Category.ALL) base else base.filter {
                "Filtering $it ${it.category}".info(this)
                it.category.contains(category)
            }
            filt.sortedFor(order)
        }
            .flowOn(Dispatchers.Default)
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val resultCount: StateFlow<Int> = filtered
        .map { it.size }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), 0)

    // 3) paging is built on top of the filtered list, cached in the ViewModel
    val results: Flow<PagingData<Plant>> = filtered
        .flatMapLatest { list ->
            Pager(PagingConfig(pageSize = 20, prefetchDistance = 5)) {
                ListPagingSource(list)
            }.flow
        }
        .cachedIn(viewModelScope)

    // ── Helpers ────────────────────────────────────────────────────────────
    private fun suggest(q: String): List<Plant> {
        if (q.isEmpty()) return emptyList()
        return Searcher.search(plantRepository.plant.values, q)
            .distinctBy { it.nama_ilmiah }
            .take(20)
    }

    private fun List<Plant>.sortedFor(order: SortOrder): List<Plant> = when (order) {
        SortOrder.HIGHEST -> sortedByDescending { it.score }
        SortOrder.LOWEST -> sortedBy { it.score }
    }
}

/** Generic offset-based PagingSource over an in-memory list. */
class ListPagingSource<T : Any>(private val items: List<T>) : PagingSource<Int, T>() {

    override fun getRefreshKey(state: PagingState<Int, T>): Int? =
        state.anchorPosition?.let { max(0, it - state.config.initialLoadSize / 2) }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, T> {
        val start = (params.key ?: 0).coerceIn(0, items.size)
        val end = min(start + params.loadSize, items.size)
        return LoadResult.Page(
            data = items.subList(start, end),
            prevKey = if (start == 0) null else max(0, start - params.loadSize),
            nextKey = if (end >= items.size) null else end,
        )
    }
}
