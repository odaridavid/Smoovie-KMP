package dev.odaridavid.smoovie.person.data

import dev.odaridavid.smoovie.TMDB_BASE_URL
import dev.odaridavid.smoovie.person.domain.PersonRepository
import dev.odaridavid.smoovie.utils.TtlCache
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter

class PersonRepositoryImpl(
    private val client: HttpClient,
) : PersonRepository {
    private val detailCache = TtlCache<Int, PersonDetail>(CACHE_TTL_MS)

    override suspend fun getPersonDetail(personId: Int): PersonDetail =
        detailCache.getOrFetch(personId) {
            client
                .get("${Path.PERSON_DETAIL}/$personId") {
                    parameter(Parameter.APPEND_TO_RESPONSE, "movie_credits,tv_credits")
                }.body()
        }

    private object Path {
        const val PERSON_DETAIL = "${TMDB_BASE_URL}/person"
    }

    private object Parameter {
        const val APPEND_TO_RESPONSE = "append_to_response"
    }

    private companion object {
        const val CACHE_TTL_MS = 60 * 60 * 1_000L
    }
}
