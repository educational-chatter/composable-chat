package my.zukoap.composablechat.di.modules

import android.app.Application
import my.zukoap.composablechat.di.sdkModules
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class AppModule : Application() {
    override fun onCreate() {
        super.onCreate()

        startKoin {
            // Koin Android logger
            androidLogger()
            //inject Android context
            androidContext(this@AppModule)
            // use modules
            modules(sdkModules)
        }
    }
}