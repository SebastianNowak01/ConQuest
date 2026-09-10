package com.maeldev.conquest

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.rules.TestWatcher
import org.junit.runner.Description

/**
 * Replaces [Dispatchers.Main] with a [TestDispatcher] for the duration of a test.
 *
 * ViewModels launch their work on `viewModelScope`, which is bound to the main dispatcher.
 * Without this the only way to observe that work was to sleep and hope; with an unconfined
 * test dispatcher the coroutine runs eagerly on the calling thread, so a ViewModel call has
 * already taken effect by the time it returns.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class MainDispatcherRule(
    val testDispatcher: TestDispatcher = UnconfinedTestDispatcher(),
) : TestWatcher() {
    override fun starting(description: Description) {
        Dispatchers.setMain(testDispatcher)
    }

    override fun finished(description: Description) {
        Dispatchers.resetMain()
    }
}
