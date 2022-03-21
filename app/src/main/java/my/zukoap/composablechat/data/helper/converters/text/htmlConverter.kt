package my.zukoap.composablechat.data.helper.converters.text

import android.os.Build
import android.text.Html
import android.util.Log
import my.zukoap.composablechat.common.ChatParams
import my.zukoap.composablechat.common.ClickableLinkMode
import my.zukoap.composablechat.domain.entity.tags.*
import java.util.regex.Matcher
import java.util.regex.Pattern

fun String.convertTextToNormalString(listTag: ArrayList<Tag>): String {
    fun <T: Tag> setTagsByPatterns(text: String, regexs: Array<CharSequence>, factory : (startPosition: Int, endPosition: Int, value: String) -> T, listTag: ArrayList<Tag>) {
        regexs.forEach { regex ->
            val pattern: Pattern = Pattern.compile(regex.toString())
            val matcher: Matcher = pattern.matcher(text)
            while (matcher.find()) {
                listTag.add(factory(matcher.start(), matcher.end(), matcher.group()))
            }
        }
    }
    fun String.selectPhones(): String {
        setTagsByPatterns(
            this,
            ChatParams.phonePatterns!!,
            { startPosition: Int, endPosition: Int, value: String ->
                PhoneTag(
                    startPosition,
                    endPosition,
                    value
                        .replace("(", "")
                        .replace(")", "")
                        .replace("-", "")
                        .replace(" ", "")
                )
            },
            listTag
        )
        return this
    }
    return when {
        this.replace("\n", "").let {
            it.matches(Regex(".*<(strong|i|a|img|ul|li|br|p).*")) ||
            it.matches(Regex(".*&(nbsp|pound|euro|para|sect|copy|reg|trade|deg|plusmn|frac14|frac12|frac34|times|divide|fnof);.*")) ||
            it.matches(Regex(".*&(larr|uarr|rarr|darr|harr);.*")) ||
            it.matches(Regex(".*&(spades|clubs|hearts|diams|quot|amp|lt|gt);.*")) ||
            it.matches(Regex(".*&(hellip|prime|Prime);.*")) ||
            it.matches(Regex(".*&(ndash|mdash|lsquo|rsquo|sbquo|ldquo|rdquo|bdquo|laquo|raquo);.*")) ||
            it.matches(Regex(".*&#[0-9]*;.*"))
        } -> convertFromHtmlTextToNormalString(listTag).selectPhones()
        else -> convertFromBaseTextToNormalString(listTag).selectPhones()
    }
}

fun String.convertFromBaseTextToNormalString(listTag: ArrayList<Tag>): String {
    fun selectUrl(input: String, protocol: String) {
        var startIndex = 0
        startIndex = input.indexOf(protocol, startIndex, true)

        while (startIndex != -1) {
            val indexSpace = input.indexOf(' ', startIndex, true)
            val indexNewLine = input.indexOf("\n", startIndex, true)
            val indexEndSentence = input.indexOf(".\n", startIndex, true)
            val endIndex = when {
                indexSpace != -1 && indexNewLine != -1 && indexEndSentence != -1 -> Math.min(Math.min(indexSpace, indexNewLine), indexEndSentence)
                indexSpace != -1 && indexNewLine != -1 && indexEndSentence == -1 -> Math.min(indexSpace, indexNewLine)
                indexSpace != -1 && indexNewLine == -1 && indexEndSentence != -1 -> Math.min(indexSpace, indexEndSentence)
                indexSpace != -1 && indexNewLine == -1 && indexEndSentence == -1 -> indexSpace
                indexSpace == -1 && indexNewLine != -1 && indexEndSentence != -1 -> Math.min(indexNewLine, indexEndSentence)
                indexSpace == -1 && indexNewLine != -1 && indexEndSentence == -1 -> indexNewLine
                indexSpace == -1 && indexNewLine == -1 && indexEndSentence != -1 -> indexEndSentence
                else -> -1
            }
            var url = if (endIndex == -1) {
                input.substring(startIndex)
            } else {
                input.substring(startIndex, endIndex)
            }
            if (protocol == "www") {
                url = "https://$url"
            }
            listTag.add(
                UrlTag(
                    startIndex,
                    if (endIndex == -1) input.length - 1 else endIndex - 1,
                    url
                )
            )
            startIndex = input.indexOf(protocol, startIndex + 1, true)
        }
    }
    when (ChatParams.clickableLinkMode) {
        ClickableLinkMode.ALL -> {
            selectUrl(this, "http")
            selectUrl(this, "ws")
        }
        ClickableLinkMode.SECURE -> {
            selectUrl(this, "https")
            selectUrl(this, "wss")
        }
    }
//    selectUrl(this, "www")
    return this
}

