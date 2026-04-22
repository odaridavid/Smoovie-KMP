package dev.odaridavid.smoovie

import dev.odaridavid.smoovie.person.PersonDetail
import dev.odaridavid.smoovie.person.domain.PersonRepository

class FakePersonRepository(
    var personDetail: PersonDetail? = null,
    var error: Exception? = null,
) : PersonRepository {
    override suspend fun getPersonDetail(personId: Int): PersonDetail {
        error?.let { throw it }
        return personDetail ?: error("No person detail configured")
    }
}
