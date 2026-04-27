package dev.odaridavid.smoovie.utils

import kotlin.test.Test
import kotlin.test.assertEquals

class FormattersTest {
    // toDisplayRating

    @Test
    fun `given zero rating - when formatted - then returns empty string`() {
        assertEquals("", 0.0.toDisplayRating())
    }

    @Test
    fun `given rating with decimal - when formatted - then returns one decimal place`() {
        assertEquals("7.5", 7.5.toDisplayRating())
    }

    @Test
    fun `given whole number rating - when formatted - then trailing zero is shown`() {
        assertEquals("8.0", 8.0.toDisplayRating())
    }

    @Test
    fun `given maximum rating - when formatted - then returns ten point zero`() {
        assertEquals("10.0", 10.0.toDisplayRating())
    }

    @Test
    fun `given rating with floating point imprecision - when formatted - then rounds down`() {
        // 7.45 * 10 = 74.499... in IEEE 754, so rounds down to 74 → "7.4"
        assertEquals("7.4", 7.45.toDisplayRating())
    }

    @Test
    fun `given rating requiring rounding down - when formatted - then rounds correctly`() {
        assertEquals("7.4", 7.44.toDisplayRating())
    }

    @Test
    fun `given rating near boundary - when formatted - then rounds correctly`() {
        assertEquals("9.9", 9.9.toDisplayRating())
    }

    // toReadableDate

    @Test
    fun `given ISO date - when formatted - then returns day month year`() {
        assertEquals("15 Jan 2023", "2023-01-15".toReadableDate())
    }

    @Test
    fun `given date with leading zero day - when formatted - then zero is stripped`() {
        assertEquals("1 Dec 2023", "2023-12-01".toReadableDate())
    }

    @Test
    fun `given date with single digit month - when formatted - then leading zero in month stripped`() {
        assertEquals("9 Mar 2023", "2023-03-09".toReadableDate())
    }

    @Test
    fun `given date with two digit day - when formatted - then day preserved`() {
        assertEquals("25 Aug 2024", "2024-08-25".toReadableDate())
    }

    @Test
    fun `given malformed date with invalid month - when formatted - then returns input unchanged`() {
        assertEquals("2023-13-01", "2023-13-01".toReadableDate())
    }

    @Test
    fun `given malformed date with zero month - when formatted - then returns input unchanged`() {
        assertEquals("2023-00-15", "2023-00-15".toReadableDate())
    }

    @Test
    fun `given date string with only two parts - when formatted - then returns input unchanged`() {
        assertEquals("2023-01", "2023-01".toReadableDate())
    }

    @Test
    fun `given empty string - when formatted - then returns empty string`() {
        assertEquals("", "".toReadableDate())
    }

    @Test
    fun `given all twelve months - when formatted - then each month name is correct`() {
        val expected =
            listOf(
                "Jan",
                "Feb",
                "Mar",
                "Apr",
                "May",
                "Jun",
                "Jul",
                "Aug",
                "Sep",
                "Oct",
                "Nov",
                "Dec",
            )
        expected.forEachIndexed { index, name ->
            val month = (index + 1).toString().padStart(2, '0')
            assertEquals("1 $name 2023", "2023-$month-01".toReadableDate())
        }
    }
}
