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
import kotlin.test.assertNull
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class SettingsViewModelTest {
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
    fun `given store has region - when viewmodel is created - then state reflects stored region`() =
        runTest {
            val store = FakeSettingsPreferencesStore(initialRegionCode = "DE")

            val viewModel = SettingsViewModel(store)

            assertEquals(
                "DE",
                viewModel.state.value.selectedRegion
                    ?.code,
            )
            assertEquals(
                "Germany",
                viewModel.state.value.selectedRegion
                    ?.displayName,
            )
        }

    @Test
    fun `given store has no region - when viewmodel is created - then selected region is null`() =
        runTest {
            val store = FakeSettingsPreferencesStore(initialRegionCode = null)

            val viewModel = SettingsViewModel(store)

            assertNull(viewModel.state.value.selectedRegion)
        }

    @Test
    fun `given store has unknown code - when viewmodel is created - then selected region is null`() =
        runTest {
            val store = FakeSettingsPreferencesStore(initialRegionCode = "ZZ")

            val viewModel = SettingsViewModel(store)

            assertNull(viewModel.state.value.selectedRegion)
        }

    @Test
    fun `given a region is selected - when onRegionSelected is called - then store is updated`() =
        runTest {
            val store = FakeSettingsPreferencesStore()
            val viewModel = SettingsViewModel(store)
            val region = SUPPORTED_REGIONS.first { it.code == "FR" }

            viewModel.onRegionSelected(region)

            assertEquals("FR", store.regionCode.value)
            assertEquals(
                "FR",
                viewModel.state.value.selectedRegion
                    ?.code,
            )
        }

    @Test
    fun `given store has crash reporting enabled - when viewmodel is created - then state reflects it`() =
        runTest {
            val store = FakeSettingsPreferencesStore(initialCrashReportingEnabled = true)

            val viewModel = SettingsViewModel(store)

            assertTrue(viewModel.state.value.crashReportingEnabled)
        }

    @Test
    fun `given store default - when viewmodel is created - then crash reporting is off`() =
        runTest {
            val store = FakeSettingsPreferencesStore()

            val viewModel = SettingsViewModel(store)

            assertEquals(false, viewModel.state.value.crashReportingEnabled)
        }

    @Test
    fun `given crash reporting is on - when onCrashReportingToggled false - then store and controller are updated`() =
        runTest {
            val store = FakeSettingsPreferencesStore(initialCrashReportingEnabled = true)
            val viewModel = SettingsViewModel(store)

            viewModel.onCrashReportingToggled(false)

            assertEquals(false, store.crashReportingEnabled.value)
            assertEquals(false, viewModel.state.value.crashReportingEnabled)
            assertEquals(listOf(false), crashReporting.calls)
        }

    @Test
    fun `given crash reporting is off - when onCrashReportingToggled true - then store and controller are updated`() =
        runTest {
            val store = FakeSettingsPreferencesStore(initialCrashReportingEnabled = false)
            val viewModel = SettingsViewModel(store)

            viewModel.onCrashReportingToggled(true)

            assertEquals(true, store.crashReportingEnabled.value)
            assertEquals(true, viewModel.state.value.crashReportingEnabled)
            assertEquals(listOf(true), crashReporting.calls)
        }
}
