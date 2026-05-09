package dev.odaridavid.smoovie.settings

data class Region(
    val code: String,
    val displayName: String,
)

val SUPPORTED_REGIONS: List<Region> =
    listOf(
        Region("US", "United States"),
        Region("GB", "United Kingdom"),
        Region("DE", "Germany"),
        Region("FR", "France"),
        Region("IT", "Italy"),
        Region("ES", "Spain"),
        Region("NL", "Netherlands"),
        Region("SE", "Sweden"),
        Region("NO", "Norway"),
        Region("DK", "Denmark"),
        Region("CA", "Canada"),
        Region("AU", "Australia"),
        Region("BR", "Brazil"),
        Region("MX", "Mexico"),
        Region("JP", "Japan"),
        Region("KR", "South Korea"),
        Region("IN", "India"),
        Region("KE", "Kenya"),
    ).sortedBy { it.displayName }

fun resolveRegion(code: String?): Region? = code?.let { c -> SUPPORTED_REGIONS.firstOrNull { it.code == c } }
