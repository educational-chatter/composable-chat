package my.zukoap.composablechat.domain.entity.file

enum class TypeFile(val value: String) {
    FILE("application/*"),
    IMAGE("image/*"),
    GIF("image/gif")
}