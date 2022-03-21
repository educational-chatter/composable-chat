package my.zukoap.composablechat.domain.repository

import javax.inject.Singleton


interface PersonRepository {
    suspend fun updatePersonName(personId: String?, currentPersonName: String?)
    suspend fun getPersonPreview(personId: String, visitorToken: String): String?
}