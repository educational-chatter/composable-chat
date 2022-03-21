package my.zukoap.composablechat.domain.entity.tags

sealed class Tag(
    val name: String,
    @Transient
    open val pointStart: Int,
    @Transient
    open var pointEnd: Int
)
data class StrikeTag(
    override val pointStart: Int,
    override var pointEnd: Int
): Tag("strike", pointStart, pointEnd)
data class StrongTag(
    override val pointStart: Int,
    override var pointEnd: Int
): Tag("strong", pointStart, pointEnd)
data class BTag(
    override val pointStart: Int,
    override var pointEnd: Int
): Tag("b", pointStart, pointEnd)
data class ItalicTag(
    override val pointStart: Int,
    override var pointEnd: Int
): Tag("i", pointStart, pointEnd)
data class EmTag(
    override val pointStart: Int,
    override var pointEnd: Int
): Tag("em", pointStart, pointEnd)
data class UrlTag(
    override val pointStart: Int,
    override var pointEnd: Int,
    val url: String
): Tag("a", pointStart, pointEnd)
data class ImageTag(
    override val pointStart: Int,
    override var pointEnd: Int,
    val url: String,
    val width: Int,
    val height: Int
): Tag("img", pointStart, pointEnd)
data class HostListTag(
    override val pointStart: Int,
    override var pointEnd: Int,
    val countNesting: Int
): Tag("ul", pointStart, pointEnd)
data class ItemListTag(
    override val pointStart: Int,
    override var pointEnd: Int,
    val countNesting: Int
): Tag("li", pointStart, pointEnd)
data class PhoneTag(
    override val pointStart: Int,
    override var pointEnd: Int,
    val phone: String
): Tag("phone", pointStart, pointEnd)

class AttrTag(
    val attrName: String,
    val value: String
)