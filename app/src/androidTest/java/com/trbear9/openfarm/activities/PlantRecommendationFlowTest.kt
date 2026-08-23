package com.trbear9.openfarm.activities

import androidx.compose.ui.test.junit4.v2.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.core.graphics.createBitmap
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry
import com.trbear9.ui.view.MainActivity
import junit.framework.TestCase.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test

import org.junit.runner.RunWith;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4::class)
class PlantRecommendationFlowTest {
    fun useContext() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext;
        assertEquals("com.tbear9.openfarm", context.packageName);
    }
    @get:Rule
    val compose = createAndroidComposeRule<MainActivity>()

    @Before
    fun intentInit(){
    }

    @Test
    fun flow(){
        compose.onNodeWithText("Scan Tanahmu")
    }
}