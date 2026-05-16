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
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
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
import androidx.compose.material.icons.filled.QuestionMark
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
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import androidx.navigation.NavController
import com.pseudoankit.coachmark.LocalCoachMarkScope
import com.pseudoankit.coachmark.UnifyCoachmark
import com.pseudoankit.coachmark.model.HighlightedViewConfig
import com.pseudoankit.coachmark.model.ToolTipPlacement
import com.pseudoankit.coachmark.scope.enableCoachMark
import com.pseudoankit.coachmark.util.CoachMarkKey
import com.trbear9.plants.Data
import com.trbear9.plants.TFService
import com.trbear9.ui.util.DataStore
import com.trbear9.ui.util.Screen
import com.trbear9.ui.App
import com.trbear9.ui.R
import com.trbear9.ui.util.MarkKey
import com.trbear9.ui.util.getContext
import com.trbear9.ui.util.highlightConfig
import com.trbear9.ui.viewmodel.ScanTanahButton
import com.trbear9.ui.viewmodel.perm
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.*
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush

//var firstTime = false
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        perm = registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) {}
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

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
fun Home(nav: NavController? = null) {
    val context = getContext()
    var scroll = rememberScrollState()
    var coffe by remember { mutableStateOf(false) }

    UnifyCoachmark() {
        LaunchedEffect(Unit) {
            if (DataStore.firstTime) {
                coffe = true
            }
        }
        Scaffold(
            bottomBar = {
                var selected by remember { mutableIntStateOf(0) }
                NavigationBar {
                    NavigationBarItem(
                        selected = selected == 0,
                        onClick = {
                            selected = 0
                            nav?.navigate(Screen.home)
                        },
                        icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
                        label = { Text("Home") }
                    )
                    NavigationBarItem(
                        selected = selected == 1,
                        onClick = {
                            selected = 1
                            nav?.navigate(Screen.soilResult)
                        },
                        icon = { Icon(Icons.Default.Park, contentDescription = "Hasil") },
                        label = { Text("Tanaman") }
                    )
                }
            },
            topBar = {
                TopAppBar(
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF6FAD4F)),
                    modifier = Modifier.wrapContentHeight(),
                    title = {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                Modifier
                                    .fillMaxHeight()
                                    .weight(7f)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Image(
                                        painter = painterResource(id = R.drawable.icon_app),
                                        contentDescription = "Help",
                                        modifier = Modifier
                                            .size(40.dp)
                                            .padding(end = 5.dp)
                                            .clickable { nav?.navigate(Screen.about) }
                                    )
                                    Column(
                                        horizontalAlignment = Alignment.Start,
                                        verticalArrangement = Arrangement.Center,
                                        modifier = Modifier.fillMaxSize()
                                    ) {
                                        Text(
                                            text = buildAnnotatedString {
                                                withStyle(
                                                    style = SpanStyle(
                                                        color = Color.White,
                                                        fontWeight = FontWeight.Bold,
                                                        fontSize = 24.sp,
                                                    )
                                                ) {
                                                    append("TaniPertiwi")
                                                }
                                                withStyle(
                                                    style = SpanStyle(
                                                        color = Color.DarkGray,
                                                        fontWeight = FontWeight.Normal,
                                                        fontSize = 16.sp,
                                                    )
                                                ) {
                                                    append("\nby Jaksara")
                                                }
                                            },
                                            lineHeight = 18.sp,
                                            modifier = Modifier.clickable { nav?.navigate(Screen.about) }
                                        )
                                    }
                                }
                            }

                            Box(
                                Modifier
                                    .fillMaxHeight()
                                    .weight(3f)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.MonetizationOn,
                                        tint = Color.Black,
                                        contentDescription = "Buy us a coffe!",
                                        modifier = Modifier
                                            .padding(top = 20.dp, bottom = 20.dp)
                                            .size(30.dp)
                                            .clickable { coffe = !coffe }
                                    )

                                    Icon(
                                        imageVector = Icons.Default.QuestionMark,
                                        tint = Color.Black,
                                        contentDescription = "Help",
                                        modifier = Modifier
                                            .padding(20.dp)
                                            .size(30.dp)
                                            .clickable {
                                                nav?.navigate(Screen.help)
                                            }
                                            .enableCoachMark(
                                                key = MarkKey.help,
                                                toolTipPlacement = ToolTipPlacement.Start,
                                                highlightedViewConfig = highlightConfig,
                                            ) { MarkKey.help.tooltip(ToolTipPlacement.Start) }
                                    )
                                }
                            }
                        }
                    }
                )
            }
        ) {
            if (coffe) {
                BasicAlertDialog(
                    onDismissRequest = { coffe = false },
                    modifier = Modifier
                        .padding(it)
                        .wrapContentSize(),
                ) {
                    Column(
                        modifier = Modifier
                            .wrapContentSize()
                            .clip(RoundedCornerShape(24.dp))
                            .background(Color(0xFFFFFFFF))
                            .padding(horizontal = 24.dp, vertical = 28.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Icon
                        Box(
                            modifier = Modifier
                                .size(56.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFFFF3E0)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Favorite,
                                contentDescription = "Support",
                                tint = Color(0xFFFF9100),
                                modifier = Modifier.size(28.dp)
                            )
                        }

                        Spacer(Modifier.height(14.dp))

                        // Headline
                        Text(
                            text = "Dukung pengembangan aplikasi ini",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.SemiBold,
                            textAlign = TextAlign.Center,
                            lineHeight = 24.sp
                        )

                        Spacer(Modifier.height(10.dp))

                        // Description
                        Text(
                            text = "Jika kamu merasa project ini membantu, kamu bisa mendukung kami melalui donasi. Dukunganmu membantu biaya server dan pengembangan fitur baru agar project ini tetap berjalan.",
                            fontSize = 13.sp,
                            textAlign = TextAlign.Center,
                            color = Color.Gray,
                            lineHeight = 20.sp
                        )

                        Spacer(Modifier.height(16.dp))

                        // Social proof hint
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .background(Color(0xFFF5F5F5))
                                .padding(horizontal = 14.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = Color(0xFF1D9E75),
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = "Sekali donasi sudah sangat berarti untuk keberlanjutan aplikasi ini",
                                fontSize = 12.sp,
                                color = Color.DarkGray,
                                lineHeight = 18.sp
                            )
                        }

                        Spacer(Modifier.height(18.dp))

                        // Primary CTA
                        Button(
                            onClick = {
                                val intent = Intent(
                                    Intent.ACTION_VIEW,
                                    "https://trakteer.id/kujatic/tip".toUri()
                                )
                                context.startActivity(intent)
                            },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFFFF9100)
                            ),
                            shape = RoundedCornerShape(14.dp),
                            contentPadding = PaddingValues(vertical = 13.dp)
                        ) {
                            Text(
                                text = "Donasi sekarang via Saweria",
                                fontSize = 15.sp,
                                color = Color.White,
                                fontWeight = FontWeight.SemiBold
                            )
                        }

                        Spacer(Modifier.height(4.dp))

                        // Dismiss
                        TextButton(
                            onClick = {
                                coffe = false
                                if (DataStore.firstTime) {
                                    show(MarkKey.scantanah, MarkKey.help)
                                }
                            }
                        ) {
                            Text(
                                text = "Mungkin nanti",
                                color = Color.LightGray,
                                fontSize = 13.sp
                            )
                        }
                    }
                }
            }
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(it)
                    .background(Color(0xFF6FAD4F))
                    .verticalScroll(scroll),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    Modifier
                        .fillMaxWidth()
                        .weight(1f)
                ) {
                    BoxWithConstraints(Modifier.fillMaxSize()) {
                        val dynamicFontSize = (maxHeight.value / 4.4).sp
                        val icon = (maxHeight / 3)
                        Button(
                            onClick = { nav?.navigate(Screen.search) },
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(10.dp)
                                .clip(RoundedCornerShape(10.dp)),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.White
                            ),
                        ) {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = "Search",
                                modifier = Modifier.size(icon),
                                tint = Color.Black
                            )
                            Text(
                                text = "Cek database tanaman",
                                textAlign = TextAlign.Center,
                                fontWeight = FontWeight.Medium,
                                color = Color.Black,
                                fontSize = dynamicFontSize,
                            )

                        }
                    }
                }
                Box(
                    Modifier
                        .fillMaxWidth()
                        .weight(2f)
                ) {
                    BoxWithConstraints(Modifier.fillMaxSize()) {
                        val dynamicFontSize = (maxHeight.value / 4).sp
                        val dy2 = (maxHeight.value / 8).sp
                        Text(
                            text = buildAnnotatedString {
                                append("Tani Pertiwi")
                                withStyle(
                                    style = SpanStyle(
                                        fontWeight = FontWeight.Bold,
                                        fontSize = dy2,
                                        fontFamily = FontFamily.SansSerif,
                                        color = Color(0xFF20481D)
                                    )
                                ) {
                                    append("\n")
                                    append("Panduan cerdas untuk Agrikultur berkelanjutan berbasis tehnologi.")
                                }
                            },
                            color = Color(0xFF1B1B1B),
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = dynamicFontSize,
                            fontFamily = FontFamily.SansSerif,
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                .align(Alignment.Center)
                                .fillMaxSize()
                                .padding(top = 20.dp)
                        )
                    }
                }
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(5f)
                        .padding(bottom = 62.dp)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.background_opsi_mainactivity_rescaled),
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(16 / 13.5f)
                            .align(Alignment.Center),
                    )
                }
            }
            Box(
                Modifier
                    .fillMaxSize()
                    .padding(it)
                    .background(Brush.verticalGradient(
                        0.7f to Color.Transparent,
                        1f to Color(0xCC143418)
                    ))
            ) {
                BoxWithConstraints(
                    Modifier
                        .fillMaxWidth()
                        .fillMaxWidth()
                        .aspectRatio(16 / 3f)
                        .align(Alignment.BottomCenter)
                        .padding(start = 50.dp, end = 50.dp, bottom = 20.dp)
                ) {
                    val dynamicFontSize = (maxHeight.value / 2).sp
                    Button(
                        modifier = Modifier
                            .fillMaxSize()
                            .shadow(10.dp, RoundedCornerShape(15.dp))
                            .enableCoachMark(
                                key = MarkKey.scantanah,
                                toolTipPlacement = ToolTipPlacement.Top,
                                highlightedViewConfig = highlightConfig,
                            ) {
                                MarkKey.scantanah.tooltip(ToolTipPlacement.Top)
                            },
                        onClick = {
                            ScanTanahButton(context)
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFF5A623)
                        ),
//                            border = BorderStroke(2.dp, Color.White),
                    ) {
                        Text(
                            text = "Scan tanahmu!",
                            fontSize = dynamicFontSize,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White,
                            modifier = Modifier
                                .fillMaxSize()
                                .align(Alignment.CenterVertically),
                            textAlign = TextAlign.Center,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ColumnScope.CoachMarkTargetText(
    text: String,
    alignment: Alignment.Horizontal,
    key: MarkKey,
    placement: ToolTipPlacement,
    tooltip: @Composable (() -> Unit)? = null
) {
    val coachMarkScope = LocalCoachMarkScope.current

    Text(
        text = text,
        modifier = Modifier
            .align(alignment)
            .enableCoachMark(
                key = key,
                toolTipPlacement = placement,
                highlightedViewConfig = HighlightedViewConfig(
                    shape = HighlightedViewConfig.Shape.Rect(12.dp),
                    padding = PaddingValues(8.dp)
                ),
                coachMarkScope = coachMarkScope,
                tooltip = tooltip
            )
            .padding(16.dp),
        color = Color.Black
    )
}

@Composable
fun Tooltip(key: CoachMarkKey) {
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
