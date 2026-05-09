package dev.odaridavid.smoovie.settings

import dev.odaridavid.smoovie.FakeSettingsPreferencesStore
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

@OptIn(ExperimentalCoroutinesApi::class)
class SettingsViewModelTest {
    private val testDispatcher = UnconfinedTestDispatcher()

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
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
}
