package com.maeldev.conquest.components

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MyInputFieldTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun myInputField_displaysLabelAndUpdatesValue() {
        var textValue = ""
        
        composeTestRule.setContent {
            MyInputField(
                label = "Test Label",
                value = textValue,
                onValueChange = { textValue = it }
            )
        }

        // Verify label is displayed
        composeTestRule.onNodeWithText("Test Label").assertExists()
        
        // Enter text
        composeTestRule.onNodeWithText("Test Label").performTextInput("New text")
        
        // Verify state is updated
        assertEquals("New text", textValue)
    }
}
