package my.zukoap.composablechat.domain.use_cases

import my.zukoap.composablechat.domain.repository.PersonRepository
import javax.inject.Inject

class PersonUseCase(
    private val personRepository: PersonRepository
) {
    suspend fun updatePersonName(personId: String?, currentPersonName: String?) = personRepository.updatePersonName(personId, currentPersonName)
    suspend fun getPersonPreview(personId: String, visitorToken: String): String? = personRepository.getPersonPreview(personId, visitorToken)
}