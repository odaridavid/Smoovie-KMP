package dev.odaridavid.smoovie.trivia.domain

import dev.odaridavid.smoovie.movies.data.MovieDetail
import dev.odaridavid.smoovie.movies.domain.MoviesRepository
import dev.odaridavid.smoovie.shared.data.CastMember
import dev.odaridavid.smoovie.trivia.TriviaQuestionKind
import dev.odaridavid.smoovie.trivia.TriviaQuestionUiModel
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope

class GenerateMovieTriviaUseCase(
    private val repository: MoviesRepository,
) {
    suspend operator fun invoke(movieId: Int): List<TriviaQuestionUiModel> =
        coroutineScope {
            val detail = repository.getMovieDetail(movieId)
            val genrePoolDeferred = async { runCatching { repository.getGenres() }.getOrDefault(emptyList()) }
            val distractorPoolDeferred = async { buildDistractorPool(detail) }

            val genrePool = genrePoolDeferred.await().map { it.name }
            val distractorPool = distractorPoolDeferred.await()

            buildList {
                yearQuestion(detail)?.let { add(it) }
                runtimeQuestion(detail)?.let { add(it) }
                genreQuestion(detail, genrePool)?.let { add(it) }
                directorQuestion(detail, distractorPool.directors)?.let { add(it) }
                castQuestion(detail, distractorPool.actors)?.let { add(it) }
            }.shuffled().take(MAX_QUESTIONS)
        }

    private suspend fun buildDistractorPool(detail: MovieDetail): DistractorPool {
        val otherIds =
            (detail.recommendations?.results.orEmpty() + detail.similar?.results.orEmpty())
                .map { it.id }
                .filter { it != detail.id }
                .distinct()
                .take(MAX_DISTRACTOR_SOURCES)
        val otherDetails = otherIds.mapNotNull { runCatching { repository.getMovieDetail(it) }.getOrNull() }
        val actors = otherDetails.flatMap { it.credits?.cast?.map(::castName) ?: emptyList() }.distinct()
        val directors = otherDetails.mapNotNull { directorName(it) }.distinct()
        return DistractorPool(actors = actors, directors = directors)
    }

    private fun yearQuestion(detail: MovieDetail): TriviaQuestionUiModel? {
        val year = detail.releaseDate.take(4).toIntOrNull() ?: return null
        val distractors = listOf(year - 3, year - 2, year - 1, year + 1, year + 2, year + 3).map { it.toString() }
        return buildQuestion("year", TriviaQuestionKind.RELEASE_YEAR, detail.title, year.toString(), distractors)
    }

    private fun runtimeQuestion(detail: MovieDetail): TriviaQuestionUiModel? {
        val runtime = detail.runtime?.takeIf { it > 0 } ?: return null
        val distractors =
            listOf(runtime - 30, runtime - 15, runtime + 15, runtime + 30, runtime + 45)
                .filter { it > 0 }
                .map(::formatRuntime)
        return buildQuestion("runtime", TriviaQuestionKind.RUNTIME, detail.title, formatRuntime(runtime), distractors)
    }

    private fun genreQuestion(
        detail: MovieDetail,
        genrePool: List<String>,
    ): TriviaQuestionUiModel? {
        val correct = detail.genres.firstOrNull()?.name ?: return null
        val movieGenres = detail.genres.map { it.name }.toSet()
        val distractors = genrePool.filter { it !in movieGenres }
        return buildQuestion("genre", TriviaQuestionKind.GENRE, detail.title, correct, distractors)
    }

    private fun directorQuestion(
        detail: MovieDetail,
        directorPool: List<String>,
    ): TriviaQuestionUiModel? {
        val correct = directorName(detail) ?: return null
        return buildQuestion("director", TriviaQuestionKind.DIRECTOR, detail.title, correct, directorPool)
    }

    private fun castQuestion(
        detail: MovieDetail,
        actorPool: List<String>,
    ): TriviaQuestionUiModel? {
        val correct =
            detail.credits
                ?.cast
                ?.minByOrNull { it.order }
                ?.let(::castName) ?: return null
        return buildQuestion("cast", TriviaQuestionKind.CAST, detail.title, correct, actorPool)
    }

    private fun buildQuestion(
        id: String,
        kind: TriviaQuestionKind,
        subject: String,
        correct: String,
        distractors: List<String>,
    ): TriviaQuestionUiModel? {
        val picked =
            distractors
                .filter { it.isNotBlank() && it != correct }
                .distinct()
                .shuffled()
                .take(MAX_OPTIONS - 1)
        if (picked.size < MIN_DISTRACTORS) return null
        val options = (picked + correct).shuffled()
        return TriviaQuestionUiModel(
            id = id,
            kind = kind,
            subject = subject,
            options = options,
            correctIndex = options.indexOf(correct),
        )
    }

    private data class DistractorPool(
        val actors: List<String>,
        val directors: List<String>,
    )

    private companion object {
        const val MAX_QUESTIONS = 5
        const val MAX_OPTIONS = 4
        const val MIN_DISTRACTORS = 2
        const val MAX_DISTRACTOR_SOURCES = 2
        const val DIRECTOR_JOB = "Director"

        fun castName(member: CastMember) = member.name

        fun directorName(detail: MovieDetail): String? =
            detail.credits
                ?.crew
                ?.firstOrNull { it.job == DIRECTOR_JOB }
                ?.name

        fun formatRuntime(minutes: Int): String = "${minutes / 60}h ${minutes % 60}m"
    }
}
