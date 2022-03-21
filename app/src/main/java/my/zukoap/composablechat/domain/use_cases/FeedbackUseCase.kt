package my.zukoap.composablechat.domain.use_cases

import my.zukoap.composablechat.domain.repository.FeedbackRepository
import javax.inject.Inject

class FeedbackUseCase(
    private val feedbackRepository: FeedbackRepository
) {

    fun giveFeedbackOnOperator(countStars: Int) {
        feedbackRepository.giveFeedbackOnOperator(countStars)
    }

}