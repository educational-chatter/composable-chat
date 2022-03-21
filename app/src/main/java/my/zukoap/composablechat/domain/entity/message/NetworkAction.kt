package my.zukoap.composablechat.domain.entity.message

import com.google.gson.annotations.SerializedName
import my.zukoap.composablechat.data.local.db.entity.ActionEntity

data class NetworkAction (
    @SerializedName(value = "action_id")
    val actionId: String,
    @SerializedName (value = "action_text")
    val actionText: String
) {

    companion object {

        fun map(actionEntity: ActionEntity) = NetworkAction(
            actionId = actionEntity.actionId,
            actionText = actionEntity.actionText
        )

    }

}