package com.trbear9.plants.parameters

import android.content.Context
import android.graphics.Bitmap
import com.trbear9.ui.util.debug
import com.trbear9.ui.util.info
import lombok.*

@Getter
@AllArgsConstructor
@NoArgsConstructor
/**
 * Obsolete
 * it is now using Parameter.kt as global user variable / parameter
 */
@Deprecated("Use global parameter")
class UserVariable {
    @Setter
    private var tanah: String? = null

    //parameters
    var soil: SoilParameters = SoilParameters()
    var geo: GeoParameters = GeoParameters()
    var custom: CustomParameters? = null

    var image: Bitmap? = null
    var context: Context? = null
    var hash: String? = null


}
