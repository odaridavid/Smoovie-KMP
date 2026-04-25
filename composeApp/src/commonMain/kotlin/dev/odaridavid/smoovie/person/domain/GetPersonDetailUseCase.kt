package dev.odaridavid.smoovie.person.domain

import dev.odaridavid.smoovie.configuration.ConfigurationStore
import dev.odaridavid.smoovie.configuration.ProfileSize
import dev.odaridavid.smoovie.person.PersonDetailUiModel
import dev.odaridavid.smoovie.person.toDetailUiModel

class GetPersonDetailUseCase(
    private val repository: PersonRepository,
    private val configurationStore: ConfigurationStore,
) {
    suspend operator fun invoke(personId: Int): PersonDetailUiModel =
        repository.getPersonDetail(personId).let { person ->
            person.toDetailUiModel(
                profileUrl = configurationStore.profileUrl(person.profilePath, ProfileSize.LARGE),
                moviePosterUrlResolver = { configurationStore.posterUrl(it) },
                movieBackdropUrlResolver = { configurationStore.backdropUrl(it) },
                tvPosterUrlResolver = { configurationStore.posterUrl(it) },
                tvBackdropUrlResolver = { configurationStore.backdropUrl(it) },
            )
        }
}
