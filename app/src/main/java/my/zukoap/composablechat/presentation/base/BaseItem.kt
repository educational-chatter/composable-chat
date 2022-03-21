package my.zukoap.composablechat.presentation.base

abstract class BaseItem {
    //abstract fun getLayout() : Int
    abstract fun <T : BaseItem> isSame(item: T) : Boolean
}