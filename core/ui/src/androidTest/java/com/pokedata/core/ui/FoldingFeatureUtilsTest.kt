package com.pokedata.core.ui

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class FoldingFeatureUtilsTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun rememberHingePadding_returnsZeroOnNonFoldableDevice() {
        composeTestRule.setContent {
            val padding = rememberHingePadding()
            assertTrue(
                "Hinge padding should be zero on non-foldable device",
                padding.calculateTopPadding() == 0f &&
                    padding.calculateBottomPadding() == 0f &&
                    padding.calculateLeftPadding(androidx.compose.ui.unit.LayoutDirection.Ltr) == 0f &&
                    padding.calculateRightPadding(androidx.compose.ui.unit.LayoutDirection.Ltr) == 0f
            )
        }
    }
}
