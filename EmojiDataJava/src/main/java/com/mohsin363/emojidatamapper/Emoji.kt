package com.mohsin363.emojidatamapper

data class Emoji(
    val name: String?,
    val unified: String,
    val aliases: List<String>,
    internal val isObsolete: Boolean,
    val category: Category,
    val sortOrder: Int,
    val skinVariations: List<SkinVariation>,
    internal val isPristine: Boolean
) {
    val unicode get() = unified.unicode
}