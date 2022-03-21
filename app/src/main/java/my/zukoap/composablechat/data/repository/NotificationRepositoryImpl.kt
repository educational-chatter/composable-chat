package my.zukoap.composablechat.data.repository

import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
import my.zukoap.composablechat.data.remote.rest.NotificationApi
import my.zukoap.composablechat.domain.entity.notification.NetworkCheckSubscription
import my.zukoap.composablechat.domain.entity.notification.NetworkResultCheckSubscription
import my.zukoap.composablechat.domain.entity.notification.NetworkSubscription
import my.zukoap.composablechat.domain.entity.notification.NetworkUnsubscription
import my.zukoap.composablechat.domain.repository.NotificationRepository
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject

class NotificationRepositoryImpl(
    private val notificationApi: NotificationApi
) : NotificationRepository {

    private fun checkSubscription(uuid: String, hasSubscription: Boolean, success: () -> Unit) {
        notificationApi.checkSubscription(NetworkCheckSubscription(uuid)).enqueue(object : Callback<NetworkResultCheckSubscription> {
            override fun onResponse(call: Call<NetworkResultCheckSubscription>, response: Response<NetworkResultCheckSubscription>) {
                if (response.isSuccessful && response.body()?.result == hasSubscription) {
                    success()
                }
            }
            override fun onFailure(call: Call<NetworkResultCheckSubscription>, t: Throwable) {}
        })
    }

    override fun subscribe(uuid: String) {
        getToken { token ->
            checkSubscription(uuid, false) {
                notificationApi.subscribe(NetworkSubscription(token, uuid)).enqueue(object : Callback<Unit> {
                    override fun onResponse(call: Call<Unit>, response: Response<Unit>) {}
                    override fun onFailure(call: Call<Unit>, t: Throwable) {}
                })
            }
        }
    }

    override fun unSubscribe(uuid: String) {
        checkSubscription(uuid, true) {
            notificationApi.unsubscribe(NetworkUnsubscription(uuid)).enqueue(object : Callback<Unit> {
                override fun onResponse(call: Call<Unit>, response: Response<Unit>) {}
                override fun onFailure(call: Call<Unit>, t: Throwable) {}
            })
        }
    }

    override fun getToken(success: (token: String) -> Unit) {
        FirebaseMessaging.getInstance().token
            .addOnCompleteListener(OnCompleteListener { task ->
                if (!task.isSuccessful) {
                    return@OnCompleteListener
                }
                val token = task.result
                token?.let(success)
            })
    }

}