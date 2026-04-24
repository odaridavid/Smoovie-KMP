package dev.odaridavid.smoovie.shows.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SeasonDetail(
    val id: Int,
    val name: String,
    val overview: String = "",
    @SerialName("poster_path") val posterPath: String? = null,
    @SerialName("season_number") val seasonNumber: Int = 0,
    @SerialName("air_date") val airDate: String? = null,
    val episodes: List<Episode> = emptyList(),
)

@Serializable
data class Episode(
    val id: Int,
    val name: String,
    val overview: String = "",
    @SerialName("still_path") val stillPath: String? = null,
    @SerialName("season_number") val seasonNumber: Int = 0,
    @SerialName("episode_number") val episodeNumber: Int = 0,
    @SerialName("air_date") val airDate: String? = null,
    val runtime: Int? = null,
    @SerialName("vote_average") val voteAverage: Double = 0.0,
    @SerialName("vote_count") val voteCount: Int = 0,
)
