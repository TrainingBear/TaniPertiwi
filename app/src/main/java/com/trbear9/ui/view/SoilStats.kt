//   Copyright 2025 by Patryk Goworowski and Patrick Michalik.
//
//   Licensed under the Apache License, Version 2.0 (the "License");
//   you may not use this file except in compliance with the License.
//   You may obtain a copy of the License at
//
//       http://www.apache.org/licenses/LICENSE-2.0
//
//   Unless required by applicable law or agreed to in writing, software
//   distributed under the License is distributed on an "AS IS" BASIS,
//   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
//   See the License for the specific language governing permissions and
//   limitations under the License.
package com.trbear9.ui.view

import android.graphics.Typeface
import android.os.Bundle
import android.text.Layout
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.patrykandpatrick.vico.compose.cartesian.CartesianChartHost
import com.patrykandpatrick.vico.compose.cartesian.axis.auto
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberAxisLabelComponent
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberStart
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberTop
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberColumnCartesianLayer
import com.patrykandpatrick.vico.compose.cartesian.rememberCartesianChart
import com.patrykandpatrick.vico.compose.cartesian.rememberVicoScrollState
import com.patrykandpatrick.vico.compose.common.component.rememberLineComponent
import com.patrykandpatrick.vico.compose.common.component.rememberTextComponent
import com.patrykandpatrick.vico.compose.common.fill
import com.patrykandpatrick.vico.core.cartesian.axis.Axis
import com.patrykandpatrick.vico.core.cartesian.axis.BaseAxis
import com.patrykandpatrick.vico.core.cartesian.axis.HorizontalAxis
import com.patrykandpatrick.vico.core.cartesian.axis.VerticalAxis
import com.patrykandpatrick.vico.core.cartesian.data.CartesianChartModelProducer
import com.patrykandpatrick.vico.core.cartesian.data.CartesianLayerRangeProvider
import com.patrykandpatrick.vico.core.cartesian.data.CartesianValueFormatter
import com.patrykandpatrick.vico.core.cartesian.data.columnSeries
import com.patrykandpatrick.vico.core.cartesian.layer.ColumnCartesianLayer
import com.patrykandpatrick.vico.core.common.Insets
import com.patrykandpatrick.vico.core.common.Position
import com.patrykandpatrick.vico.core.common.component.Shadow
import com.patrykandpatrick.vico.core.common.data.ExtraStore
import com.trbear9.plants.TFService
import com.trbear9.plants.parameters.Response
import com.trbear9.ui.viewmodel.getDosis
import com.trbear9.ui.viewmodel.inputs
import com.trbear9.ui.viewmodel.retention
import com.trbear9.ui.viewmodel.round
import dev.jeziellago.compose.markdowntext.MarkdownText
import kotlin.random.Random

// ─────────────────────────────────────────────────────────────────────────────
//  SoilStats.kt  –  redesigned soil analytics screen
//  Palette: #F9F6F0 cream · #1B4332 dark green · #52B788 mid green
//           #D8F3DC light green · #F5A623 amber · #E9ECE7 divider
// ─────────────────────────────────────────────────────────────────────────────

private val SoilGreenDark = Color(0xFF1B4332)
private val SoilGreenMid = Color(0xFF52B788)
private val SoilGreenLight = Color(0xFFD8F3DC)
private val SoilCream = Color(0xFFF9F6F0)
private val SoilAmber = Color(0xFFF5A623)
private val SoilAmberLight = Color(0xFFFFF3E0)
private val SoilDivider = Color(0xFFE0EDE4)
private val SoilTextPrimary = Color(0xFF1A1A1A)
private val SoilTextMuted = Color(0xFF6B7B6E)
private val SoilCardBg = Color(0xFFFFFFFE)

val labelListKey = ExtraStore.Key<List<String>>()
private val clipRound: Dp = 16.dp

class SoilStatsActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SoilStats { onBackPressedDispatcher.onBackPressed(); finish() }
        }
    }
}

