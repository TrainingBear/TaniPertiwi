package com.trbear9.ui.view

import android.annotation.SuppressLint
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.trbear9.ui.R
import com.trbear9.ui.util.DataStore
import com.trbear9.ui.util.Screen

// ─────────────────────────────────────────────────────────────────────────────
//  Palette (shared with PlantDetail / SoilStats)
// ─────────────────────────────────────────────────────────────────────────────
private val GuideGreenDark = Color(0xFF1B4332)
private val GuideGreenMid = Color(0xFF52B788)
private val GuideGreenLight = Color(0xFFD8F3DC)
private val GuideCream = Color(0xFFF9F6F0)
private val GuideAmber = Color(0xFFF5A623)
private val GuideCardBg = Color(0xFFFFFFFE)
private val GuideTextMuted = Color(0xFF6B7B6E)
private val GuideDivider = Color(0xFFE0EDE4)

object Guide {
    /** MUST BE NULLED AFTER USE */
    internal var guidePointer:
            Triple<String, Triple<String, String, List<String>?>, List<Triple<Pair<Int?, String?>?, String, String>>>? =
        null
}

// ─────────────────────────────────────────────────────────────────────────────
//  Guide – main list screen
// ─────────────────────────────────────────────────────────────────────────────
@SuppressLint("UnusedBoxWithConstraintsScope")
@OptIn(ExperimentalMaterial3Api::class)
@Preview(device = "spec:width=411dp,height=891dp,dpi=160")
@Composable
fun Guide(nav: NavController? = null) {
    var query by remember { mutableStateOf("") }

    // Progress
    var achieved by remember { mutableStateOf(0) }
    LaunchedEffect(Unit) {
        var count = 0
        if (DataStore.contains("tanah")) count++
        if (DataStore.contains("tanaman")) count++
        if (DataStore.contains("pupuk")) count++
        achieved = count
    }
    val progress by animateFloatAsState(
        targetValue = achieved / 3f,
        animationSpec = tween(800),
        label = "progressAnim"
    )

    val allItems = remember {
        listOf(
            GuideItem(
                id = "tanah",
                num = 1,
                title = "Tanah",
                desc = "Tanah sangat vital peranannya bagi semua kehidupan di bumi",
                icon = R.drawable.icon_app,
                credits = listOf(
                    "https://en.wikipedia.org/wiki/Soil_pH",
                    "https://fnb.tech/id/what-is-soil-ph/",
                    "https://id.wikipedia.org/wiki/Tanah",
                    "https://agrotek.id/istilah/retensi-air/",
                    "https://en.wikipedia.org/wiki/Soil_water_(retention)"
                ),
                details = listOf(
                    Triple(
                        null, //TODO isi tod klo mau
                        "Pengertian",
                        "Tanah sangat vital peranannya bagi semua kehidupan di bumi " +
                                "karena tanah mendukung kehidupan tumbuhan dengan menyediakan unsur " +
                                "hara dan air sekaligus sebagai penopang akar tanaman. Komposisi " +
                                "tanah berbeda-beda pada satu lokasi dengan lokasi yang lain. " +
                                "Air dan udara merupakan bagian dari tanah."
                    ),
                    Triple(
                        Pair(R.drawable.lapisanairtanah, null),
                        "Retensi Air Tanah",
                        "Retensi air adalah kemampuan tanah untuk menahan atau menyimpan " +
                                "air di dalamnya setelah proses penyiraman atau hujan. Mengacu " +
                                "pada kapasitas tanah untuk mempertahankan kelembaban dan ketersediaan " +
                                "air bagi tanaman. Retensi air dalam tanah dipengaruhi oleh beberapa faktor, diantaranya:\n\n" +
                                "- Tekstur tanah: Tanah berbutir halus seperti lempung memiliki " +
                                "kemampuan yang lebih baik untuk menahan air dibandingkan dengan " +
                                "tanah berbutir kasar seperti pasir. Partikel halus dalam " +
                                "tanah lempung memiliki kapasitas adsorpsi (penyerapan air) yang tinggi.\n\n" +
                                "- Struktur Tanah: Struktur tanah yang hancur atau terkompaksi " +
                                "(padat) dapat menghambat infiltrasi air dan menyebabkan aliran " +
                                "permukaan, sehingga mengurangi kemampuan tanah untuk menahan air.\n\n" +
                                "- Kandungan Bahan Organik: Bahan organik meningkatkan kemampuan tanah " +
                                "untuk menahan air dengan membentuk agregat dan meningkatkan retensi air di dalam pori-pori tanah.\n\n" +
                                "- Kedalaman Tanah: Tanah yang dalam memiliki daya tampung yang " +
                                "lebih besar untuk menahan atau menyimpan air dibandingkan dengan tanah yang dangkal."
                    ),
                    Triple(
                        Pair(R.drawable.diagramphtanah, null),
                        "pH Tanah",
                        "Tingkat pH tanah mengacu pada tingkat keasaman atau kebasaannya " +
                                "tanah, jika tanah terlalu asam atau terlalu basa, hal itu " +
                                "dapat membatasi kemampuan tanaman untuk menyerap nutrisi tertentu. " +
                                "Pada tanah asam (pH rendah), nutrisi penting seperti nitrogen, " +
                                "fosfor, dan kalium sering kali tidak tersedia bagi tanaman. " +
                                "Di sisi lain, tanah basa (pH tinggi) dapat menyebabkan pengikatan " +
                                "mikronutrien seperti zat besi, seng, dan mangan, sehingga " +
                                "tanaman tidak dapat menyerapnya. Berikut adalah klasifikasi pH pada tanah:\n" +
                                "- Tanah Asam (pH 0-6.9): Umum terjadi di wilayah dengan curah " +
                                "hujan tinggi dan tanah yang sudah tua. Kondisi ini dapat menyebabkan kekurangan nutrisi.\n" +
                                "- Tanah Netral (pH 7): Ideal untuk sebagian besar tanaman.\n" +
                                "- Tanah Alkali (pH 7.1-14): Sering ditemukan di daerah kering " +
                                "dan gersang. Hal ini dapat menyebabkan kekurangan nutrisi bagi tanaman tertentu."
                    )
                )
            ),
            GuideItem(
                id = "tanaman",
                num = 2,
                title = "Tanaman",
                desc = "Tanaman menjadi dasar seluruh rantai makanan dan sumber pangan",
                icon = R.drawable.icon_app,
                credits = listOf(
                    "https://id.wikipedia.org/wiki/Tanaman",
                    "https://id.wikipedia.org/wiki/Hama",
                    "https://id.wikipedia.org/wiki/Hidroponik",
                    "https://id.wikipedia.org/wiki/Irigasi"
                ),
                details = listOf(
                    Triple(
                        Pair(
                            R.drawable.padi,
                            "https://pertanian.uma.ac.id/2023/04/27/sawah-padi-ter" +
                                    "masuk-salah-satu-tumbuhan-yang-penting-bagi-manusia/"
                        ),
                        "Pengertian",
                        "Tanaman adalah organisme autotrof yang mampu menghasilkan makanan " +
                                "sendiri melalui fotosintesis menggunakan cahaya, air, dan CO₂. " +
                                "Tanaman menjadi dasar seluruh rantai makanan dan berperan penting " +
                                "sebagai sumber pangan, energi, oksigen, dan berbagai komoditas " +
                                "agrikultur. Hampir semua tanaman adalah \"tumbuhan\", tetapi tanaman " +
                                "kadang mencakup pula beberapa fungi (yakni jamur pangan " +
                                "seperti jamur kancing dan jamur merang) dan alga (seperti " +
                                "alga penghasil agar-agar dan nori) yang sengaja dibudidayakan " +
                                "untuk dimanfaatkan nilai ekonominya."
                    ),
                    Triple(
                        Pair(R.drawable.hama, "Shutterstock"),
                        "Hama",
                        "Istilah hama dapat digunakan untuk semua organisme, tetapi " +
                                "istilah ini paling sering dipakai hanya kepada hewan. Organisme yang " +
                                "bersifat invasif juga merupakan hama. Suatu hewan juga dapat " +
                                "disebut hama jika menyebabkan kerusakan pada ekosistem alami " +
                                "atau menjadi agen penyebaran penyakit dalam habitat manusia. " +
                                "Hama dapat dikontrol dengan cara:\n" +
                                "- menggunakan hewan predatornya\n" +
                                "- insektisida dengan dosis yang aman\n" +
                                "- memasang jebakan/perangkap untuk hama seperti tikus ataupun ular."
                    ),
                    Triple(
                        null,
                        "Metode Budidaya",
                        ""
                    )
                )
            ),
            GuideItem(
                id = "pupuk",
                num = 3,
                title = "Pupuk",
                desc = "Pupuk menyediakan zat hara yang sangat dibutuhkan tanaman",
                icon = R.drawable.icon_app,
                credits = listOf(
                    "https://id.wikipedia.org/wiki/Pupuk",
                    "https://id.my-best.com/139242"
                ), details = listOf(
                    Triple(
                        Pair(R.drawable.pupukhighlight, null),
                        "Pengertian",
                        "Pupuk juga penting bagi tanaman, seperti menyediakan dan " +
                                "meningkatkan ketersediaan zat hara yang sangat dibutuhkan " +
                                "tanamanmu sehingga tanaman dapat tumbuh dengan optimal. Akan " +
                                "tetapi perlu juga diperhatikan apa kebutuhan tumbuhanmu, agar " +
                                "tumbuhan tidak mendapat terlalu banyak zat makanan. Terlalu sedikit " +
                                "atau terlalu banyak zat makanan dapat berbahaya bagi tumbuhan."
                    ),
                    Triple(
                        Pair(R.drawable.pupuknpk, null),
                        "Pupuk NPK",
                        "Pupuk NPK adalah pupuk buatan yang berbentuk cair atau padat " +
                                "berupa butiran kasar yang mengandung unsur hara utama nitrogen, " +
                                "fosfor, dan kalium. Berikut fungsi ketiga unsur dalam pupuk NPK:\n" +
                                "- Nitrogen (N): membantu pertumbuhan vegetatif, terutama daun.\n" +
                                "- Fosfor (P): membantu pertumbuhan akar dan tunas.\n" +
                                "- Kalium (K): membantu pembungaan dan pembuahan."
                    ),
                    Triple(
                        Pair(R.drawable.forjenismerekpupuk, null),
                        "Rekomendasi Merek Pupuk",
                        "- Meroke Tetap Jaya, Mutiara Pupuk NPK 16-16-16: Kombinasi " +
                                "amonium dan nitrat membuat pertumbuhan bunga jadi seimbang, " +
                                "Meski berbentuk padat, pupuk ini mudah larut dalam air sehingga " +
                                "memudahkan pemberian dan memaksimalkan penyerapan oleh tanaman. " +
                                "Kombinasi nitrat-nitrogen dengan amonium-nitrogen yang terkandung " +
                                "di dalamnya pun efektif memacu pertumbuhan.\n\n" +
                                "- Saprotan Utama, Pak Tani Pupuk NPK 16-16-16: Tanah merupakan " +
                                "media tanam yang sangat penting dan menentukan kesuburan suatu " +
                                "tanaman. Karena itu, tidak ada salahnya Anda menyiapkan unsur " +
                                "hara dalam tanah sebelum mulai ditanami. Bisa digunakan sebagai " +
                                "pupuk dasar ataupun pupuk susulan.\n\n" +
                                "- Kalatham, Pupuk Gandasil B - Pupuk Bunga dan Buah: Menunjang " +
                                "pembentukan bunga dan pembesaran buah dengan kandungan NPK " +
                                "seimbang (6-20-30). Berbentuk kristal yang mudah larut dalam " +
                                "air sehingga penyerapan nutrisi berlangsung cepat dan merata. " +
                                "Mengandung vitamin pertumbuhan (Aneurine, Lactoflavine, Nicotinic " +
                                "acid amide) yang membantu tanaman lebih kuat dan responsif terhadap pemupukan.\n\n" +
                                "- Meroke Tetap Jaya, Mutiara Grower Pupuk NPK 15-09-20+TE: " +
                                "Trace element (TE) adalah unsur hara mikro yang dibutuhkan " +
                                "oleh tanaman dalam kadar yang lebih sedikit. Unsur TE di dalam " +
                                "pupuk ini adalah boron, seng, dan mangan. Boron berfungsi untuk " +
                                "menunjang pembentukan dan pembelahan sel, seng untuk mengaktifkan " +
                                "enzim, dan mangan untuk memacu pertumbuhan tunas baru. Jumlah " +
                                "unsur hara pupuk ini memang sedikit, tetapi adanya TE dapat " +
                                "membantu tanaman mendapatkan suplai unsur hara secara merata.\n\n" +
                                "- Dinosaurus, Pupuk Cair Bio-Organik: Pupuk organik cair (POC) " +
                                "merek ini merupakan pupuk organik serbaguna karena bisa digunakan " +
                                "untuk menutrisi semua jenis tanaman. Pupuk ini kaya akan micronutrient, " +
                                "macronutrient, serta mikroba yang penting untuk pertumbuhan dan " +
                                "imunitas tanaman. Selain itu, Anda juga dapat memanfaatkannya " +
                                "sebagai dekomposer untuk mempercepat proses pengomposan."
                    )
                )
            )
        )
    }

    val filtered = if (query.isBlank()) allItems
    else allItems.filter {
        it.title.contains(query, ignoreCase = true) || it.desc.contains(
            query,
            ignoreCase = true
        )
    }

    Scaffold(
        containerColor = Color.Transparent, topBar = {
            // ── Top bar ───────────────────────────────────────────────
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.linearGradient(
                            colors = listOf(GuideGreenDark, Color(0xFF2D6A4F), GuideGreenMid)
                        )
                    )
            ) {
                TopAppBar(
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color.Transparent
                    ),
                    navigationIcon = {
                        IconButton(
                            onClick = { nav?.navigate(Screen.home) },
                            modifier = Modifier
                                .padding(8.dp)
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(Color.White.copy(alpha = 0.15f))
                        ) {
                            Icon(
                                Icons.AutoMirrored.Filled.ArrowBack,
                                "Kembali",
                                tint = Color.White
                            )
                        }
                    },
                    title = {
                        OutlinedTextField(
                            value = query,
                            onValueChange = { query = it },
                            placeholder = {
                                Text(
                                    "Cari panduan...",
                                    color = Color.White.copy(alpha = 0.6f),
                                    fontSize = 14.sp
                                )
                            },
                            textStyle = TextStyle(color = Color.White, fontSize = 14.sp),
                            singleLine = true,
                            trailingIcon = {
                                Icon(
                                    Icons.Default.Search,
                                    null,
                                    tint = Color.White.copy(alpha = 0.7f)
                                )
                            },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Ascii),
                            colors = OutlinedTextFieldDefaults.colors(
                                unfocusedBorderColor = Color.White.copy(alpha = 0.3f),
                                focusedBorderColor = Color.White.copy(alpha = 0.8f),
                                cursorColor = Color.White,
                                unfocusedContainerColor = Color.White.copy(alpha = 0.1f),
                                focusedContainerColor = Color.White.copy(alpha = 0.15f),
                            ),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                                .padding(end = 12.dp)
                        )
                    }
                )
            }
        },
        content = { padding ->
            Column(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize()
            ) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                listOf(
                                    Color(0xFFE8F5E9), GuideCream, Color(0xFFF0EDE6)
                                )
                            )
                        ),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 14.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // ── Progress card ─────────────────────────────────────
                    item {
                        Card(
                            shape = RoundedCornerShape(20.dp),
                            colors = CardDefaults.cardColors(containerColor = GuideCardBg),
                            elevation = CardDefaults.cardElevation(3.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(20.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                // Icon image
                                Box(
                                    modifier = Modifier
                                        .size(72.dp)
                                        .clip(RoundedCornerShape(16.dp))
                                        .background(GuideGreenLight),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Image(
                                        painter = painterResource(R.drawable.icon_app),
                                        contentDescription = "Sapling",
                                        contentScale = ContentScale.Fit,
                                        modifier = Modifier.size(52.dp)
                                    )
                                }
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        "Progres Belajar",
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = GuideGreenDark
                                    )
                                    Spacer(Modifier.height(4.dp))
                                    Text(
                                        "$achieved dari 3 topik selesai",
                                        fontSize = 13.sp,
                                        color = GuideTextMuted
                                    )
                                    Spacer(Modifier.height(10.dp))
                                    // Progress bar
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(10.dp)
                                            .clip(RoundedCornerShape(50))
                                            .background(GuideGreenLight)
                                    ) {
                                        LinearProgressIndicator(
                                            progress = { progress },
                                            modifier = Modifier.fillMaxSize(),
                                            color = GuideGreenMid,
                                            trackColor = Color.Transparent,
                                            strokeCap = ProgressIndicatorDefaults.LinearStrokeCap
                                        )
                                    }
                                    Spacer(Modifier.height(6.dp))
                                    Text(
                                        "${(progress * 100).toInt()}% selesai",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = GuideGreenMid
                                    )
                                }
                            }
                        }
                    }

                    // ── Section header ────────────────────────────────────
                    item {
                        Text(
                            "Topik Panduan",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = GuideTextMuted,
                            modifier = Modifier.padding(start = 4.dp, top = 4.dp)
                        )
                    }

                    // ── Guide cards ───────────────────────────────────────
                    items(filtered.size) { index ->
                        val item = filtered[index]
                        val isDone = DataStore.contains(item.id)
                        GuideCard(nav = nav, item = item, isDone = isDone)
                    }
                    item { Spacer(Modifier.height(24.dp)) }
                }
            }
        })
}

