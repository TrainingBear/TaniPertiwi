package com.trbear9.ui.view

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.Park
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import androidx.navigation.NavController
import com.pseudoankit.coachmark.UnifyCoachmark
import com.pseudoankit.coachmark.model.ToolTipPlacement
import com.trbear9.plants.Data
import com.trbear9.plants.TFService
import com.trbear9.ui.util.DataStore
import com.trbear9.ui.util.Screen
import com.trbear9.ui.App
import com.trbear9.ui.R
import com.trbear9.ui.util.MarkKey
import com.trbear9.ui.util.getContext
import com.trbear9.ui.util.highlightConfig
import com.trbear9.ui.viewmodel.scanTanahButton
import com.trbear9.ui.viewmodel.perm
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.*
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush

//var firstTime = false
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.ui.graphics.vector.ImageVector
import com.trbear9.ui.R.drawable.icon_app
import com.trbear9.ui.viewmodel.recommendationResult

// ─────────────────────────────────────────────────────────────────────────────
//  Palette
// ─────────────────────────────────────────────────────────────────────────────
private val HomeGreenDark = Color(0xFF1B4332)
private val HomeGreenMid = Color(0xFF52B788)
private val HomeGreenLight = Color(0xFFD8F3DC)
private val HomeCream = Color(0xFFF9F6F0)
private val HomeAmber = Color(0xFFF5A623)
private val HomeAmberLight = Color(0xFFFFF3E0)
private val HomeCardBg = Color(0xFFFFFFFE)
private val HomeTextPrimary = Color(0xFF1A1A1A)
private val HomeTextMuted = Color(0xFF6B7B6E)

// ─────────────────────────────────────────────────────────────────────────────
//  MainActivity
// ─────────────────────────────────────────────────────────────────────────────
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        perm = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {}
        setContent {
            var isReady by remember { mutableStateOf(false) }
            LaunchedEffect(Unit) {
                withContext(Dispatchers.IO) {
                    DataStore.init(this@MainActivity)
                    Data.load(this@MainActivity)
                }
                isReady = true
            }
            App(this@MainActivity, isReady)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        TFService.close()
    }
}

