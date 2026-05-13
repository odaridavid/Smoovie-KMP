package dev.odaridavid.smoovie.settings

import dev.odaridavid.smoovie.FakeCrashReportingController
import dev.odaridavid.smoovie.FakeSettingsPreferencesStore
import dev.odaridavid.smoovie.observability.CrashReportingControllerRegistry
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class CrashReportingConsentViewModelTest {
    private val testDispatcher = UnconfinedTestDispatcher()
    private val crashReporting = FakeCrashReportingController()

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        CrashReportingControllerRegistry.instance = crashReporting
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
        CrashReportingControllerRegistry.instance = null
    }

    @Test
    fun `given prompt unseen - when viewmodel is created - then sheet is visible`() =
        runTest {
            val store = FakeSettingsPreferencesStore(initialHasSeenCrashReportingPrompt = false)

            val viewModel = CrashReportingConsentViewModel(store)

            assertTrue(viewModel.isVisible.value)
        }

    @Test
    fun `given prompt seen - when viewmodel is created - then sheet is hidden`() =
        runTest {
            val store = FakeSettingsPreferencesStore(initialHasSeenCrashReportingPrompt = true)

            val viewModel = CrashReportingConsentViewModel(store)

            assertFalse(viewModel.isVisible.value)
        }

    @Test
    fun `given prompt is shown - when onEnable - then crash reporting is on, prompt marked seen, controller invoked`() =
        runTest {
            val store = FakeSettingsPreferencesStore()
            val viewModel = CrashReportingConsentViewModel(store)

            viewModel.onEnable()

            assertTrue(store.crashReportingEnabled.value)
            assertTrue(store.hasSeenCrashReportingPrompt.value)
            assertFalse(viewModel.isVisible.value)
            assertEquals(listOf(true), crashReporting.calls)
        }

    @Test
    fun `given prompt is shown - when onDecline - then crash reporting stays off, prompt marked seen, controller untouched`() =
        runTest {
            val store = FakeSettingsPreferencesStore()
            val viewModel = CrashReportingConsentViewModel(store)

            viewModel.onDecline()

            assertFalse(store.crashReportingEnabled.value)
            assertTrue(store.hasSeenCrashReportingPrompt.value)
            assertFalse(viewModel.isVisible.value)
            assertTrue(crashReporting.calls.isEmpty())
        }
}
