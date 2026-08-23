package com.trbear9.ui

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.IntOffset
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.trbear9.ui.view.AboutUs
import com.trbear9.ui.view.Guide
import com.trbear9.ui.view.PointDetail
import com.trbear9.ui.view.SearchLayout
import com.trbear9.ui.view.SoilResultScreen
import com.trbear9.ui.view.SoilStatsActivity
import com.trbear9.ui.util.Screen
import com.trbear9.plants.parameters.SoilParameters
import com.trbear9.plants.parameters.UserVariable
import com.trbear9.ui.model.PlantRepository
import com.trbear9.ui.model.plantRepository
import com.trbear9.ui.view.CameraActivity
import com.trbear9.ui.view.Home
import com.trbear9.ui.view.InputSoil
import com.trbear9.ui.view.LoadingScreen
import com.trbear9.ui.view.MainActivity
import com.trbear9.ui.util.Util
import com.trbear9.ui.viewmodel.PlantResult
import com.trbear9.ui.viewmodel.depth
import com.trbear9.ui.viewmodel.geo
import com.trbear9.ui.viewmodel.getLocation
import com.trbear9.ui.viewmodel.gps
import com.trbear9.ui.viewmodel.image
import com.trbear9.ui.viewmodel.pH
import com.trbear9.ui.viewmodel.perm
import com.trbear9.ui.viewmodel.recommendationResult
import com.trbear9.ui.viewmodel.soil

object LocalNav {
    var current: NavController? = null
}

// ─────────────────────────────────────────────────────────────────────────────
//  Shared transition constants
// ─────────────────────────────────────────────────────────────────────────────
private const val DURATION_MS = 500
private const val FADE_DURATION = 250
private val easing = FastOutSlowInEasing

/** Slide in from right (forward navigation) */
private val enterSlideFromRight: AnimatedContentTransitionScope<*>.() -> EnterTransition = {
    slideIn(
        animationSpec = tween(DURATION_MS, easing = easing),
        initialOffset = { IntOffset(it.width, 0) }
    ) + fadeIn(tween(FADE_DURATION))
}

/** Slide out to left (forward navigation – old screen leaving) */
private val exitSlideToLeft: AnimatedContentTransitionScope<*>.() -> ExitTransition = {
    slideOut(
        animationSpec = tween(DURATION_MS, easing = easing),
        targetOffset = { IntOffset(-it.width / 4, 0) }
    ) + fadeOut(tween(FADE_DURATION))
}

/** Slide in from left (back navigation – screen returning) */
private val popEnterSlideFromLeft: AnimatedContentTransitionScope<*>.() -> EnterTransition = {
    slideIn(
        animationSpec = tween(DURATION_MS, easing = easing),
        initialOffset = { IntOffset(-it.width / 4, 0) }
    ) + fadeIn(tween(FADE_DURATION))
}

/** Slide out to right (back navigation – screen being popped) */
private val popExitSlideToRight: AnimatedContentTransitionScope<*>.() -> ExitTransition = {
    slideOut(
        animationSpec = tween(DURATION_MS, easing = easing),
        targetOffset = { IntOffset(it.width, 0) }
    ) + fadeOut(tween(FADE_DURATION))
}

/** Fade-only transition – used for screens that feel more "modal" (stats overlay, loading) */
private val enterFade: AnimatedContentTransitionScope<*>.() -> EnterTransition = {
    fadeIn(tween(FADE_DURATION + 80))
}
private val exitFade: AnimatedContentTransitionScope<*>.() -> ExitTransition = {
    fadeOut(tween(FADE_DURATION))
}

/** Slide up from bottom – used for detail/overlay screens */
private val enterSlideFromBottom: AnimatedContentTransitionScope<*>.() -> EnterTransition = {
    slideIn(
        animationSpec = tween(DURATION_MS, easing = easing),
        initialOffset = { IntOffset(0, it.height / 5) }
    ) + fadeIn(tween(FADE_DURATION))
}
private val exitSlideToBottom: AnimatedContentTransitionScope<*>.() -> ExitTransition = {
    slideOut(
        animationSpec = tween(DURATION_MS, easing = easing),
        targetOffset = { IntOffset(0, it.height / 5) }
    ) + fadeOut(tween(FADE_DURATION))
}

