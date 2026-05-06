package com.trbear9.ui.view

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Bitmap
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.StarHalf
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCut
import androidx.compose.material.icons.filled.Coronavirus
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material.icons.filled.HourglassEmpty
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.OpenInBrowser
import androidx.compose.material.icons.filled.Park
import androidx.compose.material.icons.filled.Science
import androidx.compose.material.icons.filled.Spa
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material.icons.filled.Whatshot
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.focusModifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import coil.compose.AsyncImage
import coil.compose.AsyncImagePainter
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.google.accompanist.flowlayout.FlowRow
import com.pseudoankit.coachmark.UnifyCoachmark
import com.pseudoankit.coachmark.model.ToolTipPlacement
import com.pseudoankit.coachmark.scope.enableCoachMark
import com.trbear9.plants.Data
import com.trbear9.ui.fragment.NavigateSoilStats
import com.trbear9.ui.util.Util
import com.trbear9.plants.E.A_maximum_ph
import com.trbear9.plants.E.A_minimum_ph
import com.trbear9.plants.E.Climate_zone
import com.trbear9.plants.E.MAX_crop_cycle
import com.trbear9.plants.E.MIN_crop_cycle
import com.trbear9.plants.E.O_maximum_temperature
import com.trbear9.plants.E.O_minimum_temperature
import com.trbear9.plants.parameters.PlantDummy
import com.trbear9.plants.parameters.blob.Plant
import com.trbear9.ui.fragment.CONS
import com.trbear9.ui.fragment.ImageAsset
import com.trbear9.ui.fragment.Kat
import com.trbear9.ui.util.MarkKey
import com.trbear9.ui.util.highlightConfig
import com.trbear9.ui.view.Divider
import com.trbear9.ui.viewmodel.DataModelAcces
import com.trbear9.ui.viewmodel.getDetailedImage
import dev.jeziellago.compose.markdowntext.MarkdownText
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class PlantDetail : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val ref = intent.getSerializableExtra("plant", Plant::class.java)
            val score = intent.getIntExtra("score", 1)
            PlantDetail(ref = ref!!, onExit = { this.finish() }, score = score)
        }
    }
}

private val GreenDark = Color(0xFF1B4332)
private val GreenMid = Color(0xFF52B788)
private val GreenLight = Color(0xFFD8F3DC)
private val Cream = Color(0xFFF9F6F0)
private val Amber = Color(0xFFF5A623)
private val AmberLight = Color(0xFFFFF3E0)
private val Divider = Color(0xFFE9ECE7)
private val TextPrimary = Color(0xFF1A1A1A)
private val TextMuted = Color(0xFF6B7B6E)

