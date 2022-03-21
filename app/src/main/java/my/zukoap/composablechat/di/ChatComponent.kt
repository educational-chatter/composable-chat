package my.zukoap.composablechat.di

import my.zukoap.composablechat.di.modules.init.networkModule
import my.zukoap.composablechat.di.modules.init.repositoryModule
import org.koin.core.module.Module

/*
import androidx.fragment.app.Fragment
import com.crafttalk.chat.di.modules.chat.*
import com.crafttalk.chat.presentation.ChatView
import dagger.BindsInstance
import dagger.Subcomponent
import my.zukoap.composablechat.di.modules.chat.NetworkModule
import my.zukoap.composablechat.di.modules.chat.RepositoryModule
import my.zukoap.composablechat.di.modules.chat.ViewModelModule

@ChatScope
@Subcomponent(
    modules = [
        NetworkModule::class,
        RepositoryModule::class,
        ViewModelModule::class
    ]
)
interface ChatComponent {
    @Subcomponent.Builder
    interface Builder {
        @BindsInstance fun parentFragment(parentFragment: Fragment): Builder
        fun build(): ChatComponent
    }
    fun inject(chatView: ChatView)
}*/

val chatComponent: List<Module> = listOf(networkModule, repositoryModule, )
