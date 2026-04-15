package dev.odaridavid.smoovie.movies

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
)

@Serializable
data class Genre(
    val id: Int,
    val name: String,
)
