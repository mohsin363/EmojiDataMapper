package com.mohsin363.emojidatamapper

data class SkinVariation(
    val types: List<SkinVariationType>,
    val unified: String
) {
    val unicode get() = unified.unicode
}
