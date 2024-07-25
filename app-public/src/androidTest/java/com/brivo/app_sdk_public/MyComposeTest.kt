package com.brivo.app_sdk_public

import androidx.compose.foundation.layout.Box
import androidx.compose.material3.Text
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
class MyComposeUITest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun myUIComponentTest0() {

        // Render the Compose UI
        composeTestRule.setContent {
            Box {
                Text(text = "Hello, World!")
            }
        }

        // Verify the UI is displayed
        composeTestRule.onNodeWithText("Hello, World!").assertIsDisplayed()
    }
}