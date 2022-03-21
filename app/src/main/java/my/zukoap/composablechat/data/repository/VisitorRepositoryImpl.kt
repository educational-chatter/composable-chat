package my.zukoap.composablechat.data.repository

import android.content.SharedPreferences
import com.google.gson.Gson
import my.zukoap.composablechat.domain.entity.auth.Visitor
import my.zukoap.composablechat.domain.repository.VisitorRepository
import javax.inject.Inject

class VisitorRepositoryImpl(
    private val pref: SharedPreferences,
    private val gson: Gson
) : VisitorRepository {

    private var visitor: Visitor? = null

    override fun getVisitorFromClient(): Visitor? = visitor

    override fun setVisitorFromClient(visitor: Visitor?) {
        this.visitor = visitor
    }

    override fun getVisitorFromSharedPreferences(): Visitor? {
        return if (pref.getBoolean(FIELD_IS_VISITOR, false)) {
            val json = pref.getString(FIELD_VISITOR, "")
            gson.fromJson(json, Visitor::class.java)
        } else {
            null
        }
    }

    override fun saveVisitor(visitor: Visitor) {
        val prefEditor = pref.edit()
        prefEditor.putString(FIELD_VISITOR, visitor.getJsonObject().toString())
        prefEditor.putBoolean(FIELD_IS_VISITOR, true)
        prefEditor.apply()
    }

    override fun deleteVisitor() {
        val prefEditor = pref.edit()
        prefEditor.remove(FIELD_VISITOR)
        prefEditor.putBoolean(FIELD_IS_VISITOR, false)
        prefEditor.apply()
    }

    companion object {
        private const val FIELD_VISITOR = "visitor"
        private const val FIELD_IS_VISITOR = "isVisitor"
    }

}