package my.zukoap.composablechat.data.repository

import android.database.sqlite.SQLiteConstraintException
import android.util.Log
import my.zukoap.composablechat.common.ChatParams
import my.zukoap.composablechat.common.OperatorNameMode
import my.zukoap.composablechat.common.OperatorPreviewMode
import my.zukoap.composablechat.data.helper.network.toData
import my.zukoap.composablechat.data.local.db.dao.MessageDao
import my.zukoap.composablechat.data.local.db.dao.PersonDao
import my.zukoap.composablechat.data.local.db.entity.PersonEntity
import my.zukoap.composablechat.data.remote.rest.PersonApi
import my.zukoap.composablechat.domain.repository.PersonRepository
import javax.inject.Inject

class PersonRepositoryImpl(
    private val personDao: PersonDao,
    private val messageDao: MessageDao,
    private val personApi: PersonApi
) : PersonRepository {

    override suspend fun updatePersonName(personId: String?, currentPersonName: String?) {
        personId ?: return
        currentPersonName ?: return
        if (personId.isEmpty()) return
        when (ChatParams.operatorNameMode) {
            OperatorNameMode.ACTUAL -> {
                messageDao.updatePersonName(personId, currentPersonName)
            }
        }
    }

    override suspend fun getPersonPreview(personId: String, visitorToken: String): String? {
        if (personId.isEmpty()) return null
        return try {
            when (ChatParams.operatorPreviewMode) {
                OperatorPreviewMode.CACHE -> {
                    personDao.getPersonPreview(personId)
                        ?: personApi.getPersonPreview(
                            personId = personId,
                            visitorToken = visitorToken
                        ).toData()?.picture?.apply {
                            try {
                                personDao.addPersonPreview(PersonEntity(personId, this))
                                messageDao.updatePersonPreview(personId, this)
                            } catch (ex: SQLiteConstraintException) {}
                        }
                }
                OperatorPreviewMode.ALWAYS_REQUEST -> {
                    personApi.getPersonPreview(
                        personId = personId,
                        visitorToken = visitorToken
                    ).toData()?.picture.apply {
                        messageDao.updatePersonPreview(personId, this)
                    }
                }
                else -> null
            }
        } catch (ex: Exception) {
            Log.e("FAIL_REQUEST", "getPersonPreview fail: ${ex.message}")
            null
        }
    }

}