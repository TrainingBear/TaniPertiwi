package com.trbear9.ui.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Link
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.trbear9.ui.R
import com.trbear9.ui.util.DataStore
import com.trbear9.ui.util.Screen
import dev.jeziellago.compose.markdowntext.MarkdownText

// ─────────────────────────────────────────────────────────────────────────────
//  Palette
// ─────────────────────────────────────────────────────────────────────────────
private val PDGreenDark   = Color(0xFF1B4332)
private val PDGreenMid    = Color(0xFF52B788)
private val PDGreenLight  = Color(0xFFD8F3DC)
private val PDCream       = Color(0xFFF9F6F0)
private val PDCardBg      = Color(0xFFFFFFFE)
private val PDTextMuted   = Color(0xFF6B7B6E)
private val PDDivider     = Color(0xFFE0EDE4)

// ─────────────────────────────────────────────────────────────────────────────
//  PointDetail
// ─────────────────────────────────────────────────────────────────────────────
@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
fun PointDetail(
    nav: NavController? = null,
    title: String = "Tanah",
    subtitle: String = "Tanah sangat vital peranannya bagi semua kehidupan di bumi karena tanah mendukung kehidupan tumbuhan.",
    id: String = " ",
    credits: List<String>? = listOf("en.wikipedia.org/wiki/Soil_pH", "agrotek.id/istilah/retensi-air"),
    details: List<Triple<Pair<Int?, String?>?, String, String>> = listOf(
        Triple(
            Pair(R.drawable.padi, "pertanian.uma.ac.id"),
            "Pengertian",
            "Tanah sangat vital peranannya bagi semua kehidupan di bumi karena tanah mendukung kehidupan tumbuhan dengan menyediakan unsur hara dan air sekaligus sebagai penopang akar tanaman."
        )
    )
) {
    val isAlreadyDone = DataStore.contains(id)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(listOf(Color(0xFFE8F5E9), PDCream, Color(0xFFF0EDE6)))
            )
    ) {
        Scaffold(containerColor = Color.Transparent) { padding ->
            LazyColumn(
                modifier       = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentPadding = PaddingValues(bottom = 32.dp)
            ) {

                // ── Hero header ───────────────────────────────────────────
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                Brush.linearGradient(
                                    colors = listOf(PDGreenDark, Color(0xFF2D6A4F), PDGreenMid)
                                )
                            )
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 24.dp)
                        ) {
                            // Back button
                            IconButton(
                                onClick  = { nav?.navigateUp() },
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(Color.White.copy(alpha = 0.15f))
                            ) {
                                Icon(
                                    Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                                    "Kembali",
                                    tint     = Color.White,
                                    modifier = Modifier.size(24.dp)
                                )
                            }

                            Spacer(Modifier.height(12.dp))

                            Text(
                                text       = title,
                                fontSize   = 30.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color      = Color.White,
                                overflow   = TextOverflow.Visible
                            )

                            Spacer(Modifier.height(10.dp))

                            // Subtitle inset box
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(Color.White.copy(alpha = 0.12f))
                                    .padding(14.dp)
                            ) {
                                Text(
                                    text       = subtitle,
                                    fontSize   = 15.sp,
                                    color      = Color.White.copy(alpha = 0.9f),
                                    textAlign  = TextAlign.Justify,
                                    lineHeight = 23.sp
                                )
                            }
                        }
                    }
                }

                // ── Content cards ─────────────────────────────────────────
                itemsIndexed(details) { index, perInfo ->
                    Spacer(Modifier.height(if (index == 0) 16.dp else 0.dp))
                    ContentCard(index = index + 1, total = details.size, perInfo = perInfo)
                    Spacer(Modifier.height(12.dp))
                }

                // ── Credits ───────────────────────────────────────────────
                if (!credits.isNullOrEmpty()) {
                    item {
                        Card(
                            shape     = RoundedCornerShape(14.dp),
                            colors    = CardDefaults.cardColors(containerColor = PDCardBg),
                            elevation = CardDefaults.cardElevation(1.dp),
                            modifier  = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(
                                    verticalAlignment     = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Icon(Icons.Default.Link, null, tint = PDGreenMid, modifier = Modifier.size(18.dp))
                                    Text(
                                        "Sumber Informasi",
                                        fontSize   = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        color      = PDGreenDark
                                    )
                                }
                                Spacer(Modifier.height(10.dp))
                                credits.forEachIndexed { i, credit ->
                                    if (i > 0) HorizontalDivider(
                                        color     = PDDivider,
                                        thickness = 0.5.dp,
                                        modifier  = Modifier.padding(vertical = 6.dp)
                                    )
                                    Text(
                                        text     = credit,
                                        fontSize = 12.sp,
                                        color    = PDTextMuted,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                            }
                        }
                        Spacer(Modifier.height(16.dp))
                    }
                }

                // ── Finish / Back button ──────────────────────────────────
                item {
                    Column(
                        modifier            = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        if (isAlreadyDone) {
                            Row(
                                modifier              = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(PDGreenLight)
                                    .padding(vertical = 16.dp),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment     = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.CheckCircle, null, tint = PDGreenMid, modifier = Modifier.size(22.dp))
                                Spacer(Modifier.width(8.dp))
                                Text(
                                    "Topik ini sudah selesai!",
                                    fontSize   = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color      = PDGreenDark
                                )
                            }
                            Spacer(Modifier.height(12.dp))
                            OutlinedButton(
                                onClick  = { nav?.navigate(Screen.help) },
                                shape    = RoundedCornerShape(16.dp),
                                colors   = ButtonDefaults.outlinedButtonColors(contentColor = PDGreenDark),
                                modifier = Modifier.fillMaxWidth().height(52.dp)
                            ) {
                                Text("Kembali ke Panduan", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                            }
                        } else {
                            Button(
                                onClick  = {
                                    DataStore.setBoolean(id, true)
                                    nav?.navigate(Screen.help)
                                },
                                shape    = RoundedCornerShape(16.dp),
                                colors   = ButtonDefaults.buttonColors(containerColor = PDGreenDark),
                                elevation = ButtonDefaults.buttonElevation(defaultElevation = 6.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(56.dp)
                                    .shadow(8.dp, RoundedCornerShape(16.dp))
                            ) {
                                Icon(Icons.Default.CheckCircle, null, tint = Color.White, modifier = Modifier.size(20.dp))
                                Spacer(Modifier.width(10.dp))
                                Text(
                                    "Tandai Selesai",
                                    fontSize   = 17.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color      = Color.White
                                )
                            }
                        }
                    }
                    Spacer(Modifier.height(32.dp))
                }
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
//  ContentCard – individual info section
// ─────────────────────────────────────────────────────────────────────────────
@Composable
private fun ContentCard(
    index: Int,
    total: Int,
    perInfo: Triple<Pair<Int?, String?>?, String, String>
) {
    Card(
        shape     = RoundedCornerShape(16.dp),
        colors    = CardDefaults.cardColors(containerColor = PDCardBg),
        elevation = CardDefaults.cardElevation(2.dp),
        modifier  = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {

            // Section header badge
            if (perInfo.second.isNotBlank()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(PDGreenLight)
                        .padding(horizontal = 16.dp, vertical = 10.dp),
                    verticalAlignment     = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier         = Modifier
                            .size(28.dp)
                            .clip(CircleShape)
                            .background(PDGreenMid),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            index.toString(),
                            fontSize   = 13.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color      = Color.White
                        )
                    }
                    Text(
                        perInfo.second,
                        fontSize   = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color      = PDGreenDark,
                        modifier   = Modifier.weight(1f)
                    )
                    Text(
                        "$index / $total",
                        fontSize = 12.sp,
                        color    = PDTextMuted
                    )
                }
            }

            // Image
            if (perInfo.first?.first != null) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(16 / 9f)
                        .background(PDGreenLight)
                ) {
                    Image(
                        painter            = painterResource(perInfo.first!!.first!!),
                        contentDescription = perInfo.second,
                        contentScale       = ContentScale.Crop,
                        modifier           = Modifier.fillMaxSize()
                    )
                    // Image credit badge
                    perInfo.first?.second?.let { credit ->
                        Box(
                            modifier = Modifier
                                .align(Alignment.BottomStart)
                                .padding(8.dp)
                                .clip(RoundedCornerShape(6.dp))
                                .background(Color.Black.copy(alpha = 0.5f))
                                .padding(horizontal = 8.dp, vertical = 3.dp)
                        ) {
                            Text("© $credit", fontSize = 10.sp, color = Color.White)
                        }
                    }
                }
            }

            // Markdown body
            if (perInfo.third.isNotBlank()) {
                MarkdownText(
                    markdown = perInfo.third,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                )
            }
        }
    }
}