// ─────────────────────────────────────────────────────────────────────────────
//  App
// ─────────────────────────────────────────────────────────────────────────────
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun App(app: MainActivity, isReady: Boolean) {
    val context = LocalContext.current
    val nav = rememberNavController()

    DisposableEffect(Unit) {
        LocalNav.current = nav
        onDispose { LocalNav.current = null }
    }

    LaunchedEffect(isReady) {
        if (isReady) {
            nav.navigate(Screen.home) {
                popUpTo(Screen.firstLoading) {
                    inclusive = true
                }
                launchSingleTop = true
            }
        }
    }

    NavHost(
        navController = nav,
        startDestination = Screen.firstLoading,
        // Default transitions for the whole graph (overridden per-composable below)
        enterTransition = enterSlideFromRight,
        exitTransition = exitSlideToLeft,
        popEnterTransition = popEnterSlideFromLeft,
        popExitTransition = popExitSlideToRight,
    ) {

        // ── Loading ───────────────────────────────────────────────────────
        composable(
            route = Screen.firstLoading,
            enterTransition = { enterFade() },
            exitTransition = { exitFade() },
            popEnterTransition = { enterFade() },
            popExitTransition = { exitFade() },
        ) {
            LoadingScreen()
        }

        // ── Home ──────────────────────────────────────────────────────────
        composable(
            route = Screen.home,
            // Home uses fade so returning to it doesn't feel jarring
            enterTransition = { enterFade() },
            exitTransition = { exitFade() },
            popEnterTransition = { enterFade() },
            popExitTransition = { exitFade() },
        ) {
            Home(nav)
        }

        // ── Camera ────────────────────────────────────────────────────────
        composable(
            route = Screen.camera,
            enterTransition = enterSlideFromRight,
            exitTransition = exitSlideToLeft,
            popEnterTransition = popEnterSlideFromLeft,
            popExitTransition = popExitSlideToRight,
        ) {
            CameraActivity(nav, onClick = {
                nav.navigate(Screen.inputSoil)
            })
        }

        // ── Input Soil ────────────────────────────────────────────────────
        composable(
            route = Screen.inputSoil,
            enterTransition = enterSlideFromRight,
            exitTransition = exitSlideToLeft,
            popEnterTransition = popEnterSlideFromLeft,
            popExitTransition = popExitSlideToRight,
        ) {
            InputSoil(nav, nextCallback = {
                perm.launch(arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION))
                if (ContextCompat.checkSelfPermission(
                        context,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    Toast.makeText(
                        context,
                        "Tolong izinkan kamera untuk menggunakan fitur ini!",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    getLocation(context)
                    gps = true
                    soil = SoilParameters()
                    if (pH != null) soil.pH = pH!!.coerceAtMost(14f)
                    soil.numericDepth = depth
                    recommendationResult.collected = false
                    recommendationResult.flow = plantRepository.process()
                    Util.debug("Job has been finished!")

                    if (!isNetworkAvailable(app)) {
                        Toast.makeText(context, "Tidak ada koneksi internet!", Toast.LENGTH_SHORT)
                            .show()
                    }
                    nav.navigate(Screen.soilResult)
                }
            })
        }

        // ── Soil Result ───────────────────────────────────────────────────
        composable(
            route = Screen.soilResult,
            // Slide up from bottom – feels like results "appearing"
            enterTransition = enterSlideFromBottom,
            exitTransition = exitFade,
            popEnterTransition = { enterFade() },
            popExitTransition = exitSlideToBottom,
        ) {
            SoilResultScreen()
        }

        // ── Search ────────────────────────────────────────────────────────
        composable(
            route = Screen.search,
            enterTransition = enterSlideFromRight,
            exitTransition = exitSlideToLeft,
            popEnterTransition = popEnterSlideFromLeft,
            popExitTransition = popExitSlideToRight,
        ) {
            SearchLayout(nav = nav)
        }

        // ── Soil Stats (launched as Activity, keep transition minimal) ────
        composable(
            route = Screen.soilStats,
            enterTransition = { enterFade() },
            exitTransition = { exitFade() },
            popEnterTransition = { enterFade() },
            popExitTransition = { exitFade() },
        ) {
            val intent = Intent(context, SoilStatsActivity::class.java)
            context.startActivity(intent)
        }

        // ── Guide (Help) ──────────────────────────────────────────────────
        composable(
            route = Screen.help,
            enterTransition = enterSlideFromRight,
            exitTransition = exitSlideToLeft,
            popEnterTransition = popEnterSlideFromLeft,
            popExitTransition = popExitSlideToRight,
        ) {
            Guide(nav)
        }

        // ── Guide Point Detail ────────────────────────────────────────────
        composable(
            route = Screen.guidePointDetail,
            // Slides up – feels like a detail sheet opening
            enterTransition = enterSlideFromBottom,
            exitTransition = exitFade,
            popEnterTransition = { enterFade() },
            popExitTransition = exitSlideToBottom,
        ) {
            PointDetail(
                nav = nav,
                title = Guide.guidePointer!!.second.first,
                subtitle = Guide.guidePointer!!.second.second,
                id = Guide.guidePointer!!.first,
                details = Guide.guidePointer!!.third,
                credits = Guide.guidePointer!!.second.third
            )
        }

        // ── About Us ──────────────────────────────────────────────────────
        composable(
            route = Screen.about,
            enterTransition = enterSlideFromRight,
            exitTransition = exitSlideToLeft,
            popEnterTransition = popEnterSlideFromLeft,
            popExitTransition = popExitSlideToRight,
        ) {
            AboutUs(nav)
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
//  Helpers
// ─────────────────────────────────────────────────────────────────────────────
fun isNetworkAvailable(context: Context): Boolean {
    val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val network = cm.activeNetwork ?: return false
    val capabilities = cm.getNetworkCapabilities(network) ?: return false
    return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
}