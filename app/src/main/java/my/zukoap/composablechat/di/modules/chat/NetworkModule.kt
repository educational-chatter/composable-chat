package my.zukoap.composablechat.di.modules.chat

import com.google.gson.GsonBuilder
import my.zukoap.composablechat.common.ChatParams
import my.zukoap.composablechat.data.remote.rest.ConfigurationApi
import my.zukoap.composablechat.data.remote.rest.FileApi
import okhttp3.OkHttpClient
import org.koin.core.qualifier.named
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/*
@Module
object NetworkModule {

    @Upload
    @ChatScope
    @Provides
    fun provideRetrofitClientUpload(okHttpClient: OkHttpClient): Retrofit = Retrofit
        .Builder()
        .baseUrl("${ChatParams.urlChatScheme}://${ChatParams.urlChatHost}")
        .client(okHttpClient)
        .addConverterFactory(
            GsonConverterFactory.create(
                GsonBuilder()
                    .setLenient()
                    .create()
            )
        )
        .build()

    @ChatScope
    @Provides
    fun provideFileApi(@Upload retrofit: Retrofit): FileApi = retrofit.create(FileApi::class.java)

    @ChatScope
    @Provides
    fun provideConfigurationApi(@Base retrofit: Retrofit): ConfigurationApi = retrofit.create(
        ConfigurationApi::class.java
    )

}*/

fun provideRetrofitClientUpload(okHttpClient: OkHttpClient): Retrofit {
    return Retrofit
        .Builder()
        .baseUrl("${ChatParams.urlChatScheme}://${ChatParams.urlChatHost}")
        .client(okHttpClient)
        .addConverterFactory(
            GsonConverterFactory.create(
                GsonBuilder()
                    .setLenient()
                    .create()
            )
        )
        .build()
}


fun provideFileApi(retrofit: Retrofit): FileApi = retrofit.create(FileApi::class.java)


fun provideConfigurationApi(retrofit: Retrofit): ConfigurationApi = retrofit.create(
    ConfigurationApi::class.java
)


val chatNetworkModule = module {
    //scope(named("chatScope")) {
        factory<Retrofit>(named("upload")) {
            provideRetrofitClientUpload(get<OkHttpClient>())
        }

        factory<FileApi> {
            provideFileApi(get<Retrofit>(named("upload")))
        }

        factory<ConfigurationApi> {
            provideConfigurationApi(get<Retrofit>(named("upload")))
        }
    //}
}