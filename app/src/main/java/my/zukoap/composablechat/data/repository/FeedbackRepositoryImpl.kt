package my.zukoap.composablechat.data.repository

import my.zukoap.composablechat.data.remote.socket.SocketApi
import my.zukoap.composablechat.domain.repository.FeedbackRepository
import javax.inject.Inject

class FeedbackRepositoryImpl(
    private val socketApi: SocketApi
) : FeedbackRepository {

    override fun giveFeedbackOnOperator(countStars: Int) {
        socketApi.giveFeedbackOnOperator(countStars)
    }

}