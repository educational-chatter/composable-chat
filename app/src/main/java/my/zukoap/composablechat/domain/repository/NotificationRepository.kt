package my.zukoap.composablechat.domain.repository

import javax.inject.Singleton


interface NotificationRepository {
     fun subscribe(uuid: String)
     fun unSubscribe(uuid: String)
     fun getToken(success: (token: String) -> Unit)
}