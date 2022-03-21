package my.zukoap.composablechat.di.modules.init

import com.google.gson.Gson
import my.zukoap.composablechat.common.ChatParams
import my.zukoap.composablechat.data.helper.network.TLSSocketFactory.Companion.enableTls
import my.zukoap.composablechat.data.local.db.dao.MessageDao
import my.zukoap.composablechat.data.remote.rest.NotificationApi
import my.zukoap.composablechat.data.remote.rest.PersonApi
import my.zukoap.composablechat.data.remote.socket.SocketApi
import okhttp3.CertificatePinner
import okhttp3.OkHttpClient
import org.koin.android.ext.koin.androidContext
import org.koin.core.qualifier.named
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/*@Module
class NetworkModule {

    @Singleton
    @Provides
    fun provideCertificatePinner(): CertificatePinner? {
        return ChatParams.certificatePinning?.let {
            CertificatePinner.Builder()
                .add(ChatParams.urlChatHost!!, it)
                .build()
        }
    }

    @Singleton
    @Provides
    fun provideOkHttpClient(certificate: CertificatePinner?) = OkHttpClient
        .Builder()
        .enableTls()
        .apply {
            certificate?.let { certificatePinner(it) }
            ChatParams.fileConnectTimeout?.let { connectTimeout(it, ChatParams.timeUnitTimeout) }
            ChatParams.fileReadTimeout?.let { readTimeout(it, ChatParams.timeUnitTimeout) }
            ChatParams.fileWriteTimeout?.let { writeTimeout(it, ChatParams.timeUnitTimeout) }
            ChatParams.fileCallTimeout?.let { callTimeout(it, ChatParams.timeUnitTimeout) }
        }
        .build()

    @Base
    @Singleton
    @Provides
    fun provideBaseRetrofitClient(okHttpClient: OkHttpClient, gson: Gson) = Retrofit.Builder()
        .baseUrl("${ChatParams.urlChatScheme}://${ChatParams.urlChatHost}")
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create(gson))
        .build()

    @Singleton
    @Provides
    fun provideNotificationApi(@Base retrofit: Retrofit): NotificationApi =
        retrofit.create(NotificationApi::class.java)

    @Singleton
    @Provides
    fun providePersonApi(@Base retrofit: Retrofit): PersonApi =
        retrofit.create(PersonApi::class.java)

    @Singleton
    @Provides
    fun provideSocketApi(messageDao: MessageDao, gson: Gson, context: Context) = SocketApi(
        messageDao,
        gson,
        context
    )

}*/

/*
fun provideCertificatePinner(): CertificatePinner? {
    return ChatParams.certificatePinning?.let {
        CertificatePinner.Builder()
            .add(ChatParams.urlChatHost!!, it)
            .build()
    }
}

fun provideOkHttpClient(certificate: CertificatePinner?): OkHttpClient {
    return OkHttpClient
        .Builder()
        .enableTls()
        .apply {
            certificate?.let { certificatePinner(it) }
            ChatParams.fileConnectTimeout?.let { connectTimeout(it, ChatParams.timeUnitTimeout) }
            ChatParams.fileReadTimeout?.let { readTimeout(it, ChatParams.timeUnitTimeout) }
            ChatParams.fileWriteTimeout?.let { writeTimeout(it, ChatParams.timeUnitTimeout) }
            ChatParams.fileCallTimeout?.let { callTimeout(it, ChatParams.timeUnitTimeout) }
        }
        .build()
}

fun provideBaseRetrofitClient(okHttpClient: OkHttpClient, gson: Gson): Retrofit {
    return Retrofit.Builder()
        .baseUrl("${ChatParams.urlChatScheme}://${ChatParams.urlChatHost}")
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create(gson))
        .build()
}

fun provideNotificationApi(retrofit: Retrofit): NotificationApi =
    retrofit.create(NotificationApi::class.java)

fun providePersonApi(retrofit: Retrofit): PersonApi = retrofit.create(PersonApi::class.java)

fun provideSocketApi(messagesDao: MessageDao, gson: Gson, context: Context) = SocketApi(
    messagesDao,
    gson,
    context
)

val networkModule = module {
    factory<CertificatePinner?> { provideCertificatePinner() }
    factory<OkHttpClient> { provideOkHttpClient(getOrNull<CertificatePinner>()) }
    single<Retrofit>(named("base")) { provideBaseRetrofitClient(get<OkHttpClient>(), get<Gson>()) }
    single<NotificationApi> { provideNotificationApi(get<Retrofit>(named("base"))) }
    single<PersonApi> { providePersonApi(get<Retrofit>(named("base"))) }
    single<SocketApi> { provideSocketApi(get<MessageDao>(), get<Gson>(), androidContext()) }
}
*/


val networkModule = module {
    factory<CertificatePinner?> {
        ChatParams.certificatePinning?.let {
            CertificatePinner.Builder()
                .add(ChatParams.urlChatHost!!, it)
                .build()
        }
    }

    single<OkHttpClient> {
        OkHttpClient
            .Builder()
            .enableTls()
            .apply {
                getOrNull<CertificatePinner>()?.let { certificatePinner(it) }
                ChatParams.fileConnectTimeout?.let {
                    connectTimeout(
                        it,
                        ChatParams.timeUnitTimeout
                    )
                }
                ChatParams.fileReadTimeout?.let { readTimeout(it, ChatParams.timeUnitTimeout) }
                ChatParams.fileWriteTimeout?.let { writeTimeout(it, ChatParams.timeUnitTimeout) }
                ChatParams.fileCallTimeout?.let { callTimeout(it, ChatParams.timeUnitTimeout) }
            }
            .build()
    }

    single<Retrofit>(named("base")) {
        Retrofit.Builder()
            .baseUrl("${ChatParams.urlChatScheme}://${ChatParams.urlChatHost}")
            .client(get<OkHttpClient>())
            .addConverterFactory(GsonConverterFactory.create(get<Gson>()))
            .build()
    }

    single<NotificationApi> {
        get<Retrofit>(named("base")).create(NotificationApi::class.java)
    }

    single<PersonApi> {
        get<Retrofit>(named("base")).create(PersonApi::class.java)
    }

    single<SocketApi> {
        SocketApi(
            get<MessageDao>(),
            get<Gson>(),
            androidContext()
        )
    }

}