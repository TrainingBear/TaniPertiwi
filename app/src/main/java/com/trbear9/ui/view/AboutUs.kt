package com.trbear9.ui.view

import android.content.Intent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.Message
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import androidx.navigation.NavController
import com.trbear9.ui.R
import com.trbear9.ui.util.Screen
import dev.jeziellago.compose.markdowntext.BuildConfig

// ─────────────────────────────────────────────────────────────────────────────
//  Palette (consistent with the rest of the app)
// ─────────────────────────────────────────────────────────────────────────────
private val AUGreenDark   = Color(0xFF1B4332)
private val AUGreenMid    = Color(0xFF52B788)
private val AUGreenLight  = Color(0xFFD8F3DC)
private val AUCream       = Color(0xFFF9F6F0)
private val AUAmber       = Color(0xFFF5A623)
private val AUAmberLight  = Color(0xFFFFF3E0)
private val AUCardBg      = Color(0xFFFFFFFE)
private val AUTextPrimary = Color(0xFF1A1A1A)
private val AUTextMuted   = Color(0xFF6B7B6E)
private val AUDivider     = Color(0xFFE0EDE4)
private val AUBlue        = Color(0xFF1565C0)


// ─────────────────────────────────────────────────────────────────────────────
//  AboutUs screen
// ─────────────────────────────────────────────────────────────────────────────
@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
fun AboutUs(nav: NavController? = null) {
    val context    = LocalContext.current
    val uriHandler = LocalUriHandler.current
    val packageInfo = context.packageManager.getPackageInfo(context.packageName, 0)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(listOf(Color(0xFFE8F5E9), AUCream, Color(0xFFF0EDE6)))
            )
    ) {
        Scaffold(containerColor = Color.Transparent) { padding ->
            LazyColumn(
                modifier       = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentPadding = PaddingValues(bottom = 48.dp)
            ) {

                // ── Hero ──────────────────────────────────────────────────
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                Brush.linearGradient(
                                    colors = listOf(AUGreenDark, Color(0xFF2D6A4F), AUGreenMid)
                                )
                            )
                    ) {
                        // Back button
                        IconButton(
                            onClick  = { nav?.navigate(Screen.home) },
                            modifier = Modifier
                                .padding(12.dp)
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(Color.White.copy(alpha = 0.15f))
                                .align(Alignment.TopStart)
                        ) {
                            Icon(
                                Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                                "Kembali",
                                tint     = Color.White,
                                modifier = Modifier.size(24.dp)
                            )
                        }

                        Column(
                            modifier            = Modifier
                                .fillMaxWidth()
                                .padding(top = 56.dp, bottom = 32.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            // App icon / sapling image in circle
                            Box(
                                modifier = Modifier
                                    .size(110.dp)
                                    .clip(CircleShape)
                                    .background(Color.White.copy(alpha = 0.15f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Image(
                                    painter            = painterResource(R.drawable.oak_sapling),
                                    contentDescription = "TaniPertiwi logo",
                                    contentScale       = ContentScale.Fit,
                                    modifier           = Modifier.size(80.dp)
                                )
                            }

                            Spacer(Modifier.height(16.dp))

                            Text(
                                "Tani Pertiwi",
                                fontSize   = 34.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color      = Color.White
                            )
                            Text(
                                "Panduan cerdas untuk Agrikultur\nberkelanjutan berbasis teknologi.",
                                fontSize   = 14.sp,
                                color      = Color.White.copy(alpha = 0.8f),
                                textAlign  = TextAlign.Center,
                                lineHeight = 21.sp,
                                modifier   = Modifier.padding(horizontal = 40.dp, vertical = 8.dp)
                            )

                            // Version chip
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(20.dp))
                                    .background(Color.White.copy(alpha = 0.15f))
                                    .padding(horizontal = 16.dp, vertical = 6.dp)
                            ) {
                                Text(
                                    "v${packageInfo.versionName}  •  2025",
                                    fontSize  = 13.sp,
                                    color     = Color.White.copy(alpha = 0.9f),
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }
                }

                // ── Intro card ────────────────────────────────────────────
                item {
                    Spacer(Modifier.height(16.dp))
                    AUCard(modifier = Modifier.padding(horizontal = 16.dp)) {
                        AUSectionTitle("Pengenalan", Icons.Default.Info)
                        Spacer(Modifier.height(10.dp))
                        Text(
                            text = "Aplikasi ini mulai dikembangkan pada 24 Mei 2025 oleh " +
                                    "Kukuh, Refan (Pelajar SMA Negeri 1 Ambarawa) dan " +
                                    "Renaldi (Pelajar MAN 3 Jakarta Pusat).",
                            fontSize   = 15.sp,
                            color      = AUTextPrimary,
                            textAlign  = TextAlign.Justify,
                            lineHeight = 24.sp
                        )
                    }
                    Spacer(Modifier.height(10.dp))
                }

                // ── Team logos ────────────────────────────────────────────
                item {
                    AUCard(modifier = Modifier.padding(horizontal = 16.dp)) {
                        AUSectionTitle("Institusi", Icons.Default.School)
                        Spacer(Modifier.height(14.dp))
                        Row(
                            modifier              = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly,
                            verticalAlignment     = Alignment.CenterVertically
                        ) {
                            // SMAN 1 Ambarawa
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier            = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .clickable {
                                        context.startActivity(
                                            Intent(Intent.ACTION_VIEW, "https://sman1ambarawa.sch.id/".toUri())
                                        )
                                    }
                                    .padding(12.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(64.dp)
                                        .clip(CircleShape)
                                        .background(AUGreenLight),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Image(
                                        painter            = painterResource(R.drawable.smanega),
                                        contentDescription = "SMA Negeri 1 Ambarawa",
                                        contentScale       = ContentScale.Fit,
                                        modifier           = Modifier.size(52.dp)
                                    )
                                }
                                Spacer(Modifier.height(6.dp))
                                Text("SMAN 1 Ambarawa", fontSize = 11.sp, color = AUTextMuted, textAlign = TextAlign.Center)
                            }

                            // Divider
                            Box(modifier = Modifier.width(1.dp).height(60.dp).background(AUDivider))

                            // MAN 3 Jakarta
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier            = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .clickable {
                                        context.startActivity(
                                            Intent(Intent.ACTION_VIEW, "https://man3jkt.sch.id/".toUri())
                                        )
                                    }
                                    .padding(12.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(64.dp)
                                        .clip(CircleShape)
                                        .background(AUAmberLight),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Image(
                                        painter            = painterResource(R.drawable.logoman3),
                                        contentDescription = "MAN 3 Jakarta",
                                        contentScale       = ContentScale.Fit,
                                        modifier           = Modifier.size(52.dp)
                                    )
                                }
                                Spacer(Modifier.height(6.dp))
                                Text("MAN 3 Jakarta Pusat", fontSize = 11.sp, color = AUTextMuted, textAlign = TextAlign.Center)
                            }
                        }
                    }
                    Spacer(Modifier.height(10.dp))
                }

                // ── License expand card ───────────────────────────────────
                item {
                    val licenseExpand = remember { mutableStateOf(false) }
                    val licenseAnnotated = buildAnnotatedString {
                        withStyle(SpanStyle(fontWeight = FontWeight.Bold, color = AUGreenDark)) {
                            append("© 2025 Jaksara\n\n")
                        }
                        append("Aplikasi ini di dalam lisensi Creative Commons Attribution-ShareAlike 4.0 International (CC BY-SA 4.0). " +
                                "Anda bebas menggunakan dan membagikan aplikasi ini dengan atribusi yang tepat.\n\n")
                        withStyle(SpanStyle(fontWeight = FontWeight.Medium)) { append("License details: ") }
                        pushStringAnnotation("URL", "https://creativecommons.org/licenses/by-sa/4.0/")
                        withStyle(SpanStyle(color = AUBlue, textDecoration = TextDecoration.Underline)) {
                            append("creativecommons.org/licenses/by-sa/4.0/")
                        }
                        pop()
                        append("\n\n")
                        withStyle(SpanStyle(fontWeight = FontWeight.Bold)) { append("Data pihak ketiga:\n") }
                        append("• Ecocrop database © FAO 2025  ")
                        pushStringAnnotation("URL", "https://gaez.fao.org/pages/ecocrop-search")
                        withStyle(SpanStyle(color = AUBlue, textDecoration = TextDecoration.Underline)) {
                            append("gaez.fao.org")
                        }
                        pop()
                        append("\n• Taxonomic plant © Royal Botanic Gardens, Kew – CC BY 4.0  ")
                        pushStringAnnotation("URL", "https://www.kew.org/science/data-and-resources")
                        withStyle(SpanStyle(color = AUBlue, textDecoration = TextDecoration.Underline)) {
                            append("kew.org")
                        }
                        pop()
                        append("\n• Weather API © Open-Meteo – CC BY 4.0  ")
                        pushStringAnnotation("URL", "https://open-meteo.com/")
                        withStyle(SpanStyle(color = AUBlue, textDecoration = TextDecoration.Underline)) {
                            append("open-meteo.com")
                        }
                        pop()
                    }
                    AUExpandCard(
                        title       = "Lisensi",
                        icon        = Icons.Default.LocalPolice,
                        accent      = AUGreenDark,
                        accentLight = AUGreenLight,
                        isExpanded  = licenseExpand
                    ) {
                        ClickableText(
                            text     = licenseAnnotated,
                            onClick  = { offset ->
                                licenseAnnotated.getStringAnnotations("URL", offset, offset)
                                    .firstOrNull()?.let { uriHandler.openUri(it.item) }
                            },
                            modifier = Modifier.fillMaxWidth().padding(16.dp)
                        )
                    }
                    Spacer(Modifier.height(10.dp))
                }

                // ── Social Media expand card ──────────────────────────────
                item {
                    val socialExpand = remember { mutableStateOf(false) }
                    val socialAnnotated = buildAnnotatedString {
                        withStyle(SpanStyle(fontWeight = FontWeight.Bold)) { append("Discord  ") }
                        pushStringAnnotation("URL", "https://discord.gg/fbAZSd3Hf2")
                        withStyle(SpanStyle(color = AUBlue, textDecoration = TextDecoration.Underline)) {
                            append("discord.gg/fbAZSd3Hf2")
                        }
                        pop()
                        append("\n\n")
                        withStyle(SpanStyle(fontWeight = FontWeight.Bold)) { append("YouTube  ") }
                        pushStringAnnotation("URL", "https://www.youtube.com/channel/UCglCTRnlIGvO5o5VfI1t9NQ")
                        withStyle(SpanStyle(color = AUBlue, textDecoration = TextDecoration.Underline)) {
                            append("youtube.com/c/kujatic")
                        }
                        pop()
                    }
                    AUExpandCard(
                        title       = "Media Sosial",
                        icon        = Icons.AutoMirrored.Filled.Message,
                        accent      = Color(0xFF1565C0),
                        accentLight = Color(0xFFE3F2FD),
                        isExpanded  = socialExpand
                    ) {
                        ClickableText(
                            text     = socialAnnotated,
                            onClick  = { offset ->
                                socialAnnotated.getStringAnnotations("URL", offset, offset)
                                    .firstOrNull()?.let { uriHandler.openUri(it.item) }
                            },
                            modifier = Modifier.fillMaxWidth().padding(16.dp)
                        )
                    }
                    Spacer(Modifier.height(10.dp))
                }

                // ── FAQ expand card ───────────────────────────────────────
                item {
                    val faqExpand = remember { mutableStateOf(false) }
                    val faqs = listOf(
                        "Bagaimana aplikasi ini dapat dipercaya?" to
                                "Kami menggunakan data tanaman dari ECOCROP oleh FAO. Setiap data umum pada tanaman sudah terverifikasi oleh para ahli, sehingga semua data tanaman di aplikasi ini sangat dapat dipercaya.",
                        "Mengapa terdapat tanaman asing di hasil rekomendasi saya?" to
                                "Data tanaman mencakup tanaman di seluruh dunia. Tanaman tersebut tidak tersebar secara luas, sehingga tidak tersebar ke Indonesia.",
                        "Apakah aplikasi ini berguna untuk petani maupun pemula?" to
                                "Ya, sangat berguna seperti mencari informasi maupun rekomendasi tanaman atau tumbuhan, yang cocok untuk ditanam sesuai dengan tanah/media yang ingin ditanam."
                    )
                    AUExpandCard(
                        title       = "FAQ",
                        icon        = Icons.Default.QuestionAnswer,
                        accent      = AUAmber,
                        accentLight = AUAmberLight,
                        isExpanded  = faqExpand
                    ) {
                        Column(
                            modifier            = Modifier.fillMaxWidth().padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(14.dp)
                        ) {
                            faqs.forEachIndexed { i, (q, a) ->
                                if (i > 0) HorizontalDivider(color = AUDivider, thickness = 0.5.dp)
                                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                    Text(
                                        "Q: $q",
                                        fontSize   = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        color      = AUTextPrimary
                                    )
                                    Text(
                                        "A: $a",
                                        fontSize   = 14.sp,
                                        color      = AUTextMuted,
                                        lineHeight = 21.sp
                                    )
                                }
                            }
                        }
                    }
                    Spacer(Modifier.height(24.dp))
                }

                // ── Footer ────────────────────────────────────────────────
                item {
                    Column(
                        modifier            = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            "Dibuat dengan ❤️ oleh tim Jaksara",
                            fontSize  = 13.sp,
                            color     = AUTextMuted,
                            fontStyle = FontStyle.Italic
                        )
                        Spacer(Modifier.height(4.dp))
                        Text(
                            "Version ${packageInfo.versionName}  •  © 2025",
                            fontSize = 12.sp,
                            color    = AUTextMuted.copy(alpha = 0.6f)
                        )
                    }
                    Spacer(Modifier.height(32.dp))
                }
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
//  Helper composables
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun AUCard(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier  = modifier.fillMaxWidth(),
        shape     = RoundedCornerShape(16.dp),
        colors    = CardDefaults.cardColors(containerColor = AUCardBg),
        elevation = CardDefaults.cardElevation(2.dp),
    ) {
        Column(modifier = Modifier.padding(16.dp), content = content)
    }
}

