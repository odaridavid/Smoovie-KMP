package dev.odaridavid.smoovie.shows

import dev.odaridavid.smoovie.movies.toDisplayRating
import dev.odaridavid.smoovie.movies.toReadableDate
import dev.odaridavid.smoovie.shows.data.Episode
import dev.odaridavid.smoovie.shows.data.SeasonDetail

data class SeasonDetailUiModel(
    val id: Int,
    val seasonNumber: Int,
    val name: String,
    val overview: String,
    val year: String,
    val posterUrl: String?,
    val episodeCountLabel: String,
    val episodes: List<EpisodeUiModel>,
)

data class EpisodeUiModel(
    val id: Int,
    val episodeNumber: Int,
    val name: String,
    val overview: String,
    val airDate: String,
    val runtimeLabel: String,
    val voteAverage: String,
    val stillUrl: String?,
    val headerLabel: String,
)

internal fun SeasonDetail.toUiModel(
    posterUrl: String?,
    stillUrlResolver: (String?) -> String? = { null },
) = SeasonDetailUiModel(
    id = id,
    seasonNumber = seasonNumber,
    name = name,
    overview = overview,
    year = airDate.orEmpty().take(4),
    posterUrl = posterUrl,
    episodeCountLabel = if (episodes.size == 1) "1 episode" else "${episodes.size} episodes",
    episodes =
        episodes
            .sortedBy { it.episodeNumber }
            .map { it.toUiModel(stillUrl = stillUrlResolver(it.stillPath)) },
)

private fun Episode.toUiModel(stillUrl: String?) =
    EpisodeUiModel(
        id = id,
        episodeNumber = episodeNumber,
        name = name,
        overview = overview,
        airDate = airDate?.toReadableDate().orEmpty(),
        runtimeLabel = runtime?.let { "$it min" }.orEmpty(),
        voteAverage = voteAverage.toDisplayRating(),
        stillUrl = stillUrl,
        headerLabel =
            if (name.isNotBlank()) {
                "Ep $episodeNumber · $name"
            } else {
                "Ep $episodeNumber"
            },
    )
