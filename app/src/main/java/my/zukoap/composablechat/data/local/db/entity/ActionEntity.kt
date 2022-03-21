package my.zukoap.composablechat.data.local.db.entity

import androidx.room.ColumnInfo
import my.zukoap.composablechat.domain.entity.message.NetworkAction

data class ActionEntity(
    @ColumnInfo(name = "action_id")
    val actionId: String,
    @ColumnInfo(name = "action_text")
    val actionText: String,
    @ColumnInfo(name = "is_selected")
    val isSelected: Boolean
) {
    companion object {

        fun map(actions: List<NetworkAction>): List<ActionEntity> {
            return actions.map {
                ActionEntity(
                    it.actionId,
                    it.actionText,
                    false
                )
            }
        }

        fun map(actions: List<NetworkAction>, actionsSelected: List<String>): List<ActionEntity> {
            return actions.map {
                ActionEntity(
                    it.actionId,
                    it.actionText,
                    actionsSelected.contains(it.actionId)
                )
            }
        }

    }
}