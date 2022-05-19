package my.zukoap.composablechat.domain.use_cases

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import io.mockk.every
import io.mockk.mockk
import my.zukoap.composablechat.domain.repository.ConditionRepository
import my.zukoap.composablechat.domain.repository.MessageRepository
import org.junit.Assert.assertEquals
import org.junit.BeforeClass
import org.junit.Rule
import org.junit.Test

class MessageUseCaseTest {

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    companion object {
        val messageRepository: MessageRepository = mockk()
        val conditionRepository: ConditionRepository = mockk()
        val visitorUseCase: VisitorUseCase = mockk()
        val personUseCase: PersonUseCase = mockk()

            @BeforeClass
            @JvmStatic
            fun setup() {
                every { messageRepository.getCountUnreadMessages(any()) } returns 10
                every { messageRepository.getCountUnreadMessagesRange(any(), any()) } returns 5
            }
    }

    val messageUseCase = MessageUseCase(
        messageRepository,
        conditionRepository,
        visitorUseCase,
        personUseCase
    )

    @Test
    fun getCountUnreadMessages() {
        assertEquals(10, messageUseCase.getCountUnreadMessages(2, null))
    }

    @Test
    fun getCountUnreadMessagesRange() {
        assertEquals(5, messageUseCase.getCountUnreadMessages(2, 3))
    }
}