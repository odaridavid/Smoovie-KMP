package dev.odaridavid.smoovie.shows.data

import dev.odaridavid.smoovie.movies.data.Credits
import dev.odaridavid.smoovie.movies.data.ReviewsResponse
import dev.odaridavid.smoovie.movies.data.VideosResponse
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TvShowDetail(
    val id: Int,
    val name: String,
    val overview: String = "",
    val tagline: String = "",
    @SerialName("poster_path") val posterPath: String? = null,
    @SerialName("backdrop_path") val backdropPath: String? = null,
    @SerialName("first_air_date") val firstAirDate: String = "",
    @SerialName("last_air_date") val lastAirDate: String = "",
    @SerialName("vote_average") val voteAverage: Double = 0.0,
    @SerialName("vote_count") val voteCount: Int = 0,
    @SerialName("in_production") val inProduction: Boolean = false,
    @SerialName("number_of_seasons") val numberOfSeasons: Int = 0,
    @SerialName("number_of_episodes") val numberOfEpisodes: Int = 0,
    val status: String = "",
    val genres: List<TvGenre> = emptyList(),
    val networks: List<Network> = emptyList(),
    val seasons: List<Season> = emptyList(),
    val credits: Credits? = null,
    val videos: VideosResponse? = null,
    val reviews: ReviewsResponse? = null,
    val recommendations: TvShowsResponse? = null,
    val similar: TvShowsResponse? = null,
)

@Serializable
data class Network(
    val id: Int,
    val name: String,
    @SerialName("logo_path") val logoPath: String? = null,
    @SerialName("origin_country") val originCountry: String = "",
)

@Serializable
data class Season(
    val id: Int,
    val name: String,
    val overview: String = "",
    @SerialName("poster_path") val posterPath: String? = null,
    @SerialName("season_number") val seasonNumber: Int = 0,
    @SerialName("episode_count") val episodeCount: Int = 0,
    @SerialName("air_date") val airDate: String? = null,
)