@SuppressLint("NotConstructor")
@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
fun PlantDetail(score: Int = 0, ref: Plant? = PlantDummy(), onExit: () -> Unit = {}) {
    val context = LocalContext.current
    val scroll = rememberScrollState()
    var image by remember { mutableStateOf<Bitmap?>(null) }

    LaunchedEffect(Unit) {
        withContext(Dispatchers.IO) {
            if (ref != null) image = ImageAsset.getImage(context, ref.fullsize)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Cream)
    ) {
        Column(modifier = Modifier.verticalScroll(scroll)) {

            // ── Hero image ────────────────────────────────────────────────
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(16 / 10f)
            ) {
                var isError by remember { mutableStateOf(false) }
                var isLoading by remember { mutableStateOf(false) }

                AsyncImage(
                    model = getDetailedImage(ref),
                    contentDescription = "${ref?.nama_ilmiah} image",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize(),
                    onError = { isError = true; isLoading = false },
                    onLoading = { isLoading = true; isError = false },
                    onSuccess = { isLoading = false; isError = false }
                )

                // Scrim – bottom fade so title text is readable
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                0f to Color.Transparent,
                                0.55f to Color.Transparent,
                                1f to Color(0xCC1B4332)
                            )
                        )
                )

                if (isError) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(GreenLight),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = CONS.noImage,
                            contentDescription = null,
                            tint = GreenMid,
                            modifier = Modifier.size(64.dp)
                        )
                        Spacer(Modifier.height(8.dp))
                        Text(
                            "Gambar tidak tersedia",
                            color = TextMuted,
                            fontSize = 13.sp,
                            textAlign = TextAlign.Center
                        )
                    }
                }

                if (isLoading) {
                    Box(Modifier
                        .fillMaxSize()
                        .background(GreenLight)) {
                        CircularProgressIndicator(
                            color = GreenMid,
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }
                }

                // Close button – top left
                IconButton(
                    onClick = onExit,
                    modifier = Modifier
                        .padding(12.dp)
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.85f))
                        .align(Alignment.TopStart)
                ) {
                    Icon(Icons.Default.Close, "Close", tint = GreenDark)
                }

                // Score badge – top right
                if (score > 0) {
                    val star = (score / 10f) * 5
                    val half = (star - star.toInt()) > 0.1f
                    Row(
                        modifier = Modifier
                            .padding(12.dp)
                            .clip(RoundedCornerShape(20.dp))
                            .background(Color.Black.copy(alpha = 0.45f))
                            .padding(horizontal = 10.dp, vertical = 5.dp)
                            .align(Alignment.TopEnd),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        repeat(star.toInt()) {
                            Icon(
                                Icons.Default.Star,
                                null,
                                tint = Color(0xFFF9C429),
                                modifier = Modifier.size(16.dp)
                            )
                        }
                        if (half) Icon(
                            Icons.AutoMirrored.Filled.StarHalf,
                            null,
                            tint = Color(0xFFF9C429),
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }

                // Plant name overlay – bottom of hero
                if (ref != null) {
                    Column(
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(start = 16.dp, end = 16.dp, bottom = 16.dp)
                    ) {
                        val plant = DataModelAcces(ref)
                        Text(
                            text = plant.namaUmum ?: "Tidak diketahui",
                            fontSize = 26.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White,
                            lineHeight = 30.sp
                        )
                        if (plant.author != null) {
                            Text(
                                text = plant.author!!,
                                fontSize = 13.sp,
                                color = Color.White.copy(alpha = 0.75f),
                                fontStyle = FontStyle.Italic
                            )
                        }
                    }
                }
            }

            // ── Body ──────────────────────────────────────────────────────
            if (ref != null) {
                val plant = DataModelAcces(ref)

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 20.dp),
                    verticalArrangement = Arrangement.spacedBy(20.dp)
                ) {

                    // Description card
                    SectionCard() {
                        Text(
                            text = plant.deskripsi ?: "Deskripsi untuk tanaman ini belum tersedia.",
                            fontSize = 15.sp,
                            color = TextPrimary,
                            lineHeight = 24.sp
                        )
                    }

                    // Stats grid
                    SectionCard {
                        SectionTitle("Informasi Tanaman", Icons.Default.Eco)
                        Spacer(Modifier.height(12.dp))
                        // 2-column grid of stat chips
                        val stats = buildList {
                            plant.minPH?.let {
                                add(
                                    Triple(
                                        "PH Ideal",
                                        "${plant.minPH}–${plant.maxPH}",
                                        Icons.Default.Science
                                    )
                                )
                            }
                            if (plant.maxCycle != "0") {
                                add(
                                    Triple(
                                        "Waktu Panen",
                                        if (plant.minCycle != plant.maxCycle) "${plant.minCycle}–${plant.maxCycle} hari"
                                        else "${plant.minCycle} hari",
                                        Icons.Default.HourglassEmpty
                                    )
                                )
                            }
                            plant.minTemperature?.let {
                                add(
                                    Triple(
                                        "Temperatur",
                                        "${plant.minTemperature}–${plant.maxTemperature}°C",
                                        Icons.Default.Whatshot
                                    )
                                )
                            }
                            plant.photoperiod?.let {
                                add(Triple("Fotosintesis", it, Icons.Default.Lightbulb))
                            }
                            plant.salinity?.let {
                                add(Triple("Kadar Garam", it, Icons.Default.WaterDrop))
                            }
                            add(Triple("Kecocokan Tanah", "$score / 10", Icons.Default.Star))
                        }
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            stats.chunked(2).forEach { row ->
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    row.forEach { (head, body, icon) ->
                                        StatChip(
                                            label = head,
                                            value = body,
                                            icon = icon,
                                            modifier = Modifier.weight(1f)
                                        )
                                    }
                                    // if odd item, fill last slot with a spacer
                                    if (row.size == 1) Spacer(Modifier.weight(1f))
                                }
                            }
                        }
                    }

                    // Climate & category
                    SectionCard {
                        SectionTitle("Ekologi & Kategori", Icons.Default.Park)
                        Spacer(Modifier.height(10.dp))
                        MarkdownText(
                            markdown = buildString {
                                append("**Kecocokan iklim:** ${plant.iklim}\n")
                                append("**Kategori:** ${plant.kategori}\n")
                                plant.sinonim?.let { if(it.isNotEmpty()) append("**Nama lain:** $it\n") }
                                plant.skalaProduksi?.let { if(it.isNotEmpty()) append("**Skala produksi:** $it") }
                            },
                            style = TextStyle(
                                fontSize = 15.sp,
                                color = TextPrimary,
                                lineHeight = 24.sp
                            )
                        )
                    }

                    // Taxonomy button
                    plant.takson?.let { url ->
                        Button(
                            onClick = {
                                val intent = Intent(Intent.ACTION_VIEW, url.toString().toUri())
                                context.startActivity(intent)
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp),
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Amber),
                            elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
                        ) {
                            Icon(
                                Icons.Default.OpenInBrowser,
                                null,
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(Modifier.width(8.dp))
                            Text(
                                "Lihat Taksonomi Lengkap",
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                color = Color.White
                            )
                        }
                    }

                    // Care section
                    SectionCard {
                        SectionTitle("Perawatan", Icons.Default.Spa)
                        Spacer(Modifier.height(12.dp))
                        Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                            CareRow(
                                "Penyiraman",
                                ref.plantCare?.watering ?: "Belum tersedia",
                                Icons.Default.WaterDrop
                            )
                            HorizontalDividerLine()
                            CareRow(
                                "Kontrol Hama",
                                ref.plantCare?.pestDiseaseManagement ?: "Belum tersedia",
                                Icons.Default.Coronavirus
                            )
                            HorizontalDividerLine()
                            CareRow(
                                "Pemupukan",
                                ref.plantCare?.fertilization ?: "Belum tersedia",
                                Icons.Default.LocalHospital
                            )
                            HorizontalDividerLine()
                            CareRow(
                                "Sinar Matahari",
                                ref.plantCare?.sunlight ?: "Belum tersedia",
                                Icons.Default.WbSunny
                            )
                            HorizontalDividerLine()
                            CareRow(
                                "Pruning",
                                ref.plantCare?.pruning ?: "Belum tersedia",
                                Icons.Default.ContentCut
                            )
                        }
                    }

                    // Production scale
                    SectionCard {
                        SectionTitle("Skala Produksi", Icons.Default.Storefront)
                        Spacer(Modifier.height(12.dp))
                        ProductionScaleRow(
                            "Rumah Tangga",
                            ref.productSystem?.rumah_tangga ?: "Belum tersedia",
                            GreenLight,
//                            GreenDark
                                    Color.Black
                        )
                        Spacer(Modifier.height(8.dp))
                        ProductionScaleRow(
                            "Komersial",
                            ref.productSystem?.komersial ?: "Belum tersedia",
                            AmberLight,
//                            Color(0xFF7B4F00)
                                    Color.Black
                        )
                        Spacer(Modifier.height(8.dp))
                        ProductionScaleRow(
                            "Industri",
                            ref.productSystem?.industri ?: "Belum tersedia",
                            Color(0xFFEDE7F6),
//                            Color(0xFF4527A0)
                                    Color.Black
                        )
                    }

                    // Risk (optional)
                    plant.resiko?.let { resiko ->
                        if(resiko.isNotEmpty()) SectionCard(backgroundColor = Color(0xFFFFF8E1)) {
                            SectionTitle("Resiko", Icons.Default.Warning, iconTint = Amber)
                            Spacer(Modifier.height(8.dp))
                            Text(resiko, fontSize = 15.sp, color = TextPrimary, lineHeight = 24.sp)
                        }
                    }

                    Spacer(Modifier.height(80.dp)) // bottom padding for FAB/nav
                }
            }
        }

        // Coachmark overlay (unchanged)
        UnifyCoachmark {
            NavigateSoilStats(Modifier.fillMaxSize())
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
//  Helper composables
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun SectionCard(
    backgroundColor: Color = Color.White,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            content = content
        )
    }
}

