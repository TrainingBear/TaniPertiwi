package com.trbear9.plants.DUMMY

import com.trbear9.plants.metadata_enumerates.Category
import com.trbear9.plants.parameters.blob.Plant
import com.trbear9.plants.parameters.blob.PlantCare
import com.trbear9.plants.parameters.blob.ProductSystem

class AdamHawa : Plant() {
    init {
        score = 6
        thumbnail = "Nanas Kering.jpg"
        fullsize = "Nanas Kering.jpg"
        nama_ilmiah = "Tradescantia spathacea"
        common_names = "Nanas Kerang"
        commonName = "Nanas Kerang"
        description =
            "Nanas Kerang adalah sejenis tumbuhan yang biasa ditanam orang sebagai tanaman hias. Tumbuhan ini biasanya tumbuh subur di tanah yang lembap. Termasuk anggota suku gawar-gawaran yang berasal dari Meksiko dan Hindia Barat. Tumbuhan Nanas Kerang ini berasal dari Meksiko."
        difficulty = "EASY"
        minPH = "5.5"
        maxPH = "7"
        minTemperature = "18"
        maxTemperature = "27"
        family = "Tradescantia"
        genus = "spathacea"
        photoperiod = "sensitive"
        kingdom = "Plantae"
        taxon = "https://powo.science.kew.org/taxon/urn:lsid:ipni.org:names:173471-1"
        plantCare = PlantCare(
            "watering Sirami secara teratur, jaga agar tanah selalu lembap tapi tidak tergenang. Biasanya 2–3 kali seminggu, lebih sering pada cuaca panas.",
            "pruning :angkas daun luar dengan memotong batang dekat pangkal untuk merangsang pertumbuhan baru. Hindari memanen lebih dari sepertiga tanaman sekaligus.",
            "fertilization Beri pupuk NPK seimbang (misalnya 10-10-10) setiap 4–6 minggu sekali atau pupuk kompos organik untuk menjaga kesuburan tanah.",
            "sunlight Tempatkan di area dengan sinar matahari penuh hingga teduh parsial, minimal 4–6 jam cahaya matahari per hari.",
            "pest_disease_management Waspadai hama seperti kutu daun dan penyakit jamur (busuk abu-abu, embun tepung). Gunakan insektisida alami seperti sabun insektisida, jaga sirkulasi udara, aplikasikan fungisida jika perlu."
        )
        productSystem = ProductSystem(
            "Penghijauan interior dan eksterior rumah, pot hias di teras.",
            "Lansekap taman hotel, perkantoran, pusat perbelanjaan.",
            "Sumber serat daun untuk tali, bahan kertas, serta ekstrak potensial obat herbal."
        )
        category.add(Category.ALL)
        category.add(Category.ENVIRONMENTAL)
        category.add(Category.ORNAMENTALS_TURF)
        category.add(Category.COVER_CROP)
    }
}
