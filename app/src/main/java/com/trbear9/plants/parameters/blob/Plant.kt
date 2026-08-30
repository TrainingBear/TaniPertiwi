package com.trbear9.plants.parameters.blob

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonSetter
import com.fasterxml.jackson.annotation.Nulls
import com.fasterxml.jackson.databind.JsonNode
import com.trbear9.plants.Constants
import com.trbear9.ui.util.Util
import lombok.AllArgsConstructor
import lombok.Getter
import lombok.NoArgsConstructor
import lombok.Setter
import org.apache.commons.csv.CSVRecord
import java.io.Serializable

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
open class Plant(
    var record: CSVRecord? = null
) : Serializable {
    var full_taxon: JsonNode? = null

    @JvmField
    var nama_umum: MutableSet<String> = HashSet()

    @JvmField
    var category: MutableSet<String> = HashSet()

    @JsonSetter(nulls = Nulls.SKIP)
    var fullsize: String = "Unknown"
    @JsonSetter(nulls = Nulls.SKIP)
    var thumbnail: String = "Unknown"

    @JvmField
    @JsonSetter(nulls = Nulls.SKIP)
    @JsonProperty("nama_ilmiah")
    var nama_ilmiah: String = "Unknown"

    @JvmField
    @JsonSetter(nulls = Nulls.SKIP)
    @JsonProperty("common_names")
    var common_names: String = "Unknown"

    @JvmField
    @JsonSetter(nulls = Nulls.SKIP)
    var family: String = "Unknown"
    @JsonSetter(nulls = Nulls.SKIP)
    var ph: String = "Unknown"
    @JsonSetter(nulls = Nulls.SKIP)
    var temp: String = "Unknown"

    @JvmField
    @JsonSetter(nulls = Nulls.SKIP)
    var genus: String = "Unknown"

    @JsonSetter(nulls = Nulls.SKIP)
    @JvmField
    var kingdom: String = "Unknown"

    @JsonSetter(nulls = Nulls.SKIP)
    @JvmField
    var taxon: String = "Unknown"

    @JvmField
    @JsonSetter(nulls = Nulls.SKIP)
    var kategori: String = "Unknown"

    @JvmField
    @JsonSetter(nulls = Nulls.SKIP)
    @JsonProperty("min_panen")
    var min_panen: Int = 0

    @JvmField
    @JsonSetter(nulls = Nulls.SKIP)
    @JsonProperty("max_panen")
    var max_panen: Int = 0

    @JvmField
    @JsonSetter(nulls = Nulls.SKIP)
    @JsonProperty("plant_care")
    var plantCare: PlantCare = PlantCare()

    @JvmField
    @JsonSetter(nulls = Nulls.SKIP)
    var difficulty: String = "?"
    @JvmField
    @JsonSetter(nulls = Nulls.SKIP)
    var description: String = "Tidak tersedia untuk tanaman ini"

    @JvmField
    @JsonSetter(nulls = Nulls.SKIP)
    @JsonProperty("product_system")
    var productSystem: ProductSystem = ProductSystem()

    @JvmField
    @JsonSetter(nulls = Nulls.SKIP)
    @JsonProperty("common_name")
    var commonName: String = "Unknown"

    @JvmField
    @JsonProperty("prune_guide")
    @JsonSetter(nulls = Nulls.SKIP)
    var pruneGuide: String = "Unknown"

    @JsonIgnore var isAvaiable = false
    @JsonIgnore var minTemperature = record?.get(Constants.O_minimum_temperature)?:"Unknown"
    @JsonIgnore var maxTemperature = record?.get(Constants.O_minimum_temperature)?:"Unknown"
    @JsonIgnore var minPH = record?.get(Constants.O_minimum_ph)?:"Unknown"
    @JsonIgnore var maxPH = record?.get(Constants.O_minimum_ph)?:"Unknown"
    @JsonIgnore var minCycle = record?.get(Constants.MIN_crop_cycle)?:"Unknown"
    @JsonIgnore var maxCycle = record?.get(Constants.MAX_crop_cycle)?:"Unknown"
    @JsonIgnore var salinity = record?.get(Constants.A_soil_salinity)?:"Unknown"
    @JsonIgnore var photoperiod = record?.get(Constants.Photoperiod)?:"Unknown"
    @JsonIgnore var resiko = record?.get(Constants.Introduction_risk)?:"Unknown"
    @JsonIgnore var skalaProduksi = record?.get(Constants.Production_system)?:"Unknown"
    @JsonIgnore var author = record?.get(Constants.Authority)?:"Unknown"
    @JsonIgnore var sinonim = record?.get(Constants.Synonyms)?:"Unknown"
    @JsonIgnore var takson = taxon?:"Unknown"
    @JsonIgnore var iklim = Util.bulkClimateTranslate(record?.get(Constants.Climate_zone)) ?:"Unknown"

    fun load(record:  CSVRecord){
        minTemperature = record.get(Constants.O_minimum_temperature);
        maxTemperature = record.get(Constants.O_minimum_temperature);
        minPH = record.get(Constants.O_minimum_ph);
        maxPH = record.get(Constants.O_minimum_ph);
        minCycle = record.get(Constants.MIN_crop_cycle);
        maxCycle = record.get(Constants.MAX_crop_cycle);
        salinity = record.get(Constants.A_soil_salinity);
        photoperiod = record.get(Constants.Photoperiod);
        resiko = record.get(Constants.Introduction_risk);
        skalaProduksi = record.get(Constants.Production_system)
        author = record.get(Constants.Authority)
        sinonim = record.get(Constants.Synonyms)
        takson = taxon
        iklim = Util.bulkClimateTranslate(record.get(Constants.Climate_zone))?:"Unknown"
    }
}