@Composable
private fun SectionTitle(
    text: String,
    icon: ImageVector,
    iconTint: Color = GreenMid
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, null, tint = iconTint, modifier = Modifier.size(20.dp))
        Spacer(Modifier.width(8.dp))
        Text(text, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = GreenDark)
    }
}

@Composable
private fun StatChip(
    label: String,
    value: String,
    icon: ImageVector,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(GreenLight)
            .padding(horizontal = 12.dp, vertical = 10.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, null, tint = GreenMid, modifier = Modifier.size(14.dp))
            Spacer(Modifier.width(4.dp))
            Text(label, fontSize = 11.sp, color = TextMuted, fontWeight = FontWeight.Medium)
        }
        Spacer(Modifier.height(4.dp))
        Text(value, fontSize = 14.sp, color = GreenDark, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun CareRow(header: String, content: String, icon: ImageVector) {
    Row(modifier = Modifier.fillMaxWidth()) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(GreenLight),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, null, tint = GreenMid, modifier = Modifier.size(20.dp))
        }
        Spacer(Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(header, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = GreenDark)
            Spacer(Modifier.height(2.dp))
            Text(content, fontSize = 14.sp, color = TextPrimary, lineHeight = 21.sp)
        }
    }
}

@Composable
private fun HorizontalDividerLine() {
    Divider(color = Divider, thickness = 0.5.dp)
}