@Preview
@Composable
fun SoilStats(click: () -> Unit = {}) {
    val scroll = rememberScrollState()
    val response by remember { mutableStateOf(inputs.soilResult.response) }
    val collected by remember { mutableStateOf(inputs.soilResult.collected) }
    val soilType by remember { mutableStateOf(response?.soilMax?.first ?: "Belum diketahui") }
    val soilPred by remember { mutableFloatStateOf(response?.soilMax?.second ?: 0f) }
    val ferr by remember { mutableStateOf(response?.soil?.fertility ?: "Belum diketahui") }
    val texr by remember { mutableStateOf(response?.soil?.texture ?: "Belum diketahui") }
    val drain by remember { mutableStateOf(response?.soil?.drainage ?: "Belum diketahui") }
    val depth by remember { mutableIntStateOf(inputs.soil.numericDepth) }
    val geo by remember { mutableStateOf(response?.geo) }
    val modelProducer = remember { CartesianChartModelProducer() }
    val list = remember { mutableStateListOf<Float>() }

    LaunchedEffect(Unit) {
        modelProducer.runTransaction {
            columnSeries {
                response?.soilPrediction?.forEach { pair -> list.add(pair.second) }
                series(
                    if (list.isEmpty()) List(6) { Random.nextFloat() } else list
                )
                extras { it[labelListKey] = TFService.labels }
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(Color(0xFFE8F5E9), SoilCream, Color(0xFFF0EDE6))
                )
            )
    ) {
        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                // ── Page title ────────────────────────────────────────────
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .background(SoilGreenLight)
                        .statusBarsPadding()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            "Analisis Tanah",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = SoilGreenDark
                        )
                        Text(
                            "Hasil pemindaian tanahmu",
                            fontSize = 13.sp,
                            color = SoilTextMuted
                        )
                    }
                }
            },
            content = { padding ->
                Column(
                    modifier = Modifier
                        .padding(padding)
                        .verticalScroll(scroll)
                        .padding(horizontal = 14.dp, vertical = 10.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // ── Soil photo ────────────────────────────────────────────
                    SoilCard {
                        SoilSectionTitle("Foto yang di potret", Icons.Default.CameraAlt)
                        Spacer(Modifier.height(10.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .aspectRatio(16 / 9f)
                                .clip(RoundedCornerShape(12.dp))
                                .background(SoilGreenLight)
                        ) {
                            Image(
                                painter = if (inputs.image != null)
                                    BitmapPainter(inputs.image!!.asImageBitmap())
                                else rememberVectorPainter(Icons.Default.Image),
                                contentDescription = "Foto tanah",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                            // confidence badge overlay
                            Box(
                                modifier = Modifier
                                    .align(Alignment.BottomStart)
                                    .padding(8.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(SoilGreenDark.copy(alpha = 0.75f))
                                    .padding(horizontal = 10.dp, vertical = 5.dp)
                            ) {
                                Text(
                                    text = soilType,
                                    color = Color.White,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Box(
                                modifier = Modifier
                                    .align(Alignment.BottomEnd)
                                    .padding(8.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(SoilAmber.copy(alpha = 0.9f))
                                    .padding(horizontal = 10.dp, vertical = 5.dp)
                            ) {
                                Text(
                                    text = "${String.format("%.1f", soilPred * 100)}%",
                                    color = Color.White,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    // ── Prediction chart ──────────────────────────────────────
                    SoilCard {
                        SoilSectionTitle("Diagram Prediksi Jenis Tanah", Icons.Default.BarChart)
                        Spacer(Modifier.height(10.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(230.dp)
                        ) {
                            JetpackComposeBasicColumnChart(
                                modelProducer,
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                    }

                    // ── Prediction results with tab ───────────────────────────
                    SoilCard {
                        var isTanahPage by remember { mutableStateOf(true) }
                        val value: Response? = response
                        val pH: Float? = inputs.soil.pH ?: value?.soil?.pH

                        SoilSectionTitle("Hasil Prediksi", Icons.Default.Analytics)
                        Spacer(Modifier.height(10.dp))

                        // Tab row
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .background(SoilGreenLight)
                                .padding(4.dp),
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            SoilTab(
                                label = "Tanah",
                                selected = isTanahPage,
                                onClick = { isTanahPage = true },
                                modifier = Modifier.weight(1f)
                            )
                            SoilTab(
                                label = "Geografis",
                                selected = !isTanahPage,
                                onClick = { isTanahPage = false },
                                modifier = Modifier.weight(1f)
                            )
                        }

                        Spacer(Modifier.height(12.dp))

                        if (isTanahPage) {
                            val pred = soilPred * 100.0
                            Column(verticalArrangement = Arrangement.spacedBy(0.dp)) {
                                SoilDataRow(
                                    "pH Tanah",
                                    (inputs.soil.pH ?: value?.soil?.pH).toString()
                                )
                                SoilDividerLine()
                                SoilDataRow(
                                    "Tipe Tanah",
                                    "$soilType  •  ${String.format("%.2f", pred)}%"
                                )
                                SoilDividerLine()
                                SoilDataRow("Tekstur", texr.toString())
                                SoilDividerLine()
                                SoilDataRow("Drainase", drain.toString())
                                SoilDividerLine()
                                SoilDataRow("Kesuburan", ferr.toString())
                                SoilDividerLine()
                                SoilDataRow("Kedalaman", "$depth cm")
                            }
                        } else {
                            Column(verticalArrangement = Arrangement.spacedBy(0.dp)) {
                                SoilDataRow("MDPL", geo?.altitude?.round() ?: "–")
                                SoilDividerLine()
                                SoilDataRow("Iklim", geo?.iklim?.head ?: "–")
                                SoilDividerLine()
                                SoilDataRow("Min °C", geo?.min?.round() ?: "–")
                                SoilDividerLine()
                                SoilDataRow("Max °C", geo?.max?.round() ?: "–")
                            }
                        }
                    }

                    // ── pH neutralisation advice ──────────────────────────────
                    val value: Response? = response
                    val pH: Float? = inputs.soil.pH ?: value?.soil?.pH
                    if (pH != null) {
                        if (pH <= 7f) {
                            val dosisKg = getDosis(soilType, pH, 7f, depth) * 1000
                            SoilAdviceCard(
                                icon = Icons.Default.LocalHospital,
                                title = "Netralisasi pH Tanah",
                                accent = SoilGreenDark,
                                accentLight = SoilGreenLight
                            ) {
                                MarkdownText(
                                    markdown = if (pH == 7f) "pH tanah anda bagus dan netral!"
                                    else "Untuk mencapai pH netral **dari ${
                                        inputs.soil.pH ?: (value?.soil?.pH.toString() + " (default)")
                                    } → 7**, dibutuhkan **${dosisKg.round()} kg** kapur dolomit per hektar " +
                                            "atau **${(dosisKg / 100).round()} kg** per meter persegi.",
                                    style = TextStyle(
                                        fontSize = 14.sp,
                                        color = SoilTextPrimary,
                                        lineHeight = 22.sp
                                    )
                                )
                            }
                        } else {
                            SoilAdviceCard(
                                icon = Icons.Default.LocalHospital,
                                title = "Netralisasi pH Tanah",
                                accent = SoilAmber,
                                accentLight = SoilAmberLight
                            ) {
                                MarkdownText(
                                    markdown = "**Tanah Anda terlalu basa (pH > 7).** Kondisi ini dapat menghambat penyerapan hara. " +
                                            "Tambahkan kompos, pupuk kandang, atau serasah daun untuk menurunkan pH secara alami. " +
                                            "Untuk hasil lebih cepat, tambahkan sedikit belerang (sulfur) dan jaga kelembapan tanah.",
                                    style = TextStyle(
                                        fontSize = 14.sp,
                                        color = SoilTextPrimary,
                                        lineHeight = 22.sp
                                    )
                                )
                            }
                        }
                    }

                    // ── Water retention advice ────────────────────────────────
                    SoilAdviceCard(
                        icon = Icons.Default.WaterDrop,
                        title = "Retensi Air",
                        accent = Color(0xFF1565C0),
                        accentLight = Color(0xFFE3F2FD)
                    ) {
                        MarkdownText(
                            markdown = retention(soilType),
                            style = TextStyle(
                                fontSize = 14.sp,
                                color = SoilTextPrimary,
                                lineHeight = 22.sp
                            )
                        )
                    }

                    Spacer(Modifier.height(72.dp))
                }
            })

        // Close button – floating top-left
        IconButton(
            onClick = click,
            modifier = Modifier
                .align(Alignment.TopStart)
                .statusBarsPadding()
                .padding(start = 16.dp)
                .size(36.dp)
                .clip(CircleShape)
                .background(Color.White.copy(alpha = 0.8f))
        ) {
            Icon(Icons.Default.Close, "Tutup", tint = SoilGreenDark)
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
//  Private helper composables
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun SoilCard(content: @Composable ColumnScope.() -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(clipRound),
        colors = CardDefaults.cardColors(containerColor = SoilCardBg),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            content = content
        )
    }
}

@Composable
private fun SoilSectionTitle(text: String, icon: ImageVector) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(32.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(SoilGreenLight),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, null, tint = SoilGreenMid, modifier = Modifier.size(18.dp))
        }
        Spacer(Modifier.width(10.dp))
        Text(text, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = SoilGreenDark)
    }
}

@Composable
private fun SoilTab(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(if (selected) SoilGreenDark else Color.Transparent)
            .clickable(onClick = onClick)
            .padding(vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            fontSize = 14.sp,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
            color = if (selected) Color.White else SoilTextMuted
        )
    }
}

@Composable
private fun SoilDataRow(key: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(key, fontSize = 14.sp, color = SoilTextMuted, fontWeight = FontWeight.Normal)
        Text(value, fontSize = 14.sp, color = SoilTextPrimary, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun SoilDividerLine() {
    Divider(color = SoilDivider, thickness = 0.5.dp)
}

@Composable
private fun SoilAdviceCard(
    icon: ImageVector,
    title: String,
    accent: Color,
    accentLight: Color,
    content: @Composable () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(clipRound),
        colors = CardDefaults.cardColors(containerColor = SoilCardBg),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // Coloured header band
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(accentLight)
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(accent.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(icon, null, tint = accent, modifier = Modifier.size(18.dp))
                }
                Spacer(Modifier.width(10.dp))
                Text(title, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = accent)
            }
            // Body
            Box(modifier = Modifier.padding(16.dp)) { content() }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
//  Chart (unchanged logic, updated column color to match palette)
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun JetpackComposeBasicColumnChart(
    modelProducer: CartesianChartModelProducer,
    modifier: Modifier
) {
    val scroll = rememberVicoScrollState()
    CartesianChartHost(
        chart = rememberCartesianChart(
            rememberColumnCartesianLayer(
                columnProvider = ColumnCartesianLayer.ColumnProvider.series(
                    rememberLineComponent(
                        fill = fill(SoilGreenMid),
                        thickness = 10.dp,
                        shadow = Shadow(4f),
                        margins = Insets(2f),
                    ),
                ),
                columnCollectionSpacing = 2.dp,
                dataLabel = rememberTextComponent(
                    textAlignment = Layout.Alignment.ALIGN_NORMAL,
                ),
                verticalAxisPosition = Axis.Position.Vertical.Start,
                rangeProvider = remember {
                    CartesianLayerRangeProvider.fixed(minY = 0.0, maxY = 1.0)
                },
            ),
            startAxis = VerticalAxis.rememberStart(
                labelRotationDegrees = 0f,
                size = BaseAxis.Size.auto(0.dp, 100.dp),
                verticalLabelPosition = Position.Vertical.Center,
                horizontalLabelPosition = VerticalAxis.HorizontalLabelPosition.Outside,
                label = rememberAxisLabelComponent(color = SoilGreenDark)
            ),
            topAxis = HorizontalAxis.rememberTop(
                label = rememberTextComponent(
                    textSize = 12.sp,
                    textAlignment = Layout.Alignment.ALIGN_CENTER,
                    typeface = Typeface.MONOSPACE,
                    lineCount = 1,
                    lineHeight = 16.sp,
                ),
                valueFormatter = CartesianValueFormatter { context, x, _ ->
                    context.model.extraStore[labelListKey][x.toInt()]
                },
                labelRotationDegrees = -80f,
            )
        ),
        animateIn = true,
        modelProducer = modelProducer,
        modifier = modifier,
        scrollState = scroll
    )
}