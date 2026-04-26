package dev.odaridavid.smoovie.movies.data

import dev.odaridavid.smoovie.shared.data.Credits
import dev.odaridavid.smoovie.shared.data.Keyword
import dev.odaridavid.smoovie.shared.data.ReviewsResponse
import dev.odaridavid.smoovie.shared.data.VideosResponse
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MovieDetail(
    val id: Int,
    val title: String,
    val overview: String,
    @SerialName("poster_path") val posterPath: String? = null,
    @SerialName("backdrop_path") val backdropPath: String? = null,
    @SerialName("release_date") val releaseDate: String = "",
    @SerialName("vote_average") val voteAverage: Double = 0.0,
    @SerialName("vote_count") val voteCount: Int = 0,
    val runtime: Int? = null,
    val tagline: String = "",
    val genres: List<Genre> = emptyList(),
    val credits: Credits? = null,
    val reviews: ReviewsResponse? = null,
    val videos: VideosResponse? = null,
    val recommendations: MoviesResponse? = null,
    val similar: MoviesResponse? = null,
    @SerialName("release_dates") val releaseDates: ReleaseDatesResponse? = null,
)

@Serializable
data class ReleaseDatesResponse(
    val results: List<CountryReleaseDates> = emptyList(),
)

@Serializable
data class CountryReleaseDates(
    @SerialName("iso_3166_1") val countryCode: String,
    @SerialName("release_dates") val releaseDates: List<ReleaseDate> = emptyList(),
)

@Serializable
data class ReleaseDate(
    val certification: String = "",
    val type: Int = 0,
)

@Serializable
data class Genre(
    val id: Int,
    val name: String,
)

@Serializable
data class GenresResponse(
    val genres: List<Genre>,
)

@Serializable
data class KeywordsResponse(
    val id: Int,
    val keywords: List<Keyword> = emptyList(),
)
