package my.zukoap.composablechat.domain.repository

interface FeedbackRepository {
    fun giveFeedbackOnOperator(countStars: Int)
}