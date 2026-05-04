package com.trbear9.ui.viewmodel

import kotlin.math.absoluteValue

//3. Dosis Dolomit (t/ha) = CaCO3 / 0,75
fun getDosis(name: String, current: Float, target: Float, depth: Int): Float {

    // target = 7
    // current = pH saat ini
    val selisih = target - current

    val CaCO3: Float = when (name) {
        "Aluvial" -> {
            selisih.absoluteValue * 3.0f * (depth / 20)
        }

        "Andosol" -> {
            selisih.absoluteValue * 5.0f * (depth / 20)
        }

        "Humus" -> {
            selisih.absoluteValue * 4.0f * (depth / 20)
        }

        "Kapur" -> {
            selisih.absoluteValue * 3.0f * (depth / 20)
        }

        "Laterit" -> {
            selisih.absoluteValue * 3.5f * (depth / 20)
        }

        "Pasir" -> {
            selisih.absoluteValue * 2.0f * (depth / 20)
        }

        else -> -1.0f
    }
    // Dosis Dolomit (t/ha) = CaCO3 / 0,75
    return CaCO3 / 75
}

fun retention(name: String?): String {
    return when (name) {
        "Aluvial" -> " Tanah aluvial memiliki kemampuan menahan air sedang sehingga perlu dikelola agar " +
                "tetap **lembap**. Gunakan mulsa organik (jerami atau daun kering) setebal **5–10** cm untuk " +
                "mengurangi **penguapan**, serta tambahkan biochar **2–5** ton/ha agar kapasitas simpan air " +
                "meningkat. Penyiraman dilakukan saat **kelembapan** turun di bawah **60%** kapasitas lapang " +
                "dengan metode irigasi sederhana atau tetes. Tambahkan pupuk organik **10–15** ton/ha dan " +
                "tanam cover crop untuk menjaga struktur serta **kelembapan** tanah. "

        "Andosol" -> " Tanah andosol umumnya memiliki porositas tinggi dan mampu menahan air cukup baik, " +
                "tetapi mudah kehilangan kelembapan saat **kering**. Untuk menjaga kestabilan air, gunakan " +
                "mulsa organik (jerami, daun kering) setebal **5–10** cm dan tambahkan biochar **3–6** ton/ha " +
                "agar daya simpan air lebih optimal. Lakukan penyiraman ketika kelembapan tanah turun " +
                "di bawah **70%** kapasitas lapang, serta gunakan irigasi tetes atau sprinkle agar **distribusi** " +
                "air lebih merata. Pemberian pupuk organik **15–20** ton/ha serta penanaman cover crop dianjurkan " +
                "untuk memperbaiki struktur tanah dan mempertahankan **kelembapan** lebih lama. "

        "Humus" -> " Tanah humus memiliki kandungan bahan organik tinggi sehingga daya menahan airnya " +
                "sangat baik. Namun, **kelembapan** tetap perlu **dijaga** agar stabil bagi tanaman. Gunakan " +
                "mulsa organik (jerami, daun kering) setebal **5–10** cm untuk mengurangi **penguapan**, serta " +
                "tambahkan biochar **2–4** ton/ha bila diperlukan agar struktur tanah lebih **kokoh**. Penyiraman " +
                "cukup dilakukan ketika **kelembapan tanah** turun hingga sekitar **70%** kapasitas lapang, karena " +
                "humus cenderung mampu menyimpan air lebih **lama**. Pemberian pupuk organik tambahan **10–15** " +
                "ton/ha dan penanaman cover crop akan membantu mempertahankan **kelembapan** sekaligus meningkatkan " +
                "**kesuburan** tanah. "

        "Retensi" -> " Tanah kapur umumnya bertekstur **kasar**, cepat meresapkan air, tetapi sulit menyimpannya " +
                "sehingga **kelembapan cepat hilang**. Untuk meningkatkan retensi air, gunakan mulsa organik " +
                "(jerami, serasah, daun kering) setebal **7–10** cm agar mengurangi **penguapan**. Tambahkan biochar " +
                "**4–6** ton/ha serta pupuk organik **15–20** ton/ha untuk memperbaiki porositas dan daya simpan " +
                "air. Penyiraman perlu lebih **sering**, terutama saat **kelembapan** turun di bawah **60%** kapasitas " +
                "lapang, dengan sistem irigasi tetes atau alur agar **penyerapan** lebih efisien. Penanaman " +
                "cover crop juga disarankan untuk menjaga **kelembapan** tanah dan menambah bahan organik. "

        "Laterit" -> " Tanah laterit memiliki kandungan liat tinggi, drainase kurang baik, dan mudah " +
                "mengeras saat kering sehingga retensi airnya **rendah**. Untuk menjaga ketersediaan air, " +
                "gunakan mulsa organik (jerami, serasah, daun kering) setebal **5–8** cm agar kelembapan " +
                "lebih **stabil**. Tambahkan biochar 3–5 ton/ha serta pupuk organik **15–20** ton/ha untuk memperbaiki " +
                "struktur tanah dan meningkatkan **kapasitas** menahan air. Penyiraman dilakukan ketika kelembapan " +
                "turun hingga **<65%** kapasitas lapang, dengan metode irigasi tetes atau sprinkle agar air " +
                "terserap merata. Penanaman cover crop dianjurkan untuk mencegah **pemadatan**, menjaga kelembapan, " +
                "dan menambah bahan organik. "

        "Pasir" -> " Tanah pasir memiliki pori besar, sehingga cepat **meloloskan** air dan sangat rendah " +
                "daya menahan airnya. Untuk meningkatkan retensi, gunakan mulsa organik (jerami, sekam, " +
                "daun kering) setebal **7–10** cm agar mengurangi penguapan. Tambahkan biochar 5–7 ton/ha " +
                "serta pupuk organik **20–25** ton/ha untuk meningkatkan kandungan bahan organik dan memperbaiki " +
                "struktur **tanah**. Penyiraman perlu lebih **sering**, dilakukan saat kelembapan turun di bawah " +
                "**50–55%** kapasitas lapang, dengan metode irigasi tetes agar air langsung terserap ke zona " +
                "perakaran. Penanaman cover crop juga penting untuk menahan kelembapan dan menambah unsur " +
                "organik pada **tanah berpasir**. "

        else -> "Belum tersedia untuk jenis tanah ini"
    }
}

fun Double.round(): String = String.format("%.2f", this)
fun Float.round(): String = String.format("%.2f", this)