// ─────────────────────────────────────────────────────────────────────────────
//  Data model for a guide item
// ─────────────────────────────────────────────────────────────────────────────
private data class GuideItem(
    val id: String,
    val num: Int,
    val title: String,
    val desc: String,
    val icon: Int,
    val credits: List<String>?,
    val details: List<Triple<Pair<Int?, String?>?, String, String>>
)

// ─────────────────────────────────────────────────────────────────────────────
//  GuideCard composable
// ─────────────────────────────────────────────────────────────────────────────
@Composable
private fun GuideCard(
    nav: NavController?,
    item: GuideItem,
    isDone: Boolean,
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isDone) Color(0xFFF0FAF4) else GuideCardBg
        ),
        elevation = CardDefaults.cardElevation(2.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                Guide.guidePointer = Triple(
                    item.id,
                    Triple(item.title, item.desc, item.credits),
                    item.details
                )
                nav?.navigate(Screen.guidePointDetail)
            }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Number badge
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(if (isDone) GuideGreenMid else GuideGreenLight),
                contentAlignment = Alignment.Center
            ) {
                if (isDone) {
                    Icon(
                        Icons.Default.CheckCircle,
                        null,
                        tint = Color.White,
                        modifier = Modifier.size(26.dp)
                    )
                } else {
                    Text(
                        item.num.toString(),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = GuideGreenDark
                    )
                }
            }

            // Text
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        item.title,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = GuideGreenDark
                    )
                    if (isDone) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(GuideGreenLight)
                                .padding(horizontal = 7.dp, vertical = 2.dp)
                        ) {
                            Text(
                                "Selesai",
                                fontSize = 11.sp,
                                color = GuideGreenMid,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
                Spacer(Modifier.height(3.dp))
                Text(
                    item.desc,
                    fontSize = 13.sp,
                    color = GuideTextMuted,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    lineHeight = 18.sp
                )
            }

            // Arrow
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(if (isDone) GuideGreenLight else Color(0xFFF1F1F1)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.AutoMirrored.Filled.KeyboardArrowRight,
                    null,
                    tint = if (isDone) GuideGreenMid else GuideTextMuted,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}