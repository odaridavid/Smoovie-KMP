package dev.odaridavid.smoovie.person

import dev.odaridavid.smoovie.person.data.MovieCredits
import dev.odaridavid.smoovie.person.data.PersonDetail
import dev.odaridavid.smoovie.person.data.PersonMovieCredit
import kotlin.test.Test
import kotlin.test.assertEquals

class PersonDetailUiModelTest {
    @Test
    fun `given person with biography - when mapped - then biography is trimmed`() {
        val person = personDetail(biography = "  Bio with whitespace.  ")

        val uiModel = person.toDetailUiModel(profileUrl = null)

        assertEquals("Bio with whitespace.", uiModel.biography)
    }

    @Test
    fun `given birthday in ISO format - when mapped - then birthday is human readable`() {
        val person = personDetail(birthday = "1969-11-04")

        val uiModel = person.toDetailUiModel(profileUrl = null)

        assertEquals("4 Nov 1969", uiModel.birthday)
    }

    @Test
    fun `given null birthday - when mapped - then birthday is empty`() {
        val person = personDetail(birthday = null)

        val uiModel = person.toDetailUiModel(profileUrl = null)

        assertEquals("", uiModel.birthday)
    }

    @Test
    fun `given no movie credits - when mapped - then filmography is empty`() {
        val person = personDetail(movieCredits = null)

        val uiModel = person.toDetailUiModel(profileUrl = null)

        assertEquals(emptyList(), uiModel.filmography)
    }

    @Test
    fun `given cast credits - when mapped - then filmography is sorted by popularity descending`() {
        val person =
            personDetail(
                movieCredits =
                    MovieCredits(
                        cast =
                            listOf(
                                credit(id = 1, title = "Low", popularity = 5.0),
                                credit(id = 2, title = "High", popularity = 50.0),
                                credit(id = 3, title = "Mid", popularity = 20.0),
                            ),
                    ),
            )

        val uiModel = person.toDetailUiModel(profileUrl = null)

        assertEquals(listOf(2, 3, 1), uiModel.filmography.map { it.movie.id })
    }

    @Test
    fun `given duplicate movie credits - when mapped - then duplicates are removed`() {
        val person =
            personDetail(
                movieCredits =
                    MovieCredits(
                        cast =
                            listOf(
                                credit(id = 1, character = "Young Cooper", popularity = 5.0),
                                credit(id = 1, character = "Old Cooper", popularity = 5.0),
                                credit(id = 2, popularity = 10.0),
                            ),
                    ),
            )

        val uiModel = person.toDetailUiModel(profileUrl = null)

        assertEquals(listOf(2, 1), uiModel.filmography.map { it.movie.id })
    }

    @Test
    fun `given more than twenty credits - when mapped - then filmography is capped at 20`() {
        val person =
            personDetail(
                movieCredits =
                    MovieCredits(
                        cast = (1..25).map { credit(id = it, popularity = it.toDouble()) },
                    ),
            )

        val uiModel = person.toDetailUiModel(profileUrl = null)

        assertEquals(20, uiModel.filmography.size)
    }

    @Test
    fun `given credit with character - when mapped - then role is populated`() {
        val person =
            personDetail(
                movieCredits =
                    MovieCredits(cast = listOf(credit(id = 1, character = "Cooper"))),
            )

        val uiModel = person.toDetailUiModel(profileUrl = null)

        assertEquals("Cooper", uiModel.filmography.first().role)
    }

    @Test
    fun `given credit with poster path - when mapped - then resolver is used for poster url`() {
        val person =
            personDetail(
                movieCredits =
                    MovieCredits(cast = listOf(credit(id = 1, posterPath = "/abc.jpg"))),
            )

        val uiModel =
            person.toDetailUiModel(
                profileUrl = null,
                moviePosterUrlResolver = { path -> path?.let { "https://img.example$it" } },
            )

        assertEquals(
            "https://img.example/abc.jpg",
            uiModel.filmography
                .first()
                .movie.posterUrl,
        )
    }

    private fun personDetail(
        biography: String = "",
        birthday: String? = null,
        movieCredits: MovieCredits? = null,
    ) = PersonDetail(
        id = 1,
        name = "Test Person",
        biography = biography,
        birthday = birthday,
        placeOfBirth = "Somewhere",
        knownForDepartment = "Acting",
        profilePath = null,
        popularity = 10.0,
        movieCredits = movieCredits,
    )

    private fun credit(
        id: Int,
        title: String = "Movie $id",
        character: String = "",
        popularity: Double = 10.0,
        posterPath: String? = null,
    ) = PersonMovieCredit(
        id = id,
        title = title,
        character = character,
        popularity = popularity,
        posterPath = posterPath,
        releaseDate = "2023-01-01",
        voteAverage = 7.0,
    )
}