@Composable
private fun AUSectionTitle(text: String, icon: ImageVector, tint: Color = AUGreenMid) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(32.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(AUGreenLight),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, null, tint = tint, modifier = Modifier.size(18.dp))
        }
        Spacer(Modifier.width(10.dp))
        Text(text, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = AUGreenDark)
    }
}

@Composable
private fun AUExpandCard(
    title:       String,
    icon:        ImageVector,
    accent:      Color,
    accentLight: Color,
    isExpanded:  MutableState<Boolean>,
    content:     @Composable ColumnScope.() -> Unit
) {
    val arrowAngle by animateFloatAsState(
        targetValue   = if (isExpanded.value) 90f else 0f,
        animationSpec = tween(300),
        label         = "arrowRotate"
    )

    Card(
        modifier  = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
        shape     = RoundedCornerShape(16.dp),
        colors    = CardDefaults.cardColors(containerColor = AUCardBg),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        // Header row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(if (isExpanded.value) RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp) else RoundedCornerShape(16.dp))
                .background(accentLight)
                .clickable { isExpanded.value = !isExpanded.value }
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment     = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
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
            Text(
                title,
                fontSize   = 16.sp,
                fontWeight = FontWeight.Bold,
                color      = accent,
                modifier   = Modifier.weight(1f)
            )
            Icon(
                Icons.Default.KeyboardArrowRight,
                null,
                tint     = accent,
                modifier = Modifier.size(22.dp).rotate(arrowAngle)
            )
        }

        // Expandable body
        AnimatedVisibility(
            visible = isExpanded.value,
            enter   = expandVertically(
                expandFrom    = Alignment.Top,
                animationSpec = tween(350, easing = LinearOutSlowInEasing)
            ) + fadeIn(tween(250)),
            exit    = shrinkVertically(
                shrinkTowards = Alignment.Top,
                animationSpec = tween(300, easing = FastOutLinearInEasing)
            ) + fadeOut(tween(200))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(AUCardBg),
                content = content
            )
        }
    }
}