// ─────────────────────────────────────────────────────────────────────────────
//  Home
// ─────────────────────────────────────────────────────────────────────────────
@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
fun Home(nav: NavController? = null) {
    val context = getContext()
    val scroll = rememberScrollState()
    var showDonate by remember { mutableStateOf(false) }

    // Entrance animation
    var visible by remember { mutableStateOf(false) }
    val alpha by animateFloatAsState(
        targetValue = if (visible) 1f else 0f,
        animationSpec = tween(600, easing = FastOutSlowInEasing),
        label = "homeAlpha"
    )
    LaunchedEffect(Unit) {
        if (DataStore.firstTime) showDonate = true
        visible = true
    }

    // Sapling subtle float
    val infiniteTransition = rememberInfiniteTransition(label = "heroFloat")
    val heroOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = -10f,
        animationSpec = infiniteRepeatable(tween(2200, easing = EaseInOutSine), RepeatMode.Reverse),
        label = "heroOffset"
    )

    UnifyCoachmark {
        LaunchedEffect(Unit) {
            if (DataStore.firstTime) show(MarkKey.scantanah, MarkKey.help)
        }

        // ── Donate dialog ─────────────────────────────────────────────────
        if (showDonate) {
            BasicAlertDialog(
                onDismissRequest = { showDonate = false },
                modifier = Modifier.padding(horizontal = 8.dp)
            ) {
                Card(
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = HomeCardBg),
                    elevation = CardDefaults.cardElevation(8.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(horizontal = 24.dp, vertical = 28.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .size(56.dp)
                                .clip(CircleShape)
                                .background(HomeAmberLight),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Default.Favorite,
                                null,
                                tint = HomeAmber,
                                modifier = Modifier.size(28.dp)
                            )
                        }
                        Spacer(Modifier.height(14.dp))
                        Text(
                            "Dukung pengembangan aplikasi ini",
                            fontSize = 17.sp, fontWeight = FontWeight.SemiBold,
                            textAlign = TextAlign.Center, lineHeight = 24.sp
                        )
                        Spacer(Modifier.height(8.dp))
                        Text(
                            "Jika kamu merasa project ini membantu, kamu bisa mendukung kami melalui donasi. Dukunganmu membantu biaya server dan pengembangan fitur baru.",
                            fontSize = 13.sp, textAlign = TextAlign.Center,
                            color = HomeTextMuted, lineHeight = 20.sp
                        )
                        Spacer(Modifier.height(14.dp))
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .background(HomeGreenLight)
                                .padding(horizontal = 14.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Icon(
                                Icons.Default.CheckCircle,
                                null,
                                tint = HomeGreenMid,
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                "Sekali donasi sudah sangat berarti untuk keberlanjutan aplikasi ini",
                                fontSize = 12.sp, color = HomeGreenDark, lineHeight = 18.sp
                            )
                        }
                        Spacer(Modifier.height(18.dp))
                        Button(
                            onClick = {
                                context.startActivity(
                                    Intent(
                                        Intent.ACTION_VIEW,
                                        "https://trakteer.id/kujatic/tip".toUri()
                                    )
                                )
                            },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(containerColor = HomeAmber),
                            shape = RoundedCornerShape(14.dp),
                            contentPadding = PaddingValues(vertical = 13.dp)
                        ) {
                            Text(
                                "Donasi via Trakteer",
                                fontSize = 15.sp,
                                color = Color.White,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                        Spacer(Modifier.height(4.dp))
                        TextButton(onClick = { showDonate = false }) {
                            Text("Mungkin nanti", color = HomeTextMuted, fontSize = 13.sp)
                        }
                    }
                }
            }
        }

        Scaffold(
            containerColor = Color.Transparent,
            bottomBar = {
                NavigationBar(containerColor = HomeCardBg) {
                    NavigationBarItem(
                        selected = true,
                        onClick = { nav?.navigate(Screen.home) },
                        icon = { Icon(Icons.Default.Home, "Home") },
                        label = { Text("Home") }
                    )
                    NavigationBarItem(
                        selected = false,
                        onClick = { nav?.navigate(Screen.soilResult) },
                        icon = { Icon(Icons.Default.Park, "Tanaman") },
                        label = { Text("Tanaman") }
                    )
                }
            }
        ) { padding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            listOf(
                                Color(0xFFE8F5E9),
                                HomeCream,
                                Color(0xFFF0EDE6)
                            )
                        )
                    )
            ) {
                // Decorative blobs
                Box(
                    modifier = Modifier
                        .size(260.dp)
                        .offset(x = 120.dp, y = (-80).dp)
                        .clip(CircleShape)
                        .background(HomeGreenLight.copy(alpha = 0.45f))
                        .align(Alignment.TopEnd)
                )
                Box(
                    modifier = Modifier
                        .size(180.dp)
                        .offset(x = (-60).dp, y = 60.dp)
                        .clip(CircleShape)
                        .background(HomeGreenMid.copy(alpha = 0.08f))
                        .align(Alignment.TopStart)
                )

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .verticalScroll(scroll)
                        .alpha(alpha),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    // ── Top app bar row ───────────────────────────────────
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Logo + title
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            modifier = Modifier.clickable { nav?.navigate(Screen.about) }
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(HomeGreenLight),
                                contentAlignment = Alignment.Center
                            ) {
                                Image(
                                    painter = painterResource(icon_app),
                                    contentDescription = "App icon",
                                    modifier = Modifier.size(28.dp)
                                )
                            }
                            Column {
                                Text(
                                    "TaniPertiwi",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = HomeGreenDark
                                )
                                Text(
                                    "by Jaksara",
                                    fontSize = 12.sp,
                                    color = HomeTextMuted
                                )
                            }
                        }

                        // Action icons
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            IconButton(
                                onClick = { showDonate = true },
                                modifier = Modifier
                                    .size(38.dp)
                                    .clip(CircleShape)
                                    .background(HomeAmberLight)
                            ) {
                                Icon(
                                    Icons.Default.MonetizationOn,
                                    "Donasi",
                                    tint = HomeAmber,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Spacer(Modifier.width(4.dp))
                            IconButton(
                                onClick = { nav?.navigate(Screen.help) },
                                modifier = Modifier
                                    .size(38.dp)
                                    .clip(CircleShape)
                                    .background(HomeGreenLight)
                                    .enableCoachMark(
                                        key = MarkKey.help,
                                        toolTipPlacement = ToolTipPlacement.Start,
                                        highlightedViewConfig = highlightConfig,
                                    ) { MarkKey.help.tooltip(ToolTipPlacement.Start) }
                            ) {
                                Icon(
                                    Icons.Default.MenuBook,
                                    "Panduan",
                                    tint = HomeGreenDark,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }

                    // ── Hero section ──────────────────────────────────────
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                            .clip(RoundedCornerShape(24.dp))
                            .background(
                                Brush.linearGradient(
                                    colors = listOf(HomeGreenDark, Color(0xFF2D6A4F), HomeGreenMid)
                                )
                            )
                    ) {
                        // Decorative circle in hero
                        Box(
                            modifier = Modifier
                                .size(160.dp)
                                .offset(x = 60.dp, y = (-30).dp)
                                .clip(CircleShape)
                                .background(Color.White.copy(alpha = 0.06f))
                                .align(Alignment.TopEnd)
                        )

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 22.dp, vertical = 28.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    "Panduan Cerdas",
                                    fontSize = 26.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Color.White,
                                    lineHeight = 32.sp
                                )
                                Text(
                                    "Agrikultur",
                                    fontSize = 26.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Color(0xFFA7F3C2),
                                    lineHeight = 32.sp
                                )
                                Spacer(Modifier.height(10.dp))
                                Text(
                                    "Rekomendasi tanaman berbasis\nanalisis tanah dan teknologi AI",
                                    fontSize = 13.sp,
                                    color = Color.White.copy(alpha = 0.75f),
                                    lineHeight = 20.sp
                                )
                            }
                            // Floating sapling
                            Image(
                                painter = painterResource(R.drawable.icon_app),
                                contentDescription = null,
                                contentScale = ContentScale.Fit,
                                modifier = Modifier
                                    .size(100.dp)
                                    .offset(y = heroOffset.dp)
                            )
                        }
                    }

                    Spacer(Modifier.height(20.dp))

                    // ── Quick stats row ───────────────────────────────────
                    val soilHasResult = recommendationResult.response.completed
