package com.trbear9.plants.parameters

import com.trbear9.plants.Constants
import com.trbear9.plants.metadata_enumerates.Category
import com.trbear9.plants.metadata_enumerates.Lifespan
import lombok.*

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
class CustomParameters : Parameters {
    var category: Category? = null
    var lifeSpan: Lifespan? = null
    var query: String? = null
    var panen: Int? = null
    fun getParameters(): MutableMap<String?, String?> {
        val map: MutableMap<String?, String?> = HashMap()
        map.put(Constants.Category, category?.origin)
        map.put(Constants.Life_span, lifeSpan?.origin)
        map.put("PANEN", panen?.toString())
        map.put("QUERY", query)
        return map
    }
}
