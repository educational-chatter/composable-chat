package my.zukoap.composablechat.domain.repository

import my.zukoap.composablechat.domain.entity.auth.Visitor
import javax.inject.Singleton


interface VisitorRepository {
    fun getVisitorFromClient(): Visitor?
    fun setVisitorFromClient(visitor: Visitor?)
    fun getVisitorFromSharedPreferences(): Visitor?
    fun saveVisitor(visitor: Visitor)
    fun deleteVisitor()
}