package dev.odaridavid.smoovie.person.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PersonDetail(
    val id: Int,
    val name: String,
    val biography: String = "",
    val birthday: String? = null,
    val deathday: String? = null,
    @SerialName("place_of_birth") val placeOfBirth: String? = null,
    @SerialName("known_for_department") val knownForDepartment: String = "",
    @SerialName("profile_path") val profilePath: String? = null,
    val popularity: Double = 0.0,
    @SerialName("movie_credits") val movieCredits: MovieCredits? = null,
    @SerialName("tv_credits") val tvCredits: TvCredits? = null,
)

@Serializable
data class MovieCredits(
    val cast: List<PersonMovieCredit> = emptyList(),
    val crew: List<PersonMovieCredit> = emptyList(),
)

@Serializable
data class PersonMovieCredit(
    val id: Int,
    val title: String,
    val overview: String = "",
    val character: String = "",
    val job: String = "",
    @SerialName("poster_path") val posterPath: String? = null,
    @SerialName("backdrop_path") val backdropPath: String? = null,
    @SerialName("release_date") val releaseDate: String = "",
    @SerialName("vote_average") val voteAverage: Double = 0.0,
    val popularity: Double = 0.0,
)

@Serializable
data class TvCredits(
    val cast: List<PersonTvCredit> = emptyList(),
    val crew: List<PersonTvCredit> = emptyList(),
)

@Serializable
data class PersonTvCredit(
    val id: Int,
    val name: String,
    val overview: String = "",
    val character: String = "",
    val job: String = "",
    @SerialName("poster_path") val posterPath: String? = null,
    @SerialName("backdrop_path") val backdropPath: String? = null,
    @SerialName("first_air_date") val firstAirDate: String = "",
    @SerialName("vote_average") val voteAverage: Double = 0.0,
    val popularity: Double = 0.0,
    @SerialName("episode_count") val episodeCount: Int = 0,
)
