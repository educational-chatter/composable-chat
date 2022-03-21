package my.zukoap.composablechat.domain.use_cases

import my.zukoap.composablechat.common.ChatParams
import my.zukoap.composablechat.domain.repository.ConfigurationRepository
import javax.inject.Inject

class ConfigurationUseCase(
    private val configurationRepository: ConfigurationRepository
) {

    fun getConfiguration() {
        val config = configurationRepository.getConfiguration() ?: return
        ChatParams.glueMessage = config.chatAnnouncement
    }

}