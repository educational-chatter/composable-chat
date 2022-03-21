package my.zukoap.composablechat.data.repository

import my.zukoap.composablechat.data.helper.network.toData
import my.zukoap.composablechat.data.remote.rest.ConfigurationApi
import my.zukoap.composablechat.domain.repository.ConfigurationRepository
import javax.inject.Inject

class ConfigurationRepositoryImpl(
    private val configurationApi: ConfigurationApi
) : ConfigurationRepository {
    override fun getConfiguration() = configurationApi.getConfiguration().toData()
}