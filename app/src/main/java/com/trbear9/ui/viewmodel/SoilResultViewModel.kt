package com.trbear9.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.trbear9.plants.parameters.blob.Plant
import com.trbear9.ui.util.info
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.milliseconds

/**
 * Same shape as [SearchViewModel], but the data source is the soil recommendation
 * result (`recommendationResult`) whose items are Pair<score, Plant>.
 * Reuses [SortOrder], [ALL_CATEGORY] and [ListPagingSource].
 */
@OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
class SoilResultViewModel : ViewModel() {

    // ── Inputs ─────────────────────────────────────────────────────────────
    private val _query = MutableStateFlow("")
    private val _submitted = MutableStateFlow("")
    private val _category = MutableStateFlow(ALL_CATEGORY)
    private val _order = MutableStateFlow(SortOrder.HIGHEST)

    val query: StateFlow<String> = _query
    val category: StateFlow<String> = _category
    val order: StateFlow<SortOrder> = _order

    fun onQueryChange(value: String) { _query.value = value }
    fun onCategoryChange(value: String) { _category.value = value }
    fun onOrderChange(value: SortOrder) { _order.value = value }
    fun submit() { _submitted.value = _query.value.trim() }

    // ── Recommendation collection (was a LaunchedEffect in the screen) ────
    private val _ready = MutableStateFlow(recommendationResult.collected)
    /** true once the recommendation flow has fully finished. */
    val ready: StateFlow<Boolean> = _ready

    init {
        viewModelScope.launch {
            val result = recommendationResult
            if (result.collected) {
                _ready.value = true
                return@launch
            }
            "Collecting".info(this)
            result.flow
                ?.onCompletion { cause ->
                    // only mark as collected on a real completion, not on cancellation/error
                    if (cause == null) {
                        result.collected = true
                        result.flow = null
                        _ready.value = true
                        "Collected".info(this)
                    }
                }
                ?.catch { "Collect failed: ${it.message}".info(this) }
                ?.collect { /* results are read from recommendationResult */ }
        }
    }

    // ── Autocomplete ───────────────────────────────────────────────────────
    val suggestions: StateFlow<List<String>> = _query
        .debounce(150.milliseconds)
        .map { q ->
            if (q.isBlank()) emptyList()
            else mutableSetOf<String>().also { completer(q.trim(), it, 5) }.toList()
        }
        .flowOn(Dispatchers.Default)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    // ── Results ────────────────────────────────────────────────────────────
    private val filtered: StateFlow<List<Pair<Int, Plant>>> =
        combine(_ready, _submitted, _category, _order) { ready, q, category, order ->
            when {
                !ready -> emptyList()
                q.isEmpty() -> recommendationResult.plantByCategory[category]
                    ?.flatten(order.label).orEmpty()
                else -> search(q, order.label, category)
            }
        }
            .flowOn(Dispatchers.Default)
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val resultCount: StateFlow<Int> = filtered
        .map { it.size }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), 0)

    val results: Flow<PagingData<Pair<Int, Plant>>> = filtered
        .flatMapLatest { list ->
            Pager(PagingConfig(pageSize = 20, prefetchDistance = 5)) {
                ListPagingSource(list)
            }.flow
        }
        .cachedIn(viewModelScope)
}
