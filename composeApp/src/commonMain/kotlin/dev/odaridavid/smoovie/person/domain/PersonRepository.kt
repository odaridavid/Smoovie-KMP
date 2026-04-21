package dev.odaridavid.smoovie.person.domain

import dev.odaridavid.smoovie.person.PersonDetail

interface PersonRepository {
    suspend fun getPersonDetail(personId: Int): PersonDetail
}
