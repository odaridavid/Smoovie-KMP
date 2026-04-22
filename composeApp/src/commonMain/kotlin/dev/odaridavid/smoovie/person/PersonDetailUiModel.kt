package dev.odaridavid.smoovie.person

import dev.odaridavid.smoovie.movies.MovieUiModel
import dev.odaridavid.smoovie.movies.toDisplayRating
import dev.odaridavid.smoovie.movies.toReadableDate
import dev.odaridavid.smoovie.person.data.PersonDetail
import dev.odaridavid.smoovie.person.data.PersonMovieCredit

data class PersonSummaryUiModel(
    val id: Int,
    val name: String,
    val profileUrl: String?,
)

data class PersonDetailUiModel(
    val id: Int,
    val name: String,
    val biography: String,
    val birthday: String,
    val placeOfBirth: String,
    val knownForDepartment: String,
    val profileUrl: String?,
    val filmography: List<PersonFilmographyItemUiModel> = emptyList(),
)

data class PersonFilmographyItemUiModel(
    val movie: MovieUiModel,
    val role: String,
)

internal fun PersonDetail.toDetailUiModel(
    profileUrl: String?,
    moviePosterUrlResolver: (String?) -> String? = { null },
    movieBackdropUrlResolver: (String?) -> String? = { null },
) = PersonDetailUiModel(
    id = id,
    name = name,
    biography = biography.trim(),
    birthday = birthday?.toReadableDate().orEmpty(),
    placeOfBirth = placeOfBirth.orEmpty(),
    knownForDepartment = knownForDepartment,
    profileUrl = profileUrl,
    filmography =
        buildFilmography(
            cast = movieCredits?.cast.orEmpty(),
            posterResolver = moviePosterUrlResolver,
            backdropResolver = movieBackdropUrlResolver,
        ),
)

private fun buildFilmography(
    cast: List<PersonMovieCredit>,
    posterResolver: (String?) -> String?,
    backdropResolver: (String?) -> String?,
): List<PersonFilmographyItemUiModel> {
    val seen = mutableSetOf<Int>()
    return cast
        .asSequence()
        .sortedByDescending { it.popularity }
        .filter { seen.add(it.id) }
        .take(MAX_FILMOGRAPHY_DISPLAY)
        .map { credit ->
            PersonFilmographyItemUiModel(
                movie =
                    MovieUiModel(
                        id = credit.id,
                        title = credit.title,
                        overview = credit.overview,
                        releaseDate = credit.releaseDate.toReadableDate(),
                        voteAverage = credit.voteAverage.toDisplayRating(),
                        backdropUrl = backdropResolver(credit.backdropPath),
                        posterUrl = posterResolver(credit.posterPath),
                    ),
                role = credit.character,
            )
        }.toList()
}

private const val MAX_FILMOGRAPHY_DISPLAY = 20