fun String.convertFromHtmlTextToNormalString(listTag: ArrayList<Tag>): String {
    // атрибуты должны удовлетворять формату: <attrName>="<value>"
    fun String.getAttrTag(startIndex: Int, isSingleTag: Boolean): AttrTag? {
        val endTagIndex =
            if (isSingleTag) this.indexOf("/>", startIndex, true)
            else this.indexOf(">", startIndex, true)
        if (endTagIndex <= startIndex || this.substring(startIndex, endTagIndex).trim().isEmpty()) return null

        val separatorIndex = this.indexOf("=", startIndex, true)
        val startSingleQuotesValueIndex = this.indexOf("\'", separatorIndex + 1, true)
        val lastSingleQuotesValueIndex = this.indexOf("\'", startSingleQuotesValueIndex + 1, true)
        val startDoubleQuotesValueIndex = this.indexOf("\"", separatorIndex + 1, true)
        val lastDoubleQuotesValueIndex = this.indexOf("\"", startDoubleQuotesValueIndex + 1, true)

        val startResultValueIndex = if (startSingleQuotesValueIndex == -1) startDoubleQuotesValueIndex else startSingleQuotesValueIndex
        val lastResultValueIndex = if (lastSingleQuotesValueIndex == -1) lastDoubleQuotesValueIndex else lastSingleQuotesValueIndex

        return AttrTag(
            this.substring(startIndex, separatorIndex).trim(),
            this.substring(startResultValueIndex + 1, lastResultValueIndex).trim()
        )
    }

    fun addTag(tagName: String, listAttrs: List<AttrTag>, startIndex: Int, endIndex: Int) {
        when (tagName) {
            "strike" -> listTag.add(StrikeTag(startIndex + 1, endIndex))
            "strong" -> listTag.add(StrongTag(startIndex + 1, endIndex))
            "b" -> listTag.add(BTag(startIndex + 1, endIndex))
            "i" -> listTag.add(ItalicTag(startIndex + 1, endIndex))
            "em" -> listTag.add(EmTag(startIndex + 1, endIndex))
            "a" -> listTag.add(UrlTag(startIndex + 1, endIndex, listAttrs.find { it.attrName == "href" }?.value ?: ""))
            "img" -> {
                listTag.add(
                    ImageTag(
                        startIndex + 1,
                        endIndex,
                        listAttrs.find { it.attrName == "src" }?.value ?: "",
                        listAttrs.find { it.attrName == "width" }?.value?.toInt() ?: 0,
                        listAttrs.find { it.attrName == "height" }?.value?.toInt() ?: 0
                    )
                )
            }
            "ul" -> {
                listTag.add(
                    HostListTag(
                        startIndex + 1,
                        endIndex,
                        ((listTag.findLast { it.javaClass == HostListTag::class && it.pointEnd == -1 } as? HostListTag)?.countNesting ?: -1) + 1
                    )
                )
            }
            "li" -> {
                listTag.add(
                    ItemListTag(
                        startIndex + 1,
                        endIndex,
                        ((listTag.findLast { it.javaClass == HostListTag::class && it.pointEnd == -1 } as? HostListTag)?.countNesting ?: -1) + 1
                    )
                )
            }
        }
    }
    fun addTag(tagName: String, listAttrs: List<AttrTag>, startIndex: Int) {
        addTag(tagName, listAttrs, startIndex, -1)
    }
    fun updateStateTag(tagName: String, endIndex: Int) {
        listTag.findLast { it.name == tagName && it.pointEnd == -1 }?.let { it.pointEnd = endIndex }
    }

    fun checkCloseTag(startIndex: Int): Boolean {
        val closeTagIndex = this.indexOf(">", startIndex, true)
        return closeTagIndex > startIndex && closeTagIndex < this.length && this[closeTagIndex - 1] == '/'
    }
    fun replyOrExecuteTag(tagName: String, resultString: StringBuilder, block: () -> Unit) {
        when (tagName) {
            "p" -> {
                resultString.append("\n\n")
            }
            "br" -> {
                resultString.append("\n")
            }
            "li" -> {
                block()
                resultString.append("\n")
            }
            else -> block()
        }
    }

    val result = StringBuilder()
    val length = this.length
    val tagName = StringBuilder()
    val listAttrsTag = mutableListOf<AttrTag>()

    var isSelectTag = false
    var isSingleTag = false
    var isCloseTag = false
    var isReplaceTag = false
    var isFillAttrTag = false

    this.forEachIndexed { index, char ->
        when (char) {
            ' ' -> {
                if (isSelectTag) {
                    if ((isSingleTag || !isCloseTag) && !isReplaceTag) {
                        try {
                            getAttrTag(index, isSingleTag)?.let {
                                isFillAttrTag = true
                                listAttrsTag.add(it)
                            }
                        } catch (ex: StringIndexOutOfBoundsException) {
                            Log.e("FAIL_PARSE", "getAttrTag fail - ${ex.message}")
                        }
                    }
                } else {
                    result.append(char)
                }
            }
            '&' -> {
                isSelectTag = true
                isSingleTag = false
                isCloseTag = false
                isReplaceTag = true
                isFillAttrTag = false
                tagName.clear()
                listAttrsTag.clear()
            }
            '<' -> {
                isSelectTag = true
                isSingleTag = checkCloseTag(index)
                isCloseTag = length - index > 1 && (this[index + 1] == '/' || isSingleTag)
                isReplaceTag = false
                isFillAttrTag = false
                tagName.clear()
                listAttrsTag.clear()
            }
            '/' -> {
                if (isSelectTag) {
                    return@forEachIndexed
                } else {
                    result.append(char)
                }
            }
            ';' -> {
                if (isReplaceTag) {
                    result.append(
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                            Html.fromHtml("&${tagName};", Html.FROM_HTML_MODE_LEGACY)
                        } else {
                            @Suppress("DEPRECATION")
                            Html.fromHtml("&${tagName};")
                        }
                    )
                    isSelectTag = false
                    isSingleTag = false
                    isCloseTag = false
                    isReplaceTag = false
                    isFillAttrTag = false
                } else {
                    result.append(char)
                }
            }
            '>' -> {
                when (true) {
                    isSingleTag -> {
                        replyOrExecuteTag(tagName.toString(), result) {
                            addTag(tagName.toString().trim(), listAttrsTag, result.lastIndex, result.lastIndex)
                        }
                    }
                    !isSingleTag && isCloseTag -> {
                        replyOrExecuteTag(tagName.toString().trim(), result) {
                            updateStateTag(tagName.toString().trim(), result.lastIndex)
                        }
                    }
                    !isSingleTag && !isCloseTag -> addTag(tagName.toString().trim(), listAttrsTag, result.lastIndex)
                }
                isSelectTag = false
                isSingleTag = false
                isCloseTag = false
                isReplaceTag = false
                isFillAttrTag = false
            }
            else -> {
                when (true) {
                    isFillAttrTag -> return@forEachIndexed
                    isSelectTag -> tagName.append(char)
                    else -> result.append(char)
                }
            }
        }
    }
    return result.toString()
}