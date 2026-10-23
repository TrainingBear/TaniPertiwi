package com.trbear9.plants.parameters;

import com.trbear9.plants.metadata_enumerates.Category;
import com.trbear9.plants.parameters.blob.Plant;
import com.trbear9.plants.parameters.blob.PlantCare;
import com.trbear9.plants.parameters.blob.ProductSystem;

public class PlantDummy extends Plant {
    public PlantDummy() {
        nama_ilmiah = "Abelmoschus esculentus";
        common_names = "Okra";
        family = "Abelmoschaceae";
        genus = "Abelmoschus";
        kingdom = "Plantae";
        taxon = "Abelmoschus esculentus";
        kategori = "Sayuran";
        min_panen = 1;
        max_panen = 3;
        plantCare = new PlantCare("1-2 hari setelah panen", "Potong daun yang sudah tua", "Penyiraman 2 kali dalam seminggu", "Cahaya matahari 6 jam", "Pengendalian hama dan penyakit");
        productSystem = new ProductSystem("Rumah Tangga", "Komersial", "Industri");
        commonName = "Okra";
        pruneGuide = "Potong daun yang sudah tua";
        nama_umum.add("Okra");
        category.add(Category.ALL);
        category.add(Category.VEGETABLES);
    }
}
