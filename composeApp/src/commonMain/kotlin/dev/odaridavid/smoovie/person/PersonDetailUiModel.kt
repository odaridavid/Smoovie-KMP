package dev.odaridavid.smoovie.person

import dev.odaridavid.smoovie.movies.MovieUiModel
import dev.odaridavid.smoovie.utils.toDisplayRating
import dev.odaridavid.smoovie.utils.toReadableDate
import dev.odaridavid.smoovie.person.data.PersonDetail
import dev.odaridavid.smoovie.person.data.PersonMovieCredit
import dev.odaridavid.smoovie.person.data.PersonTvCredit
import dev.odaridavid.smoovie.shows.TvShowUiModel

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
    val movieFilmography: List<PersonMovieFilmographyItem> = emptyList(),
    val tvFilmography: List<PersonTvFilmographyItem> = emptyList(),
)

data class PersonMovieFilmographyItem(
    val movie: MovieUiModel,
    val role: String,
)

data class PersonTvFilmographyItem(
    val tvShow: TvShowUiModel,
    val role: String,
)

internal fun PersonDetail.toDetailUiModel(
    profileUrl: String?,
    moviePosterUrlResolver: (String?) -> String? = { null },
    movieBackdropUrlResolver: (String?) -> String? = { null },
    tvPosterUrlResolver: (String?) -> String? = { null },
    tvBackdropUrlResolver: (String?) -> String? = { null },
) = PersonDetailUiModel(
    id = id,
    name = name,
    biography = biography.trim(),
    birthday = birthday?.toReadableDate().orEmpty(),
    placeOfBirth = placeOfBirth.orEmpty(),
    knownForDepartment = knownForDepartment,
    profileUrl = profileUrl,
    movieFilmography =
        buildMovieFilmography(
            cast = movieCredits?.cast.orEmpty(),
            posterResolver = moviePosterUrlResolver,
            backdropResolver = movieBackdropUrlResolver,
        ),
    tvFilmography =
        buildTvFilmography(
            cast = tvCredits?.cast.orEmpty(),
            posterResolver = tvPosterUrlResolver,
            backdropResolver = tvBackdropUrlResolver,
        ),
)

private fun buildMovieFilmography(
    cast: List<PersonMovieCredit>,
    posterResolver: (String?) -> String?,
    backdropResolver: (String?) -> String?,
): List<PersonMovieFilmographyItem> {
    val seen = mutableSetOf<Int>()
    return cast
        .asSequence()
        .sortedByDescending { it.popularity }
        .filter { seen.add(it.id) }
        .map { credit ->
            PersonMovieFilmographyItem(
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

private fun buildTvFilmography(
    cast: List<PersonTvCredit>,
    posterResolver: (String?) -> String?,
    backdropResolver: (String?) -> String?,
): List<PersonTvFilmographyItem> {
    val seen = mutableSetOf<Int>()
    return cast
        .asSequence()
        .sortedByDescending { it.popularity }
        .filter { seen.add(it.id) }
        .map { credit ->
            PersonTvFilmographyItem(
                tvShow =
                    TvShowUiModel(
                        id = credit.id,
                        name = credit.name,
                        overview = credit.overview,
                        firstAirDate = credit.firstAirDate.toReadableDate(),
                        voteAverage = credit.voteAverage.toDisplayRating(),
                        backdropUrl = backdropResolver(credit.backdropPath),
                        posterUrl = posterResolver(credit.posterPath),
                    ),
                role = credit.character,
            )
        }.toList()
}
