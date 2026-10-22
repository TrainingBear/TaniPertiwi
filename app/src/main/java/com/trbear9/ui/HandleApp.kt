package com.trbear9.ui

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.widget.Toast
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideIn
import androidx.compose.animation.slideOut
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.IntOffset
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.trbear9.plants.parameters.SoilParameters
import com.trbear9.ui.model.plantRepository
import com.trbear9.ui.util.Screen
import com.trbear9.ui.util.Util
import com.trbear9.ui.view.AboutUs
import com.trbear9.ui.view.CameraActivity
import com.trbear9.ui.view.Guide
import com.trbear9.ui.view.Home
import com.trbear9.ui.view.InputSoil
import com.trbear9.ui.view.LoadingScreen
import com.trbear9.ui.view.MainActivity
import com.trbear9.ui.view.PointDetail
import com.trbear9.ui.view.SearchLayout
import com.trbear9.ui.view.SoilResultScreen
import com.trbear9.ui.view.SoilStatsActivity
import com.trbear9.ui.viewmodel.Parameter
import com.trbear9.ui.viewmodel.depth
import com.trbear9.ui.viewmodel.getLocation
import com.trbear9.ui.viewmodel.gps
import com.trbear9.ui.viewmodel.pH
import com.trbear9.ui.viewmodel.perm
import com.trbear9.ui.viewmodel.recommendationResult
import com.trbear9.ui.viewmodel.resetParameter
import com.trbear9.ui.viewmodel.soil

// ─────────────────────────────────────────────────────────────────────────────
//  Navigation Local State
// ─────────────────────────────────────────────────────────────────────────────

/** CompositionLocal providing the active [NavController] across the UI hierarchy */
val LocalNavController = staticCompositionLocalOf<NavController?> { null }

/**
 * Global accessor for legacy non-composable access.
 * Prefer using [LocalNavController] or passing [NavController] directly in Composables.
 */
object LocalNav {
    var current: NavController? = null
}

// ─────────────────────────────────────────────────────────────────────────────
//  Transition Specifications
// ─────────────────────────────────────────────────────────────────────────────

object NavTransitions {
    private const val DURATION_MS = 500
    private const val FADE_DURATION = 250
    private val easing = FastOutSlowInEasing

    /** Slide in from right (forward navigation) */
    val enterSlideFromRight: AnimatedContentTransitionScope<*>.() -> EnterTransition = {
        slideIn(
            animationSpec = tween(DURATION_MS, easing = easing),
            initialOffset = { IntOffset(it.width, 0) }
        ) + fadeIn(tween(FADE_DURATION))
    }

    /** Slide out to left (forward navigation – old screen leaving) */
    val exitSlideToLeft: AnimatedContentTransitionScope<*>.() -> ExitTransition = {
        slideOut(
            animationSpec = tween(DURATION_MS, easing = easing),
            targetOffset = { IntOffset(-it.width / 4, 0) }
        ) + fadeOut(tween(FADE_DURATION))
    }

    /** Slide in from left (back navigation – screen returning) */
    val popEnterSlideFromLeft: AnimatedContentTransitionScope<*>.() -> EnterTransition = {
        slideIn(
            animationSpec = tween(DURATION_MS, easing = easing),
            initialOffset = { IntOffset(-it.width / 4, 0) }
        ) + fadeIn(tween(FADE_DURATION))
    }

    /** Slide out to right (back navigation – screen being popped) */
    val popExitSlideToRight: AnimatedContentTransitionScope<*>.() -> ExitTransition = {
        slideOut(
            animationSpec = tween(DURATION_MS, easing = easing),
            targetOffset = { IntOffset(it.width, 0) }
        ) + fadeOut(tween(FADE_DURATION))
    }

    /** Fade-only transition – used for modal/overlay screens (loading, home, stats) */
    val enterFade: AnimatedContentTransitionScope<*>.() -> EnterTransition = {
        fadeIn(tween(FADE_DURATION + 80))
    }
    val exitFade: AnimatedContentTransitionScope<*>.() -> ExitTransition = {
        fadeOut(tween(FADE_DURATION))
    }

    /** Slide up from bottom – used for detail/overlay screens */
    val enterSlideFromBottom: AnimatedContentTransitionScope<*>.() -> EnterTransition = {
        slideIn(
            animationSpec = tween(DURATION_MS, easing = easing),
            initialOffset = { IntOffset(0, it.height / 5) }
        ) + fadeIn(tween(FADE_DURATION))
    }
    val exitSlideToBottom: AnimatedContentTransitionScope<*>.() -> ExitTransition = {
        slideOut(
            animationSpec = tween(DURATION_MS, easing = easing),
            targetOffset = { IntOffset(0, it.height / 5) }
        ) + fadeOut(tween(FADE_DURATION))
    }
}

