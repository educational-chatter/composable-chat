package my.zukoap.composablechat.domain.use_cases

import my.zukoap.composablechat.common.AuthType
import my.zukoap.composablechat.common.ChatParams
import my.zukoap.composablechat.domain.entity.auth.Visitor
import my.zukoap.composablechat.domain.repository.VisitorRepository
import javax.inject.Inject

class VisitorUseCase(
    private val visitorRepository: VisitorRepository
) {
    fun getVisitor() : Visitor? {
        return when (ChatParams.authMode) {
            AuthType.AUTH_WITH_FORM -> {
                visitorRepository.getVisitorFromSharedPreferences()
            }
            AuthType.AUTH_WITHOUT_FORM -> {
                val visitor = visitorRepository.getVisitorFromClient() ?: throw Exception("Visitor must not be null!")
                visitor
            }
            else -> throw Exception("Not found type auth!")
        }
    }
    fun setVisitor(visitor: Visitor?) = visitorRepository.setVisitorFromClient(visitor)
    fun saveVisitor(visitor: Visitor) = visitorRepository.saveVisitor(visitor)

    fun clearDataVisitor() {
        when (ChatParams.authMode) {
            AuthType.AUTH_WITH_FORM -> visitorRepository.deleteVisitor()
            AuthType.AUTH_WITHOUT_FORM -> visitorRepository.setVisitorFromClient(null)
        }
    }

}