//                    Row(
//                        modifier = Modifier
//                            .fillMaxWidth()
//                            .padding(horizontal = 16.dp),
//                        horizontalArrangement = Arrangement.spacedBy(10.dp)
//                    ) {
//                        StatCard(
//                            modifier = Modifier.weight(1f),
//                            icon = Icons.Default.Grass,
//                            label = "Panduan",
//                            value = "3 Topik",
//                            color = HomeGreenMid,
//                            bgColor = HomeGreenLight,
//                            onClick = { nav?.navigate(Screen.help) }
//                        )
//                        StatCard(
//                            modifier = Modifier.weight(1f),
//                            icon = Icons.Default.Search,
//                            label = "Database",
//                            value = "Tanaman",
//                            color = HomeAmber,
//                            bgColor = HomeAmberLight,
//                            onClick = { nav?.navigate(Screen.search) }
//                        )
//                        StatCard(
//                            modifier = Modifier.weight(1f),
//                            icon = Icons.Default.Park,
//                            label = "Hasil Scan",
//                            value = if (soilHasResult) "Tersedia" else "Belum ada",
//                            color = Color(0xFF1565C0),
//                            bgColor = Color(0xFFE3F2FD),
//                            onClick = { if (soilHasResult) nav?.navigate(Screen.soilResult) }
//                        )
//                    }

                    Spacer(Modifier.height(20.dp))

                    // ── Feature cards ─────────────────────────────────────
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            "Fitur Utama",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = HomeTextMuted,
                            modifier = Modifier.padding(start = 2.dp)
                        )
                        
                        // scan tanah
                        FeatureCard(
                            icon = Icons.Default.Search,
                            title = "Analisa dan Rekomendasi Tanaman",
                            subtitle = "Scan tanahmu dan cari tanaman yang cocok di kebunmu",
                            iconBg = HomeAmberLight,
                            iconColor = HomeAmber,
                            onClick = { scanTanahButton(context) }
                        )

                        // Guide card
                        FeatureCard(
                            icon = Icons.Default.MenuBook,
                            title = "Panduan Bertani",
                            subtitle = "Pelajari dasar-dasar tanah, tanaman, dan pemupukan",
                            iconBg = HomeGreenLight,
                            iconColor = HomeGreenMid,
                            onClick = { nav?.navigate(Screen.help) }
                        )

                        // Search database card
                        FeatureCard(
                            icon = Icons.Default.Storage,
                            title = "Cek Database Tanaman",
                            subtitle = "Cari dan temukan info lengkap ribuan jenis tanaman",
                            iconBg = HomeAmberLight,
                            iconColor = HomeAmber,
                            onClick = { nav?.navigate(Screen.search) }
                        )

                        // Soil result card (if available)
                        if (false && soilHasResult) {
                            FeatureCard(
                                icon = Icons.Default.Park,
                                title = "Hasil Rekomendasi",
                                subtitle = "Lihat tanaman yang cocok dengan tanah yang sudah di-scan",
                                iconBg = Color(0xFFE3F2FD),
                                iconColor = Color(0xFF1565C0),
                                onClick = { nav?.navigate(Screen.soilResult) }
                            )
                        }
                    }

                    // ── Hero image ────────────────────────────────────────
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 20.dp)
                            .clip(RoundedCornerShape(20.dp))
                            .background(
                                Brush.linearGradient(
                                    colors = listOf(HomeGreenDark, Color(0xFF299164), HomeGreenMid)
                                ))
                    ) {
                        Image(
                            painter = painterResource(R.drawable.background_opsi_mainactivity_rescaled),
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .fillMaxWidth()
                                .aspectRatio(16 / 9f)
                        )
                        // Gradient scrim at bottom
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .align(Alignment.BottomCenter)
                                .height(80.dp)
                                .background(
                                    Brush.verticalGradient(
                                        listOf(
                                            Color.Transparent,
                                            HomeGreenDark.copy(alpha = 0.6f)
                                        )
                                    )
                                )
                        )
                        Text(
                            "Mulai analisis tanah sekarang →",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.White,
                            modifier = Modifier
                                .align(Alignment.BottomStart)
                                .padding(16.dp)
                        )
                    }

                    // Bottom padding for FAB
                    Spacer(Modifier.height(100.dp))
                }

                // ── Scan FAB ──────────────────────────────────────────────
                Button(
                    onClick = { scanTanahButton(context) },
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = (padding.calculateBottomPadding().value + 16).dp)
                        .fillMaxWidth()
                        .padding(horizontal = 32.dp)
                        .height(56.dp)
                        .shadow(12.dp, RoundedCornerShape(18.dp))
                        .enableCoachMark(
                            key = MarkKey.scantanah,
                            toolTipPlacement = ToolTipPlacement.Top,
                            highlightedViewConfig = highlightConfig,
                        ) { MarkKey.scantanah.tooltip(ToolTipPlacement.Top) },
                    shape = RoundedCornerShape(18.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = HomeAmber),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 8.dp)
                ) {
                    Icon(
                        Icons.Default.CameraAlt,
                        null,
                        tint = Color.White,
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(Modifier.width(10.dp))
                    Text(
                        "Scan Tanahmu!",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White
                    )
                }
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
//  Helper composables
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun StatCard(
    modifier: Modifier = Modifier,
    icon: ImageVector,
    label: String,
    value: String,
    color: Color,
    bgColor: Color,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier.clickable(onClick = onClick),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = HomeCardBg),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(bgColor),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, null, tint = color, modifier = Modifier.size(20.dp))
            }
            Text(
                value,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = HomeTextPrimary,
                textAlign = TextAlign.Center
            )
            Text(label, fontSize = 11.sp, color = HomeTextMuted, textAlign = TextAlign.Center)
        }
    }
}

