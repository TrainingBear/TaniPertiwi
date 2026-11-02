package com.trbear9.plants.DUMMY

import com.trbear9.plants.metadata_enumerates.Category
import com.trbear9.plants.parameters.blob.Plant
import com.trbear9.plants.parameters.blob.PlantCare
import com.trbear9.plants.parameters.blob.ProductSystem

class Bugenvil : Plant() {
    init {
        fullsize = "Bougainvillea glabra fullsize.jpg"
        thumbnail = "Bougainvillea glabra thumbnail.jpg"
        family = "Nyctaginaceae"
        genus = "Bougainvillea"
        kingdom = "Plantae"
        taxon = "https://powo.science.kew.org//taxon/urn:lsid:ipni.org:names:35444-2"
        difficulty = "EASY"
        description =
            "Bougainvillea glabra adalah tanaman semak merambat berkayu dengan bunga hias berwarna cerah. Tahan kekeringan, cepat berbunga, dan cocok untuk pagar hidup, halaman, maupun pot."
        nama_ilmiah = "Bougainvillea glabra"
        min_panen = 0
        max_panen = 0
        plantCare = PlantCare(
            "Penyiraman secukupnya, 2–3 kali seminggu pada musim kemarau dan dikurangi pada musim hujan untuk menghindari genangan.",
        "Pangkas setelah periode berbunga untuk membentuk tanaman dan merangsang tunas baru, potong cabang mati atau tumbuh tidak teratur.",
        "Pemupukan seimbang NPK (10-10-10) setiap 4–6 minggu, dapat ditambah pupuk daun untuk merangsang warna bunga.",
        "Membutuhkan sinar matahari langsung minimal 6–8 jam per hari, cocok di lokasi terbuka.",
        "Waspadai kutu daun, kutu putih, dan tungau; gunakan insektisida organik atau sabun insektisida; hindari kelebihan air untuk mencegah busuk akar."
        )
        productSystem = ProductSystem(
            "Tanaman hias halaman dan pot, mudah dirawat dan cepat berbunga.",
            "Dikembangkan di pembibitan dan dijual sebagai tanaman hias untuk lanskap taman dan trotoar.",
            "Tidak umum diaplikasikan pada skala industri."
        )
        commonName = "Bugenvil licin (Bunga Kertas)"
        common_names = "Bugenvil licin (Bunga Kertas)"
        pruneGuide = "https://www.gardena.co.id/blog/cara-memangkas-bougainvillea"
    }
}
