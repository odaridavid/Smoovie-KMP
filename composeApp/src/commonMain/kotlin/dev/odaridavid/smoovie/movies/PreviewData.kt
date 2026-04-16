import dev.odaridavid.smoovie.movies.CastMemberUiModel
import dev.odaridavid.smoovie.movies.Movie
import dev.odaridavid.smoovie.movies.MovieDetailUiModel
import dev.odaridavid.smoovie.movies.toUiModel

internal val previewMovies =
    listOf(
        Movie(
            id = 1,
            title = "Interstellar",
            overview =
                "A team of explorers travel through a wormhole in space " +
                    "in an attempt to ensure humanity's survival.",
            releaseDate = "2014-11-05",
            voteAverage = 8.6,
        ),
        Movie(
            id = 2,
            title = "The Dark Knight",
            overview =
                "When the Joker wreaks havoc on Gotham, Batman must face " +
                    "his greatest psychological and physical test.",
            releaseDate = "2008-07-18",
            voteAverage = 9.0,
        ),
        Movie(
            id = 3,
            title = "Inception",
            overview =
                "A thief is given the task of planting an idea " +
                    "into a CEO's mind via dream-sharing technology.",
            releaseDate = "2010-07-16",
            voteAverage = 8.8,
        ),
        Movie(
            id = 4,
            title = "Inception",
            overview = "",
            releaseDate = "",
            voteAverage = 0.0,
        ),
    )

internal val previewMovieUiModels = previewMovies.map { it.toUiModel(backdropUrl = null) }

internal val previewCast =
    listOf(
        CastMemberUiModel(id = 1, name = "Matthew McConaughey", character = "Cooper", profileUrl = null),
        CastMemberUiModel(id = 2, name = "Anne Hathaway", character = "Brand", profileUrl = null),
        CastMemberUiModel(id = 3, name = "Jessica Chastain", character = "Murph", profileUrl = null),
        CastMemberUiModel(id = 4, name = "Michael Caine", character = "Professor Brand", profileUrl = null),
        CastMemberUiModel(id = 5, name = "Matt Damon", character = "Mann", profileUrl = null),
    )

internal val previewMovieDetailUiModel =
    MovieDetailUiModel(
        id = 1,
        title = "Interstellar",
        overview =
            "A team of explorers travel through a wormhole in space " +
                "in an attempt to ensure humanity's survival.",
        releaseDate = "5 Nov 2014",
        voteAverage = "8.6",
        voteCount = "34521",
        backdropUrl = null,
        posterUrl = null,
        runtime = "2h 49m",
        tagline = "Mankind was born on Earth. It was never meant to die here.",
        genres = "Adventure, Drama, Science Fiction",
        cast = previewCast,
    )
