package my.zukoap.composablechat.di.modules.init

import com.google.gson.Gson
import org.koin.dsl.module

/*@Module
class GsonModule {

    @Provides
    @Singleton
    fun provideGson(): Gson = Gson()

}*/

val gsonModule = module {
    single<Gson> { Gson() }
}