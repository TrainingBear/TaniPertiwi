package com.trbear9.openfarm.activities

import android.Manifest
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.hasContentDescription
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.v2.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextReplacement
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.rule.GrantPermissionRule
import com.trbear9.ui.R
import com.trbear9.ui.util.DataStore
import com.trbear9.ui.view.MainActivity
import com.trbear9.ui.viewmodel.image
import com.trbear9.ui.viewmodel.recommendationResult
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Instrumented test for the plant recommendation flow.
 */
@RunWith(AndroidJUnit4::class)
class PlantRecommendationFlowTest {

    @get:Rule
    val compose = createAndroidComposeRule<MainActivity>()

    @get:Rule
    val permissionRule: GrantPermissionRule = GrantPermissionRule.grant(
        Manifest.permission.CAMERA,
        Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.ACCESS_FINE_LOCATION
    )

    @Before
    fun setup() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        // Initialize DataStore and set firstTime to false to prevent the donation dialog
        // from appearing and blocking the UI interaction.
        DataStore.init(context)
        DataStore.completeFirstTime()
    }

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun flow() {
        // 1. Click Scan Tanahmu
        wait("Scan Tanahmu!")
        compose.onNodeWithText("Scan Tanahmu!", ignoreCase = true).performClick()

        // Wait for the camera screen and click the camera button
        // The camera button has content description "Circle Button"
        compose.waitUntilAtLeastOneExists(hasContentDescription("Circle Button"), 15000)
        compose.onNodeWithContentDescription("Circle Button").performClick()

        // 2. Input image as BitMap inside Parameter.kt
        // We set the global image variable directly as required.
        image = BitmapFactory.decodeResource(
            InstrumentationRegistry.getInstrumentation().targetContext.resources,
            R.drawable.aluvialsoiltest
        )

        // 3. Click Next, leaving pH Tanah and depth empty
        wait("Next")

        // Ensure the fields are empty. In InputSoil, they default to "?" if null.
        // The user wants them empty.
        compose.onNodeWithText("pH tanah").performTextReplacement("")
        compose.onNodeWithText("Kedalaman tanah (cm)").performTextReplacement("30")

        compose.onNodeWithText("Next", ignoreCase = true).performClick()
        compose.waitUntil(10_000) { recommendationResult.collected }
        wait("Analisa tanahmu")
        for ((score, pack) in recommendationResult.plants) {
            for (plant in pack) {
                println("certified state of ${plant.commonName}($score): ${plant.isAvaiable}")
            }
        }

        wait("don't finish yet")
    }

    @OptIn(ExperimentalTestApi::class)
    fun wait(text: String) {
        compose.waitUntilAtLeastOneExists(
            matcher = hasText(text, ignoreCase = true),
            timeoutMillis = 600000
        )
    }
}