// ─────────────────────────────────────────────────────────────────────────────
//  Main Application Entry & Navigation Host
// ─────────────────────────────────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun App(app: MainActivity, isReady: Boolean) {
    val nav = rememberNavController()

    // Expose controller to CompositionLocal and global legacy reference
    DisposableEffect(nav) {
        LocalNav.current = nav
        onDispose { LocalNav.current = null }
    }

    // Handle initial app readiness navigation
    LaunchedEffect(isReady) {
        if (isReady) {
            nav.navigate(Screen.home) {
                popUpTo(Screen.firstLoading) { inclusive = true }
                launchSingleTop = true
            }
        }
    }

    CompositionLocalProvider(LocalNavController provides nav) {
        NavHost(
            navController = nav,
            startDestination = Screen.firstLoading,
            // Default transitions for standard forward/back slide screens
            enterTransition = NavTransitions.enterSlideFromRight,
            exitTransition = NavTransitions.exitSlideToLeft,
            popEnterTransition = NavTransitions.popEnterSlideFromLeft,
            popExitTransition = NavTransitions.popExitSlideToRight,
        ) {
            appNavGraph(app = app, nav = nav)
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
//  Navigation Graph Builder
// ─────────────────────────────────────────────────────────────────────────────

private fun NavGraphBuilder.appNavGraph(
    app: MainActivity,
    nav: NavController
) {
    // ── Loading ───────────────────────────────────────────────────────
    composable(
        route = Screen.firstLoading,
        enterTransition = NavTransitions.enterFade,
        exitTransition = NavTransitions.exitFade,
        popEnterTransition = NavTransitions.enterFade,
        popExitTransition = NavTransitions.exitFade,
    ) {
        LoadingScreen()
    }

    // ── Home ──────────────────────────────────────────────────────────
    composable(
        route = Screen.home,
        enterTransition = NavTransitions.enterFade,
        exitTransition = NavTransitions.exitFade,
        popEnterTransition = NavTransitions.enterFade,
        popExitTransition = NavTransitions.exitFade,
    ) {
        Home(nav)
    }

    // ── Camera ────────────────────────────────────────────────────────
    composable(route = Screen.camera) {
        CameraActivity(nav, onClick = { nav.navigate(Screen.inputSoil) })
    }

    // ── Input Soil ────────────────────────────────────────────────────
    composable(route = Screen.inputSoil) {
        val context = LocalContext.current
//        resetParameter()
        InputSoil(nav, nextCallback = {
            processSoilAnalysisAndNavigate(context = context, app = app, nav = nav)
        })
    }

    // ── Soil Result ───────────────────────────────────────────────────
    composable(
        route = Screen.soilResult,
        enterTransition = NavTransitions.enterSlideFromBottom,
        exitTransition = NavTransitions.exitFade,
        popEnterTransition = NavTransitions.enterFade,
        popExitTransition = NavTransitions.exitSlideToBottom,
    ) {
        SoilResultScreen()
    }

    // ── Search ────────────────────────────────────────────────────────
    composable(route = Screen.search) {
        SearchLayout(nav = nav)
    }

    // ── Soil Stats (External Activity Launch) ──────────────────────────
    composable(
        route = Screen.soilStats,
        enterTransition = NavTransitions.enterFade,
        exitTransition = NavTransitions.exitFade,
        popEnterTransition = NavTransitions.enterFade,
        popExitTransition = NavTransitions.exitFade,
    ) {
        val context = LocalContext.current
        LaunchedEffect(Unit) {
            context.startActivity(Intent(context, SoilStatsActivity::class.java))
            nav.popBackStack()
        }
    }

    // ── Guide (Help) ──────────────────────────────────────────────────
    composable(route = Screen.help) {
        Guide(nav)
    }

    // ── Guide Point Detail ────────────────────────────────────────────
    composable(
        route = Screen.guidePointDetail,
        enterTransition = NavTransitions.enterSlideFromBottom,
        exitTransition = NavTransitions.exitFade,
        popEnterTransition = NavTransitions.enterFade,
        popExitTransition = NavTransitions.exitSlideToBottom,
    ) {
        val guidePointer = Guide.guidePointer
        if (guidePointer != null) {
            PointDetail(
                nav = nav,
                title = guidePointer.second.first,
                subtitle = guidePointer.second.second,
                id = guidePointer.first,
                details = guidePointer.third,
                credits = guidePointer.second.third
            )
        }
    }

    // ── About Us ──────────────────────────────────────────────────────
    composable(route = Screen.about) {
        AboutUs(nav)
    }
}

// ─────────────────────────────────────────────────────────────────────────────
//  Helpers & Actions
// ─────────────────────────────────────────────────────────────────────────────

private fun processSoilAnalysisAndNavigate(
    context: Context,
    app: MainActivity,
    nav: NavController
) {
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
        return
    }

    getLocation(context)
    gps = true

    soil = SoilParameters()
    if (pH != null) soil.pH = pH!!.coerceAtMost(14f)
    soil.numericDepth = depth

    Parameter.start2info2Process()
    recommendationResult.collected = false
    recommendationResult.flow = plantRepository.process()
    Util.debug("Job has been finished!")

    if (!isNetworkAvailable(app)) {
        Toast.makeText(context, "Tidak ada koneksi internet!", Toast.LENGTH_SHORT).show()
    }
    nav.navigate(Screen.soilResult)
}

fun isNetworkAvailable(context: Context): Boolean {
    val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val network = cm.activeNetwork ?: return false
    val capabilities = cm.getNetworkCapabilities(network) ?: return false
    return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
}
