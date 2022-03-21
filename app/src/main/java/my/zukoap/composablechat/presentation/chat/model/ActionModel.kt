package my.zukoap.composablechat.presentation.chat.model

import androidx.annotation.DrawableRes
import my.zukoap.composablechat.presentation.base.BaseItem

sealed class ActionModel(
    open val id: String,
    open val actionText: String,
    open val isSelected: Boolean
) : BaseItem()

data class ActionItem(
    override val id: String,
    override val actionText: String,
    override val isSelected: Boolean,
    //@DrawableRes val backgroundRes: Int
) : ActionModel(id, actionText, isSelected) {
    override fun <T : BaseItem> isSame(item: T): Boolean {
        return item is ActionItem && item.id == id
    }
}