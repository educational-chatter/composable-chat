package my.zukoap.composablechat.di

import javax.inject.Qualifier

@Qualifier
@Retention(value = AnnotationRetention.RUNTIME)
annotation class Base

@Qualifier
@Retention(value = AnnotationRetention.RUNTIME)
annotation class Upload