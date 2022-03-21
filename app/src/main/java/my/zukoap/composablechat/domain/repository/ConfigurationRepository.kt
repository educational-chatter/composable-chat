package my.zukoap.composablechat.domain.repository

import my.zukoap.composablechat.domain.entity.configuration.NetworkResultConfiguration

interface ConfigurationRepository {
    fun getConfiguration(): NetworkResultConfiguration?
}