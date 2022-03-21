package my.zukoap.composablechat.di.modules.init

import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import android.os.Build
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

/*@Module
class SharedPreferencesModule {

    @Provides
    @Singleton
    fun provideSharedPreferences(
        context: Context
    ): SharedPreferences {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val masterKey = MasterKey.Builder(context)
                .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                .build()
            return EncryptedSharedPreferences.create(
                context,
                "crafttalkChatInfo",
                masterKey,
                EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            )
        } else {
            context.getSharedPreferences("crafttalkChatInfo", MODE_PRIVATE)
        }
    }


}*/

val sharedPreferencesModule = module {
    single<SharedPreferences> {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val masterKey = MasterKey.Builder(androidContext())
                .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                .build()
            EncryptedSharedPreferences.create(
                androidContext(),
                "crafttalkChatInfo",
                masterKey,
                EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            )
        } else {
            androidContext().getSharedPreferences("crafttalkChatInfo", MODE_PRIVATE)
        }
    }
}