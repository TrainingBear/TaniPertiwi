package com.trbear9.plants.parameters

import com.trbear9.plants.metadata_enumerates.Climate
import lombok.*

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
class GeoParameters : Parameters {
    var iklim: Climate? = Climate.TROPICAL_WET_AND_DRY
    var latitude: Double = -7.2565293 // di sekitar ambarawa (default)
    var longtitude: Double = 110.402824 // di sekitar ambarawa (default)
    var altitude: Double = 0.0
    /** Curah hujan */
    var rainfall: Float = 2000.0f // di jawa tengah (default)
    /** Tempratur min*/
    var min: Float = 18.0f
    /** Tempratur max */
    var max: Float = 31.0f // di seluruh indonesia (default)
}