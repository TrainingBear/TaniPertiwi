package com.trbear9.plants.parameters.blob

import lombok.AllArgsConstructor
import lombok.Getter
import lombok.NoArgsConstructor
import lombok.Setter
import java.io.Serializable

@Getter
@Setter
data class ProductSystem(
    var rumah_tangga: String = "Belum Diketahui",
    var komersial: String = "Belum Diketahui",
    var industri: String = "Belum Diketahui"
) : Serializable {
    companion object {
        private const val serialVersionUID = 6886084022447035419L
    }
}

