package my.zukoap.composablechat.presentation

import android.content.Context
import my.zukoap.composablechat.R
import my.zukoap.composablechat.common.HashUtils
import my.zukoap.composablechat.domain.entity.auth.Visitor
import my.zukoap.composablechat.presentation.DataVisitor.birthday
import my.zukoap.composablechat.presentation.DataVisitor.contract
import my.zukoap.composablechat.presentation.DataVisitor.email
import my.zukoap.composablechat.presentation.DataVisitor.firstName
import my.zukoap.composablechat.presentation.DataVisitor.lastName
import my.zukoap.composablechat.presentation.DataVisitor.phone
import my.zukoap.composablechat.presentation.DataVisitor.source
import my.zukoap.composablechat.presentation.DataVisitor.token
import my.zukoap.composablechat.presentation.DataVisitor.uuid

fun getVisitor(context: Context): Visitor = Visitor(
    uuid,
    token,
    firstName,
    lastName,
    email,
    phone,
    contract,
    birthday,
    HashUtils.getHash("SHA-256", "${getSalt(context)}${HashUtils.getHash("SHA-256", "${getSalt(context)}${source}")}")
)

object DataVisitor {
    const val uuid = "test_uuid"
    const val token = "test_token"
    const val firstName = "Karl"
    const val lastName = "Testovich"
    const val email = "email"
    const val phone = "000000000"
    const val contract = "contract_test"
    const val birthday = "00.00.00"
    const val source = "${uuid}${firstName}${lastName}${contract}${phone}${email}${birthday}"
}

fun getSalt(context: Context): String = context.getString(R.string.salt)