@Composable
private fun FeatureCard(
    icon: ImageVector,
    title: String,
    subtitle: String,
    iconBg: Color,
    iconColor: Color,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = HomeCardBg),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(iconBg),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, null, tint = iconColor, modifier = Modifier.size(24.dp))
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(title, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = HomeTextPrimary)
                Spacer(Modifier.height(2.dp))
                Text(subtitle, fontSize = 12.sp, color = HomeTextMuted, lineHeight = 18.sp)
            }
            Icon(
                Icons.Default.KeyboardArrowRight,
                null,
                tint = HomeTextMuted,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

private val LSGreenDark = Color(0xFF1B4332)
private val LSGreenMid = Color(0xFF52B788)
private val LSGreenLight = Color(0xFFD8F3DC)
private val LSCream = Color(0xFFF9F6F0)

@Composable
@Preview
fun LoadingScreen() {
    // ── Animation states ───────────────────────────────────────────────────
    val infiniteTransition = rememberInfiniteTransition(label = "splash")

    // Logo: gentle pulse scale
    val logoScale by infiniteTransition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(1400, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "logoScale"
    )

    // Dot loading indicator
    val dotPhase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 3f,
        animationSpec = infiniteRepeatable(
            animation = tween(900, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "dotPhase"
    )

    // Entrance fade-in on first composition
    var visible by remember { mutableStateOf(false) }
    val contentAlpha by animateFloatAsState(
        targetValue = if (visible) 1f else 0f,
        animationSpec = tween(700, easing = FastOutSlowInEasing),
        label = "contentAlpha"
    )
    val contentSlide by animateFloatAsState(
        targetValue = if (visible) 0f else 40f,
        animationSpec = tween(700, easing = FastOutSlowInEasing),
        label = "contentSlide"
    )
    LaunchedEffect(Unit) { visible = true }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    0f to Color(0xFFE8F5E9),
                    0.5f to LSCream,
                    1f to Color(0xFFF0EDE6)
                )
            )
    ) {
        // Decorative blurred circle top-right
        Box(
            modifier = Modifier
                .size(220.dp)
                .offset(x = 100.dp, y = (-60).dp)
                .clip(CircleShape)
                .background(LSGreenLight.copy(alpha = 0.5f))
                .align(Alignment.TopEnd)
        )
        // Decorative blurred circle bottom-left
        Box(
            modifier = Modifier
                .size(160.dp)
                .offset(x = (-50).dp, y = 50.dp)
                .clip(CircleShape)
                .background(LSGreenMid.copy(alpha = 0.12f))
                .align(Alignment.BottomStart)
        )

        // ── Center content ─────────────────────────────────────────────
        Column(
            modifier = Modifier
                .align(Alignment.Center)
                .alpha(contentAlpha)
                .offset(y = contentSlide.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Logo in circle container
            Box(
                modifier = Modifier
                    .size(150.dp)
                    .scale(logoScale)
                    .clip(RoundedCornerShape(36.dp))
                    .background(
                        Brush.verticalGradient(
                            listOf(LSGreenLight, Color.White)
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(R.drawable.icon_app),
                    contentDescription = "App icon",
                    modifier = Modifier.size(100.dp)
                )
            }

            Spacer(Modifier.height(28.dp))

            // App name
            Text(
                text = "Tani Pertiwi",
                fontWeight = FontWeight.ExtraBold,
                fontSize = 36.sp,
                color = LSGreenDark,
                textAlign = TextAlign.Center,
                letterSpacing = 0.5.sp
            )

            Spacer(Modifier.height(6.dp))

            // Tagline
            Text(
                text = "Agrikultur Cerdas Berbasis Teknologi",
                fontSize = 13.sp,
                color = LSGreenMid,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(36.dp))

            // Animated dot indicator
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                repeat(3) { index ->
                    val dotAlpha = when {
                        dotPhase.toInt() == index -> 1f
                        else -> 0.25f
                    }
                    val dotScale = if (dotPhase.toInt() == index) 1.3f else 1f
                    Box(
                        modifier = Modifier
                            .size(9.dp)
                            .scale(dotScale)
                            .clip(CircleShape)
                            .alpha(dotAlpha)
                            .background(LSGreenMid)
                    )
                }
            }
        }

        // ── Bottom logo ────────────────────────────────────────────────
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .alpha(contentAlpha)
                .padding(bottom = 48.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
//            Text(
//                "Powered by",
//                fontSize = 11.sp,
//                color    = Color(0xFFADB5AD),
//                fontWeight = FontWeight.Normal
//            )
//            Image(
//                painter            = painterResource(R.drawable.smanega),
//                contentDescription = "Fert Innovation logo",
//                modifier           = Modifier.width(140.dp)
//            )
        }
    }
}