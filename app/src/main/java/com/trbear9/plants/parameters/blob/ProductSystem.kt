package com.trbear9.plants.parameters.blob

import lombok.AllArgsConstructor
import lombok.Getter
import lombok.NoArgsConstructor
import lombok.Setter
import java.io.Serializable

@Getter
@Setter
data class ProductSystem(
    var rumah_tangga: String = "Unknwon",
    var komersial: String = "Unknwon",
    var industri: String = "Unknwon"
) : Serializable {
}