@Composable
private fun ProductionScaleRow(label: String, text: String, bgColor: Color, textColor: Color) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(bgColor)
            .padding(12.dp)
    ) {
        Text(
            label,
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            color = textColor.copy(alpha = 0.8f)
        )
        Spacer(Modifier.height(4.dp))
        Text(text, fontSize = 14.sp, color = textColor, lineHeight = 21.sp)
    }
}

// ─────────────────────────────────────────────────────────────────────────────
//  Legacy Label / Content kept for backward compat (used elsewhere in the app)
// ─────────────────────────────────────────────────────────────────────────────

@Composable
fun Content(header: String, content: String, icon: ImageVector) {
    Row(modifier = Modifier.padding(top = 10.dp)) {
        Icon(icon, contentDescription = "Icon", modifier = Modifier.size(24.dp))
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = buildAnnotatedString {
                withStyle(
                    SpanStyle(
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                ) { append("$header:") }
                append(" $content")
            },
            modifier = Modifier.padding(top = 8.dp)
        )
    }
}

@Composable
fun Label(head: String, body: String, icon: ImageVector) {
    Box(
        modifier = Modifier
            .wrapContentSize()
            .clip(RoundedCornerShape(8.dp))
    ) {
        Row {
            Column {
                Text(head, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                Row {
                    Image(icon, contentDescription = head)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(body, fontSize = 15.sp)
                }
            }
        }
    }
}