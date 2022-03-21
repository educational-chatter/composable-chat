package my.zukoap.composablechat.domain.use_cases

import my.zukoap.composablechat.domain.repository.NotificationRepository
import javax.inject.Inject

class NotificationUseCase(
    private val notificationRepository: NotificationRepository,
    private val visitorUseCase: VisitorUseCase
) {

    fun subscribeNotification() {
        visitorUseCase.getVisitor()?.let {
            notificationRepository.subscribe(it.uuid)
        }
    }

    fun unsubscribeNotification() {
        visitorUseCase.getVisitor()?.let {
            notificationRepository.unSubscribe(it.uuid)
        }
    }

}