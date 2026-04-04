package com.pokedata.core.ui.extensions

fun String.capitalizeFirst(): String {
    return this.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
        .replace("-", " ")
}

fun Int.formatHeight(): String {
    val meters = this / 10.0
    return "${meters} m"
}

fun Int.formatWeight(): String {
    val kg = this / 10.0
    return "${kg} kg"
}
