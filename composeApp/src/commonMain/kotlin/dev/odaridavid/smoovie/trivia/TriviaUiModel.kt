package dev.odaridavid.smoovie.trivia

enum class TriviaQuestionKind {
    RELEASE_YEAR,
    RUNTIME,
    GENRE,
    DIRECTOR,
    CAST,
}

data class TriviaQuestionUiModel(
    val id: String,
    val kind: TriviaQuestionKind,
    val subject: String,
    val options: List<String>,
    val correctIndex: Int,
)
