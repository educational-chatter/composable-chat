package my.zukoap.composablechat.initialization

interface ChatMessageListener {
    fun getNewMessages(countMessages: Int)
}