package my.zukoap.composablechat.presentation.helper.converters

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.ParagraphStyle
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextIndent
import androidx.compose.ui.unit.ExperimentalUnitApi
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import my.zukoap.composablechat.domain.entity.tags.*
import my.zukoap.composablechat.presentation.ui.theme.hyperlinkColor
import kotlin.math.min


@OptIn(ExperimentalUnitApi::class)
fun String.convertToAnnotatedString(
    //authorIsUser: Boolean,
    spanStructureList: List<Tag>
): AnnotatedString {
    val strBuild = StringBuilder(this)
    spanStructureList.forEach {
        if (it is ItemListTag) {
            strBuild.insert(it.pointStart, "\u2022\t")
        }
    }
    return with(AnnotatedString.Builder(strBuild.toString())) {
        spanStructureList.forEach {
            when (it) {
                is StrikeTag -> addStyle(
                    SpanStyle(textDecoration = TextDecoration.LineThrough),
                    it.pointStart,
                    it.pointEnd + 1
                )
                is StrongTag, is BTag -> addStyle(
                    SpanStyle(fontWeight = FontWeight.Bold),
                    it.pointStart,
                    it.pointEnd + 1
                )
                is ItalicTag, is EmTag -> addStyle(
                    SpanStyle(fontStyle = FontStyle.Italic),
                    it.pointStart,
                    it.pointEnd + 1
                )
                is UrlTag -> {
                    addStringAnnotation(
                        tag = "URL",
                        annotation = it.url,
                        start = it.pointStart,
                        end = it.pointEnd + 1
                    )
                    addStyle(
                        SpanStyle(
                            color = hyperlinkColor,
                            textDecoration = TextDecoration.Underline
                        ),
                        it.pointStart,
                        it.pointEnd + 1
                    )
                }
                is ImageTag -> {
                    // load bitmap use it.url
//                ...
//                result.setSpan(
//                    ImageSpan(context, bitmap, DynamicDrawableSpan.ALIGN_BASELINE),
//                    it.pointStart,
//                    it.pointEnd + 1,
//                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
//                )
                }
                is ItemListTag -> {}
                is HostListTag -> {
                    addStyle(
                        ParagraphStyle(textIndent = TextIndent(TextUnit(80F, TextUnitType.Sp))),
                        it.pointStart,
                        it.pointEnd,
                    )
                }
                is PhoneTag -> {
                    addStringAnnotation(
                        tag = "Phone",
                        annotation = it.phone,
                        start = it.pointStart,
                        it.pointEnd
                    )
                    addStyle(
                        SpanStyle(
                            color = hyperlinkColor,
                            textDecoration = TextDecoration.Underline
                        ),
                        it.pointStart,
                        it.pointEnd
                    )
                }
            }
        }
        toAnnotatedString()
    }
}