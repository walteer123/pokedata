package com.pokedata.core.designsystem.utils

fun String.capitalizeFirst(): String {
    return this.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
        .replace("-", " ")
}
